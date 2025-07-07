// core/src/commonMain/kotlin/com/x3squaredcircles/core/validators/RemovePhotoCommandValidator.kt
package com.x3squaredcircles.core.validators

import com.x3squaredcircles.core.commands.RemovePhotoCommand



class RemovePhotoCommandValidator : IValidator<RemovePhotoCommand> {
    override fun validate(value: RemovePhotoCommand): ValidationResult {
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