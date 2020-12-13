import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.TextMessage.Strict
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage, WebSocketRequest}
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.{Future, Promise}

object RunWebSocket {
  def main(args: Array[String]) = {

    val kafkaserver = args(0)


    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val config = system.settings.config.getConfig("akka.kafka.producer")

    val producerSettings =
      ProducerSettings(config, new StringSerializer, new StringSerializer)
        .withBootstrapServers(kafkaserver)

    // Future[Done] is the materialized value of Sink.foreach,
    // emitted when the stream completes

    val incoming: Sink[Message, Future[Done]] =
      Flow[Message].mapAsync(4) {
        case message: TextMessage.Strict =>
          message.textStream.map(value => new ProducerRecord[String, String]("transactions-raw", value))
            .runWith(Producer.plainSink(producerSettings))

          println(message.text)
          Future.successful(Done)
        case message: TextMessage.Streamed =>
          message.textStream.runForeach(x => (x.toString))
        case message: BinaryMessage =>
          message.dataStream.runWith(Sink.ignore)
      }.toMat(Sink.last)(Keep.right)

    // send this as a message over the WebSocket
    val commandMessages = Seq(TextMessage("{\"op\":\"ping\"}"), TextMessage("{\"op\":\"unconfirmed_sub\"}"))
    val outgoing: Source[Strict, Promise[Option[Nothing]]] =
      Source(commandMessages.to[scala.collection.immutable.Seq]).concatMat(Source.maybe)(Keep.right)

    // flow to use (note: not re-usable!)
    val webSocketFlow = Http().webSocketClientFlow(WebSocketRequest("wss://ws.blockchain.info/inv"))

    // the materialized value is a tuple with
    // upgradeResponse is a Future[WebSocketUpgradeResponse] that
    // completes or fails when the connection succeeds or fails
    // and closed is a Future[Done] with the stream completion from the incoming sink
    val ((completionPromise, upgradeResponse), closed) =
    outgoing
      .viaMat(webSocketFlow)(Keep.both)
      .toMat(incoming)(Keep.both)
      .run()


  }
}
