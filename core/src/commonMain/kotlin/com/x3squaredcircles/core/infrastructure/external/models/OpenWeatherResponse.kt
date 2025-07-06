// infrastructure/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/external/models/OpenWeatherResponse.kt
package com.x3squaredcircles.core.infrastructure.external.models



@Serializable
data class OpenWeatherResponse(
    @SerialName("lat")
    val lat: Double,
    @SerialName("lon")
    val lon: Double,
    @SerialName("timezone")
    val timezone: String,
    @SerialName("timezone_offset")
    val timezoneOffset: Int,
    @SerialName("current")
    val current: CurrentWeather,
    @SerialName("hourly")
    val hourly: List<HourlyWeather>,
    @SerialName("daily")
    val daily: List<DailyForecast>
)

@Serializable
data class CurrentWeather(
    @SerialName("dt")
    val dt: Long,
    @SerialName("sunrise")
    val sunrise: Long,
    @SerialName("sunset")
    val sunset: Long,
    @SerialName("temp")
    val temp: Double,
    @SerialName("feels_like")
    val feelsLike: Double,
    @SerialName("pressure")
    val pressure: Int,
    @SerialName("humidity")
    val humidity: Int,
    @SerialName("dew_point")
    val dewPoint: Double,
    @SerialName("uvi")
    val uvi: Double,
    @SerialName("clouds")
    val clouds: Int,
    @SerialName("visibility")
    val visibility: Int,
    @SerialName("wind_speed")
    val windSpeed: Double,
    @SerialName("wind_deg")
    val windDeg: Double,
    @SerialName("wind_gust")
    val windGust: Double? = null,
    @SerialName("weather")
    val weather: List<WeatherDescription>
)

@Serializable
data class HourlyWeather(
    @SerialName("dt")
    val dt: Long,
    @SerialName("temp")
    val temp: Double,
    @SerialName("feels_like")
    val feelsLike: Double,
    @SerialName("pressure")
    val pressure: Int,
    @SerialName("humidity")
    val humidity: Int,
    @SerialName("dew_point")
    val dewPoint: Double,
    @SerialName("uvi")
    val uvi: Double,
    @SerialName("clouds")
    val clouds: Int,
    @SerialName("visibility")
    val visibility: Int,
    @SerialName("wind_speed")
    val windSpeed: Double,
    @SerialName("wind_deg")
    val windDeg: Double,
    @SerialName("wind_gust")
    val windGust: Double? = null,
    @SerialName("weather")
    val weather: List<WeatherDescription>,
    @SerialName("pop")
    val pop: Double
)

@Serializable
data class DailyForecast(
    @SerialName("dt")
    val dt: Long,
    @SerialName("sunrise")
    val sunrise: Long,
    @SerialName("sunset")
    val sunset: Long,
    @SerialName("moonrise")
    val moonRise: Long,
    @SerialName("moonset")
    val moonSet: Long,
    @SerialName("moon_phase")
    val moonPhase: Double,
    @SerialName("temp")
    val temp: DailyTemperature,
    @SerialName("feels_like")
    val feelsLike: DailyFeelsLike,
    @SerialName("pressure")
    val pressure: Int,
    @SerialName("humidity")
    val humidity: Int,
    @SerialName("dew_point")
    val dewPoint: Double,
    @SerialName("wind_speed")
    val windSpeed: Double,
    @SerialName("wind_deg")
    val windDeg: Double,
    @SerialName("wind_gust")
    val windGust: Double? = null,
    @SerialName("weather")
    val weather: List<WeatherDescription>,
    @SerialName("clouds")
    val clouds: Int,
    @SerialName("pop")
    val pop: Double,
    @SerialName("rain")
    val rain: Double? = null,
    @SerialName("uvi")
    val uvi: Double
)

@Serializable
data class DailyTemperature(
    @SerialName("day")
    val day: Double,
    @SerialName("min")
    val min: Double,
    @SerialName("max")
    val max: Double,
    @SerialName("night")
    val night: Double,
    @SerialName("eve")
    val eve: Double,
    @SerialName("morn")
    val morn: Double
)

@Serializable
data class DailyFeelsLike(
    @SerialName("day")
    val day: Double,
    @SerialName("night")
    val night: Double,
    @SerialName("eve")
    val eve: Double,
    @SerialName("morn")
    val morn: Double
)

@Serializable
data class WeatherDescription(
    @SerialName("id")
    val id: Int,
    @SerialName("main")
    val main: String,
    @SerialName("description")
    val description: String,
    @SerialName("icon")
    val icon: String
)