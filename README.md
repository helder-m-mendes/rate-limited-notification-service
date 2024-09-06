# rate-limited-notification-service

This service was built on kotlin and gradle, and essentially it is a notification service that has rate limiting based on a config file setting rules for each client.
The retry logic uses a queue to reprocess unsent messages. For that, we need docker as a prerequisite to run the service. Alternatively the service can be run without docker, but the retry logic will not work.

### How to run
Clone the repository, and go to the root directory of the project. there are 3 ways to execute the project:



#### 1. Using gradle
First you need to up localstack for the retry logic, then starting the service.


and go to the root directory of the project and execute the following command:
```
./docker-compose up localstack
./gradlew run
```

#### 2. Using docker
Just run:
```
./docker-compose up
```
that will run both the main service and localstack.


#### 3. Using an IDE
Alternatively, you can open the project with an IDE with kotlin support like IntelliJ IDEA, where you can click with the right button on the file Application.kt and choose "Run Main.kt".
before that make sure you up the localstack:
```
./docker-compose up localstack
```

### How to run tests
To run the tests, you can execute the following command:
```
./gradlew test
```

This will also generate a test coverage report at the following path:
```
build/reports/jacoco/test/html/index.html
```
