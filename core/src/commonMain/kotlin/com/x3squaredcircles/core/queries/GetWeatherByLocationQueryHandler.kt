// core/src/commonMain/kotlin/com/x3squaredcircles/core/queries/GetWeatherByLocationQueryHandler.kt
package com.x3squaredcircles.core.queries

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.domain.entities.Weather
import com.x3squaredcircles.core.dtos.WeatherDataDto
import com.x3squaredcircles.core.infrastructure.services.IWeatherService
import com.x3squaredcircles.core.mediator.IMediator
import com.x3squaredcircles.core.mediator.IQueryHandler

class GetWeatherByLocationQueryHandler(
        private val unitOfWork: IUnitOfWork,
        private val weatherService: IWeatherService,
        private val mediator: IMediator
) : IQueryHandler<GetWeatherByLocationQuery, Result<WeatherDataDto>> {
    override suspend fun handle(request: GetWeatherByLocationQuery): Result<WeatherDataDto> {
        return try {
            // Get location first - this is required
            val locationResult = unitOfWork.locations.getByIdAsync(request.locationId)
            if (!locationResult.isSuccess || locationResult.data == null) {
                return Result.failure<WeatherDataDto>("Weather_Error_LocationNotFound")
            }

            val location = locationResult.data

            // Get weather data - this might be null for new locations
            val weather = unitOfWork.weather.getByLocationIdAsync(request.locationId)

            // Check if weather data is fresh
            if (weather != null && isWeatherDataFresh(weather)) {
                // Return cached data immediately
                val weatherDto = mapWeatherToDto(weather)
                return Result.success(weatherDto)
            }

            // Data is stale or doesn't exist - fetch fresh data
            val freshWeatherResult =
                    weatherService.updateWeatherForLocationAsync(request.locationId)

            if (!freshWeatherResult.isSuccess) {
                return Result.failure<WeatherDataDto>(
                        "Weather_Error_FetchDataFailed: ${
                if (freshWeatherResult is Result.Failure<*>) freshWeatherResult.errorMessage else "Unknown error"
            }"
                )
            }

            Result.success(freshWeatherResult.data!!)
        } catch (ex: Exception) {
            Result.failure<WeatherDataDto>(
                    "Weather_Error_RetrieveDataFailedWithException: ${ex.message}"
            )
        }
    }

    private fun isWeatherDataFresh(weather: Weather): Boolean {
        if (weather == null) return false

        // Check data age - weather data older than 1 hour is considered stale
        val maxAge = 3600000L // 1 hour in milliseconds
        val isDataFresh = System.currentTimeMillis() - weather.lastUpdate <= maxAge

        return isDataFresh
    }

    private fun mapWeatherToDto(weather: Weather): WeatherDataDto {
        return WeatherDataDto(
                id = weather.id,
                locationId = weather.locationId,
                latitude = weather.coordinate?.latitude ?: 0.0,
                longitude = weather.coordinate?.longitude ?: 0.0,
                timezone = weather.timezone,
                timezoneOffset = weather.timezoneOffset,
                lastUpdate = weather.lastUpdate,
                temperature = weather.getCurrentForecast()?.temperature ?: 0.0,
                description = weather.getCurrentForecast()?.description ?: "",
                icon = weather.getCurrentForecast()?.icon ?: "",
                windSpeed = weather.getCurrentForecast()?.wind?.speed ?: 0.0,
                windDirection = weather.getCurrentForecast()?.wind?.direction ?: 0.0,
                windGust = weather.getCurrentForecast()?.wind?.gust,
                humidity = weather.getCurrentForecast()?.humidity ?: 0,
                pressure = weather.getCurrentForecast()?.pressure ?: 0,
                clouds = weather.getCurrentForecast()?.clouds ?: 0,
                uvIndex = weather.getCurrentForecast()?.uvIndex ?: 0.0,
                precipitation = weather.getCurrentForecast()?.precipitation,
                sunrise = weather.getCurrentForecast()?.sunrise ?: 0L,
                sunset = weather.getCurrentForecast()?.sunset ?: 0L,
                moonRise = weather.getCurrentForecast()?.moonRise,
                moonSet = weather.getCurrentForecast()?.moonSet,
                moonPhase = weather.getCurrentForecast()?.moonPhase ?: 0.0,
                minimumTemp = weather.getCurrentForecast()?.minTemperature ?: 0.0,
                maximumTemp = weather.getCurrentForecast()?.maxTemperature ?: 0.0
        )
    }
}
