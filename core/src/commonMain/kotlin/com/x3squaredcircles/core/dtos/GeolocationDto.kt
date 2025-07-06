// core\src\commonMain\kotlin\com\x3squaredcircles\core\dtos\GeolocationDto.kt
package com.x3squaredcircles.core.dtos

data class GeolocationDto(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double? = null,
    val accuracy: Double? = null,
    val timestamp: Long // Using epoch milliseconds instead of Instant
)