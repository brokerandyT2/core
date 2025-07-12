// core\src\commonMain\kotlin\com\x3squaredcircles\core\presentation\LocationViewModel.kt
package com.x3squaredcircles.core.presentation
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IMediator
import com.x3squaredcircles.core.commands.SaveLocationCommand
import com.x3squaredcircles.core.queries.GetLocationByIdQuery
import com.x3squaredcircles.core.services.IMediaService
import com.x3squaredcircles.core.services.IGeolocationService
import com.x3squaredcircles.core.dtos.LocationDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.round
class LocationViewModel(
private val mediator: IMediator,
private val mediaService: IMediaService,
private val geolocationService: IGeolocationService,
errorDisplayService: IErrorDisplayService? = null
) : BaseViewModel(null, errorDisplayService), INavigationAware {
    private val _id = MutableStateFlow(0)
val id: StateFlow<Int> = _id.asStateFlow()

private val _title = MutableStateFlow("")
val title: StateFlow<String> = _title.asStateFlow()

private val _description = MutableStateFlow("")
val description: StateFlow<String> = _description.asStateFlow()

private val _latitude = MutableStateFlow(0.0)
val latitude: StateFlow<Double> = _latitude.asStateFlow()

private val _longitude = MutableStateFlow(0.0)
val longitude: StateFlow<Double> = _longitude.asStateFlow()

private val _city = MutableStateFlow("")
val city: StateFlow<String> = _city.asStateFlow()

private val _state = MutableStateFlow("")
val state: StateFlow<String> = _state.asStateFlow()

private val _photo = MutableStateFlow("")
val photo: StateFlow<String> = _photo.asStateFlow()

private val _timestamp = MutableStateFlow(System.currentTimeMillis())
val timestamp: StateFlow<Long> = _timestamp.asStateFlow()

private val _dateFormat = MutableStateFlow("g")
val dateFormat: StateFlow<String> = _dateFormat.asStateFlow()

private val _isNewLocation = MutableStateFlow(true)
val isNewLocation: StateFlow<Boolean> = _isNewLocation.asStateFlow()

private val _isLocationTracking = MutableStateFlow(false)
val isLocationTracking: StateFlow<Boolean> = _isLocationTracking.asStateFlow()

fun setId(value: Int) { _id.value = value }
fun setTitle(value: String) { _title.value = value }
fun setDescription(value: String) { _description.value = value }
fun setLatitude(value: Double) { _latitude.value = value }
fun setLongitude(value: Double) { _longitude.value = value }
fun setCity(value: String) { _city.value = value }
fun setState(value: String) { _state.value = value }
fun setPhoto(value: String) { _photo.value = value }
fun setIsNewLocation(value: Boolean) { _isNewLocation.value = value }

suspend fun saveAsync() {
    try {
        setIsBusy(true)
        clearErrors()

        val command = SaveLocationCommand(
            id = if (_id.value > 0) _id.value else null,
            title = _title.value,
            description = _description.value,
            latitude = _latitude.value,
            longitude = _longitude.value,
            city = _city.value,
            state = _state.value,
            photoPath = _photo.value
        )

        val result: Result<LocationDto> = mediator.send(command)
        when (result) {
            is Result.Success -> {
                val locationDto = result.data 
                _id.value = locationDto?.id!!
                _timestamp.value = locationDto.timestamp
                _isNewLocation.value = false
            }
            is Result.Failure<*> -> {
                onSystemError(result.errorMessage)
            }
        }
    } catch (e: Exception) {
        onSystemError("Error saving location: ${e.message}")
    } finally {
        setIsBusy(false)
    }
}

suspend fun loadLocationAsync(locationId: Int) {
    try {
        setIsBusy(true)
        clearErrors()

        val query = GetLocationByIdQuery(locationId)
        val result = mediator.send(query) as Result<*>

        when (result) {
            is Result.Success<*> -> {
                val locationData = result.data as com.x3squaredcircles.core.dtos.LocationDto
                _id.value = locationData.id
                _title.value = locationData.title
                _description.value = locationData.description
                _latitude.value = locationData.latitude
                _longitude.value = locationData.longitude
                _city.value = locationData.city
                _state.value = locationData.state
                _photo.value = locationData.photoPath ?: ""
                _timestamp.value = locationData.timestamp
                _isNewLocation.value = false
            }
            is Result.Failure<*> -> {
                onSystemError(result.errorMessage)
            }
        }
    } catch (e: Exception) {
        onSystemError("Error loading location: ${e.message}")
    } finally {
        setIsBusy(false)
    }
}

suspend fun takePhotoAsync() {
    try {
        setIsBusy(true)
        clearErrors()

        val supportResult = mediaService.isCaptureSupported()
        when (supportResult) {
            is Result.Success<*> -> {
                val isSupported = supportResult.data as Boolean
                if (!isSupported) {
                    val pickResult = mediaService.pickPhotoAsync()
                    when (pickResult) {
                        is Result.Success<*> -> {
                            _photo.value = pickResult.data as String
                        }
                        is Result.Failure<*> -> {
                            setValidationError(pickResult.errorMessage)
                        }
                    }
                    return
                }
            }
            is Result.Failure<*> -> {
                setValidationError(supportResult.errorMessage)
                return
            }
        }

        val result = mediaService.capturePhotoAsync()
        when (result) {
            is Result.Success<*> -> {
                _photo.value = result.data as String
            }
            is Result.Failure<*> -> {
                setValidationError(result.errorMessage)
            }
        }
    } catch (e: Exception) {
        onSystemError("Error taking photo: ${e.message}")
    } finally {
        setIsBusy(false)
    }
}

suspend fun startLocationTrackingAsync() {
    try {
        if (_isLocationTracking.value) return

        setIsBusy(true)

        val permissionResult = geolocationService.requestPermissionsAsync()
        when (permissionResult) {
            is Result.Success<*> -> {
                val hasPermission = permissionResult.data as Boolean
                if (!hasPermission) {
                    setValidationError("Location permission is required")
                    return
                }
            }
            is Result.Failure<*> -> {
                setValidationError("Location permission is required")
                return
            }
        }

        val result = geolocationService.startTrackingAsync()
        when (result) {
            is Result.Success<*> -> {
                val isTracking = result.data as Boolean
                if (isTracking) {
                    _isLocationTracking.value = true

                    val locationResult = geolocationService.getCurrentLocationAsync()
                    when (locationResult) {
                        is Result.Success<*> -> {
                            val location = locationResult.data as com.x3squaredcircles.core.dtos.GeolocationDto
                            _latitude.value = round(location.latitude * 1000000.0) / 1000000.0
                            _longitude.value = round(location.longitude * 1000000.0) / 1000000.0
                        }
                        is Result.Failure<*> -> {
                            onSystemError(locationResult.errorMessage)
                        }
                    }
                }
            }
            is Result.Failure<*> -> {
                onSystemError(result.errorMessage)
            }
        }
    } catch (e: Exception) {
        onSystemError("Error starting location tracking: ${e.message}")
    } finally {
        setIsBusy(false)
    }
}

suspend fun stopLocationTrackingAsync() {
    try {
        if (!_isLocationTracking.value) return

        val result = geolocationService.stopTrackingAsync()
        when (result) {
            is Result.Success<*> -> {
                _isLocationTracking.value = false
            }
            is Result.Failure<*> -> {
                onSystemError(result.errorMessage)
            }
        }
    } catch (e: Exception) {
        onSystemError("Error stopping location tracking: ${e.message}")
    }
}

override suspend fun onNavigatedToAsync() {
    startLocationTrackingAsync()
}

override suspend fun onNavigatedFromAsync() {
    stopLocationTrackingAsync()
}
}