package services

import org.junit.jupiter.api.Assertions.*
import org.services.Gateway
import org.services.NotificationServiceImpl
import org.services.RateLimiterImpl
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.Test

class NotificationServiceImplTest {
    @Test
    fun `send notification`() {
        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        val originalOut = System.out
        System.setOut(printStream)
        try {
            val gateway = Gateway()
            val rateLimiterImpl = RateLimiterImpl()
            val notificationService = NotificationServiceImpl(gateway, rateLimiterImpl)
            notificationService.send("email", "123", "Hello")
            val output = outputStream.toString().trim()
            assertEquals("Sending notification: email, 123, Hello", output)
        } finally {
            System.setOut(originalOut)
        }
    }
}