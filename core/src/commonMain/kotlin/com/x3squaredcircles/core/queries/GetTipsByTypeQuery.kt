// core\src\commonMain\kotlin\com\x3squaredcircles\core\queries\GetTipsByTypeQuery.kt
package com.x3squaredcircles.core.queries
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.TipDto
import com.x3squaredcircles.core.mediator.IQuery
data class GetTipsByTypeQuery(
val tipTypeId: Int
) : IQuery<Result<List<TipDto>>>