package org.services

import org.dtos.Notification
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

class NotificationServiceImpl(
    private val gateway: Gateway,
    private val rateLimiter: RateLimiter,
    private val sqsClient: SqsClient,
    private val queueUrl: String
) : NotificationService {
    override fun send(type: String, userId: String, message: String) {
        if (rateLimiter.isAllowed(userId)) {
            gateway.sendNotification(type, userId, message)
        } else {
            val blockedTime = rateLimiter.getBlockedTime(userId)
            println("Rate limit exceeded for user: $userId. Adding to retry queue with delay of $blockedTime ms.")
            val notification = Notification(type, userId, message)
            val sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(notification.toString())
                .delaySeconds((blockedTime / 1000).toInt())
                .build()
            sqsClient.sendMessage(sendMessageRequest)
        }
    }
}