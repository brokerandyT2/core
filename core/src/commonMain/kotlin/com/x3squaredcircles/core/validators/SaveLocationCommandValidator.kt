// core/src/commonMain/kotlin/com/x3squaredcircles/core/validators/SaveLocationCommandValidator.kt
package com.x3squaredcircles.core.validators

import com.x3squaredcircles.core.commands.SaveLocationCommand

class SaveLocationCommandValidator : IValidator<SaveLocationCommand> {
    override fun validate(value: SaveLocationCommand): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (value.title.isBlank()) {
            errors.add("Location_ValidationError_TitleRequired")
        } else if (value.title.length > 100) {
            errors.add("Location_ValidationError_TitleMaxLength")
        }
        
        if (value.description.length > 500) {
            errors.add("Location_ValidationError_DescriptionMaxLength")
        }
        
        if (value.latitude < -90.0 || value.latitude > 90.0) {
            errors.add("Location_ValidationError_LatitudeRange")
        }
        
        if (value.longitude < -180.0 || value.longitude > 180.0) {
            errors.add("Location_ValidationError_LongitudeRange")
        }
        
        // Null Island validation (0,0 coordinates)
        if (value.latitude == 0.0 && value.longitude == 0.0) {
            errors.add("Location_ValidationError_NullIslandCoordinates")
        }
        
        if (!value.photoPath.isNullOrEmpty() && !isValidPath(value.photoPath)) {
            errors.add("Location_ValidationError_PhotoPathInvalid")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
    
    private fun isValidPath(path: String?): Boolean {
        if (path.isNullOrBlank()) return true
        
        return try {
            // Check for basic invalid path characters
            val invalidChars = charArrayOf('<', '>', ':', '"', '|', '?', '*')
            if (path.any { it in invalidChars || it.code < 32 }) {
                false
            } else {
                // Basic path validation - ensure it's not just separators
                val cleanPath = path.trim()
                cleanPath.isNotEmpty() && !cleanPath.all { it == '/' || it == '\\' }
            }
        } catch (e: Exception) {
            false
        }
    }
}