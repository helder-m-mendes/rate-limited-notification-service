package services

import dtos.RateLimitRule
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.services.Bucket
import org.services.RateLimiterImpl
import kotlin.time.Duration.Companion.minutes

class RateLimiterImplTest {
    private lateinit var rateLimiter: RateLimiterImpl

    @BeforeEach
    fun setUp() {
        val rules = listOf(
            RateLimitRule("default", 1, 1.minutes)
        )
        rateLimiter = RateLimiterImpl(rules)
    }

    @Test
    fun `test rate limiter allows requests within limit`() {
        val userId = "user"
        val bucket: Bucket = rateLimiter.getBucket(userId, "default")
        assertTrue(bucket.tryConsume())
    }

    @Test
    fun `test rate limiter blocks requests exceeding limit`() {
        val userId = "user"
        val bucket: Bucket = rateLimiter.getBucket(userId, "default")
        assertTrue(bucket.tryConsume())
        assertFalse(bucket.tryConsume()) // This should be rate-limited
    }

    @Test
    fun `test remaining blocking time`() {
        val userId = "user"
        val bucket: Bucket = rateLimiter.getBucket(userId, "default")
        bucket.tryConsume()
        val remainingTime = rateLimiter.getRemainingBlockingTime(userId, "default")
        assertTrue(remainingTime.inWholeMilliseconds > 0)
    }
}