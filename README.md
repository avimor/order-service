Order service
=============

Small, simple and reactive order service, written in Java using Bringg API, spring webflux(netty) & boot

### Installation

Install JDK

http://www.oracle.com/technetwork/java/javase/downloads/index.html

Install maven

http://maven.apache.org/download.cgi

Clone the app

`git clone https://github.com/avimor/order-service.git`

chdir and build

`cd order-service`

`mvn clean package`

This will also run the tests

### Execution

Choose one of the following options:

1. Using spring boot maven plugin by running: `mvn spring-boot:run`

2. Running as jar: `java -jar target/order-service-9.9.9-SNAPSHOT.jar`

3. Import as maven module into your favorite IDE & run as spring boot application.

More details here: http://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-running-your-application.html

See `src/main/resources/application.properties` for port settings

### Endpoints & API

#### `POST /order`

Create customer & order & return bringg's task details

Parameters:

Order object containing customer object as json body

Example:

`curl -X POST http://localhost:8090/order -H 'cache-control: no-cache' -H 'content-type: application/json;charset=UTF-8' -d 
'{
  "title": "Neo Delivery",
  "details": "Remove the bug",
  "customer": {
    "name": "Trinity",
    "address": "Rabbit Hole",
    "phone": "4561230"
  }
}'`

#### `GET /order`

List customer's orders in last week as json array of bringg tasks

Parameters:

`phone` - Customer's phone as entered in order creation

Optional parameters:

`page` - Result page number, default `1`

Example:

`curl -X GET http://localhost:8090/order?phone=7894561&page=1 -H 'cache-control: no-cache' -H 'content-type: application/json;charset=UTF-8'`

#### `GET /actuator` / `/actuator/info` / `/actuator/health`

Return application info, status & health`

#### `POST /echo`

Just echo the request body (functional endpoint)

#### `GET /`

Hello World :)
