package org.services

interface RateLimiter {
    fun isAllowed(userId: String): Boolean
}