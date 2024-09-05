package org.services

import org.dtos.Notification
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class RetryWorker(
    private val notificationService: NotificationService,
    private val sqsClient: SqsClient,
    private val queueUrl: String
) {
    private val executor = Executors.newSingleThreadScheduledExecutor()
    private val running = AtomicBoolean(true)
    private val logger = LoggerFactory.getLogger(RetryWorker::class.java)

    fun start() {
        logger.info("RetryWorker started")
        executor.scheduleAtFixedRate({
            if (running.get()) {
                logger.info("RetryWorker is running")
                val receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .build()
                val messages = sqsClient.receiveMessage(receiveMessageRequest).messages()
                for (message in messages) {
                    val notification = parseNotification(message.body())
                    notificationService.send(notification.type, notification.userId, notification.message)
                }
            } else {
                logger.info("RetryWorker is stopping")
            }
        }, 0, 1, TimeUnit.MINUTES)
    }

    fun stop() {
        logger.info("Stopping RetryWorker")
        running.set(false)
        executor.shutdown()
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executor.shutdownNow()
            Thread.currentThread().interrupt()
        }
        logger.info("RetryWorker stopped")
    }

    private fun parseNotification(messageBody: String): Notification {
        return Notification.fromJson(messageBody)
    }
}