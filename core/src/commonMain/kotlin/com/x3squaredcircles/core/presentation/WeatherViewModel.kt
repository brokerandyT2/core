// core\src\commonMain\kotlin\com\x3squaredcircles\core\presentation\WeatherViewModel.kt
package com.x3squaredcircles.core.presentation
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IMediator
import com.x3squaredcircles.core.commands.UpdateWeatherCommand
import com.x3squaredcircles.core.queries.GetWeatherForecastQuery
import com.x3squaredcircles.core.dtos.WeatherForecastDto
import com.x3squaredcircles.core.dtos.WeatherDataDto
import com.x3squaredcircles.core.dtos.DailyForecastDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.min


class WeatherViewModel(
private val mediator: IMediator,
errorDisplayService: IErrorDisplayService? = null
) : BaseViewModel(null, errorDisplayService), INavigationAware {
private val _locationId = MutableStateFlow(0)
val locationId: StateFlow<Int> = _locationId.asStateFlow()

private val _dailyForecasts = MutableStateFlow<List<DailyWeatherViewModel>>(emptyList())
val dailyForecasts: StateFlow<List<DailyWeatherViewModel>> = _dailyForecasts.asStateFlow()

private val _weatherForecast = MutableStateFlow<WeatherForecastDto?>(null)
val weatherForecast: StateFlow<WeatherForecastDto?> = _weatherForecast.asStateFlow()

private val iconUrlCache = mutableMapOf<String, String>()

init {
    initializeIconCache()
}

suspend fun loadWeatherAsync(locationId: Int) {
    try {
        setIsBusy(true)
        clearErrors()
        _locationId.value = locationId

        val weatherCommand = UpdateWeatherCommand(
            locationId = locationId,
            forceUpdate = true
        )

        val weatherResult: Result<WeatherDataDto> = mediator.send(weatherCommand)

        when (weatherResult) {
            is Result.Success -> {
                val weatherData = weatherResult.data

                val forecastQuery = GetWeatherForecastQuery(
                    latitude = weatherData?.latitude!!,
                    longitude = weatherData.longitude,
                    days = 5
                )

                val forecastResult: Result<WeatherForecastDto> = mediator.send(forecastQuery)

                when (forecastResult) {
                    is Result.Success -> {
                        _weatherForecast.value = forecastResult.data
                        processForecastDataOptimized(forecastResult?.data!!)
                    }
                    is Result.Failure -> {
                        onSystemError(forecastResult.errorMessage)
                    }
                }
            }
            is Result.Failure -> {
                onSystemError(weatherResult.errorMessage)
            }
        }
    } catch (e: Exception) {
        onSystemError("Error loading weather data: ${e.message}")
    } finally {
        setIsBusy(false)
    }
}

private suspend fun processForecastDataOptimized(forecast: WeatherForecastDto) {
    if (forecast.dailyForecasts.isEmpty()) {
        setValidationError("No forecast data available")
        return
    }

    val processedItems = withContext(Dispatchers.Default) {
        val today = System.currentTimeMillis()
        val items = mutableListOf<DailyWeatherViewModel>()
        val maxItems = min(5, forecast.dailyForecasts.size)

        for (i in 0 until maxItems) {
            val dailyForecast = forecast.dailyForecasts[i]
            val isToday = isSameDay(dailyForecast.date, today)
            items.add(createDailyWeatherViewModel(dailyForecast, isToday))
        }

        items
    }

    updateForecastCollectionOptimized(processedItems)
}

private fun createDailyWeatherViewModel(
    dailyForecast: DailyForecastDto,
    isToday: Boolean
): DailyWeatherViewModel {
    val minTemp = "${String.format("%.1f", dailyForecast.minTemperature)}°"
    val maxTemp = "${String.format("%.1f", dailyForecast.maxTemperature)}°"
    val windSpeed = "${String.format("%.1f", dailyForecast.windSpeed)} mph"
    val windGust = dailyForecast.windGust?.let { 
        "${String.format("%.1f", it)} mph" 
    } ?: "N/A"

    return DailyWeatherViewModel(
        date = dailyForecast.date,
        dayName = formatDayName(dailyForecast.date),
        description = dailyForecast.description,
        minTemperature = minTemp,
        maxTemperature = maxTemp,
        weatherIcon = getWeatherIconUrlCached(dailyForecast.icon),
        sunriseTime = formatTime(dailyForecast.sunrise),
        sunsetTime = formatTime(dailyForecast.sunset),
        windDirection = dailyForecast.windDirection,
        windSpeed = windSpeed,
        windGust = windGust,
        isToday = isToday
    )
}

private suspend fun updateForecastCollectionOptimized(newItems: List<DailyWeatherViewModel>) {
    withContext(Dispatchers.Main) {
        _dailyForecasts.value = newItems
    }
}

private fun getWeatherIconUrlCached(iconCode: String): String {
    if (iconCode.isEmpty()) return "weather_unknown.png"
    return iconUrlCache.getOrPut(iconCode) { "a$iconCode.png" }
}

private fun initializeIconCache() {
    val commonIcons = listOf(
        "01d", "01n", "02d", "02n", "03d", "03n", "04d", "04n",
        "09d", "09n", "10d", "10n", "11d", "11n", "13d", "13n", "50d", "50n"
    )

    commonIcons.forEach { icon ->
        iconUrlCache[icon] = "a$icon.png"
    }
}

private fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
    val day1 = timestamp1 / (24 * 60 * 60 * 1000)
    val day2 = timestamp2 / (24 * 60 * 60 * 1000)
    return day1 == day2
}

private fun formatDayName(timestamp: Long): String {
    //TODO: Implement actual time formatting logic
    return "Day" 
}

private fun formatTime(timestamp: Long): String {
    return "Time" 
    //TODO: Implement actual time formatting logic
}

override suspend fun onNavigatedToAsync() {
    // Implementation as needed
}

override suspend fun onNavigatedFromAsync() {
    // Implementation as needed
}

override fun dispose() {
    iconUrlCache.clear()
    super.dispose()
}
}
data class DailyWeatherViewModel(
val date: Long,
val dayName: String,
val description: String,
val minTemperature: String,
val maxTemperature: String,
val weatherIcon: String,
val sunriseTime: String,
val sunsetTime: String,
val windDirection: Double,
val windSpeed: String,
val windGust: String,
val isToday: Boolean
)