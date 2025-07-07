// core/src/commonMain/kotlin/com/x3squaredcircles/core/validators/GetLocationByTitleQueryValidator.kt
package com.x3squaredcircles.core.validators

import com.x3squaredcircles.core.queries.GetLocationByTitleQuery

class GetLocationByTitleQueryValidator : IValidator<GetLocationByTitleQuery> {
    override fun validate(value: GetLocationByTitleQuery): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (value.title.isBlank()) {
            errors.add("Location_ValidationError_TitleRequired")
        } else if (value.title.length > 100) {
            errors.add("Location_ValidationError_TitleMaxLength")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
}