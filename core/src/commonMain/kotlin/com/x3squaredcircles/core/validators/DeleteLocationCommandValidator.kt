// core/src/commonMain/kotlin/com/x3squaredcircles/core/validators/DeleteLocationCommandValidator.kt
package com.x3squaredcircles.core.validators

import com.x3squaredcircles.core.commands.DeleteLocationCommand

class DeleteLocationCommandValidator : IValidator<DeleteLocationCommand> {
    override fun validate(value: DeleteLocationCommand): ValidationResult {
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