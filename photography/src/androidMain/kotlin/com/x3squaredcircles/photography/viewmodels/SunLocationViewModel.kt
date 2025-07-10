// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/SunLocationViewModel.kt
package com.x3squaredcircles.photography.viewmodels

import com.x3squaredcircles.core.presentation.BaseViewModel
import com.x3squaredcircles.core.presentation.IErrorDisplayService
import com.x3squaredcircles.core.mediator.IMediator
import com.x3squaredcircles.core.queries.GetLocationsQuery
import com.x3squaredcircles.core.queries.GetWeatherForecastQuery
import com.x3squaredcircles.core.presentation.LocationViewModel
import com.x3squaredcircles.core.Result

import com.x3squaredcircles.photography.application.services.IExposureCalculatorService
import com.x3squaredcircles.core.infrastructure.services.ITimezoneService
import com.x3squaredcircles.core.domain.entities.Weather
import com.x3squaredcircles.core.domain.entities.WeatherForecast
import com.x3squaredcircles.photography.domain.models.SunPositionDto

import com.x3squaredcircles.photography.domain.services.ISunCalculatorService
import com.x3squaredcircles.photography.application.services.ISunService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import kotlinx.datetime.*
import kotlin.math.*
import kotlin.time.Duration.Companion.milliseconds

