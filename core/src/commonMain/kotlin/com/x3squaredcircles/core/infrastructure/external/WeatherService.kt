// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/external/WeatherService.kt
package com.x3squaredcircles.core.infrastructure.external

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.dtos.WeatherDataDto
import com.x3squaredcircles.core.dtos.WeatherForecastDto
import com.x3squaredcircles.core.dtos.DailyForecastDto
import com.x3squaredcircles.core.dtos.HourlyForecastDto
import com.x3squaredcircles.core.infrastructure.services.IWeatherService
import com.x3squaredcircles.core.infrastructure.external.models.OpenWeatherResponse
import com.x3squaredcircles.core.infrastructure.external.models.WeatherApiResponse
import com.x3squaredcircles.core.infrastructure.services.IInfrastructureExceptionMappingService
import kotlinx.coroutines.Job
import kotlinx.serialization.json.Json
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

// Note: IUnitOfWork will be injected as a dependency when this service is used
// since it's defined in the photography module
class WeatherService(
    private val httpClient: HttpClient,
    private val exceptionMapper: IInfrastructureExceptionMappingService,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : IWeatherService {

    companion object {
        private const val API_KEY_SETTING = "WeatherApiKey"
        private const val BASE_URL = "https://api.openweathermap.org/data/3.0/onecall"
        private const val MAX_FORECAST_DAYS = 7
    }

    override suspend fun getWeatherAsync(latitude: Double, longitude: Double, cancellationToken: Job): Result<WeatherDataDto> {
        return try {
            val weatherResult = getWeatherFromApiAsync(latitude, longitude, cancellationToken)
            
            when (weatherResult) {
                is Result.Success -> {
                    val apiData = weatherResult.data
                    val currentForecast = apiData.dailyForecasts.firstOrNull()
                    
                    val weatherDto = WeatherDataDto(
                        id = 0,
                        locationId = 0,
                        latitude = latitude,
                        longitude = longitude,
                        timezone = apiData.timezone,
                        timezoneOffset = apiData.timezoneOffset,
                        lastUpdate = System.currentTimeMillis(),
                        temperature = currentForecast?.temperature ?: 0.0,
                        description = currentForecast?.description ?: "",
                        icon = currentForecast?.icon ?: "",
                        windSpeed = currentForecast?.windSpeed ?: 0.0,
                        windDirection = currentForecast?.windDirection ?: 0.0,
                        windGust = currentForecast?.windGust,
                        humidity = currentForecast?.humidity ?: 0,
                        pressure = currentForecast?.pressure ?: 0,
                        clouds = currentForecast?.clouds ?: 0,
                        uvIndex = currentForecast?.uvIndex ?: 0.0,
                        precipitation = currentForecast?.precipitation,
                        sunrise = currentForecast?.sunrise ?: 0L,
                        sunset = currentForecast?.sunset ?: 0L,
                        moonRise = currentForecast?.moonRise,
                        moonSet = currentForecast?.moonSet,
                        moonPhase = currentForecast?.moonPhase ?: 0.0,
                        minimumTemp = currentForecast?.minTemperature ?: 0.0,
                        maximumTemp = currentForecast?.maxTemperature ?: 0.0
                    )
                    
                    Result.success(weatherDto)
                }
                is Result.Failure -> Result.failure(weatherResult.errorMessage)
            }
        } catch (ex: Exception) {
            val domainException = exceptionMapper.mapToWeatherDomainException(ex, "GetWeather")
            throw domainException
        }
    }

    override suspend fun updateWeatherForLocationAsync(locationId: Int, cancellationToken: Job): Result<WeatherDataDto> {
        return try {
            // This method will need to be implemented when UnitOfWork is available
            // For now, return a placeholder
            Result.failure("Method requires UnitOfWork dependency from photography module")
        } catch (ex: Exception) {
            val domainException = exceptionMapper.mapToWeatherDomainException(ex, "UpdateWeatherForLocation")
            throw domainException
        }
    }

    override suspend fun getForecastAsync(latitude: Double, longitude: Double, days: Int, cancellationToken: Job): Result<WeatherForecastDto> {
        return try {
            val result = getWeatherFromApiAsync(latitude, longitude, cancellationToken)
            
            when (result) {
                is Result.Success -> {
                    val apiData = result.data
                    val forecastDto = WeatherForecastDto(
                        weatherId = 0,
                        lastUpdate = System.currentTimeMillis(),
                        timezone = apiData.timezone,
                        timezoneOffset = apiData.timezoneOffset,
                        dailyForecasts = apiData.dailyForecasts.take(days)
                    )
                    Result.success(forecastDto)
                }
                is Result.Failure -> Result.failure(result.errorMessage)
            }
        } catch (ex: Exception) {
            val domainException = exceptionMapper.mapToWeatherDomainException(ex, "GetForecast")
            throw domainException
        }
    }

    override suspend fun updateAllWeatherAsync(cancellationToken: Job): Result<Int> {
        return try {
            // This method will need to be implemented when UnitOfWork is available
            Result.failure("Method requires UnitOfWork dependency from photography module")
        } catch (ex: Exception) {
            val domainException = exceptionMapper.mapToWeatherDomainException(ex, "UpdateAllWeather")
            throw domainException
        }
    }

    private suspend fun getWeatherFromApiAsync(latitude: Double, longitude: Double, cancellationToken: Job): Result<WeatherApiResponse> {
        return try {
            // For now, use a hardcoded API key - this will be replaced when UnitOfWork is available
            val apiKey = "YOUR_API_KEY_HERE"
            if (apiKey == "YOUR_API_KEY_HERE") {
                return Result.failure("Weather API key not configured")
            }
            
            val tempScale = "metric" // Default to metric
            val requestUrl = "$BASE_URL?lat=$latitude&lon=$longitude&appid=$apiKey&units=$tempScale&exclude=minutely"
            
            val response = httpClient.get(requestUrl)
            
            if (response.status.value in 200..299) {
                val jsonString = response.bodyAsText()
                val weatherData = json.decodeFromString<OpenWeatherResponse>(jsonString)
                val apiResponse = mapToApiResponse(weatherData)
                Result.success(apiResponse)
            } else {
                Result.failure("Weather API request failed: ${response.status}")
            }
        } catch (ex: Exception) {
            val domainException = exceptionMapper.mapToWeatherDomainException(ex, "GetWeatherFromApi")
            throw domainException
        }
    }

    private fun mapToApiResponse(response: OpenWeatherResponse): WeatherApiResponse {
        val dailyForecasts = response.daily.take(7).map { daily ->
            val weather = daily.weather.firstOrNull()
            DailyForecastDto(
                date = daily.dt * 1000,
                sunrise = daily.sunrise * 1000,
                sunset = daily.sunset * 1000,
                temperature = daily.temp.day,
                minTemperature = daily.temp.min,
                maxTemperature = daily.temp.max,
                description = weather?.description ?: "",
                icon = weather?.icon ?: "",
                windSpeed = daily.windSpeed,
                windDirection = daily.windDeg,
                windGust = daily.windGust,
                humidity = daily.humidity,
                pressure = daily.pressure,
                clouds = daily.clouds,
                uvIndex = daily.uvi,
                precipitation = daily.pop,
                moonRise = if (daily.moonRise > 0) daily.moonRise * 1000 else null,
                moonSet = if (daily.moonSet > 0) daily.moonSet * 1000 else null,
                moonPhase = daily.moonPhase
            )
        }

        val hourlyForecasts = response.hourly.take(48).map { hourly ->
            val weather = hourly.weather.firstOrNull()
            HourlyForecastDto(
                dateTime = hourly.dt * 1000,
                temperature = hourly.temp,
                feelsLike = hourly.feelsLike,
                description = weather?.description ?: "",
                icon = weather?.icon ?: "",
                windSpeed = hourly.windSpeed,
                windDirection = hourly.windDeg,
                windGust = hourly.windGust,
                humidity = hourly.humidity,
                pressure = hourly.pressure,
                clouds = hourly.clouds,
                uvIndex = hourly.uvi,
                probabilityOfPrecipitation = hourly.pop,
                visibility = hourly.visibility,
                dewPoint = hourly.dewPoint
            )
        }

        return WeatherApiResponse(
            timezone = response.timezone,
            timezoneOffset = response.timezoneOffset,
            dailyForecasts = dailyForecasts,
            hourlyForecasts = hourlyForecasts
        )
    }
}