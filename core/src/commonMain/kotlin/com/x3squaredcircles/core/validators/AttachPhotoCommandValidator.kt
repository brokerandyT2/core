// core/src/commonMain/kotlin/com/x3squaredcircles/core/validators/AttachPhotoCommandValidator.kt
package com.x3squaredcircles.core.validators

import com.x3squaredcircles.core.commands.AttachPhotoCommand

class AttachPhotoCommandValidator : IValidator<AttachPhotoCommand> {
    override fun validate(value: AttachPhotoCommand): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (value.locationId <= 0) {
            errors.add("Location_ValidationError_LocationIdRequired")
        }
        
        if (value.photoPath.isBlank()) {
            errors.add("Location_ValidationError_PhotoPathRequired")
        } else if (!isValidPath(value.photoPath)) {
            errors.add("Location_ValidationError_PhotoPathInvalid")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
    
    private fun isValidPath(path: String): Boolean {
        if (path.isBlank()) return false
        
        // Check for basic invalid path characters
        val invalidChars = charArrayOf('<', '>', ':', '"', '|', '?', '*')
        if (path.any { it in invalidChars || it.code < 32 }) {
            return false
        }
        
        return true
    }
}