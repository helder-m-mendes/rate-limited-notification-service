package org.services

import org.slf4j.LoggerFactory

class Gateway {
    private val logger = LoggerFactory.getLogger(Gateway::class.java)

    fun sendNotification(type: String, userId: String, message: String) {
        logger.info("Sending notification: $type, $userId, $message")
    }
}