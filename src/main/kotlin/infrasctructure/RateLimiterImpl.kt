package org.services

import dtos.RateLimitRule
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.TimeSource

class RateLimiterImpl(private val rules: List<RateLimitRule>): RateLimiter {
    private val buckets = ConcurrentHashMap<String, Bucket>()

    override fun getBucket(userId: String, type: String): Bucket {
        val key = "$userId:$type"
        return buckets.computeIfAbsent(key) {
            val rule = rules.find { it.type == type } ?: rules.find { it.type == "default" }!!
            Bucket(rule.limit.toInt(), rule.duration)
        }
    }

    override fun getRemainingBlockingTime(userId: String, type: String): Duration {
        val bucket = getBucket(userId, type)
        return bucket.getRemainingBlockingTime()
    }
}

class Bucket(private val limit: Int, private val duration: Duration) {
    private var tokens = limit
    private var lastRefillTime = TimeSource.Monotonic.markNow()
    private val initialDuration = duration

    @Synchronized
    fun tryConsume(): Boolean {
        refillTokens()
        return if (tokens > 0) {
            tokens--
            true
        } else {
            false
        }
    }

    @Synchronized
    fun getAvailableTokens(): Int {
        refillTokens()
        return tokens
    }

    @Synchronized
    fun getRemainingBlockingTime(): Duration {
        refillTokens()
        return if (tokens > 0) Duration.ZERO else initialDuration
    }

    private fun refillTokens() {
        val elapsed = lastRefillTime.elapsedNow()
        if (elapsed >= duration) {
            tokens = limit
            lastRefillTime = TimeSource.Monotonic.markNow()
        }
    }
}