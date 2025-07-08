// core/src/commonMain/kotlin/com/x3squaredcircles/core/queries/GetAllSettingsQuery.kt
package com.x3squaredcircles.core.queries
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IQuery
data class GetAllSettingsQueryResponse(
val id: Int,
val key: String,
val value: String,
val description: String,
val timestamp: Long
)
class GetAllSettingsQuery : IQuery<Result<List<GetAllSettingsQueryResponse>>>