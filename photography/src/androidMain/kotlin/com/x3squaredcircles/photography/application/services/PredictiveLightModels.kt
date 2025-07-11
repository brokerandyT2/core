// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/application/services/PredictiveLightModels.kt
package com.x3squaredcircles.photography.application.services

import com.x3squaredcircles.core.dtos.WeatherForecastDto
import com.x3squaredcircles.photography.domain.models.EnhancedSunTimes
import com.x3squaredcircles.photography.domain.models.MoonPhaseData
import kotlinx.coroutines.Job
import kotlinx.datetime.LocalDateTime

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
    val weatherForecast: WeatherForecastDto,
    val sunTimes: EnhancedSunTimes = EnhancedSunTimes(),
    val moonData: MoonPhaseData = MoonPhaseData()
)

data class PredictiveLightRequest(
    val locationId: Int,
    val latitude: Double,
    val longitude: Double,
    val targetDate: LocalDateTime,
    val weatherImpact: WeatherImpactAnalysis = WeatherImpactAnalysis(),
    val sunTimes: EnhancedSunTimes = EnhancedSunTimes(),
    val moonPhase: MoonPhaseData = MoonPhaseData(),
    val lastCalibrationReading: LocalDateTime? = null,
    val predictionWindowHours: Int = 24
)

data class LightMeterCalibrationRequest(
    val locationId: Int,
    val latitude: Double,
    val longitude: Double,
    val dateTime: LocalDateTime,
    val actualEV: Double,
    val weatherConditions: WeatherConditions? = null
)

data class ShootingAlertRequest(
    val locationId: Int,
    val alertTime: LocalDateTime,
    val shootingWindowStart: LocalDateTime,
    val shootingWindowEnd: LocalDateTime,
    val lightQuality: LightQuality,
    val recommendedSettings: String? = null,
    val message: String = ""
)

data class WeatherImpactAnalysis(
    val currentConditions: WeatherConditions? = null,
    val hourlyImpacts: List<HourlyWeatherImpact> = emptyList(),
    val overallLightReductionFactor: Double = 1.0,
    val summary: String = "",
    val alerts: List<WeatherAlert> = emptyList()
)

data class HourlyWeatherImpact(
    val hour: LocalDateTime,
    val lightReductionFactor: Double = 1.0,
    val colorTemperatureShift: Double = 0.0,
    val contrastReduction: Double = 0.0,
    val predictedQuality: LightQuality = LightQuality.Unknown,
    val reasoning: String = ""
)

data class WeatherConditions(
    val cloudCover: Double,
    val precipitation: Double,
    val humidity: Double,
    val visibility: Double,
    val airQualityIndex: Int,
    val windSpeed: Double,
    val description: String = "",
    val uvIndex: Double,
    val precipitationProbability: Double
)

data class WeatherAlert(
    val type: AlertType,
    val message: String = "",
    val validFrom: LocalDateTime,
    val validTo: LocalDateTime,
    val severity: AlertSeverity
)

data class PredictiveLightRecommendation(
    val generatedAt: LocalDateTime,
    val bestTimeWindow: OptimalShootingWindow,
    val alternativeWindows: List<OptimalShootingWindow> = emptyList(),
    val overallRecommendation: String = "",
    val keyInsights: List<String> = emptyList(),
    val calibrationAccuracy: Double,
    val requiresRecalibration: Boolean
)

data class OptimalShootingWindow(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val lightQuality: LightQuality,
    val optimalityScore: Double,
    val description: String = "",
    val recommendedFor: List<String> = emptyList(),
    val recommendedExposure: HourlyLightPrediction? = null,
    val warnings: List<String> = emptyList()
)

data class HourlyLightPrediction(
    val dateTime: LocalDateTime,
    val predictedEV: Double,
    val evConfidenceMargin: Double,
    val confidenceLevel: Double,
    val suggestedSettings: ExposureTriangle = ExposureTriangle(),
    val lightQuality: LightCharacteristics = LightCharacteristics(),
    val recommendations: List<String> = emptyList(),
    val isOptimalForPhotography: Boolean = false,
    val sunPosition: SunPositionDto = SunPositionDto(),
    val weatherConditions: WeatherConditions? = null,
    val theoreticalLux: Double
)

data class ExposureTriangle(
    val aperture: String = "",
    val shutterSpeed: String = "",
    val iso: String = "",
    val formattedSettings: String = "$aperture, $shutterSpeed, $iso"
)

data class LightCharacteristics(
    val colorTemperature: Double = 0.0,
    val softnessFactor: Double = 0.0,
    val optimalFor: String = ""
)

data class SunPositionDto(
    val azimuth: Double = 0.0,
    val elevation: Double = 0.0,
    val distance: Double = 1.0,
    val isAboveHorizon: Boolean = elevation > 0
)

enum class LightQuality {
    Unknown,
    Harsh,
    Soft,
    GoldenHour,
    BlueHour,
    Overcast,
    Dramatic,
    Night,
    Flat,
    Direct
}

enum class AlertType {
    Weather,
    Light,
    Shooting,
    Calibration
}

enum class AlertSeverity {
    Info,
    Warning,
    Critical
}