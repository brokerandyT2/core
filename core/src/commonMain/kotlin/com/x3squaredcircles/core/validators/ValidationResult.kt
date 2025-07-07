// core/src/commonMain/kotlin/com/x3squaredcircles/core/validators/ValidationResult.kt
package com.x3squaredcircles.core.validators
sealed class ValidationResult {
    object Success : ValidationResult()
    data class Failure(val errors: List<String>) : ValidationResult()
}
