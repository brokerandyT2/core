// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/exceptions/WeatherDomainException.kt
package com.x3squaredcircles.core.domain.exceptions

/**
 * Exception thrown when weather domain business rules are violated
 */
class WeatherDomainException(
    message: String,
    val code: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    
    /**
     * Gets the error code that identifies the specific business rule violation
     */
    fun getCode(): String = code
}