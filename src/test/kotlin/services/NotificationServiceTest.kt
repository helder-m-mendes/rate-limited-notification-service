package org.services

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.dtos.Notification
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse
import software.amazon.awssdk.services.sqs.model.Message
import kotlin.test.assertEquals

class NotificationServiceTest {

    @Test
    fun `test sending notification`() {
        val sqsClient = mockk<SqsClient>()
        val queueUrl = "http://localhost:4566/000000000000/notification-queue"

        every { sqsClient.createQueue(any<CreateQueueRequest>()) } returns CreateQueueResponse.builder().queueUrl(queueUrl).build()
        every { sqsClient.receiveMessage(any<ReceiveMessageRequest>()) } returns ReceiveMessageResponse.builder()
            .messages(Message.builder().body("""{"type":"news","userId":"user","message":"news 1"}""").build())
            .build()

        val notificationService = mockk<NotificationServiceImpl>(relaxed = true)
        every { notificationService.send(any(), any(), any()) } answers {
            sqsClient.createQueue(CreateQueueRequest.builder().queueName("notification-queue").build())
            sqsClient.receiveMessage(ReceiveMessageRequest.builder().queueUrl(queueUrl).maxNumberOfMessages(1).build())
        }

        val type = "news"
        val userId = "user"
        val message = "news 1"
        notificationService.send(type, userId, message)

        val receiveMessageRequest = ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .maxNumberOfMessages(1)
            .build()
        val messages = sqsClient.receiveMessage(receiveMessageRequest).messages()

        assertEquals(1, messages.size)
        val receivedMessage = messages[0].body()
        val expectedMessage = Notification(type, userId, message).toJson()
        assertEquals(expectedMessage, receivedMessage)

        verify { sqsClient.createQueue(any<CreateQueueRequest>()) }
        verify { sqsClient.receiveMessage(any<ReceiveMessageRequest>()) }
        verify { notificationService.send(type, userId, message) }
    }
}