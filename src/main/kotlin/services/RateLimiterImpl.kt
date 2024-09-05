package org.services

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import java.time.Duration

class RateLimiterImpl : RateLimiter {
    private val bucket: Bucket

    init {
        val limit = Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)))
        bucket = Bucket.builder().addLimit(limit).build()
    }

    override fun isAllowed(userId: String): Boolean {
        return bucket.tryConsume(1)
    }
}