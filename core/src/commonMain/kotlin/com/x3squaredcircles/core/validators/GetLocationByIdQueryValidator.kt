// core/src/commonMain/kotlin/com/x3squaredcircles/core/validators/GetLocationByIdQueryValidator.kt
package com.x3squaredcircles.core.validators

import com.x3squaredcircles.core.queries.GetLocationByIdQuery

class GetLocationByIdQueryValidator : IValidator<GetLocationByIdQuery> {
    override fun validate(value: GetLocationByIdQuery): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (value.id <= 0) {
            errors.add("Location_ValidationError_LocationIdRequired")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
}