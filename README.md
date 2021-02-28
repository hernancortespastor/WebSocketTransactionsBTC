# WebSocketTransactionsBTC

Implementación de un consumidor del WebSocket de Blockchain 'https://www.blockchain.com/es/api/api_websocket' y envío de datos en tiempo real al topic de Kafka 'transactions-raw'.


## Requisitos previos.
Servicio de Kafka disponible.

Instalar Scala y SBT:

```
sudo apt-get install scala
sudo apt-get install sbt
```
## Despliegue

Descargar repositorio:
```
git clone https://github.com/hernancortespastor/WebSocketTransactionsBTC
```

Abrir el proyecto  en Intellij seleccionando el fichero 'build.sbt' del repositorio descargado.

Abrir 'Sbt Shell' de Intellij y ejecutar:
```
assembly
```

Lanzar el .jar generado añadiendo como primer parámetro el servidor de Kafka donde se creará el topic y enviarán los datos (localhost o si Kafka está desplegado Public IPv4 DNS).

```
scala ./WebSocketTransactionsBTC-main/target/scala-2.11/BlockchainTransactionsAPP-assembly-0.1.jar <Public IPv4 DNS>:9092
```

Se muestran por consola todas las transacciones enviadas al topic de Kafka.






