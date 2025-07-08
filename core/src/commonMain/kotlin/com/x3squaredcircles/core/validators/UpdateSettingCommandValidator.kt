// core/src/commonMain/kotlin/com/x3squaredcircles/core/validators/UpdateSettingCommandValidator.kt
package com.x3squaredcircles.core.validators

import com.x3squaredcircles.core.commands.UpdateSettingCommand

class UpdateSettingCommandValidator : IValidator<UpdateSettingCommand> {
    override fun validate(value: UpdateSettingCommand): ValidationResult {
        val errors = mutableListOf<String>()

        if (value.key.isBlank()) {
            errors.add("Setting_ValidationError_KeyRequired")
        } else if (value.key.length > 50) {
            errors.add("Setting_ValidationError_KeyMaxLength")
        }

        if (value.value.length > 500) {
            errors.add("Setting_ValidationError_ValueMaxLength")
        }

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
}
