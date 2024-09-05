package services

import org.junit.jupiter.api.Assertions.*
import org.services.RateLimiterImpl
import kotlin.test.Test

class RateLimiterImplTest {
    @Test
    fun `test rate limiter`() {
        val rateLimiter = RateLimiterImpl()
        val userId = "user"
        repeat(5) {
            assertTrue(rateLimiter.isAllowed(userId))
        }
        assertFalse(rateLimiter.isAllowed(userId))
    }
}