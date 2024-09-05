package org.services

interface RateLimiter {
    fun isAllowed(userId: String): Boolean
    fun getBlockedTime(userId: String): Long
}