// core/photography/src/androidMain/kotlin/com/x3squaredcircles/photography/services/IPredictiveLightService.kt
package com.x3squaredcircles.photography.services

import com.x3squaredcircles.core.dtos.WeatherForecastDto

import com.x3squaredcircles.photography.domain.models.WeatherConditions
import com.x3squaredcircles.photography.domain.models.EnhancedSunTimes
import com.x3squaredcircles.photography.domain.models.MoonPhaseData
import com.x3squaredcircles.photography.models.WeatherImpactAnalysis

import com.x3squaredcircles.photography.models.HourlyLightPrediction
import com.x3squaredcircles.photography.models.PredictiveLightRecommendation
import kotlinx.coroutines.Job

interface IPredictiveLightService {
    suspend fun analyzeWeatherImpactAsync(
        request: WeatherImpactAnalysisRequest,
        cancellationToken: Job = Job()
    ): WeatherImpactAnalysis
    
    suspend fun generateHourlyPredictionsAsync(
        request: PredictiveLightRequest,
        cancellationToken: Job = Job()
    ): List<HourlyLightPrediction>
    
    suspend fun generateRecommendationAsync(
        request: PredictiveLightRequest,
        cancellationToken: Job = Job()
    ): PredictiveLightRecommendation
    
    suspend fun calibrateWithActualReadingAsync(
        request: LightMeterCalibrationRequest,
        cancellationToken: Job = Job()
    )
}

data class WeatherImpactAnalysisRequest(
    val weatherForecast: WeatherForecastDto = WeatherForecastDto(),
    val sunTimes: EnhancedSunTimes = EnhancedSunTimes(),
    val moonData: MoonPhaseData = MoonPhaseData()
)

data class PredictiveLightRequest(
    val locationId: Int,
    val latitude: Double,
    val longitude: Double,
    val targetDate: kotlinx.datetime.LocalDateTime,
    val weatherImpact: WeatherImpactAnalysis = WeatherImpactAnalysis(),
    val sunTimes: EnhancedSunTimes = EnhancedSunTimes(),
    val moonPhase: MoonPhaseData = MoonPhaseData(),
    val lastCalibrationReading: kotlinx.datetime.LocalDateTime? = null,
    val predictionWindowHours: Int = 24
)

data class LightMeterCalibrationRequest(
    val locationId: Int,
    val latitude: Double,
    val longitude: Double,
    val dateTime: kotlinx.datetime.LocalDateTime,
    val actualEV: Double,
    val weatherConditions: WeatherConditions? = null
)