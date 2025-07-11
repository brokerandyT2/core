// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/SunCalculatorViewModel.kt
package com.x3squaredcircles.photography.viewmodels

import kotlin.time.Duration.Companion.hours
import com.x3squaredcircles.core.presentation.BaseViewModel
import com.x3squaredcircles.core.presentation.INavigationAware
import com.x3squaredcircles.core.presentation.IErrorDisplayService
import com.x3squaredcircles.core.presentation.LocationListItemViewModel
import com.x3squaredcircles.core.mediator.IMediator
import com.x3squaredcircles.core.queries.GetLocationsQuery
import com.x3squaredcircles.photography.queries.GetSunTimesQuery
import com.x3squaredcircles.photography.domain.models.SunTimesDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toInstant
import com.x3squaredcircles.photography.viewmodels.events.OperationErrorEventArgs
import com.x3squaredcircles.photography.viewmodels.events.OperationErrorSource
import java.util.concurrent.ConcurrentHashMap

class SunCalculatorViewModel(
    private val mediator: IMediator,
    errorDisplayService: IErrorDisplayService? = null
) : BaseViewModel(null, errorDisplayService), INavigationAware {

    private val operationMutex = Mutex()
    private val calculationCache = ConcurrentHashMap<String, CachedSunCalculation>()
    private var cancellationJob: Job? = null
    private var lastCalculationTime = Clock.System.now()
    
    companion object {
        private const val CALCULATION_THROTTLE_MS = 250L
    }

    // Core properties
    private val _locations = MutableStateFlow<List<LocationListItemViewModel>>(emptyList())
    val locations: StateFlow<List<LocationListItemViewModel>> = _locations.asStateFlow()

    private val _selectedLocation = MutableStateFlow<LocationListItemViewModel?>(null)
    val selectedLocation: StateFlow<LocationListItemViewModel?> = _selectedLocation.asStateFlow()

    private val _dates = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    val dates: StateFlow<LocalDate> = _dates.asStateFlow()

    private val _locationPhoto = MutableStateFlow("")
    val locationPhoto: StateFlow<String> = _locationPhoto.asStateFlow()

    private val _dateFormat = MutableStateFlow("MM/dd/yyyy")
    val dateFormat: StateFlow<String> = _dateFormat.asStateFlow()

    private val _timeFormat = MutableStateFlow("hh:mm a")
    val timeFormat: StateFlow<String> = _timeFormat.asStateFlow()

    // Sun Times properties
    private val _sunrise = MutableStateFlow(Clock.System.now())
    val sunrise: StateFlow<Instant> = _sunrise.asStateFlow()

    private val _sunset = MutableStateFlow(Clock.System.now())
    val sunset: StateFlow<Instant> = _sunset.asStateFlow()

    private val _solarNoon = MutableStateFlow(Clock.System.now())
    val solarNoon: StateFlow<Instant> = _solarNoon.asStateFlow()

    private val _astronomicalDawn = MutableStateFlow(Clock.System.now())
    val astronomicalDawn: StateFlow<Instant> = _astronomicalDawn.asStateFlow()

    private val _astronomicalDusk = MutableStateFlow(Clock.System.now())
    val astronomicalDusk: StateFlow<Instant> = _astronomicalDusk.asStateFlow()

    private val _nauticalDawn = MutableStateFlow(Clock.System.now())
    val nauticalDawn: StateFlow<Instant> = _nauticalDawn.asStateFlow()

    private val _nauticalDusk = MutableStateFlow(Clock.System.now())
    val nauticalDusk: StateFlow<Instant> = _nauticalDusk.asStateFlow()

    private val _civilDawn = MutableStateFlow(Clock.System.now())
    val civilDawn: StateFlow<Instant> = _civilDawn.asStateFlow()

    private val _civilDusk = MutableStateFlow(Clock.System.now())
    val civilDusk: StateFlow<Instant> = _civilDusk.asStateFlow()

 private val _errorOccurredFlow = MutableSharedFlow<OperationErrorEventArgs>()
    val newErrorOccurred: SharedFlow<OperationErrorEventArgs> = _errorOccurredFlow.asSharedFlow()

    // Formatted properties - converting Instant to String
    private val _sunRiseFormatted = MutableStateFlow("")
    val sunRiseFormatted: StateFlow<String> = _sunRiseFormatted.asStateFlow()
    
    private val _sunSetFormatted = MutableStateFlow("")
    val sunSetFormatted: StateFlow<String> = _sunSetFormatted.asStateFlow()
    
    private val _solarNoonFormatted = MutableStateFlow("")
    val solarNoonFormatted: StateFlow<String> = _solarNoonFormatted.asStateFlow()
    
    private val _goldenHourMorningFormatted = MutableStateFlow("")
    val goldenHourMorningFormatted: StateFlow<String> = _goldenHourMorningFormatted.asStateFlow()
    
    private val _goldenHourEveningFormatted = MutableStateFlow("")
    val goldenHourEveningFormatted: StateFlow<String> = _goldenHourEveningFormatted.asStateFlow()
    
    private val _astronomicalDawnFormatted = MutableStateFlow("")
    val astronomicalDawnFormatted: StateFlow<String> = _astronomicalDawnFormatted.asStateFlow()
    
    private val _astronomicalDuskFormatted = MutableStateFlow("")
    val astronomicalDuskFormatted: StateFlow<String> = _astronomicalDuskFormatted.asStateFlow()
    
    private val _nauticalDawnFormatted = MutableStateFlow("")
    val nauticalDawnFormatted: StateFlow<String> = _nauticalDawnFormatted.asStateFlow()
    
    private val _nauticalDuskFormatted = MutableStateFlow("")
    val nauticalDuskFormatted: StateFlow<String> = _nauticalDuskFormatted.asStateFlow()
    
    private val _civilDawnFormatted = MutableStateFlow("")
    val civilDawnFormatted: StateFlow<String> = _civilDawnFormatted.asStateFlow()
    
    private val _civilDuskFormatted = MutableStateFlow("")
    val civilDuskFormatted: StateFlow<String> = _civilDuskFormatted.asStateFlow()

    fun setSelectedLocation(location: LocationListItemViewModel?) {
        _selectedLocation.value = location
        viewModelScope.launch {
            onSelectedLocationChangedOptimized(location)
        }
    }

    fun setDates(date: LocalDate) {
        _dates.value = date
        viewModelScope.launch {
            onDateChangedOptimized(date)
        }
    }

    fun setLocationPhoto(photo: String) {
        _locationPhoto.value = photo
    }

    fun setDateFormat(format: String) {
        _dateFormat.value = format
    }

    fun setTimeFormat(format: String) {
        _timeFormat.value = format
        updateFormattedPropertiesOptimized()
    }

    suspend fun loadLocationsAsync() {
        if (!operationMutex.tryLock()) {
            return
        }

        try {
            cancellationJob?.cancel()
            cancellationJob = Job()

            setIsBusy(true)
            clearErrors()

            val result = withContext(Dispatchers.Default) {
                try {
                    val query = GetLocationsQuery(pageNumber = 1, pageSize = 100)
                    mediator.send(query)
                } catch (e: Exception) {
                    throw IllegalStateException("Failed to load locations: ${e.message}", e)
                }
            }

            when (result) {
                is com.x3squaredcircles.core.Result.Success -> {
                    val locationViewModels = result.value.items.map { location ->
                        LocationListItemViewModel(
                            id = location.id,
                            title = location.title,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            photo = location.photoPath ?: "",
                            isDeleted = location.isDeleted
                        )
                    }
                    _locations.value = locationViewModels
                }
                is com.x3squaredcircles.core.Result.Failure -> {
                    onSystemError(result.errorMessage)
                }
            }

        } catch (e: Exception) {
            if (e !is kotlinx.coroutines.CancellationException) {
                onSystemError("Error loading locations: ${e.message}")
            }
        } finally {
            setIsBusy(false)
            operationMutex.unlock()
        }
    }

    suspend fun calculateSunAsync() {
        if (!operationMutex.tryLock()) {
            return
        }

        try {
            calculateSunOptimizedAsync()
        } finally {
            operationMutex.unlock()
        }
    }

    private suspend fun calculateSunOptimizedAsync() {
        val now = Clock.System.now()
        val timeDiff = now.minus(lastCalculationTime).inWholeMilliseconds
        if (timeDiff < CALCULATION_THROTTLE_MS) {
            return
        }
        lastCalculationTime = now

        if (_selectedLocation.value == null) {
            return
        }

        val cacheKey = generateCacheKey()
        val cached = calculationCache[cacheKey]
        if (cached != null && now.minus(cached.timestamp).inWholeMinutes < 30) {
            updateSunTimesFromCache(cached.sunTimes)
            return
        }

        try {
            setIsBusy(true)
            clearErrors()

            val location = _selectedLocation.value!!
            val query = GetSunTimesQuery(
                latitude = location.latitude,
                longitude = location.longitude,
                date = _dates.value
            )

            val result = withContext(Dispatchers.Default) {
                mediator.send(query)
            }

            when (result) {
                is com.x3squaredcircles.core.Result.Success -> {
                    val sunTimes = result.value
                    

                    updateSunTimesFromResult(sunTimes)
                    updateFormattedPropertiesOptimized()


                    calculationCache[cacheKey] = CachedSunCalculation(
                        sunTimes = sunTimes,
                        timestamp = now
                    )
                }
                is com.x3squaredcircles.core.Result.Failure -> {
                    onSystemError("Failed to calculate sun times: ${result.errorMessage}")
                }
            }

        } catch (e: Exception) {
            if (e !is kotlinx.coroutines.CancellationException) {
                onSystemError("Error calculating sun times: ${e.message}")
            }
        } finally {
            setIsBusy(false)
        }
    }

    private fun updateSunTimesFromResult(sunTimes: SunTimesDto) {
        _sunrise.value =sunTimes.sunrise.toInstant(TimeZone.currentSystemDefault())
        _sunset.value = sunTimes.sunset.toInstant(TimeZone.currentSystemDefault())
        _solarNoon.value = sunTimes.solarNoon.toInstant(TimeZone.currentSystemDefault())
        _astronomicalDawn.value = sunTimes.astronomicalDawn.toInstant(TimeZone.currentSystemDefault())
        _astronomicalDusk.value = sunTimes.astronomicalDusk.toInstant(TimeZone.currentSystemDefault())
        _nauticalDawn.value = sunTimes.nauticalDawn.toInstant(TimeZone.currentSystemDefault())
        _nauticalDusk.value = sunTimes.nauticalDusk.toInstant(TimeZone.currentSystemDefault())
        _civilDawn.value = sunTimes.civilDawn.toInstant(TimeZone.currentSystemDefault())
        _civilDusk.value = sunTimes.civilDusk.toInstant(TimeZone.currentSystemDefault())
    }

    private fun updateSunTimesFromCache(sunTimes: SunTimesDto) {

        updateSunTimesFromResult(sunTimes)
        updateFormattedPropertiesOptimized()

    }

    private suspend fun onSelectedLocationChangedOptimized(location: LocationListItemViewModel?) {
        if (location != null) {
            calculateSunOptimizedAsync()
        }
    }

    private suspend fun onDateChangedOptimized(date: LocalDate) {
        calculateSunOptimizedAsync()
    }

   private fun updateFormattedPropertiesOptimized() {
        // Update formatted properties when time format changes
        val format = _timeFormat.value
        _sunRiseFormatted.value = formatInstant(_sunrise.value, format)
        _sunSetFormatted.value = formatInstant(_sunset.value, format)
        _solarNoonFormatted.value = formatInstant(_solarNoon.value, format)
        _goldenHourMorningFormatted.value = formatInstant(_sunrise.value + 1.hours, format)
        _goldenHourEveningFormatted.value = formatInstant(_sunset.value - 1.hours, format)
        _astronomicalDawnFormatted.value = formatInstant(_astronomicalDawn.value, format)
        _astronomicalDuskFormatted.value = formatInstant(_astronomicalDusk.value, format)
        _nauticalDawnFormatted.value = formatInstant(_nauticalDawn.value, format)
        _nauticalDuskFormatted.value = formatInstant(_nauticalDusk.value, format)
        _civilDawnFormatted.value = formatInstant(_civilDawn.value, format)
        _civilDuskFormatted.value = formatInstant(_civilDusk.value, format)
    }

    private fun formatInstant(instant: Instant, format: String): String {
        return try {
            val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
        } catch (e: Exception) {
            "00:00"
        }
    }
    private fun generateCacheKey(): String {
        val location = _selectedLocation.value
        return "${location?.latitude?.toString()?.take(6) ?: "0"}_${location?.longitude?.toString()?.take(6) ?: "0"}_${_dates.value}"
    }

    override fun onSystemError(message: String) {
        super.onSystemError(message)
        viewModelScope.launch {
            _errorOccurredFlow.emit(OperationErrorEventArgs(OperationErrorSource.UNKNOWN, message))
        }
    }

    fun calculateSun() {
        viewModelScope.launch {
            calculateSunOptimizedAsync()
        }
    }

    fun onDateChanged(date: LocalDate) {
        setDates(date)
    }

    override suspend fun onNavigatedToAsync() {
        loadLocationsAsync()
    }

    override suspend fun onNavigatedFromAsync() {
        cancellationJob?.cancel()
    }

    override fun dispose() {
        cancellationJob?.cancel()
        calculationCache.clear()
        super.dispose()
    }

    private data class CachedSunCalculation(
        val sunTimes: SunTimesDto,
        val timestamp: Instant
    )
}