// com/x3squaredcircles/photography/application/commands/exposurecalculator/CalculateExposureCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.exposurecalculator

import com.x3squaredcircles.core.validators.IValidator
import com.x3squaredcircles.core.validators.ValidationResult
import com.x3squaredcircles.photography.application.services.FixedValue

class CalculateExposureCommandValidator : IValidator<CalculateExposureCommand> {
    
    override fun validate(request: CalculateExposureCommand): ValidationResult {
        val errors = mutableListOf<String>()
        
        // Validate base exposure is not null
        if (request.baseExposure == null) {
            errors.add("ExposureCalculator_ValidationError_BaseExposureRequired")
            return ValidationResult.Failure(errors)
        }
        
        // Validate base exposure properties
        if (request.baseExposure.shutterSpeed.isBlank()) {
            errors.add("ExposureCalculator_ValidationError_ShutterSpeedRequired")
        }
        
        if (request.baseExposure.aperture.isBlank()) {
            errors.add("ExposureCalculator_ValidationError_ApertureRequired")
        }
        
        if (request.baseExposure.iso.isBlank()) {
            errors.add("ExposureCalculator_ValidationError_ISORequired")
        }
        
        // Validate EV compensation range
        if (request.evCompensation < -5.0 || request.evCompensation > 5.0) {
            errors.add("ExposureCalculator_ValidationError_EVCompensationRange")
        }
        
        // Validate target values based on what's being calculated
        when (request.toCalculate) {
            FixedValue.ShutterSpeeds -> {
                if (request.targetAperture.isBlank()) {
                    errors.add("ExposureCalculator_ValidationError_TargetApertureRequired")
                } else if (!isValidAperture(request.targetAperture)) {
                    errors.add("ExposureCalculator_ValidationError_ValidAperture")
                }
                
                if (request.targetIso.isBlank()) {
                    errors.add("ExposureCalculator_ValidationError_TargetISORequired")
                } else if (!isValidIso(request.targetIso)) {
                    errors.add("ExposureCalculator_ValidationError_ValidISO")
                }
            }
            FixedValue.Aperture -> {
                if (request.targetShutterSpeed.isBlank()) {
                    errors.add("ExposureCalculator_ValidationError_TargetShutterSpeedRequired")
                } else if (!isValidShutterSpeed(request.targetShutterSpeed)) {
                    errors.add("ExposureCalculator_ValidationError_ValidShutterSpeed")
                }
                
                if (request.targetIso.isBlank()) {
                    errors.add("ExposureCalculator_ValidationError_TargetISORequired")
                } else if (!isValidIso(request.targetIso)) {
                    errors.add("ExposureCalculator_ValidationError_ValidISO")
                }
            }
            FixedValue.ISO -> {
                if (request.targetShutterSpeed.isBlank()) {
                    errors.add("ExposureCalculator_ValidationError_TargetShutterSpeedRequired")
                } else if (!isValidShutterSpeed(request.targetShutterSpeed)) {
                    errors.add("ExposureCalculator_ValidationError_ValidShutterSpeed")
                }
                
                if (request.targetAperture.isBlank()) {
                    errors.add("ExposureCalculator_ValidationError_TargetApertureRequired")
                } else if (!isValidAperture(request.targetAperture)) {
                    errors.add("ExposureCalculator_ValidationError_ValidAperture")
                }
            }
            else -> {
                // Empty or other values are valid for some cases
            }
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
    
    private fun isValidAperture(aperture: String): Boolean {
        if (!aperture.startsWith("f/")) return false
        val fNumberStr = aperture.substring(2)
        return fNumberStr.toDoubleOrNull() != null
    }
    
    private fun isValidShutterSpeed(shutterSpeed: String): Boolean {
        return when {
            shutterSpeed.endsWith("\"") -> {
                // Handle speeds like "30""
                val value = shutterSpeed.dropLast(1)
                value.toDoubleOrNull() != null
            }
            shutterSpeed.contains("/") -> {
                // Handle fractional speeds like "1/125"
                val parts = shutterSpeed.split("/")
                parts.size == 2 && parts[0].toDoubleOrNull() != null && parts[1].toDoubleOrNull() != null
            }
            else -> {
                // Handle decimal speeds like "0.5"
                shutterSpeed.toDoubleOrNull() != null
            }
        }
    }
    
    private fun isValidIso(iso: String): Boolean {
        val isoValue = iso.toIntOrNull()
        return isoValue != null && isoValue > 0
    }
}