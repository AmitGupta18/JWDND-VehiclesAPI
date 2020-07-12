# Eureka Server

Eureka server to register and discover instances of different microservices.
Pricing service and Vehicles API needs to be registered with eureka server.
Vehicles API will discover Pricing service using Eureka server. 

## Instructions

#### Run the Code

```
$ mvn clean package
```

```
$ java -jar target/eureka-server-0.0.1-SNAPSHOT.jar
```

Import it in your favorite IDE as a Maven Project.
