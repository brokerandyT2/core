// infrastructure/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/data/RepositoryExceptionWrapper.kt
package com.x3squaredcircles.core.infrastructure.data

import com.x3squaredcircles.core.infrastructure.services.IInfrastructureExceptionMappingService

object RepositoryExceptionWrapper {
    
    suspend fun <T> executeWithExceptionMappingAsync(
        operation: suspend () -> T,
        exceptionMapper: IInfrastructureExceptionMappingService,
        operationName: String,
        entityType: String
    ): T {
        return try {
            operation()
        } catch (ex: Exception) {
            val domainException = when (entityType.lowercase()) {
                "location" -> exceptionMapper.mapToLocationDomainException(ex, operationName)
                "weather" -> exceptionMapper.mapToWeatherDomainException(ex, operationName)
                "setting" -> exceptionMapper.mapToSettingDomainException(ex, operationName)
                "tip" -> exceptionMapper.mapToTipDomainException(ex, operationName)
                "tiptype" -> exceptionMapper.mapToTipTypeDomainException(ex, operationName)
                else -> ex
            }
            throw domainException
        }
    }
    
    fun <T> executeWithExceptionMapping(
        operation: () -> T,
        exceptionMapper: IInfrastructureExceptionMappingService,
        operationName: String,
        entityType: String
    ): T {
        return try {
            operation()
        } catch (ex: Exception) {
            val domainException = when (entityType.lowercase()) {
                "location" -> exceptionMapper.mapToLocationDomainException(ex, operationName)
                "weather" -> exceptionMapper.mapToWeatherDomainException(ex, operationName)
                "setting" -> exceptionMapper.mapToSettingDomainException(ex, operationName)
                "tip" -> exceptionMapper.mapToTipDomainException(ex, operationName)
                "tiptype" -> exceptionMapper.mapToTipTypeDomainException(ex, operationName)
                else -> ex
            }
            throw domainException
        }
    }
}