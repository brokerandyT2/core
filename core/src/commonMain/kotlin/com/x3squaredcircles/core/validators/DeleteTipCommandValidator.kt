// core/src/commonMain/kotlin/com/x3squaredcircles/core/validators/DeleteTipCommandValidator.kt
package com.x3squaredcircles.core.validators

import com.x3squaredcircles.core.commands.DeleteTipCommand



class DeleteTipCommandValidator : IValidator<DeleteTipCommand> {
    override fun validate(value: DeleteTipCommand): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (value.id <= 0) {
            errors.add("Tip_ValidationError_IdRequired")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
}