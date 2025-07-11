// core/src/commonMain/kotlin/com/x3squaredcircles/core/queries/GetSettingByKeyQuery.kt
package com.x3squaredcircles.core.queries

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IQuery

data class GetSettingByKeyQuery(
    val key: String
) : IQuery<Result<GetSettingByKeyQueryResponse>>

data class GetSettingByKeyQueryResponse(
    val id: Int,
    val key: String,
    val value: String,
    val description: String,
    val timestamp: Long
)