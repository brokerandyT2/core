// core/src/commonMain/kotlin/com/x3squaredcircles/core/common/models/Error.kt
package com.x3squaredcircles.core.common.models

/**
 * Represents an error in the application
 */
data class Error(
    /**
     * Error code for identifying the type of error
     */
    val code: String,
    
    /**
     * Human-readable error message
     */
    val message: String,
    
    /**
     * Property name associated with the error (for validation errors)
     */
    val propertyName: String? = null
) {
    companion object {
        /**
         * Creates a validation error
         */
        fun validation(propertyName: String, message: String): Error {
            return Error("VALIDATION_ERROR", message, propertyName)
        }

        /**
         * Creates a not found error
         */
        fun notFound(message: String): Error {
            return Error("NOT_FOUND", message)
        }

        /**
         * Creates a database error
         */
        fun database(message: String): Error {
            return Error("DATABASE_ERROR", message)
        }

        /**
         * Creates a general domain error
         */
        fun domain(message: String): Error {
            return Error("DOMAIN_ERROR", message)
        }
    }
}