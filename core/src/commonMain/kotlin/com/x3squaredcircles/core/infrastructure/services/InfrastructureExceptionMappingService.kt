// infrastructure/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/services/InfrastructureExceptionMappingService.kt
package com.x3squaredcircles.core.infrastructure.services

import com.x3squaredcircles.core.domain.exceptions.LocationDomainException
import com.x3squaredcircles.core.domain.exceptions.WeatherDomainException
import com.x3squaredcircles.core.domain.exceptions.SettingDomainException
import com.x3squaredcircles.core.domain.exceptions.TipDomainException
import com.x3squaredcircles.core.domain.exceptions.TipTypeDomainException

class InfrastructureExceptionMappingService : IInfrastructureExceptionMappingService {

    override fun mapToLocationDomainException(exception: Exception, operation: String): LocationDomainException {
        return when (exception) {
            is IllegalArgumentException -> when {
                exception.message?.contains("UNIQUE constraint failed") == true ->
                    LocationDomainException("A location with this title already exists", "DUPLICATE_TITLE", exception)
                exception.message?.contains("CHECK constraint failed") == true ->
                    LocationDomainException("Invalid coordinate values provided", "INVALID_COORDINATES", exception)
                else ->
                    LocationDomainException("Database operation failed: ${exception.message}", "DATABASE_ERROR", exception)
            }
            is SecurityException ->
                LocationDomainException("Access denied", "AUTHORIZATION_ERROR", exception)
            else -> LocationDomainException("Infrastructure error in $operation: ${exception.message}", "INFRASTRUCTURE_ERROR", exception)
        }
    }

    override fun mapToWeatherDomainException(exception: Exception, operation: String): WeatherDomainException {
        return when (exception) {
            is IllegalArgumentException -> when {
                exception.message?.contains("401") == true ->
                    WeatherDomainException("Weather API authentication failed", "INVALID_API_KEY", exception)
                exception.message?.contains("429") == true ->
                    WeatherDomainException("Weather API rate limit exceeded", "RATE_LIMIT_EXCEEDED", exception)
                exception.message?.contains("404") == true ->
                    WeatherDomainException("Weather data not available for location", "LOCATION_NOT_FOUND", exception)
                else ->
                    WeatherDomainException("Weather API error: ${exception.message}", "API_UNAVAILABLE", exception)
            }
            else -> WeatherDomainException("Infrastructure error in $operation: ${exception.message}", "INFRASTRUCTURE_ERROR", exception)
        }
    }

    override fun mapToSettingDomainException(exception: Exception, operation: String): SettingDomainException {
        return when (exception) {
            is IllegalArgumentException -> when {
                exception.message?.contains("UNIQUE constraint failed") == true ->
                    SettingDomainException("A setting with this key already exists", "DUPLICATE_KEY", exception)
                else ->
                    SettingDomainException("Database operation failed: ${exception.message}", "DATABASE_ERROR", exception)
            }
            is SecurityException ->
                SettingDomainException("Setting is read-only", "READ_ONLY_SETTING", exception)
            else -> SettingDomainException("Infrastructure error in $operation: ${exception.message}", "INFRASTRUCTURE_ERROR", exception)
        }
    }

    override fun mapToTipDomainException(exception: Exception, operation: String): TipDomainException {
        return when (exception) {
            is IllegalArgumentException -> when {
                exception.message?.contains("UNIQUE constraint failed") == true ->
                    TipDomainException("A tip with this title already exists", "DUPLICATE_TITLE", exception)
                exception.message?.contains("FOREIGN KEY constraint failed") == true ->
                    TipDomainException("Invalid tip type specified", "INVALID_TIP_TYPE", exception)
                else ->
                    TipDomainException("Database operation failed: ${exception.message}", "DATABASE_ERROR", exception)
            }
            else -> TipDomainException("Infrastructure error in $operation: ${exception.message}", "INFRASTRUCTURE_ERROR", exception)
        }
    }

    override fun mapToTipTypeDomainException(exception: Exception, operation: String): TipTypeDomainException {
        return when (exception) {
            is IllegalArgumentException -> when {
                exception.message?.contains("UNIQUE constraint failed") == true ->
                    TipTypeDomainException("A tip type with this name already exists", "DUPLICATE_NAME", exception)
                exception.message?.contains("FOREIGN KEY constraint failed") == true ->
                    TipTypeDomainException("Cannot delete tip type that is in use", "TIP_TYPE_IN_USE", exception)
                else ->
                    TipTypeDomainException("Database operation failed: ${exception.message}", "DATABASE_ERROR", exception)
            }
            else -> TipTypeDomainException("Infrastructure error in $operation: ${exception.message}", "INFRASTRUCTURE_ERROR", exception)
        }
    }
}