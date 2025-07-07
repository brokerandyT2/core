// core/src/commonMain/kotlin/com/x3squaredcircles/core/validators/GetTipTypeByIdQueryValidator.kt
package com.x3squaredcircles.core.validators

import com.x3squaredcircles.core.queries.GetTipTypeByIdQuery

class GetTipTypeByIdQueryValidator : IValidator<GetTipTypeByIdQuery> {
    override fun validate(value: GetTipTypeByIdQuery): ValidationResult {
        val errors = mutableListOf<String>()
        if (value.id <= 0) {
            errors.add("TipType_ValidationError_IdRequired")
        }

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
}
