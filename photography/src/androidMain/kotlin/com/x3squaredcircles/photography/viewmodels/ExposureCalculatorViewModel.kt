// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/ExposureCalculatorViewModel.kt
package com.x3squaredcircles.photography.viewmodels

import com.x3squaredcircles.core.presentation.BaseViewModel
import com.x3squaredcircles.core.mediator.IMediator
import com.x3squaredcircles.core.dtos.TipTypeDto
import com.x3squaredcircles.core.dtos.TipDto
import com.x3squaredcircles.core.queries.GetAllTipTypesQuery
import com.x3squaredcircles.core.queries.GetTipsByTypeQuery
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.application.services.IExposureCalculatorService
import com.x3squaredcircles.photography.application.services.models.ExposureIncrements
import com.x3squaredcircles.photography.application.services.models.FixedValue
import com.x3squaredcircles.photography.application.services.models.ExposureTriangleDto
import com.x3squaredcircles.photography.application.services.models.ExposureSettingsDto
import com.x3squaredcircles.photography.application.services.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.milliseconds
import com.x3squaredcircles.core.presentation.IErrorDisplayService
class ExposureCalculatorViewModel(
    private val mediator: IMediator,
    private val exposureCalculatorService: IExposureCalculatorService,
    errorDisplayService: IErrorDisplayService? = null
) : BaseViewModel(null, errorDisplayService) {

    // Threading and caching
    private val calculationLock = Mutex()
    private var lastCalculationTime = Clock.System.now()
    private val calculationThrottleMs = 200L
    private val pickerValuesCache = mutableMapOf<String, String>()

    // Core properties
    private val _shutterSpeedSelected = MutableStateFlow("")
    val shutterSpeedSelected: StateFlow<String> = _shutterSpeedSelected.asStateFlow()

    private val _fStopSelected = MutableStateFlow("")
    val fStopSelected: StateFlow<String> = _fStopSelected.asStateFlow()

    private val _isoSelected = MutableStateFlow("")
    val isoSelected: StateFlow<String> = _isoSelected.asStateFlow()

    private val _oldShutterSpeed = MutableStateFlow("")
    val oldShutterSpeed: StateFlow<String> = _oldShutterSpeed.asStateFlow()

    private val _oldFstop = MutableStateFlow("")
    val oldFstop: StateFlow<String> = _oldFstop.asStateFlow()

    private val _oldISO = MutableStateFlow("")
    val oldISO: StateFlow<String> = _oldISO.asStateFlow()

    private val _shutterSpeedResult = MutableStateFlow("")
    val shutterSpeedResult: StateFlow<String> = _shutterSpeedResult.asStateFlow()

    private val _fStopResult = MutableStateFlow("")
    val fStopResult: StateFlow<String> = _fStopResult.asStateFlow()

    private val _isoResult = MutableStateFlow("")
    val isoResult: StateFlow<String> = _isoResult.asStateFlow()

    private val _shutterSpeedsForPicker = MutableStateFlow(ShutterSpeeds.full)
    val shutterSpeedsForPicker: StateFlow<Array<String>> = _shutterSpeedsForPicker.asStateFlow()

    private val _aperturesForPicker = MutableStateFlow(Apertures.full)
    val aperturesForPicker: StateFlow<Array<String>> = _aperturesForPicker.asStateFlow()

    private val _isosForPicker = MutableStateFlow(ISOs.full)
    val isosForPicker: StateFlow<Array<String>> = _isosForPicker.asStateFlow()

    private val _fullHalfThirds = MutableStateFlow(ExposureIncrements.Full)
    val fullHalfThirds: StateFlow<ExposureIncrements> = _fullHalfThirds.asStateFlow()

    private val _toCalculate = MutableStateFlow(FixedValue.ShutterSpeeds)
    val toCalculate: StateFlow<FixedValue> = _toCalculate.asStateFlow()

    private val _evValue = MutableStateFlow(0.0)
    val evValue: StateFlow<Double> = _evValue.asStateFlow()

    private val _showError = MutableStateFlow(false)
    val showError: StateFlow<Boolean> = _showError.asStateFlow()

    // Lock state properties
    private val _isShutterLocked = MutableStateFlow(false)
    val isShutterLocked: StateFlow<Boolean> = _isShutterLocked.asStateFlow()

    private val _isApertureLocked = MutableStateFlow(false)
    val isApertureLocked: StateFlow<Boolean> = _isApertureLocked.asStateFlow()

    private val _isIsoLocked = MutableStateFlow(false)
    val isIsoLocked: StateFlow<Boolean> = _isIsoLocked.asStateFlow()

    // Preset properties
    private val _selectedPreset = MutableStateFlow<TipTypeDto?>(null)
    val selectedPreset: StateFlow<TipTypeDto?> = _selectedPreset.asStateFlow()

    private val _availablePresets = MutableStateFlow<List<TipTypeDto>>(emptyList())
    val availablePresets: StateFlow<List<TipTypeDto>> = _availablePresets.asStateFlow()

    // Commands state
    private val _canCalculate = MutableStateFlow(true)
    val canCalculate: StateFlow<Boolean> = _canCalculate.asStateFlow()

    init {
        initializeDefaults()
        viewModelScope.launch {
            loadPickerValuesOptimized()
            loadPresets()
        }
    }

    fun setShutterSpeedSelected(value: String) {
        _shutterSpeedSelected.value = value
        viewModelScope.launch {
            calculateOptimized()
        }
    }

    fun setFStopSelected(value: String) {
        _fStopSelected.value = value
        viewModelScope.launch {
            calculateOptimized()
        }
    }

    fun setIsoSelected(value: String) {
        _isoSelected.value = value
        viewModelScope.launch {
            calculateOptimized()
        }
    }

    fun setFullHalfThirds(value: ExposureIncrements) {
        _fullHalfThirds.value = value
        viewModelScope.launch {
            loadPickerValuesOptimized()
        }
    }

    fun setToCalculate(value: FixedValue) {
        _toCalculate.value = value
    }

    fun setEvValue(value: Double) {
        _evValue.value = value
        viewModelScope.launch {
            calculateOptimized()
        }
    }

    fun setIsShutterLocked(value: Boolean) {
        _isShutterLocked.value = value
        updateCalculationModeOptimized()
    }

    fun setIsApertureLocked(value: Boolean) {
        _isApertureLocked.value = value
        updateCalculationModeOptimized()
    }

    fun setIsIsoLocked(value: Boolean) {
        _isIsoLocked.value = value
        updateCalculationModeOptimized()
    }

    fun setSelectedPreset(value: TipTypeDto?) {
        _selectedPreset.value = value
        value?.let {
            viewModelScope.launch {
                applyPresetOptimized(it)
            }
        }
    }

    /**
     * PERFORMANCE OPTIMIZATION: Throttled and optimized calculation
     */
    private suspend fun calculateOptimized() {
        // Throttle rapid updates
        val now = Clock.System.now()
        if ((now - lastCalculationTime) < calculationThrottleMs.milliseconds) {
            return
        }
        lastCalculationTime = now

        if (!calculationLock.tryLock()) {
            return // Skip if another calculation is in progress
        }

        try {
            withContext(Dispatchers.Default) {
                calculateCore()
            }
        } finally {
            calculationLock.unlock()
        }
    }

    /**
     * Core calculation logic
     */
    private suspend fun calculateCore() {
        try {
            val baseExposure = ExposureTriangleDto(
                shutterSpeed = _shutterSpeedSelected.value,
                aperture = _fStopSelected.value,
                iso = _isoSelected.value
            )

            val result = when (_toCalculate.value) {
                FixedValue.ShutterSpeeds -> {
                    exposureCalculatorService.calculateShutterSpeedAsync(
                        baseExposure = baseExposure,
                        targetAperture = _fStopSelected.value,
                        targetIso = _isoSelected.value,
                        increments = _fullHalfThirds.value,
                        cancellationToken = Job(),
                        evCompensation = _evValue.value
                    )
                }
                FixedValue.Aperture -> {
                    exposureCalculatorService.calculateApertureAsync(
                        baseExposure = baseExposure,
                        targetShutterSpeed = _shutterSpeedSelected.value,
                        targetIso = _isoSelected.value,
                        increments = _fullHalfThirds.value,
                        cancellationToken = Job(),
                        evCompensation = _evValue.value
                    )
                }
                FixedValue.ISO -> {
                    exposureCalculatorService.calculateIsoAsync(
                        baseExposure = baseExposure,
                        targetShutterSpeed = _shutterSpeedSelected.value,
                        targetAperture = _fStopSelected.value,
                        increments = _fullHalfThirds.value,
                        cancellationToken = Job(),
                        evCompensation = _evValue.value
                    )
                }
                else -> return
            }

            when (result) {
                is Result.Success -> {
                    withContext(Dispatchers.Main) {
                        _shutterSpeedResult.value = result.data?.shutterSpeed!!
                        _fStopResult.value = result.data?.aperture!!
                        _isoResult.value = result.data?.iso!!
                        _showError.value = false
                        clearErrors()
                    }
                }
                is Result.Failure -> {
                    withContext(Dispatchers.Main) {
                        _showError.value = true
                        setValidationError(result.errorMessage)
                    }
                }
            }
        } catch (ex: Exception) {
            withContext(Dispatchers.Main) {
                _showError.value = true
                onSystemError("Error calculating exposure: ${ex.message}")
            }
        }
    }

    /**
     * Calculate command function
     */
    fun calculate() {
        viewModelScope.launch {
            calculateOptimized()
        }
    }

    /**
     * Reset command function
     */
    fun reset() {
        viewModelScope.launch {
            resetOptimized()
        }
    }

    /**
     * Toggle lock functions
     */
    fun toggleShutterLock() {
        _isShutterLocked.value = !_isShutterLocked.value
        updateCalculationModeOptimized()
    }

    fun toggleApertureLock() {
        _isApertureLocked.value = !_isApertureLocked.value
        updateCalculationModeOptimized()
    }

    fun toggleIsoLock() {
        _isIsoLocked.value = !_isIsoLocked.value
        updateCalculationModeOptimized()
    }

    /**
     * Load presets
     */
    private suspend fun loadPresets() {
        try {
            setIsBusy(true)
            
            val query = GetAllTipTypesQuery()
            val result = mediator.send(query)

            when (result) {
                is Result.Success -> {
                    _availablePresets.value = result.data?.sortedBy { it.name } ?: emptyList()
                }
                is Result.Failure -> {
                    onSystemError("Error loading presets: ${result.errorMessage}")
                }
            }
        } catch (ex: Exception) {
            onSystemError("Error loading presets: ${ex.message}")
        } finally {
            setIsBusy(false)
        }
    }

    /**
     * PERFORMANCE OPTIMIZATION: Load picker values with caching
     */
    private suspend fun loadPickerValuesOptimized() {
        try {
            val step = _fullHalfThirds.value.toString()
            val cacheKey = step

            if (pickerValuesCache.containsKey(cacheKey)) {
                return // Values already cached
            }

            withContext(Dispatchers.Default) {
                val shutterSpeeds = when (_fullHalfThirds.value) {
                    ExposureIncrements.Full -> ShutterSpeeds.full
                    ExposureIncrements.Half -> ShutterSpeeds.halves
                    ExposureIncrements.Third -> ShutterSpeeds.thirds
                }

                val apertures = when (_fullHalfThirds.value) {
                    ExposureIncrements.Full -> Apertures.full
                    ExposureIncrements.Half -> Apertures.halves
                    ExposureIncrements.Third -> Apertures.thirds
                }

                val isos = when (_fullHalfThirds.value) {
                    ExposureIncrements.Full -> ISOs.full
                    ExposureIncrements.Half -> ISOs.halves
                    ExposureIncrements.Third -> ISOs.thirds
                }

                withContext(Dispatchers.Main) {
                    _shutterSpeedsForPicker.value = shutterSpeeds
                    _aperturesForPicker.value = apertures
                    _isosForPicker.value = isos

                    // Cache the values
                    pickerValuesCache[cacheKey] = "loaded"
                }
            }
        } catch (ex: Exception) {
            onSystemError("Error loading picker values: ${ex.message}")
        }
    }

    /**
     * Apply preset optimized
     */
    private suspend fun applyPresetOptimized(preset: TipTypeDto) {
        try {
            setIsBusy(true)

            val query = GetTipsByTypeQuery(preset.id)
            val result = mediator.send(query)

            when (result) {
                is Result.Success -> {
                    val tips = result.data
                    if (tips?.isNotEmpty()!!) {
                        val tip = tips.first()
                        
                        // Apply tip settings
                        if (tip.fstop.isNotEmpty()) {
                            _fStopSelected.value = tip.fstop
                        }
                        if (tip.shutterSpeed.isNotEmpty()) {
                            _shutterSpeedSelected.value = tip.shutterSpeed
                        }
                        if (tip.iso.isNotEmpty()) {
                            _isoSelected.value = tip.iso
                        }

                        calculateOptimized()
                    } else {
                        onSystemError("No tips found for the selected preset category")
                    }
                }
                is Result.Failure -> {
                    onSystemError("Error applying preset: ${result.errorMessage}")
                }
            }
        } catch (ex: Exception) {
            onSystemError("Error applying preset: ${ex.message}")
        } finally {
            setIsBusy(false)
        }
    }

    /**
     * PERFORMANCE OPTIMIZATION: Optimized calculation mode update
     */
    private fun updateCalculationModeOptimized() {
        // Determine which parameter to calculate based on locks
        val newToCalculate = when {
            _isShutterLocked.value -> FixedValue.ShutterSpeeds
            _isApertureLocked.value -> FixedValue.Aperture
            _isIsoLocked.value -> FixedValue.ISO
            else -> FixedValue.ShutterSpeeds // Default to calculating shutter speed if nothing is locked
        }

        _toCalculate.value = newToCalculate

        // Trigger recalculation
        viewModelScope.launch {
            calculateOptimized()
        }
    }

    /**
     * PERFORMANCE OPTIMIZATION: Optimized value storage
     */
    private fun storeOldValuesOptimized() {
        _oldShutterSpeed.value = _shutterSpeedSelected.value
        _oldFstop.value = _fStopSelected.value
        _oldISO.value = _isoSelected.value
    }

    /**
     * PERFORMANCE OPTIMIZATION: Optimized reset
     */
    private suspend fun resetOptimized() {
        try {
            // Store old values before reset
            storeOldValuesOptimized()

            // Reset to middle values
            val shutterSpeedsArray = _shutterSpeedsForPicker.value
            val aperturesArray = _aperturesForPicker.value
            val isosArray = _isosForPicker.value

            _shutterSpeedSelected.value = if (shutterSpeedsArray.isNotEmpty()) {
                shutterSpeedsArray[shutterSpeedsArray.size / 2]
            } else "1/60"

            _fStopSelected.value = if (aperturesArray.isNotEmpty()) {
                aperturesArray[aperturesArray.size / 2]
            } else "f/5.6"

            _isoSelected.value = if (isosArray.isNotEmpty()) {
                isosArray[isosArray.size / 2]
            } else "400"

            _evValue.value = 0.0
            _toCalculate.value = FixedValue.ShutterSpeeds

            // Reset lock states
            _isShutterLocked.value = false
            _isApertureLocked.value = false
            _isIsoLocked.value = false

            // Clear errors
            _showError.value = false
            clearErrors()

            // Recalculate
            calculateOptimized()
        } catch (ex: Exception) {
            onSystemError("Error resetting exposure calculator: ${ex.message}")
        }
    }

    private fun initializeDefaults() {
        _fullHalfThirds.value = ExposureIncrements.Full
        _toCalculate.value = FixedValue.ShutterSpeeds
        _evValue.value = 0.0
    }
}