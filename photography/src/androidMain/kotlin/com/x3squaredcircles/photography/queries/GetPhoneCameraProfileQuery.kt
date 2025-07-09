// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/queries/GetPhoneCameraProfileQuery.kt
package com.x3squaredcircles.photography.queries

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IQuery
import com.x3squaredcircles.photography.dtos.PhoneCameraProfileDto

class GetPhoneCameraProfileQuery : IQuery<Result<PhoneCameraProfileDto>> {
    // No parameters needed - gets the active phone camera profile
}