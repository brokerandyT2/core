// core/src/commonMain/kotlin/com/x3squaredcircles/core/validators/CreateTipCommandValidator.kt
package com.x3squaredcircles.core.validators

import com.x3squaredcircles.core.commands.CreateTipCommand



class CreateTipCommandValidator : IValidator<CreateTipCommand> {
    override fun validate(value: CreateTipCommand): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (value.tipTypeId <= 0) {
            errors.add("TipType_ValidationError_IdRequired")
        }
        
        if (value.title.isBlank()) {
            errors.add("Tip_ValidationError_TitleRequired")
        } else if (value.title.length > 100) {
            errors.add("Tip_ValidationError_TitleMaxLength")
        }
        
        if (value.content.isBlank()) {
            errors.add("Tip_ValidationError_ContentRequired")
        } else if (value.content.length > 1000) {
            errors.add("Tip_ValidationError_ContentMaxLength")
        }
        
        if (value.fstop.length > 20) {
            errors.add("Tip_ValidationError_FstopMaxLength")
        }
        
        if (value.shutterSpeed.length > 20) {
            errors.add("Tip_ValidationError_ShutterSpeedMaxLength")
        }
        
        if (value.iso.length > 20) {
            errors.add("Tip_ValidationError_IsoMaxLength")
        }
        
        if (value.i8n.isBlank()) {
            errors.add("Tip_ValidationError_LocalizationRequired")
        } else if (value.i8n.length > 10) {
            errors.add("Tip_ValidationError_LocalizationMaxLength")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
}