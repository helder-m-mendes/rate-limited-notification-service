package org.services

import org.dtos.Notification
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class RetryWorker(
    private val notificationService: NotificationService,
    private val sqsClient: SqsClient,
    private val queueUrl: String
) {
    private val executor = Executors.newSingleThreadScheduledExecutor()

    fun start() {
        executor.scheduleAtFixedRate({
            val receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .build()
            val messages = sqsClient.receiveMessage(receiveMessageRequest).messages()
            for (message in messages) {
                val notification = parseNotification(message.body())
                notificationService.send(notification.type, notification.userId, notification.message)
            }
        }, 0, 1, TimeUnit.MINUTES)
    }

    fun stop() {
        executor.shutdown()
    }

    private fun parseNotification(messageBody: String): Notification {
        return Notification.fromJson(messageBody)
    }
}