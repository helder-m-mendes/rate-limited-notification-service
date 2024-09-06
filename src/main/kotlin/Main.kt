package org.main

import dtos.RateLimitRule
import infrasctructure.jms.JmsConfig
import infrasctructure.jms.JmsProducer
import jakarta.jms.Session
import kotlinx.coroutines.runBlocking
import org.services.Gateway
import org.services.JmsListener
import org.services.NotificationServiceImpl
import org.services.RateLimiterImpl
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

fun main() = runBlocking {
    // Initialize JmsConfig
    val connectionFactory = JmsConfig.createConnectionFactory()
    val connection = connectionFactory.createConnection()
    val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    val queueUrl = JmsConfig.queueUrl()

    val queue = session.createQueue(JmsConfig.queueName)

    // Define rate limit rules
    val rules = listOf(
        RateLimitRule("status", 2, 1.minutes),
        RateLimitRule("news", 1, 10.seconds),
        RateLimitRule("marketing", 3, 1.hours),
        RateLimitRule("default", 10, 1.minutes)
    )

    // Initialize RateLimiter
    val rateLimiter = RateLimiterImpl(rules)

    // Initialize JmsProducer with queueUrl
    val jmsProducer = JmsProducer(session, queue)

    // Initialize NotificationService
    val notificationService = NotificationServiceImpl(Gateway(), rateLimiter, jmsProducer)

    // Initialize JmsListener
    val jmsListener = JmsListener(notificationService)

    // Set up JMS consumer
    val consumer = session.createConsumer(queue)
    consumer.messageListener = jmsListener

    // Start the connection to begin receiving messages
    connection.start()

    // Sample calls to notificationService.send
    notificationService.send("news", "user", "news 1")
    notificationService.send("news", "user", "news 2")
    notificationService.send("news", "user", "news 3")
    notificationService.send("news", "another user", "news 1")
    notificationService.send("update", "user", "update 1")
}