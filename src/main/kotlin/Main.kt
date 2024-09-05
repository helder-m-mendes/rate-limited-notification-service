package org

import org.services.Gateway
import org.services.NotificationServiceImpl
import org.services.RateLimiterImpl
import org.services.RetryWorker
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest
import java.net.URI

fun main() {
    val endpoint = URI.create(System.getenv("AWS_ENDPOINT") ?: "http://localhost:4566")
    val awsCredentials = AwsBasicCredentials.create("test", "test")
    val sqsClient = SqsClient.builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .endpointOverride(endpoint)
        .build()

    val createQueueRequest = CreateQueueRequest.builder()
        .queueName("notification-queue")
        .build()
    val createQueueResponse = sqsClient.createQueue(createQueueRequest)
    val queueUrl = createQueueResponse.queueUrl()

    val notificationService = NotificationServiceImpl(Gateway(), RateLimiterImpl(), sqsClient, queueUrl)
    val retryWorker = RetryWorker(notificationService, sqsClient, queueUrl)
    retryWorker.start()

    notificationService.send("news", "user", "news 1")
    notificationService.send("news", "user", "news 2")
    notificationService.send("news", "user", "news 3")
    notificationService.send("news", "another user", "news 1")
    notificationService.send("update", "user", "update 1")

    Runtime.getRuntime().addShutdownHook(Thread {
        retryWorker.stop()
    })
}