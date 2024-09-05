package org.dtos

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

data class Notification(val type: String, val userId: String, val message: String) {
    companion object {
        private val objectMapper = jacksonObjectMapper()

        fun fromJson(json: String): Notification {
            return objectMapper.readValue(json)
        }
    }

    fun toJson(): String {
        return objectMapper.writeValueAsString(this)
    }
}