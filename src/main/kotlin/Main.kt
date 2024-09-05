package org

import org.services.Gateway
import org.services.NotificationServiceImpl
import org.services.RateLimiterImpl
import org.services.RetryWorker
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient

fun main() {
    val sqsClient = SqsClient.builder().region(Region.US_EAST_1).build()
    val queueUrl = "http://localhost:4566/000000000000/notification-queue"

    val notificationService = NotificationServiceImpl(Gateway(), RateLimiterImpl(), sqsClient, queueUrl)
    val retryWorker = RetryWorker(notificationService, sqsClient, queueUrl)
    retryWorker.start()

    notificationService.send("news", "user", "news 1")
    notificationService.send("news", "user", "news 2")
    notificationService.send("news", "user", "news 3")
    notificationService.send("news", "another user", "news 1")
    notificationService.send("update", "user", "update 1")

    // Add a shutdown hook to stop the retry worker gracefully
    Runtime.getRuntime().addShutdownHook(Thread {
        retryWorker.stop()
    })
}