// core\src\commonMain\kotlin\com\x3squaredcircles\core\queries\GetAllTipTypesQuery.kt
package com.x3squaredcircles.core.queries
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.TipTypeDto
import com.x3squaredcircles.core.mediator.IQuery
class GetAllTipTypesQuery : IQuery<Result<List<TipTypeDto>>>