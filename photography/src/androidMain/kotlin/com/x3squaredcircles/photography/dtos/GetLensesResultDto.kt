// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/dtos/GetLensesResultDto.kt
package com.x3squaredcircles.photography.dtos

data class GetLensesResultDto(
    val lenses: List<LensDto> = emptyList(),
    val totalCount: Int = 0,
    val hasMore: Boolean = false
)