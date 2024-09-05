package org.services

interface NotificationService {
    fun send(type: String, userId: String, message: String)
}
