# rate-limited-notification-service

This service was built on kotlin and gradle, and essentially it is a notification service that has rate limiting based on a config file setting rules for each client.


### How to run
Clone the repository, and go to the root directory of the project and execute the following command:
```
./gradlew run
```
Alternatively, you can open the project with an IDE with kotlin support like IntelliJ IDEA, where you can click with the right button on the file Application.kt and choose "Run Main.kt".

### How to run tests
To run the tests, you can execute the following command:
```
./gradlew test
```

This will also generate a test coverage report at the following path:
```
build/reports/jacoco/test/html/index.html
```
