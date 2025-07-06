// core\src\commonMain\kotlin\com\x3squaredcircles\core\dtos\LocationListDto.kt
package com.x3squaredcircles.core.dtos
data class LocationListDto(
val id: Int,
val title: String,
val city: String,
val state: String,
val photoPath: String?,
val timestamp: Long,
val isDeleted: Boolean,
val latitude: Double,
val longitude: Double
)