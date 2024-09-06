package org.services

import dtos.RateLimitRule
import infrasctructure.jms.JmsConfig
import infrasctructure.jms.JmsProducer
import jakarta.jms.Session
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class NotificationServiceTest {
    private lateinit var sqsClient: SqsClient
    private lateinit var queueUrl: String
    private lateinit var rateLimiter: RateLimiterImpl
    private lateinit var jmsProducer: JmsProducer

    @BeforeEach
    fun setUp() {
        val connectionFactory = JmsConfig.createConnectionFactory()
        val connection = connectionFactory.createConnection()
        val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        val queue = session.createQueue(JmsConfig.queueName)
        queueUrl = JmsConfig.queueUrl()
        jmsProducer = JmsProducer(session, queue)
        // Purge the queue to ensure it's empty before each test
        JmsConfig.sqsClient.purgeQueue(PurgeQueueRequest.builder().queueUrl(queueUrl).build())

        // Define rate limit rules
        val rules = listOf(
            RateLimitRule("news", 1, 30.seconds),
        )

        // Initialize RateLimiter
        rateLimiter = RateLimiterImpl(rules)
    }

    @Test
    fun `test sending notification with rate limiter`() {
        val notificationService = NotificationServiceImpl(Gateway(), rateLimiter, jmsProducer)

        notificationService.send("news", "user", "news 1")
        notificationService.send("news", "user", "news 1")
        assertEquals(30.seconds, rateLimiter.getRemainingBlockingTime("user", "news"))
    }
}