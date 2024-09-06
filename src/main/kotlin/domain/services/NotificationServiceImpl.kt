package org.services

import infrasctructure.jms.JmsProducer
import org.dtos.Notification
import org.slf4j.LoggerFactory
import kotlin.time.DurationUnit

class NotificationServiceImpl(
    private val gateway: Gateway,
    private val rateLimiter: RateLimiter,
    private val jmsProducer: JmsProducer
) : NotificationService {
    private val logger = LoggerFactory.getLogger(NotificationServiceImpl::class.java)

    override fun send(type: String, userId: String, message: String) {
        val bucket = rateLimiter.getBucket(userId, type)
        if (bucket.tryConsume()) {
            gateway.sendNotification(type, userId, message)
            logger.info("Notification sent: $type to $userId")
        } else {
            val remainingTime = rateLimiter.getRemainingBlockingTime(userId, type)
            jmsProducer.sendMessage(Notification(type, userId, message).toJson(), remainingTime.toInt(DurationUnit.SECONDS))
            logger.warn("Rate limit exceeded for $type to $userId. Blocking for ${remainingTime.inWholeSeconds} seconds.")
        }
    }
}