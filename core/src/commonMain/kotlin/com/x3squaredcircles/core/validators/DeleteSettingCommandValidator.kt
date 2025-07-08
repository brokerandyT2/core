// core/src/commonMain/kotlin/com/x3squaredcircles/core/validators/DeleteSettingCommandValidator.kt
package com.x3squaredcircles.core.validators


import com.x3squaredcircles.core.commands.DeleteSettingCommand

class DeleteSettingCommandValidator : IValidator<DeleteSettingCommand> {
    override fun validate(value: DeleteSettingCommand): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (value.key.isBlank()) {
            errors.add("Setting_ValidationError_KeyRequired")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
}