// core/src/commonMain/kotlin/com/x3squaredcircles/core/dtos/SettingDto.kt
package com.x3squaredcircles.core.dtos

data class SettingDto(
        val id: Int,
        val key: String,
        val value: String,
        val description: String,
        val timestamp: Long
)
