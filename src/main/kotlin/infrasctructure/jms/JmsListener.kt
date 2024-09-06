package org.services

import jakarta.jms.Message
import jakarta.jms.MessageListener
import jakarta.jms.TextMessage
import org.dtos.Notification
import org.slf4j.LoggerFactory

class JmsListener(
    private val notificationService: NotificationService
) : MessageListener {
    private val logger = LoggerFactory.getLogger(JmsListener::class.java)

    override fun onMessage(message: Message?) {
        if (message == null) {
            logger.info("Received null message")
            return
        }

        try {
            if (message is TextMessage) {
                val messageBody = message.text
                if (messageBody.isNullOrEmpty()) {
                    logger.info("Received message with empty body")
                    return
                }
                val notification = parseNotification(messageBody)
                logger.info("Received message with notification: ${notification.toJson()}")
                notificationService.send(notification.type, notification.userId, notification.message)
            } else {
                logger.info("Received non-text message")
            }
        } catch (e: Exception) {
            logger.error("Failed to process message", e)
        }
    }

    private fun parseNotification(messageBody: String): Notification {
        return Notification.fromJson(messageBody)
    }
}