// core/photography/src/androidMain/kotlin/com/x3squaredcircles/photography/viewmodels/EnhancedSunCalculatorViewModel.kt
package com.x3squaredcircles.photography.viewmodels

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.infrastructure.services.ITimezoneService
import com.x3squaredcircles.core.mediator.IMediator
import com.x3squaredcircles.core.presentation.BaseViewModel
import com.x3squaredcircles.core.presentation.IErrorDisplayService
import com.x3squaredcircles.core.presentation.LocationListItemViewModel
import com.x3squaredcircles.core.queries.GetLocationsQuery
import com.x3squaredcircles.core.queries.GetSettingByKeyQuery
import com.x3squaredcircles.core.queries.GetSettingByKeyQueryResponse
import com.x3squaredcircles.core.queries.GetWeatherForecastQuery
import com.x3squaredcircles.photography.application.services.IExposureCalculatorService
import com.x3squaredcircles.photography.domain.models.SunTimesDto
import com.x3squaredcircles.photography.models.SunPathPoint
import com.x3squaredcircles.photography.models.WeatherImpactAnalysis
import com.x3squaredcircles.photography.queries.GetSunTimesQuery
import com.x3squaredcircles.photography.services.IPredictiveLightService
import com.x3squaredcircles.photography.services.IWeatherService
import com.x3squaredcircles.photography.services.getTimezoneFromCoordinatesAsync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import kotlinx.datetime.toJavaLocalDateTime

