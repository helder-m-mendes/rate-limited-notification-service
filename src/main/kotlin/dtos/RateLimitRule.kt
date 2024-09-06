package dtos

import kotlin.time.Duration

data class RateLimitRule(
    val type: String,
    val limit: Long,
    val duration: Duration
)