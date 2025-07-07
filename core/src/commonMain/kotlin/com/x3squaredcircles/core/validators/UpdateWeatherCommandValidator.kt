// core/src/commonMain/kotlin/com/x3squaredcircles/core/validators/UpdateWeatherCommandValidator.kt
package com.x3squaredcircles.core.validators

import com.x3squaredcircles.core.commands.UpdateWeatherCommand

class UpdateWeatherCommandValidator : IValidator<UpdateWeatherCommand> {
    override fun validate(value: UpdateWeatherCommand): ValidationResult {
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