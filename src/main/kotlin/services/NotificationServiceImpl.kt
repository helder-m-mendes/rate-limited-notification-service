package org.services

class NotificationServiceImpl constructor(private val gateway: Gateway) : NotificationService {
    override fun send(type: String, userId: String, message: String) {
        gateway.sendNotification(type, userId, message)
    }
}