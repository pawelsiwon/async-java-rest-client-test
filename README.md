# async-java-rest-client-test

## Requirements

Installed golang 1.20 
JDK 19 for testing virtual threads (gradle toolchain should download it automatically)

## How to run

Inside external-service folder run `make run` it should compile and run a dummy external service server  

Inside client run ./gradlew :bootRun

## How to play

Change application.properties of `client`, play with type of executor, number of requests etc.  

External service is mocked to response with times between 0 and 20 seconds. 
