// core/src/commonMain/kotlin/com/x3squaredcircles/core/validators/CreateTipTypeCommandValidator.kt
package com.x3squaredcircles.core.validators

import com.x3squaredcircles.core.commands.CreateTipTypeCommand

class CreateTipTypeCommandValidator : IValidator<CreateTipTypeCommand> {
    override fun validate(value: CreateTipTypeCommand): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (value.name.isBlank()) {
            errors.add("TipType_ValidationError_NameRequired")
        } else if (value.name.length > 100) {
            errors.add("TipType_ValidationError_NameMaxLength")
        }
        
        if (value.i8n.isBlank()) {
            errors.add("TipType_ValidationError_LocalizationRequired")
        } else if (value.i8n.length > 10) {
            errors.add("TipType_ValidationError_LocalizationMaxLength")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
}