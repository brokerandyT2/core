// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/queries/GetCameraBodiesQuery.kt
package com.x3squaredcircles.photography.queries

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IQuery
import com.x3squaredcircles.photography.dtos.GetCameraBodiesResultDto

data class GetCameraBodiesQuery(
    val skip: Int = 0,
    val take: Int = 20,
    val userCamerasOnly: Boolean = false
) : IQuery<Result<GetCameraBodiesResultDto>>