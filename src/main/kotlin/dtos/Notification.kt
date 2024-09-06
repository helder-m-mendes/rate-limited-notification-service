package org.dtos

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

data class Notification(val type: String, val userId: String, val message: String) {
    companion object {
        private val objectMapper = jacksonObjectMapper().registerKotlinModule()


        fun fromJson(json: String): Notification {
            return objectMapper.readValue<Notification>(json)
        }
    }

    fun toJson(): String {
        return objectMapper.writeValueAsString(this)
    }
}