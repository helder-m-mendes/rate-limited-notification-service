package org

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.services.Gateway
import org.services.NotificationServiceImpl
import org.services.RateLimiter
import org.services.RetryWorker
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest
import software.amazon.awssdk.services.sqs.model.QueueDoesNotExistException
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import java.net.URI

class MainTest {

    private lateinit var sqsClient: SqsClient
    private lateinit var notificationService: NotificationServiceImpl
    private lateinit var retryWorker: RetryWorker
    private val gateway: Gateway = mockk()
    private val rateLimiter: RateLimiter = mockk()

    @BeforeEach
    fun setUp() {
        val endpoint = URI.create(System.getenv("AWS_ENDPOINT") ?: "http://localhost:4566")
        val awsCredentials = AwsBasicCredentials.create("test", "test")
        sqsClient = mockk()
        every { sqsClient.createQueue(any<CreateQueueRequest>()) } returns mockk {
            every { queueUrl() } returns "http://localhost:4566/000000000000/notification-queue"
        }

        val createQueueRequest = CreateQueueRequest.builder()
            .queueName("notification-queue")
            .build()
        val createQueueResponse = sqsClient.createQueue(createQueueRequest)
        val queueUrl = createQueueResponse.queueUrl()

        notificationService = NotificationServiceImpl(gateway, rateLimiter, sqsClient, queueUrl)
        retryWorker = RetryWorker(notificationService, sqsClient, queueUrl)
        retryWorker.start()
    }

    @AfterEach
    fun tearDown() {
        retryWorker.stop()
    }

    @Test
    fun `test sending notifications`() {
        every { rateLimiter.isAllowed(any()) } returns true
        every { gateway.sendNotification(any(), any(), any()) } returns Unit

        notificationService.send("news", "user", "news 1")

        verify { gateway.sendNotification("news", "user", "news 1") }
    }

    @Test
    fun `test rate-limited notifications`() {
        every { rateLimiter.isAllowed(any()) } returns false
        every { rateLimiter.getBlockedTime(any()) } returns 2000L
        every { sqsClient.sendMessage(any<SendMessageRequest>()) } returns mockk()

        notificationService.send("news", "user", "news 1")

        verify {
            sqsClient.sendMessage(
                withArg<SendMessageRequest> {
                    it.queueUrl() == notificationService.getQueueUrl()
                    it.messageBody().contains("news 1")
                    it.delaySeconds() == 2
                }
            )
        }
    }

    @Test
    fun `test queue does not exist exception`() {
        every { rateLimiter.isAllowed(any()) } returns false
        every { rateLimiter.getBlockedTime(any()) } returns 2000L
        every { sqsClient.sendMessage(any<SendMessageRequest>()) } throws QueueDoesNotExistException.builder()
            .message("Queue does not exist").build()

        notificationService.send("news", "user", "news 1")

        verify {
            sqsClient.sendMessage(
                withArg<SendMessageRequest> {
                    it.queueUrl() == notificationService.getQueueUrl()
                    it.messageBody().contains("news 1")
                    it.delaySeconds() == 2
                }
            )
        }
    }
}