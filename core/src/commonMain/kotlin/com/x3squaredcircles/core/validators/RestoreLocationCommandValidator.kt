// core/src/commonMain/kotlin/com/x3squaredcircles/core/validators/RestoreLocationCommandValidator.kt
package com.x3squaredcircles.core.validators

import com.x3squaredcircles.core.commands.RestoreLocationCommand

class RestoreLocationCommandValidator : IValidator<RestoreLocationCommand> {
    override fun validate(value: RestoreLocationCommand): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (value.locationId <= 0) {
            errors.add("Location_ValidationError_LocationIdRequired")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
}