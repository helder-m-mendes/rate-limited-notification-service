package org.services

import kotlin.time.Duration

interface RateLimiter {
    fun getBucket(userId: String, type: String): Bucket
    fun getRemainingBlockingTime(userId: String, type: String): Duration
}