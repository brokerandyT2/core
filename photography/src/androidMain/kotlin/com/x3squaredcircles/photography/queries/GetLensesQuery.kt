// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/queries/GetLensesQuery.kt
package com.x3squaredcircles.photography.queries

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IQuery
import com.x3squaredcircles.photography.dtos.GetLensesResultDto

data class GetLensesQuery(
    val skip: Int = 0,
    val take: Int = 20,
    val userLensesOnly: Boolean = false,
    val compatibleWithCameraId: Int? = null
) : IQuery<Result<GetLensesResultDto>>