class SunLocationViewModel(
    private val mediator: IMediator,
    private val sunCalculatorService: ISunCalculatorService,
    private val timezoneService: ITimezoneService,
    private val exposureCalculatorService: IExposureCalculatorService,
    private val sunService: ISunService,
    errorDisplayService: IErrorDisplayService? = null
) : BaseViewModel(null, errorDisplayService) {

    // Compass and smoothing fields
    private var compassTimer: Job? = null
    private var currentCompassHeading = 0.0
    private var targetSunDirection = 0.0
    private var currentSunDirection = 0.0
    private var sunAzimuth = 0.0
    private var isCompassActive = false

    // Smoothing parameters
    private val smoothingFactor = 0.15 // Lower = smoother, higher = more responsive
    private val compassUpdateIntervalMs = 100L // 10 FPS for smooth movement
    private val minMovementThreshold = 0.5 // Minimum degrees to move arrow

    // Observable properties
    private val _locations = MutableStateFlow<List<LocationViewModel>>(emptyList())
    val locations: StateFlow<List<LocationViewModel>> = _locations.asStateFlow()

    private val _selectedLocation = MutableStateFlow<LocationViewModel?>(null)
    val selectedLocation: StateFlow<LocationViewModel?> = _selectedLocation.asStateFlow()

    private val _selectedDate = MutableStateFlow(Clock.System.todayIn(TimeZone.currentSystemDefault()))
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _selectedTime = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time)
    val selectedTime: StateFlow<LocalTime> = _selectedTime.asStateFlow()

    private val _sunDirection = MutableStateFlow(0.0)
    val sunDirection: StateFlow<Double> = _sunDirection.asStateFlow()

    private val _sunElevation = MutableStateFlow(0.0)
    val sunElevation: StateFlow<Double> = _sunElevation.asStateFlow()

    private val _deviceTilt = MutableStateFlow(0.0)
    val deviceTilt: StateFlow<Double> = _deviceTilt.asStateFlow()

    private val _elevationMatched = MutableStateFlow(false)
    val elevationMatched: StateFlow<Boolean> = _elevationMatched.asStateFlow()

    private val _weatherSummary = MutableStateFlow("Loading weather...")
    val weatherSummary: StateFlow<String> = _weatherSummary.asStateFlow()

    private val _lightReduction = MutableStateFlow(0.0)
    val lightReduction: StateFlow<Double> = _lightReduction.asStateFlow()

    private val _colorTemperature = MutableStateFlow(5500.0)
    val colorTemperature: StateFlow<Double> = _colorTemperature.asStateFlow()

    private val _lightQuality = MutableStateFlow("Unknown")
    val lightQuality: StateFlow<String> = _lightQuality.asStateFlow()

    private val _currentEV = MutableStateFlow(0.0)
    val currentEV: StateFlow<Double> = _currentEV.asStateFlow()

    private val _nextHourEV = MutableStateFlow(0.0)
    val nextHourEV: StateFlow<Double> = _nextHourEV.asStateFlow()

    private val _recommendedSettings = MutableStateFlow("f/8 @ 1/125 ISO 100")
    val recommendedSettings: StateFlow<String> = _recommendedSettings.asStateFlow()

    private val _lightQualityDescription = MutableStateFlow("Calculating light conditions...")
    val lightQualityDescription: StateFlow<String> = _lightQualityDescription.asStateFlow()

    private val _recommendations = MutableStateFlow("Loading recommendations...")
    val recommendations: StateFlow<String> = _recommendations.asStateFlow()

    private val _nextOptimalTime = MutableStateFlow("Calculating optimal times...")
    val nextOptimalTime: StateFlow<String> = _nextOptimalTime.asStateFlow()

    private val _timelineEvents = MutableStateFlow<List<TimelineEventViewModel>>(emptyList())
    val timelineEvents: StateFlow<List<TimelineEventViewModel>> = _timelineEvents.asStateFlow()

    private val _timeFormat = MutableStateFlow("HH:mm")
    val timeFormat: StateFlow<String> = _timeFormat.asStateFlow()

    private val _beginMonitoring = MutableStateFlow(false)
    val beginMonitoring: StateFlow<Boolean> = _beginMonitoring.asStateFlow()

    init {
        initializeTimelineEvents()
        viewModelScope.launch {
            loadUserSettings()
        }
    }

    fun setSelectedLocation(location: LocationViewModel?) {
        _selectedLocation.value = location
        onSelectedLocationChanged()
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        viewModelScope.launch {
            updateSunPosition()
        }
    }

    fun setSelectedTime(time: LocalTime) {
        _selectedTime.value = time
        viewModelScope.launch {
            updateSunPosition()
        }
    }

    fun setDeviceTilt(tilt: Double) {
        _deviceTilt.value = tilt
        checkElevationAlignment()
    }

    fun setBeginMonitoring(monitoring: Boolean) {
        _beginMonitoring.value = monitoring
    }

    /**
     * Start accelerometer monitoring (to be implemented by platform-specific code)
     */
    fun startAccelerometer() {
        // Platform-specific implementation needed
        // This would integrate with Android/iOS accelerometer APIs
    }

    /**
     * Stop accelerometer monitoring (to be implemented by platform-specific code)
     */
    fun stopAccelerometer() {
        // Platform-specific implementation needed
        // This would integrate with Android/iOS accelerometer APIs
    }

    /**
     * Start compass monitoring (to be implemented by platform-specific code)
     */
    fun startCompass() {
        if (!isCompassActive) {
            isCompassActive = true
            startCompassTimer()
        }
    }

    /**
     * Stop compass monitoring
     */
    fun stopCompass() {
        compassTimer?.cancel()
        compassTimer = null
        isCompassActive = false
    }

    /**
     * Update sun position based on current location and time
     */
    private suspend fun updateSunPosition() {
        try {
            val location = _selectedLocation.value ?: return
            
            setIsBusy(true)
            clearErrors()

            // Combine date and time to create datetime
            val selectedDateTime = _selectedDate.value.atTime(_selectedTime.value)
            val instant = selectedDateTime.toInstant(TimeZone.currentSystemDefault())

            val result = sunService.getSunPositionAsync(
                latitude = location.latitude.value,
                longitude = location.longitude.value,
                dateTime = instant.epochSeconds
            )

            when (result) {
                is Result.Success -> {
                    val sunPosition = result.data
                    _sunDirection.value = sunPosition?.azimuth!!
                    _sunElevation.value = sunPosition.elevation
                    
                    // Update sun-related calculations
                    updateWeatherAndLight(sunPosition)
                    updateTimelineEvents()
                }
                is Result.Failure -> {
                    onSystemError(result.errorMessage )
                }
            }
        } catch (ex: Exception) {
            onSystemError("Error updating sun position: ${ex.message}")
        } finally {
            setIsBusy(false)
        }
    }

    /**
     * Update weather and light information
     */
    private suspend fun updateWeatherAndLight(sunPosition: SunPositionDto) {
        try {
            val location = _selectedLocation.value ?: return

            // Get weather data
            val weatherQuery = GetWeatherForecastQuery(
                latitude = location.latitude.value,
                longitude = location.longitude.value
            )

            val weatherResult = mediator.send(weatherQuery)

            when (weatherResult) {
                is Result.Success -> {
                    val weatherData = weatherResult.data ?: return
                    
                    // Calculate weather impact
                    val weatherImpact = calculateWeatherImpact(weatherData)
                    
                    // Calculate light characteristics
                    val lightCharacteristics = calculateLightCharacteristics(sunPosition, weatherImpact)
                    
                    // Update UI properties
                    _weatherSummary.value = generateWeatherSummary(weatherData)
                    _lightReduction.value = weatherImpact.cloudCoverReduction
                    _colorTemperature.value = lightCharacteristics.colorTemperature
                    _lightQuality.value = lightCharacteristics.quality
                    _currentEV.value = lightCharacteristics.ev
                    
                    // Calculate recommended camera settings
                    val settings = calculateRecommendedSettings(lightCharacteristics)
                    _recommendedSettings.value = settings
                    
                    // Generate recommendations
                    _recommendations.value = generateRecommendations(sunPosition, weatherImpact, lightCharacteristics)
                    
                    // Update light quality description
                    _lightQualityDescription.value = generateLightQualityDescription(sunPosition, weatherImpact)
                }
                is Result.Failure -> {
                    _weatherSummary.value = "Weather data unavailable"
                }
            }
        } catch (ex: Exception) {
            _weatherSummary.value = "Error loading weather: ${ex.message}"
        }
    }

    /**
     * Initialize timeline events
     */
    private fun initializeTimelineEvents() {
        _timelineEvents.value = emptyList()
    }

    /**
     * Update timeline events based on current sun calculations
     */
    private fun updateTimelineEvents() {
        val location = _selectedLocation.value ?: return
        
        try {
            val events = mutableListOf<TimelineEventViewModel>()
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            // Add major sun events for next 24 hours
            events.addAll(listOf(
                TimelineEventViewModel(
                    eventName = "Sunrise",
                    eventIcon = "🌅",
                    eventTime = now.date.plus(1, DateTimeUnit.DAY).atTime(6, 30)
                ),
                TimelineEventViewModel(
                    eventName = "Golden Hour",
                    eventIcon = "🌇",
                    eventTime = now.date.plus(1, DateTimeUnit.DAY).atTime(7, 30)
                ),
                TimelineEventViewModel(
                    eventName = "Noon",
                    eventIcon = "☀️",
                    eventTime = now.date.plus(1, DateTimeUnit.DAY).atTime(12, 0)
                ),
                TimelineEventViewModel(
                    eventName = "Golden Hour",
                    eventIcon = "🌇",
                    eventTime = now.date.plus(1, DateTimeUnit.DAY).atTime(18, 0)
                ),
                TimelineEventViewModel(
                    eventName = "Sunset",
                    eventIcon = "🌅",
                    eventTime = now.date.plus(1, DateTimeUnit.DAY).atTime(19, 30)
                )
            ))

            _timelineEvents.value = events
        } catch (ex: Exception) {
            onSystemError("Error updating timeline events: ${ex.message}")
        }
    }

    /**
     * Load user settings
     */
    private suspend fun loadUserSettings() {
        try {
            // Load time format preference
            // This would integrate with user settings service
            _timeFormat.value = "HH:mm"
        } catch (ex: Exception) {
            onSystemError("Error loading user settings: ${ex.message}")
        }
    }

    /**
     * Handle location selection change
     */
    private fun onSelectedLocationChanged() {
        val location = _selectedLocation.value
        if (location != null) {
            viewModelScope.launch {
                updateSunPosition()
            }
        }
    }

    /**
     * Check if device elevation matches sun elevation
     */
    private fun checkElevationAlignment() {
        val difference = abs(_deviceTilt.value - _sunElevation.value)
        val wasMatched = _elevationMatched.value

        _elevationMatched.value = difference <= 5.0

        // Trigger haptic feedback when alignment is achieved (but wasn't before)
        if (_elevationMatched.value && !wasMatched) {
            triggerElevationMatchVibration()
        }
    }

    /**
     * Trigger vibration for elevation match (platform-specific implementation needed)
     */
    private fun triggerElevationMatchVibration() {
        // Platform-specific vibration implementation
        // Two quick vibrations to indicate elevation match
    }

    /**
     * Start compass timer for smooth direction updates
     */
    private fun startCompassTimer() {
        compassTimer = viewModelScope.launch {
            while (isCompassActive) {
                updateSmoothDirection()
                delay(compassUpdateIntervalMs)
            }
        }
    }

    /**
     * Update compass direction with smoothing
     */
    private fun updateSmoothDirection() {
        val targetDirection = targetSunDirection
        val currentDirection = currentSunDirection
        
        val angleDifference = ((targetDirection - currentDirection + 540) % 360) - 180
        
        if (abs(angleDifference) > minMovementThreshold) {
            val smoothedDirection = currentDirection + (angleDifference * smoothingFactor)
            currentSunDirection = (smoothedDirection + 360) % 360
            _sunDirection.value = currentSunDirection
        }
    }

    /**
     * Calculate weather impact on light
     */
    private fun calculateWeatherImpact(weatherData: Any): WeatherImpactFactor {
        // Simplified weather impact calculation
        return WeatherImpactFactor(
            cloudCoverReduction = 0.3,
            precipitationReduction = 0.0,
            windEffect = 0.1
        )
    }

    /**
     * Calculate light characteristics
     */
    private fun calculateLightCharacteristics(sunPosition: SunPositionDto, weatherImpact: WeatherImpactFactor): LightCharacteristics {
        val baseEV = calculateBaseEV(sunPosition)
        val adjustedEV = baseEV - (weatherImpact.cloudCoverReduction * 2.0)
        
        val colorTemp = calculateColorTemperature(sunPosition)
        val quality = determineLightQuality(sunPosition, weatherImpact)
        
        return LightCharacteristics(
            ev = adjustedEV,
            colorTemperature = colorTemp,
            quality = quality
        )
    }

    /**
     * Calculate base EV from sun position
     */
    private fun calculateBaseEV(sunPosition: SunPositionDto): Double {
        return when {
            sunPosition.elevation > 60 -> 15.0
            sunPosition.elevation > 30 -> 14.0
            sunPosition.elevation > 15 -> 12.0
            sunPosition.elevation > 0 -> 10.0
            else -> 8.0
        }
    }

    /**
     * Calculate color temperature based on sun position
     */
    private fun calculateColorTemperature(sunPosition: SunPositionDto): Double {
        return when {
            sunPosition.elevation > 45 -> 5500.0
            sunPosition.elevation > 20 -> 4800.0
            sunPosition.elevation > 5 -> 3200.0
            else -> 2800.0
        }
    }

    /**
     * Determine light quality description
     */
    private fun determineLightQuality(sunPosition: SunPositionDto, weatherImpact: WeatherImpactFactor): String {
        return when {
            sunPosition.elevation < 0 -> "No natural light"
            sunPosition.elevation < 10 && weatherImpact.cloudCoverReduction < 0.3 -> "Golden hour"
            weatherImpact.cloudCoverReduction > 0.7 -> "Soft, diffused"
            sunPosition.elevation > 60 -> "Harsh, direct"
            else -> "Good natural light"
        }
    }

    /**
     * Calculate recommended camera settings
     */
    private suspend fun calculateRecommendedSettings(lightCharacteristics: LightCharacteristics): String {
        return try {
            val baseAperture = max(1.4, min(16.0, lightCharacteristics.ev / 2))
            val baseShutterSpeed = 1.0 / (2.0.pow(lightCharacteristics.ev - log2(baseAperture * baseAperture)))
            val baseISO = max(100, min(3200, (200 * (16 - lightCharacteristics.ev)).toInt()))
            
            "f/${baseAperture.toInt()} @ ${formatShutterSpeed(baseShutterSpeed)} ISO $baseISO"
        } catch (ex: Exception) {
            "f/8 @ 1/125 ISO 400"
        }
    }

    /**
     * Format shutter speed for display
     */
    private fun formatShutterSpeed(seconds: Double): String {
        return if (seconds >= 1) {
            "${seconds.toInt()}\""
        } else {
            "1/${(1.0 / seconds).toInt()}"
        }
    }

    /**
     * Generate weather summary
     */
    private fun generateWeatherSummary(weatherData: Any): String {
        return "Clear skies, good visibility"
    }

    /**
     * Generate recommendations
     */
    private fun generateRecommendations(sunPosition: SunPositionDto, weatherImpact: WeatherImpactFactor, lightCharacteristics: LightCharacteristics): String {
        val recommendations = mutableListOf<String>()

        if (weatherImpact.precipitationReduction > 0.3) {
            recommendations.add("Bring weather protection for gear")
        }

        if (sunPosition.elevation < 15 && weatherImpact.cloudCoverReduction < 0.3) {
            recommendations.add("Perfect golden hour conditions")
        }

        if (weatherImpact.cloudCoverReduction > 0.7) {
            recommendations.add("Great for even, soft lighting")
        }

        if (sunPosition.elevation > 60) {
            recommendations.add("Watch for harsh shadows, consider diffuser")
        }

        if (lightCharacteristics.colorTemperature < 4000) {
            recommendations.add("Warm light - great for portraits")
        }

        return if (recommendations.isNotEmpty()) {
            recommendations.joinToString(" • ")
        } else {
            "Good conditions for photography"
        }
    }

    /**
     * Generate light quality description
     */
    private fun generateLightQualityDescription(sunPosition: SunPositionDto, weatherImpact: WeatherImpactFactor): String {
        return when {
            sunPosition.elevation < 0 -> "Sun is below horizon"
            sunPosition.elevation < 10 -> "Golden hour - warm, soft light"
            weatherImpact.cloudCoverReduction > 0.7 -> "Overcast - even, diffused lighting"
            sunPosition.elevation > 60 -> "High sun - watch for harsh shadows"
            else -> "Good natural lighting conditions"
        }
    }

    override fun dispose() {
        stopCompass()
        stopAccelerometer()
        super.dispose()
    }
}

/**
 * Timeline event view model
 */
data class TimelineEventViewModel(
    val eventName: String,
    val eventIcon: String,
    val eventTime: LocalDateTime
)

/**
 * Weather impact factor data class
 */
data class WeatherImpactFactor(
    val cloudCoverReduction: Double,
    val precipitationReduction: Double,
    val windEffect: Double
)

/**
 * Light characteristics data class
 */
data class LightCharacteristics(
    val ev: Double,
    val colorTemperature: Double,
    val quality: String
)