package org.services

class Gateway {
    fun sendNotification(type: String, userId: String, message: String) {
        println("Sending notification: $type, $userId, $message")
    }
}