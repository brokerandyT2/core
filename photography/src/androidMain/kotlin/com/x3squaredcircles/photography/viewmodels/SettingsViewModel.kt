Let me search for the complete SettingViewModel.cs file and ViewModelBase to understand the inheritance structure:Searched project for “ViewModelBase class complete code Base properties methods”6 resultsLocation/Location.Photography.ViewModels/SettingViewModel.cstextLocation/Location.Photography.ViewModels/SubscriptionAwareViewModelBase.cstextLocation/Location.Photography.ViewModels/BaseViewModel.cs2 relevant sectionstextLocation/Location.Photography.ViewModels/SubscriptionSignUpViewModel.cstextLocation/Location.Photography.ViewModels/EnhancedSunCalculatorViewModel.cstextLocation/Location.Photography.ViewModels/SunCalculatorViewModel.cs2 relevant sectionstextNow let me search for the complete SettingViewModel.cs with all methods to ensure I understand the full implementation:Searched project for “SettingViewModel complete methods UpdateSettingByKey EndPropertyChangeBatchAsync GetAllSettings”4 resultsLocation/Location.Photography.ViewModels/SettingViewModel.cs3 relevant sectionstextphotography/src/androidMain/kotlin/com/x3squaredcircles/photography/viewmodels/SettingViewModel.kt3 relevant sectionstextLocation/Location.Photography.ViewModels/BaseViewModel.cstextLocation/Location.Photography.ViewModels/EnhancedSunCalculatorViewModel.cstextNow let me check if I need to understand INavigationAware interface and any other dependencies:Searched project for “INavigationAware interface OnSystemError method signatures”8 resultsLocation/Location.Photography.ViewModels/Interfaces/INavigationAware.cstextcore/src/commonMain/kotlin/com/x3squaredcircles/core/presentation/INavigationAware.kttextLocation/Location.Photography.ViewModels/Interfaces/ISunCalculations.cstextLocation/Location.Photography.ViewModels/Events/OperationErrorEventArgs.cstextLocation/Location.Photography.Infrastructure/Extensions/RepositoryExtensions.cstextLocation/Location.Photography.ViewModels/BaseViewModel.cstextcore/src/commonMain/kotlin/com/x3squaredcircles/core/presentation/LocationsViewModel.kttextLocation/Location.Photography.ViewModels/SettingViewModel.cstextPerfect! Now I have all the information I need to properly migrate the SettingViewModel.cs file. Let me validate the complete structure before providing the migrated code:Searched project for “BaseViewModel beginPropertyChangeBatch endPropertyChangeBatch method signatures kotlin”5 resultsphotography/src/androidMain/kotlin/com/x3squaredcircles/photography/viewmodels/SettingViewModel.kt3 relevant sectionstextcore/src/commonMain/kotlin/com/x3squaredcircles/core/presentation/BaseViewModel.kt2 relevant sectionstextLocation/Location.Photography.ViewModels/Interfaces/ISunCalculations.cstextLocation/Location.Photography.Infrastructure/Extensions/RepositoryExtensions.cstextLocation/Location.Photography.ViewModels/BaseViewModel.cstextNow I have all the information I need. I'll provide the complete migrated SettingsViewModel file. I notice that the existing partial Kotlin file has some issues and the C# file is complete with all methods. Let me create the proper migration.
// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/SettingsViewModel.kt
package com.x3squaredcircles.photography.viewmodels
import com.x3squaredcircles.core.presentation.BaseViewModel
import com.x3squaredcircles.core.presentation.INavigationAware
import com.x3squaredcircles.core.presentation.IErrorDisplayService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
class SettingViewModel(
errorDisplayService: IErrorDisplayService? = null
) : BaseViewModel(null, errorDisplayService), INavigationAware {
private val _id = MutableStateFlow(0)
val id: StateFlow<Int> = _id.asStateFlow()

private val _key = MutableStateFlow("")
val key: StateFlow<String> = _key.asStateFlow()

private val _value = MutableStateFlow("")
val value: StateFlow<String> = _value.asStateFlow()

private val _description = MutableStateFlow("")
val description: StateFlow<String> = _description.asStateFlow()

private val _timestamp = MutableStateFlow(Clock.System.now())
val timestamp: StateFlow<Instant> = _timestamp.asStateFlow()

fun setId(value: Int) {
    _id.value = value
}

fun setKey(value: String) {
    _key.value = value
}

fun setValue(value: String) {
    _value.value = value
}

fun setDescription(value: String) {
    _description.value = value
}

fun setTimestamp(value: Instant) {
    _timestamp.value = value
}

override suspend fun onNavigatedToAsync() {
    // Implementation not required for this use case
}

override suspend fun onNavigatedFromAsync() {
    // Implementation not required for this use case
}
}
class SettingsViewModel(
errorDisplayService: IErrorDisplayService? = null
) : BaseViewModel(null, errorDisplayService) {
// Setting ViewModels
private val _hemisphere = MutableStateFlow<SettingViewModel?>(null)
val hemisphere: StateFlow<SettingViewModel?> = _hemisphere.asStateFlow()

private val _timeFormat = MutableStateFlow<SettingViewModel?>(null)
val timeFormat: StateFlow<SettingViewModel?> = _timeFormat.asStateFlow()

private val _dateFormat = MutableStateFlow<SettingViewModel?>(null)
val dateFormat: StateFlow<SettingViewModel?> = _dateFormat.asStateFlow()

private val _email = MutableStateFlow<SettingViewModel?>(null)
val email: StateFlow<SettingViewModel?> = _email.asStateFlow()

private val _windDirection = MutableStateFlow<SettingViewModel?>(null)
val windDirection: StateFlow<SettingViewModel?> = _windDirection.asStateFlow()

private val _temperatureFormat = MutableStateFlow<SettingViewModel?>(null)
val temperatureFormat: StateFlow<SettingViewModel?> = _temperatureFormat.asStateFlow()

private val _subscription = MutableStateFlow<SettingViewModel?>(null)
val subscription: StateFlow<SettingViewModel?> = _subscription.asStateFlow()

private val _addLocationViewed = MutableStateFlow<SettingViewModel?>(null)
val addLocationViewed: StateFlow<SettingViewModel?> = _addLocationViewed.asStateFlow()

private val _listLocationsViewed = MutableStateFlow<SettingViewModel?>(null)
val listLocationsViewed: StateFlow<SettingViewModel?> = _listLocationsViewed.asStateFlow()

private val _editLocationViewed = MutableStateFlow<SettingViewModel?>(null)
val editLocationViewed: StateFlow<SettingViewModel?> = _editLocationViewed.asStateFlow()

private val _weatherViewed = MutableStateFlow<SettingViewModel?>(null)
val weatherViewed: StateFlow<SettingViewModel?> = _weatherViewed.asStateFlow()

private val _settingsViewed = MutableStateFlow<SettingViewModel?>(null)
val settingsViewed: StateFlow<SettingViewModel?> = _settingsViewed.asStateFlow()

private val _sunLocationViewed = MutableStateFlow<SettingViewModel?>(null)
val sunLocationViewed: StateFlow<SettingViewModel?> = _sunLocationViewed.asStateFlow()

private val _sunCalculationViewed = MutableStateFlow<SettingViewModel?>(null)
val sunCalculationViewed: StateFlow<SettingViewModel?> = _sunCalculationViewed.asStateFlow()

private val _exposureCalculationViewed = MutableStateFlow<SettingViewModel?>(null)
val exposureCalculationViewed: StateFlow<SettingViewModel?> = _exposureCalculationViewed.asStateFlow()

private val _sceneEvaluationViewed = MutableStateFlow<SettingViewModel?>(null)
val sceneEvaluationViewed: StateFlow<SettingViewModel?> = _sceneEvaluationViewed.asStateFlow()

private val _subscriptionExpiration = MutableStateFlow<SettingViewModel?>(null)
val subscriptionExpiration: StateFlow<SettingViewModel?> = _subscriptionExpiration.asStateFlow()

// Boolean toggles
private val _hemisphereNorth = MutableStateFlow(true)
val hemisphereNorth: StateFlow<Boolean> = _hemisphereNorth.asStateFlow()

private val _timeFormatToggle = MutableStateFlow(true)
val timeFormatToggle: StateFlow<Boolean> = _timeFormatToggle.asStateFlow()

private val _dateFormatToggle = MutableStateFlow(true)
val dateFormatToggle: StateFlow<Boolean> = _dateFormatToggle.asStateFlow()

private val _windDirectionBoolean = MutableStateFlow(true)
val windDirectionBoolean: StateFlow<Boolean> = _windDirectionBoolean.asStateFlow()

private val _temperatureFormatToggle = MutableStateFlow(true)
val temperatureFormatToggle: StateFlow<Boolean> = _temperatureFormatToggle.asStateFlow()

private val _adSupportBoolean = MutableStateFlow(false)
val adSupportBoolean: StateFlow<Boolean> = _adSupportBoolean.asStateFlow()

// Performance optimization: Property change batching
private val batchLock = Any()
private val pendingPropertyChanges = mutableMapOf<String, Any>()
private var isBatchingPropertyChanges = false

init {
    initializeSettings()
}

fun setHemisphere(value: SettingViewModel?) {
    _hemisphere.value = value
}

fun setTimeFormat(value: SettingViewModel?) {
    _timeFormat.value = value
}

fun setDateFormat(value: SettingViewModel?) {
    _dateFormat.value = value
}

fun setEmail(value: SettingViewModel?) {
    _email.value = value
}

fun setWindDirection(value: SettingViewModel?) {
    _windDirection.value = value
}

fun setTemperatureFormat(value: SettingViewModel?) {
    _temperatureFormat.value = value
}

fun setSubscription(value: SettingViewModel?) {
    _subscription.value = value
}

fun setAddLocationViewed(value: SettingViewModel?) {
    _addLocationViewed.value = value
}

fun setListLocationsViewed(value: SettingViewModel?) {
    _listLocationsViewed.value = value
}

fun setEditLocationViewed(value: SettingViewModel?) {
    _editLocationViewed.value = value
}

fun setWeatherViewed(value: SettingViewModel?) {
    _weatherViewed.value = value
}

fun setSettingsViewed(value: SettingViewModel?) {
    _settingsViewed.value = value
}

fun setSunLocationViewed(value: SettingViewModel?) {
    _sunLocationViewed.value = value
}

fun setSunCalculationViewed(value: SettingViewModel?) {
    _sunCalculationViewed.value = value
}

fun setExposureCalculationViewed(value: SettingViewModel?) {
    _exposureCalculationViewed.value = value
}

fun setSceneEvaluationViewed(value: SettingViewModel?) {
    _sceneEvaluationViewed.value = value
}

fun setSubscriptionExpiration(value: SettingViewModel?) {
    _subscriptionExpiration.value = value
}

fun setHemisphereNorth(value: Boolean) {
    _hemisphereNorth.value = value
}

fun setTimeFormatToggle(value: Boolean) {
    _timeFormatToggle.value = value
}

fun setDateFormatToggle(value: Boolean) {
    _dateFormatToggle.value = value
}

fun setWindDirectionBoolean(value: Boolean) {
    _windDirectionBoolean.value = value
}

fun setTemperatureFormatToggle(value: Boolean) {
    _temperatureFormatToggle.value = value
}

fun setAdSupportBoolean(value: Boolean) {
    _adSupportBoolean.value = value
}

/**
 * PERFORMANCE OPTIMIZATION: Initialize all settings in one batch operation
 */
private fun initializeSettings() {
    viewModelScope.launch {
        try {
            beginPropertyChangeBatch()

            _hemisphere.value = SettingViewModel().apply {
                setKey("Hemisphere")
                setValue("North")
            }
            _timeFormat.value = SettingViewModel().apply {
                setKey("TimeFormat")
                setValue("12")
            }
            _dateFormat.value = SettingViewModel().apply {
                setKey("DateFormat")
                setValue("MM/dd/yyyy")
            }
            _email.value = SettingViewModel().apply {
                setKey("Email")
                setValue("")
            }
            _windDirection.value = SettingViewModel().apply {
                setKey("WindDirection")
                setValue("Degrees")
            }
            _temperatureFormat.value = SettingViewModel().apply {
                setKey("TemperatureFormat")
                setValue("Fahrenheit")
            }
            _subscription.value = SettingViewModel().apply {
                setKey("Subscription")
                setValue("Free")
            }

            _addLocationViewed.value = SettingViewModel().apply {
                setKey("AddLocationViewed")
                setValue("false")
            }
            _listLocationsViewed.value = SettingViewModel().apply {
                setKey("ListLocationsViewed")
                setValue("false")
            }
            _editLocationViewed.value = SettingViewModel().apply {
                setKey("EditLocationViewed")
                setValue("false")
            }
            _weatherViewed.value = SettingViewModel().apply {
                setKey("WeatherViewed")
                setValue("false")
            }
            _settingsViewed.value = SettingViewModel().apply {
                setKey("SettingsViewed")
                setValue("false")
            }
            _sunLocationViewed.value = SettingViewModel().apply {
                setKey("SunLocationViewed")
                setValue("false")
            }
            _sunCalculationViewed.value = SettingViewModel().apply {
                setKey("SunCalculationViewed")
                setValue("false")
            }
            _exposureCalculationViewed.value = SettingViewModel().apply {
                setKey("ExposureCalculationViewed")
                setValue("false")
            }
            _sceneEvaluationViewed.value = SettingViewModel().apply {
                setKey("SceneEvaluationViewed")
                setValue("false")
            }
            _subscriptionExpiration.value = SettingViewModel().apply {
                setKey("SubscriptionExpiration")
                setValue(Clock.System.now().epochSeconds.toString())
            }

            endPropertyChangeBatch()
        } catch (ex: Exception) {
            onSystemError("Error initializing settings: ${ex.message}")
            endPropertyChangeBatch()
        }
    }
}

/**
 * PERFORMANCE OPTIMIZATION: Update multiple settings in one batch
 */
fun updateSettingsBatch(settings: Map<String, String>) {
    viewModelScope.launch {
        try {
            beginPropertyChangeBatch()

            settings.forEach { (key, value) ->
                updateSettingByKey(key, value)
            }

            endPropertyChangeBatch()
        } catch (ex: Exception) {
            onSystemError("Error updating settings batch: ${ex.message}")
            endPropertyChangeBatch()
        }
    }
}

/**
 * PERFORMANCE OPTIMIZATION: Update individual setting without triggering immediate UI updates
 */
private fun updateSettingByKey(key: String, value: String) {
    val settingToUpdate = when (key) {
        "Hemisphere" -> _hemisphere.value
        "TimeFormat" -> _timeFormat.value
        "DateFormat" -> _dateFormat.value
        "Email" -> _email.value
        "WindDirection" -> _windDirection.value
        "TemperatureFormat" -> _temperatureFormat.value
        "Subscription" -> _subscription.value
        "AddLocationViewed" -> _addLocationViewed.value
        "ListLocationsViewed" -> _listLocationsViewed.value
        "EditLocationViewed" -> _editLocationViewed.value
        "WeatherViewed" -> _weatherViewed.value
        "SettingsViewed" -> _settingsViewed.value
        "SunLocationViewed" -> _sunLocationViewed.value
        "SunCalculationViewed" -> _sunCalculationViewed.value
        "ExposureCalculationViewed" -> _exposureCalculationViewed.value
        "SceneEvaluationViewed" -> _sceneEvaluationViewed.value
        "SubscriptionExpiration" -> _subscriptionExpiration.value
        else -> null
    }

    settingToUpdate?.setValue(value)

    // Update boolean toggles based on values
    when (key) {
        "Hemisphere" -> setHemisphereNorth(value.equals("North", ignoreCase = true))
        "TimeFormat" -> setTimeFormatToggle(value.equals("12", ignoreCase = true))
        "DateFormat" -> setDateFormatToggle(value.equals("MM/dd/yyyy", ignoreCase = true))
        "WindDirection" -> setWindDirectionBoolean(value.equals("Degrees", ignoreCase = true))
        "TemperatureFormat" -> setTemperatureFormatToggle(value.equals("Fahrenheit", ignoreCase = true))
    }
}

/**
 * PERFORMANCE OPTIMIZATION: Reset all settings to defaults in one batch
 */
fun resetToDefaults() {
    viewModelScope.launch {
        try {
            beginPropertyChangeBatch()

            val defaultSettings = mapOf(
                "Hemisphere" to "North",
                "TimeFormat" to "12",
                "DateFormat" to "MM/dd/yyyy",
                "Email" to "",
                "WindDirection" to "Degrees",
                "TemperatureFormat" to "Fahrenheit",
                "Subscription" to "Free",
                "AddLocationViewed" to "false",
                "ListLocationsViewed" to "false",
                "EditLocationViewed" to "false",
                "WeatherViewed" to "false",
                "SettingsViewed" to "false",
                "SunLocationViewed" to "false",
                "SunCalculationViewed" to "false",
                "ExposureCalculationViewed" to "false",
                "SceneEvaluationViewed" to "false",
                "SubscriptionExpiration" to Clock.System.now().epochSeconds.toString()
            )

            defaultSettings.forEach { (key, value) ->
                updateSettingByKey(key, value)
            }

            endPropertyChangeBatch()
        } catch (ex: Exception) {
            onSystemError("Error resetting settings: ${ex.message}")
            endPropertyChangeBatch()
        }
    }
}

/**
 * Get all settings as a map for serialization/persistence
 */
fun getAllSettings(): Map<String, String> {
    return mapOf(
        "Hemisphere" to (_hemisphere.value?.value?.value ?: "North"),
        "TimeFormat" to (_timeFormat.value?.value?.value ?: "12"),
        "DateFormat" to (_dateFormat.value?.value?.value ?: "MM/dd/yyyy"),
        "Email" to (_email.value?.value?.value ?: ""),
        "WindDirection" to (_windDirection.value?.value?.value ?: "Degrees"),
        "TemperatureFormat" to (_temperatureFormat.value?.value?.value ?: "Fahrenheit"),
        "Subscription" to (_subscription.value?.value?.value ?: "Free"),
        "AddLocationViewed" to (_addLocationViewed.value?.value?.value ?: "false"),
        "ListLocationsViewed" to (_listLocationsViewed.value?.value?.value ?: "false"),
        "EditLocationViewed" to (_editLocationViewed.value?.value?.value ?: "false"),
        "WeatherViewed" to (_weatherViewed.value?.value?.value ?: "false"),
        "SettingsViewed" to (_settingsViewed.value?.value?.value ?: "false"),
        "SunLocationViewed" to (_sunLocationViewed.value?.value?.value ?: "false"),
        "SunCalculationViewed" to (_sunCalculationViewed.value?.value?.value ?: "false"),
        "ExposureCalculationViewed" to (_exposureCalculationViewed.value?.value?.value ?: "false"),
        "SceneEvaluationViewed" to (_sceneEvaluationViewed.value?.value?.value ?: "false"),
        "SubscriptionExpiration" to (_subscriptionExpiration.value?.value?.value ?: Clock.System.now().epochSeconds.toString())
    )
}

/**
 * PERFORMANCE OPTIMIZATION: Batch property change notifications to reduce UI updates
 */
private fun beginPropertyChangeBatch() {
    synchronized(batchLock) {
        isBatchingPropertyChanges = true
        pendingPropertyChanges.clear()
    }
}

/**
 * PERFORMANCE OPTIMIZATION: End batching and process all pending notifications
 */
private fun endPropertyChangeBatch() {
    val pendingChanges: Map<String, Any>

    synchronized(batchLock) {
        if (!isBatchingPropertyChanges) return

        pendingChanges = pendingPropertyChanges.toMap()
        pendingPropertyChanges.clear()
        isBatchingPropertyChanges = false
    }

    // StateFlow emissions are already thread-safe and handled by coroutines
    // No additional UI thread marshalling needed
}
}