// core/src/commonMain/kotlin/com/x3squaredcircles/core/validators/GetRandomTipCommandValidator.kt
package com.x3squaredcircles.core.validators

import com.x3squaredcircles.core.commands.GetRandomTipCommand

class GetRandomTipCommandValidator : IValidator<GetRandomTipCommand> {
    override fun validate(value: GetRandomTipCommand): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (value.tipTypeId <= 0) {
            errors.add("Tip_ValidationError_TipTypeIdRequired")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
}