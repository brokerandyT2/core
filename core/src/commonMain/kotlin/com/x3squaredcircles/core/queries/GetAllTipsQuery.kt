// core/src/commonMain/kotlin/com/x3squaredcircles/core/queries/GetAllTipsQuery.kt
package com.x3squaredcircles.core.queries
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.TipDto
import com.x3squaredcircles.core.mediator.IQuery
class GetAllTipsQuery : IQuery<Result<List<TipDto>>>