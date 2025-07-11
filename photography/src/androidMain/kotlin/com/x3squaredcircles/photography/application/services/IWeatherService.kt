// core/photography/src/androidMain/kotlin/com/x3squaredcircles/photography/services/IWeatherService.kt
package com.x3squaredcircles.photography.services

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.WeatherDataDto
import com.x3squaredcircles.core.dtos.WeatherForecastDto
import kotlinx.coroutines.Job

interface IWeatherService {
    suspend fun getWeatherAsync(
        latitude: Double, 
        longitude: Double, 
        cancellationToken: Job = Job()
    ): Result<WeatherDataDto>
    
    suspend fun updateWeatherForLocationAsync(
        locationId: Int, 
        cancellationToken: Job = Job()
    ): Result<WeatherDataDto>
    
    suspend fun getForecastAsync(
        latitude: Double, 
        longitude: Double, 
        days: Int = 7, 
        cancellationToken: Job = Job()
    ): Result<WeatherForecastDto>
    
    suspend fun updateAllWeatherAsync(
        cancellationToken: Job = Job()
    ): Result<Int>
}