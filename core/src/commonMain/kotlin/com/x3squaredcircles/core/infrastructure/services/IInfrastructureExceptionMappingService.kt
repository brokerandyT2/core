// infrastructure/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/services/IInfrastructureExceptionMappingService.kt
package com.x3squaredcircles.core.infrastructure.services

import com.x3squaredcircles.core.domain.exceptions.LocationDomainException
import com.x3squaredcircles.core.domain.exceptions.WeatherDomainException
import com.x3squaredcircles.core.domain.exceptions.SettingDomainException
import com.x3squaredcircles.core.domain.exceptions.TipDomainException
import com.x3squaredcircles.core.domain.exceptions.TipTypeDomainException

interface IInfrastructureExceptionMappingService {
    fun mapToLocationDomainException(exception: Exception, operation: String): LocationDomainException
    fun mapToWeatherDomainException(exception: Exception, operation: String): WeatherDomainException
    fun mapToSettingDomainException(exception: Exception, operation: String): SettingDomainException
    fun mapToTipDomainException(exception: Exception, operation: String): TipDomainException
    fun mapToTipTypeDomainException(exception: Exception, operation: String): TipTypeDomainException
}