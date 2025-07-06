// core\src\commonMain\kotlin\com\x3squaredcircles\core\dtos\LocationDto.kt

package com.x3squaredcircles.core.dtos
data class LocationDto(
val id: Int,
val title: String,
val description: String,
val latitude: Double,
val longitude: Double,
val city: String,
val state: String,
val photoPath: String?,
val timestamp: Long,
val isDeleted: Boolean
)