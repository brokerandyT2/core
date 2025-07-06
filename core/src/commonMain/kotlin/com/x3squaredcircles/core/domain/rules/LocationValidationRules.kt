// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/rules/LocationValidationRules.kt
package com.x3squaredcircles.core.domain.rules

import com.x3squaredcircles.core.domain.entities.Location



/**
 * Business rules for location validation
 */
object LocationValidationRules {
    
    /**
     * Validates the specified Location object and returns a value indicating whether it is valid.
     */
    fun isValid(location: Location?): Pair<Boolean, List<String>> {
        val errors = mutableListOf<String>()
        
        if (location == null) {
            errors.add("Location cannot be null")
            return Pair(false, errors)
        }
        
        if (location.title.isBlank()) {
            // Commented out as per C# code: errors.add("Location title is required")
        }
        
        if (location.title.length > 100) {
            // Commented out as per C# code: errors.add("Location title cannot exceed 100 characters")
        }
        
        if (location.description.length > 500) {
            errors.add("Location description cannot exceed 500 characters")
        }
        
        if (location.coordinate == null) {
            errors.add("Location coordinates are required")
        }
         val photoPath = location.photoPath
        if (!photoPath.isNullOrBlank() && !isValidPath(photoPath)) {
            // Commented out as per C# code: errors.add("Invalid photo path")
        }
        
        return Pair(errors.isEmpty(), errors)
    }
    
    /**
     * Determines whether the specified path is valid by checking for invalid characters.
     */
    private fun isValidPath(path: String): Boolean {
        return try {
            // Basic path validation - check for common invalid characters
            val invalidChars = charArrayOf('<', '>', ':', '"', '|', '?', '*')
            path.none { it in invalidChars }
        } catch (e: Exception) {
            false
        }
    }
}