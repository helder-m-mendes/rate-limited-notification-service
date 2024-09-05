package org.services

class NotificationServiceImpl(
    private val gateway: Gateway,
    private val rateLimiter: RateLimiter
) : NotificationService {
    override fun send(type: String, userId: String, message: String) {
        if (rateLimiter.isAllowed(userId)) {
            gateway.sendNotification(type, userId, message)
        } else {
            println("Rate limit exceeded for user: $userId")
        }
    }
}