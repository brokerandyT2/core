// com/x3squaredcircles/photography/application/queries/exposurecalculator/GetExposureValuesQueryValidator.kt
package com.x3squaredcircles.photography.application.queries.exposurecalculator

import com.x3squaredcircles.core.validators.IValidator
import com.x3squaredcircles.core.validators.ValidationResult
import com.x3squaredcircles.photography.application.services.models.ExposureIncrements

class GetExposureValuesQueryValidator : IValidator<GetExposureValuesQuery> {
    
    override fun validate(request: GetExposureValuesQuery): ValidationResult {
        val errors = mutableListOf<String>()
        
        // Validate that increments is a valid enum value
        // In Kotlin, enum values are always valid if they exist, so this check is mainly for completeness
        try {
            // If we get here, the enum is valid
            request.increments
        } catch (e: Exception) {
            errors.add("ExposureCalculator_ValidationError_IncrementRequired")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
}