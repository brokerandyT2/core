// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/exceptions/SettingDomainException.kt
package com.x3squaredcircles.core.domain.exceptions

/**
 * Exception thrown when setting domain business rules are violated
 */
class SettingDomainException(
    message: String,
    val code: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    
    /**
     * Gets the error code that identifies the specific business rule violation
     */
    fun getCode(): String = code
}