// core/src/commonMain/kotlin/com/x3squaredcircles/core/queries/GetTipTypeByIdQuery.kt
package com.x3squaredcircles.core.queries

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.TipTypeDto
import com.x3squaredcircles.core.mediator.IQuery

data class GetTipTypeByIdQuery(val id: Int) : IQuery<Result<TipTypeDto>>
// This query is used to retrieve a specific TipType by its ID.