class EnhancedSunCalculatorViewModel(
        private val mediator: IMediator,
        private val predictiveLightService: IPredictiveLightService,
        private val timezoneService: ITimezoneService,
        private val weatherService: IWeatherService,
        private val exposureCalculatorService: IExposureCalculatorService,
        errorDisplayService: IErrorDisplayService? = null
) : BaseViewModel(null, errorDisplayService) {

    // Threading and caching
    private var cancellationTokenSource = Job()
    private val operationLock = Mutex()
    private val weatherCache = mutableMapOf<String, WeatherDataResult>()
    private val predictionCache = mutableMapOf<String, List<HourlyPredictionDisplayModel>>()
    private var lastWeatherUpdate = Clock.System.now()
    private val weatherCacheDurationMinutes = 30
    private val predictionCacheDurationMinutes = 60

    // Core properties
    private val _locations = MutableStateFlow<List<LocationListItemViewModel>>(emptyList())
    val locations: StateFlow<List<LocationListItemViewModel>> = _locations.asStateFlow()

    private val _selectedLocation = MutableStateFlow<LocationListItemViewModel?>(null)
    val selectedLocation: StateFlow<LocationListItemViewModel?> = _selectedLocation.asStateFlow()

    private val _selectedDate =
            MutableStateFlow(Clock.System.todayIn(TimeZone.currentSystemDefault()))
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _isNotBusy = MutableStateFlow(true)
    val isNotBusy: StateFlow<Boolean> = _isNotBusy.asStateFlow()

    // Enhanced properties for predictive light analysis
    private val _hourlyPredictions =
            MutableStateFlow<List<HourlyPredictionDisplayModel>>(emptyList())
    val hourlyPredictions: StateFlow<List<HourlyPredictionDisplayModel>> =
            _hourlyPredictions.asStateFlow()

    private val _sunPathPoints = MutableStateFlow<List<SunPathPoint>>(emptyList())
    val sunPathPoints: StateFlow<List<SunPathPoint>> = _sunPathPoints.asStateFlow()

    private val _optimalWindows = MutableStateFlow<List<OptimalWindowDisplayModel>>(emptyList())
    val optimalWindows: StateFlow<List<OptimalWindowDisplayModel>> = _optimalWindows.asStateFlow()

    // Device vs Location timezone display
    private val _deviceTimeZoneDisplay = MutableStateFlow("")
    val deviceTimeZoneDisplay: StateFlow<String> = _deviceTimeZoneDisplay.asStateFlow()

    private val _locationTimeZoneDisplay = MutableStateFlow("")
    val locationTimeZoneDisplay: StateFlow<String> = _locationTimeZoneDisplay.asStateFlow()

    // Current predictions and recommendations
    private val _currentPredictionText = MutableStateFlow("")
    val currentPredictionText: StateFlow<String> = _currentPredictionText.asStateFlow()

    private val _nextOptimalWindowText = MutableStateFlow("")
    val nextOptimalWindowText: StateFlow<String> = _nextOptimalWindowText.asStateFlow()

    // Sun times in both device and location time
    private val _sunriseDeviceTime = MutableStateFlow("")
    val sunriseDeviceTime: StateFlow<String> = _sunriseDeviceTime.asStateFlow()

    private val _sunriseLocationTime = MutableStateFlow("")
    val sunriseLocationTime: StateFlow<String> = _sunriseLocationTime.asStateFlow()

    private val _sunsetDeviceTime = MutableStateFlow("")
    val sunsetDeviceTime: StateFlow<String> = _sunsetDeviceTime.asStateFlow()

    private val _sunsetLocationTime = MutableStateFlow("")
    val sunsetLocationTime: StateFlow<String> = _sunsetLocationTime.asStateFlow()

    private val _solarNoonDeviceTime = MutableStateFlow("")
    val solarNoonDeviceTime: StateFlow<String> = _solarNoonDeviceTime.asStateFlow()

    private val _solarNoonLocationTime = MutableStateFlow("")
    val solarNoonLocationTime: StateFlow<String> = _solarNoonLocationTime.asStateFlow()

    // Current sun position
    private val _currentAzimuth = MutableStateFlow(0.0)
    val currentAzimuth: StateFlow<Double> = _currentAzimuth.asStateFlow()

    private val _currentElevation = MutableStateFlow(0.0)
    val currentElevation: StateFlow<Double> = _currentElevation.asStateFlow()

    private val _isSunUp = MutableStateFlow(false)
    val isSunUp: StateFlow<Boolean> = _isSunUp.asStateFlow()

    // Weather impact analysis
    private val _weatherImpact = MutableStateFlow<WeatherImpactAnalysis?>(null)
    val weatherImpact: StateFlow<WeatherImpactAnalysis?> = _weatherImpact.asStateFlow()

    // Light meter calibration
    private val _isLightMeterCalibrated = MutableStateFlow(false)
    val isLightMeterCalibrated: StateFlow<Boolean> = _isLightMeterCalibrated.asStateFlow()

    private val _lastLightMeterReading = MutableStateFlow<Instant?>(null)
    val lastLightMeterReading: StateFlow<Instant?> = _lastLightMeterReading.asStateFlow()

    private val _calibrationAccuracy = MutableStateFlow(0.0)
    val calibrationAccuracy: StateFlow<Double> = _calibrationAccuracy.asStateFlow()

    // UTC sun times for calculations
    private val _sunriseUtc = MutableStateFlow(Clock.System.now())
    val sunriseUtc: StateFlow<Instant> = _sunriseUtc.asStateFlow()

    private val _sunsetUtc = MutableStateFlow(Clock.System.now())
    val sunsetUtc: StateFlow<Instant> = _sunsetUtc.asStateFlow()

    private val _solarNoonUtc = MutableStateFlow(Clock.System.now())
    val solarNoonUtc: StateFlow<Instant> = _solarNoonUtc.asStateFlow()

    // Weather data
    private val _hourlyWeatherData = MutableStateFlow<HourlyWeatherForecastDto?>(null)
    val hourlyWeatherData: StateFlow<HourlyWeatherForecastDto?> = _hourlyWeatherData.asStateFlow()

    private val _weatherDataStatus = MutableStateFlow("")
    val weatherDataStatus: StateFlow<String> = _weatherDataStatus.asStateFlow()

    // Computed properties
    val hourlyPredictionsHeader: String = "Hourly Light Predictions"

    // Private function to update isNotBusy when isBusy changes
    private fun updateBusyState(busy: Boolean) {
        setIsBusy(busy)
        _isNotBusy.value = !busy
    }

    fun setSelectedLocation(location: LocationListItemViewModel?) {
        _selectedLocation.value = location
        viewModelScope.launch { onSelectedLocationChanged() }
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        viewModelScope.launch { onDateChanged() }
    }

    fun setIsLightMeterCalibrated(calibrated: Boolean) {
        _isLightMeterCalibrated.value = calibrated
    }

    fun setLastLightMeterReading(reading: Instant?) {
        _lastLightMeterReading.value = reading
    }

    fun setCalibrationAccuracy(accuracy: Double) {
        _calibrationAccuracy.value = accuracy
    }

    /** Load locations command function */
    fun loadLocations() {
        viewModelScope.launch { loadLocationsAsync() }
    }

    /** Calculate enhanced sun data command function */
    fun calculateEnhancedSunData() {
        viewModelScope.launch { calculateEnhancedSunDataAsync() }
    }

    /** Load hourly predictions command function */
    fun loadHourlyPredictions() {
        viewModelScope.launch { loadHourlyPredictionsAsync() }
    }

    /** Calibrate light meter command function */
    fun calibrateLightMeter() {
        viewModelScope.launch { calibrateLightMeterAsync() }
    }

    /** Load locations with error handling */
    private suspend fun loadLocationsAsync() {
        try {
            updateBusyState(true)
            clearErrors()

            val query = GetLocationsQuery(pageNumber = 1, pageSize = 100, includeDeleted = false)

            val result = mediator.send(query)

            when (result) {
                is Result.Success -> {
                    val locationViewModels =
                            result.data?.items?.map { locationDto ->
                                LocationListItemViewModel(
                                        id = locationDto.id,
                                        title = locationDto.title,
                                        latitude = locationDto.latitude,
                                        longitude = locationDto.longitude,
                                        photo = locationDto.photoPath ?: "",
                                        isDeleted = locationDto.isDeleted
                                )
                            }
                                    ?: emptyList()
                    _locations.value = locationViewModels
                }
                is Result.Failure -> {
                    onSystemError(result.errorMessage)
                }
            }
        } catch (ex: Exception) {
            onSystemError("Error loading locations: ${ex.message}")
        } finally {
            updateBusyState(false)
        }
    }

    /** Calculate enhanced sun data with predictive light analysis */
    private suspend fun calculateEnhancedSunDataAsync() {
        if (!operationLock.tryLock()) {
            return // Skip if another calculation is in progress
        }

        try {
            updateBusyState(true)
            clearErrors()

            val location = _selectedLocation.value
            if (location == null) {
                onSystemError("Please select a location first")
                return
            }

            // Cancel any existing operation
            cancellationTokenSource.cancel()
            cancellationTokenSource = Job()

            // Calculate basic sun times
            val sunTimesResult = calculateBasicSunTimes(location)
            if (sunTimesResult == null) return

            // Update timezone displays
            updateTimezoneDisplays(location)

            // Format and display sun times
            updateSunTimeDisplays(sunTimesResult)

            // Calculate sun path points for visualization
            // calculateSunPathPoints(location)

            // Get weather data and calculate impact
            loadWeatherDataAndImpact(location)

            // Generate optimal shooting windows
            generateOptimalWindows(location, sunTimesResult)
        } catch (ex: Exception) {
            onSystemError("Error calculating enhanced sun data: ${ex.message}")
        } finally {
            operationLock.unlock()
            updateBusyState(false)
        }
    }

    /** Calculate basic sun times */
    private suspend fun calculateBasicSunTimes(location: LocationListItemViewModel): SunTimesDto? {
        return try {
            val query =
                    GetSunTimesQuery(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            date = _selectedDate.value
                    )

            val result = mediator.send(query)

            when (result) {
                is Result.Success<SunTimesDto> -> {
                    val sunTimes = result.data

                    // Store UTC times for calculations
                    _sunriseUtc.value = sunTimes?.sunrise!!.toInstant(TimeZone.UTC)
                    _sunsetUtc.value = sunTimes.sunset.toInstant(TimeZone.UTC)
                    _solarNoonUtc.value = sunTimes.solarNoon.toInstant(TimeZone.UTC)

                    sunTimes
                }
                is Result.Failure<SunTimesDto> -> {
                    onSystemError(result.errorMessage)
                    null
                }
            }
        } catch (ex: Exception) {
            onSystemError("Error calculating sun times: ${ex.message}")
            null
        }
    }

    /** Update timezone displays */
    private suspend fun updateTimezoneDisplays(location: LocationListItemViewModel) {
        try {
            val deviceTz = TimeZone.currentSystemDefault()
            _deviceTimeZoneDisplay.value = deviceTz.id

            // Get location timezone
            val locationTzResult =
                    timezoneService.getTimezoneFromCoordinatesAsync(
                            location.latitude,
                            location.longitude
                    )

            when (locationTzResult) {
                is Result.Success<String> -> {
                    _locationTimeZoneDisplay.value = locationTzResult.data.toString()
                }
                is Result.Failure<String> -> {
                    _locationTimeZoneDisplay.value = "UTC"
                }
            }
        } catch (ex: Exception) {
            _deviceTimeZoneDisplay.value = "UTC"
            _locationTimeZoneDisplay.value = "UTC"
        }
    }

    /** Update sun time displays in both device and location time */
    // core/photography/src/androidMain/kotlin/com/x3squaredcircles/photography/viewmodels/UpdatedTimeFormatting.kt

    // Replace the updateSunTimeDisplays function with this updated version:

    /** Update sun time displays in both device and location time using user's preferred format */
    private suspend fun updateSunTimeDisplays(sunTimes: SunTimesDto) {
        try {
            // Get user's preferred time format from settings
            val timeFormatQuery = GetSettingByKeyQuery(key = "TimeFormat")
            val timeFormatResult = mediator.send(timeFormatQuery)

            // Determine the time format pattern
            val timePattern =
                    when (timeFormatResult) {
                        is Result.Success<GetSettingByKeyQueryResponse> -> {
                            when (timeFormatResult.data?.value) {
                                "HH:mm" -> "HH:mm" // 24-hour format
                                "hh:mm" -> "hh:mm a" // 12-hour format with AM/PM
                                else -> "HH:mm" // Default to 24-hour
                            }
                        }
                        is Result.Failure<GetSettingByKeyQueryResponse> -> {
                            "HH:mm" // Default to 24-hour format if setting not found
                        }
                    }

            // Create formatter using Java time formatting since kotlinx.datetime format is limited
            val deviceTz = TimeZone.currentSystemDefault()

            // Convert to device time and format
            val sunriseDevice = sunTimes.sunrise.toInstant(TimeZone.UTC).toLocalDateTime(deviceTz)
            val sunsetDevice = sunTimes.sunset.toInstant(TimeZone.UTC).toLocalDateTime(deviceTz)
            val solarNoonDevice =
                    sunTimes.solarNoon.toInstant(TimeZone.UTC).toLocalDateTime(deviceTz)

            // Format times using Java formatter since we need AM/PM support
            _sunriseDeviceTime.value = formatLocalDateTime(sunriseDevice, timePattern)
            _sunsetDeviceTime.value = formatLocalDateTime(sunsetDevice, timePattern)
            _solarNoonDeviceTime.value = formatLocalDateTime(solarNoonDevice, timePattern)

            // Use the original local times for location display
            _sunriseLocationTime.value = formatLocalDateTime(sunTimes.sunrise, timePattern)
            _sunsetLocationTime.value = formatLocalDateTime(sunTimes.sunset, timePattern)
            _solarNoonLocationTime.value = formatLocalDateTime(sunTimes.solarNoon, timePattern)
        } catch (ex: Exception) {
            // Fallback to default times if formatting fails
            _sunriseDeviceTime.value = "00:00"
            _sunsetDeviceTime.value = "00:00"
            _solarNoonDeviceTime.value = "00:00"
            _sunriseLocationTime.value = "00:00"
            _sunsetLocationTime.value = "00:00"
            _solarNoonLocationTime.value = "00:00"
        }
    }

    /** Helper function to format LocalDateTime using Java formatters for AM/PM support */
    private fun formatLocalDateTime(dateTime: LocalDateTime, pattern: String): String {
        return try {
            val javaDateTime = dateTime.toJavaLocalDateTime()
            val formatter = java.time.format.DateTimeFormatter.ofPattern(pattern)
            javaDateTime.format(formatter)
        } catch (ex: Exception) {
            // Fallback to simple HH:mm format
            String.format("%02d:%02d", dateTime.hour, dateTime.minute)
        }
    }

    // Additional imports needed at the top of the file:

    /**
     * Calculate sun path points for visualization
     *
     * private suspend fun calculateSunPathPoints(location: LocationListItemViewModel) { try { val
     * points = withContext(Dispatchers.Default) { val pathPoints = mutableListOf<SunPathPoint>()
     *
     * // Calculate sun position every 15 minutes throughout the day val startOfDay =
     * _selectedDate.value.atTime(0, 0)
     *
     * for (minute in 0..1440 step 15) { // 24 hours * 60 minutes, every 15 minutes val currentTime
     * = startOfDay.plus(minute.toInt(), DateTimeUnit.MINUTE)
     *
     * // Calculate sun position (simplified calculation) val hour = currentTime.hour +
     * currentTime.minute / 60.0 val azimuth = (hour / 24.0) * 360.0 // Simplified azimuth
     * calculation val elevation = kotlin.math.sin((hour - 6) / 12.0 * kotlin.math.PI) * 45.0 //
     * Simplified elevation
     *
     * if (elevation > -18) { // Only include points when sun is above astronomical twilight
     * pathPoints.add( SunPathPoint( time = currentTime, azimuth = azimuth, elevation =
     * kotlin.math.max(0.0, elevation), isVisible = elevation > 0 ) ) } }
     *
     * pathPoints }
     *
     * _sunPathPoints.value = points } catch (ex: Exception) { onSystemError("Error calculating sun
     * path: ${ex.message}") } }
     */
    /** Load weather data and calculate impact */
    private suspend fun loadWeatherDataAndImpact(location: LocationListItemViewModel) {
        try {
            _weatherDataStatus.value = "Loading weather data..."

            val weatherQuery =
                    GetWeatherForecastQuery(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            days = 1
                    )

            val weatherResult = mediator.send(weatherQuery)

            when (weatherResult) {
                is Result.Success<*> -> {
                    val weatherData = weatherResult.data
                    // Note: weatherData type needs to be properly defined based on actual DTO

                    // Calculate weather impact on photography
                    val impact = calculateWeatherImpact(weatherData)
                    _weatherImpact.value = impact

                    _weatherDataStatus.value = "Weather data loaded"
                }
                is Result.Failure<*> -> {
                    _weatherDataStatus.value = "Weather data unavailable"
                    _weatherImpact.value = null
                }
            }
        } catch (ex: Exception) {
            _weatherDataStatus.value = "Error loading weather"
            _weatherImpact.value = null
        }
    }

    /** Generate optimal shooting windows */
    // Manual datetime arithmetic when plus() methods are not available

    private suspend fun generateOptimalWindows(
            location: LocationListItemViewModel,
            sunTimes: SunTimesDto
    ) {
        try {
            val windows =
                    withContext(Dispatchers.Default) {
                        val optimalWindows = mutableListOf<OptimalWindowDisplayModel>()

                        // Helper function to add hours manually
                        fun addHours(dateTime: LocalDateTime, hours: Int): LocalDateTime {
                            val newHour = dateTime.hour + hours
                            return when {
                                newHour >= 24 -> {
                                    // Next day
                                    LocalDateTime(
                                            dateTime.year,
                                            dateTime.month,
                                            dateTime.dayOfMonth + 1,
                                            newHour - 24,
                                            dateTime.minute,
                                            dateTime.second,
                                            dateTime.nanosecond
                                    )
                                }
                                newHour < 0 -> {
                                    // Previous day
                                    LocalDateTime(
                                            dateTime.year,
                                            dateTime.month,
                                            dateTime.dayOfMonth - 1,
                                            newHour + 24,
                                            dateTime.minute,
                                            dateTime.second,
                                            dateTime.nanosecond
                                    )
                                }
                                else -> {
                                    // Same day
                                    LocalDateTime(
                                            dateTime.year,
                                            dateTime.month,
                                            dateTime.dayOfMonth,
                                            newHour,
                                            dateTime.minute,
                                            dateTime.second,
                                            dateTime.nanosecond
                                    )
                                }
                            }
                        }

                        // Golden hour windows using manual hour addition
                        optimalWindows.add(
                                OptimalWindowDisplayModel(
                                        windowType = "Morning Golden Hour",
                                        startTime = sunTimes.sunrise,
                                        endTime = addHours(sunTimes.sunrise, 1),
                                        lightQuality = "Excellent",
                                        optimalFor = "Portraits and landscapes",
                                        confidenceLevel = 0.9
                                )
                        )

                        optimalWindows.add(
                                OptimalWindowDisplayModel(
                                        windowType = "Evening Golden Hour",
                                        startTime = addHours(sunTimes.sunset, -1),
                                        endTime = sunTimes.sunset,
                                        lightQuality = "Excellent",
                                        optimalFor = "Portraits",
                                        confidenceLevel = 0.9
                                )
                        )

                        // Blue hour windows (these don't need arithmetic since they use existing
                        // times)
                        optimalWindows.add(
                                OptimalWindowDisplayModel(
                                        windowType = "Morning Blue Hour",
                                        startTime = sunTimes.civilDawn,
                                        endTime = sunTimes.sunrise,
                                        lightQuality = "Good",
                                        optimalFor = "Cityscapes",
                                        confidenceLevel = 0.7
                                )
                        )

                        optimalWindows.add(
                                OptimalWindowDisplayModel(
                                        windowType = "Evening Blue Hour",
                                        startTime = sunTimes.sunset,
                                        endTime = sunTimes.civilDusk,
                                        lightQuality = "Good",
                                        optimalFor = "Architecture and cityscapes",
                                        confidenceLevel = 0.7
                                )
                        )

                        optimalWindows
                    }

            _optimalWindows.value = windows

            // Update current prediction text
            updateCurrentPredictions(windows)
        } catch (ex: Exception) {
            onSystemError("Error generating optimal windows: ${ex.message}")
        }
    }

    /** Update current predictions */
    private suspend fun updateCurrentPredictions(windows: List<OptimalWindowDisplayModel>) {
        try {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val timePattern = getTimeFormatter() // Get user's preferred format

            val currentWindow =
                    windows.find { window -> now >= window.startTime && now <= window.endTime }

            if (currentWindow != null) {
                _currentPredictionText.value =
                        "Currently in ${currentWindow.windowType}: ${currentWindow.optimalFor}"
            } else {
                val nextWindow = windows.filter { it.startTime > now }.minByOrNull { it.startTime }
                if (nextWindow != null) {
                    // Use Java formatter for proper AM/PM support
                    val formattedTime = formatLocalDateTime(nextWindow.startTime, timePattern)
                    _nextOptimalWindowText.value =
                            "Next optimal time: ${nextWindow.windowType} starting at $formattedTime"
                } else {
                    _nextOptimalWindowText.value = "No more optimal windows today"
                }
                _currentPredictionText.value = "No optimal shooting conditions right now"
            }
        } catch (ex: Exception) {
            _currentPredictionText.value = "Error calculating predictions"
            _nextOptimalWindowText.value = ""
        }
    }

    // Helper function for formatting (if not already added):

    /** Load hourly predictions */
    private suspend fun loadHourlyPredictionsAsync() {
        try {
            updateBusyState(true)

            val location = _selectedLocation.value
            if (location == null) {
                onSystemError("Please select a location first")
                return
            }

            val cacheKey = "${location.latitude}_${location.longitude}_${_selectedDate.value}"

            // Check cache first
            val cached = predictionCache[cacheKey]
            if (cached != null) {
                _hourlyPredictions.value = cached
                return
            }

            // Generate hourly predictions
            val predictions =
                    withContext(Dispatchers.Default) { generateHourlyPredictions(location) }

            // Cache results
            predictionCache[cacheKey] = predictions
            _hourlyPredictions.value = predictions
        } catch (ex: Exception) {
            onSystemError("Error loading hourly predictions: ${ex.message}")
        } finally {
            updateBusyState(false)
        }
    }
    private suspend fun getTimeFormatter(): String {
        return try {
            val timeFormatQuery = GetSettingByKeyQuery(key = "TimeFormat")
            val timeFormatResult = mediator.send(timeFormatQuery)

            when (timeFormatResult) {
                is Result.Success<GetSettingByKeyQueryResponse> -> {
                    when (timeFormatResult.data?.value) {
                        "HH:mm" -> "HH:mm" // 24-hour format
                        "hh:mm" -> "hh:mm a" // 12-hour format with AM/PM
                        else -> "HH:mm" // Default to 24-hour
                    }
                }
                is Result.Failure<GetSettingByKeyQueryResponse> -> {
                    "HH:mm" // Default to 24-hour format if setting not found
                }
            }
        } catch (ex: Exception) {
            "HH:mm" // Fallback to 24-hour format
        }
    }
    fun addMinutes(dateTime: LocalDateTime, minutes: Int): LocalDateTime {
        val totalMinutes = dateTime.hour * 60 + dateTime.minute + minutes
        val newHour = (totalMinutes / 60) % 24
        val newMinute = totalMinutes % 60

        return LocalDateTime(
                dateTime.year,
                dateTime.month,
                dateTime.dayOfMonth,
                newHour,
                newMinute,
                dateTime.second,
                dateTime.nanosecond
        )
    }

    // Pattern 2: Adding hours to LocalDateTime
    // WRONG: someDateTime.plus(1, DateTimeUnit.HOUR)
    // CORRECT: Use the addHours function from before
    fun addHours(dateTime: LocalDateTime, hours: Int): LocalDateTime {
        val newHour = dateTime.hour + hours
        return when {
            newHour >= 24 -> {
                LocalDateTime(
                        dateTime.year,
                        dateTime.month,
                        dateTime.dayOfMonth + 1,
                        newHour - 24,
                        dateTime.minute,
                        dateTime.second,
                        dateTime.nanosecond
                )
            }
            newHour < 0 -> {
                LocalDateTime(
                        dateTime.year,
                        dateTime.month,
                        dateTime.dayOfMonth - 1,
                        newHour + 24,
                        dateTime.minute,
                        dateTime.second,
                        dateTime.nanosecond
                )
            }
            else -> {
                LocalDateTime(
                        dateTime.year,
                        dateTime.month,
                        dateTime.dayOfMonth,
                        newHour,
                        dateTime.minute,
                        dateTime.second,
                        dateTime.nanosecond
                )
            }
        }
    }
    /** Generate hourly predictions */
    private fun generateHourlyPredictions(
            location: LocationListItemViewModel
    ): List<HourlyPredictionDisplayModel> {
        val predictions = mutableListOf<HourlyPredictionDisplayModel>()
        val startOfDay = _selectedDate.value.atTime(0, 0)

        for (hour in 0..23) {
            val currentTime = addHours(startOfDay, 1)

            // Calculate light quality based on time (simplified)
            val lightQuality =
                    when (hour) {
                        in 5..7, in 18..20 -> "Excellent"
                        in 8..9, in 16..17 -> "Good"
                        in 10..15 -> "Fair"
                        else -> "Poor"
                    }

            val recommendations =
                    when (lightQuality) {
                        "Excellent" -> "Perfect for portraits and landscapes"
                        "Good" -> "Good for general photography"
                        "Fair" -> "Consider using diffusers for portraits"
                        else -> "Not recommended for photography"
                    }

            predictions.add(
                    HourlyPredictionDisplayModel(
                            time = currentTime,
                            lightQuality = lightQuality,
                            recommendations = recommendations,
                            colorTemperature = if (hour in 6..18) 5500.0 else 3200.0,
                            cloudCover = 20,
                            precipitationProbability = 0.1,
                            deviceTimeDisplay = currentTime.toString(),
                            locationTimeDisplay = currentTime.toString(),
                            predictedEV = 12.0,
                            evConfidenceMargin = 1.0,
                            suggestedAperture = "f/8",
                            suggestedShutterSpeed = "1/125s",
                            suggestedISO = "ISO 100",
                            confidenceLevel = 0.8,
                            isOptimalTime = lightQuality == "Excellent",
                            shootingQualityScore =
                                    when (lightQuality) {
                                        "Excellent" -> 90.0
                                        "Good" -> 70.0
                                        "Fair" -> 50.0
                                        else -> 20.0
                                    },
                            weatherDescription = "Clear",
                            windInfo = "5 mph N",
                            uvIndex = 6.0,
                            humidity = 60
                    )
            )
        }

        return predictions
    }

    /** Calculate weather impact */
    private fun calculateWeatherImpact(weatherData: Any?): WeatherImpactAnalysis {
        // Simplified weather impact calculation
        return WeatherImpactAnalysis(
                summary = "Good lighting conditions expected",
                overallLightReductionFactor = 0.8,
                hourlyImpacts = emptyList(),
                alerts = emptyList()
        )
    }

    /** Calibrate light meter */
    private suspend fun calibrateLightMeterAsync() {
        try {
            updateBusyState(true)

            // Simulate light meter calibration
            withContext(Dispatchers.Default) {
                kotlinx.coroutines.delay(2000) // Simulate calibration time
            }

            _isLightMeterCalibrated.value = true
            _lastLightMeterReading.value = Clock.System.now()
            _calibrationAccuracy.value = 95.0
        } catch (ex: Exception) {
            onSystemError("Error calibrating light meter: ${ex.message}")
        } finally {
            updateBusyState(false)
        }
    }

    /** Handle location selection change */
    private suspend fun onSelectedLocationChanged() {
        calculateEnhancedSunDataAsync()
    }

    /** Handle date change */
    private suspend fun onDateChanged() {
        calculateEnhancedSunDataAsync()
    }

    override fun dispose() {
        cancellationTokenSource.cancel()
        weatherCache.clear()
        predictionCache.clear()
        super.dispose()
    }
}

// Supporting data classes
data class WeatherDataResult(
        val temperature: Double,
        val humidity: Double,
        val cloudCover: Double,
        val visibility: Double
)

data class HourlyWeatherForecastDto(val hourlyData: List<HourlyWeatherData>)

data class HourlyWeatherData(
        val time: LocalDateTime,
        val temperature: Double,
        val humidity: Double,
        val cloudCover: Double
)
