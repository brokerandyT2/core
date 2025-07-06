// core\src\commonMain\kotlin\com\x3squaredcircles\core\presentation\LocationsViewModel.kt
package com.x3squaredcircles.core.presentation
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IMediator
import com.x3squaredcircles.core.queries.GetLocationsQuery
import com.x3squaredcircles.core.models.PagedList
import com.x3squaredcircles.core.dtos.LocationListDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class LocationsViewModel(
private val mediator: IMediator,
errorDisplayService: IErrorDisplayService? = null
) : BaseViewModel(null, errorDisplayService), INavigationAware 
{
private val _locations = MutableStateFlow<List<LocationListItemViewModel>>(emptyList())
val locations: StateFlow<List<LocationListItemViewModel>> = _locations.asStateFlow()

suspend fun loadLocationsAsync() {
    try {
        setIsBusy(true)
        clearErrors()

        val query = GetLocationsQuery(
            pageNumber = 1,
            pageSize = 100,
            includeDeleted = false
        )

        val result: Result<PagedList<LocationListDto>> = mediator.send(query)
        when (result) {
            is Result.Success -> {
                val locationViewModels = result.data.items.map { locationDto ->
                    LocationListItemViewModel(
                        id = locationDto.id,
                        title = locationDto.title,
                        latitude = locationDto.latitude,
                        longitude = locationDto.longitude,
                        photo = locationDto.photoPath ?: "",
                        isDeleted = locationDto.isDeleted
                    )
                }
                _locations.value = locationViewModels
            }
            is Result.Failure -> {
                onSystemError(result.errorMessage)
            }
        }
    } catch (e: Exception) {
        onSystemError("Error loading locations: ${e.message}")
    } finally {
        setIsBusy(false)
    }
}

override suspend fun onNavigatedToAsync() {
    loadLocationsAsync()
}

override suspend fun onNavigatedFromAsync() {
    // No cleanup needed
}
}
data class LocationListItemViewModel(
val id: Int,
val title: String,
val latitude: Double,
val longitude: Double,
val photo: String,
val isDeleted: Boolean
) {
val formattedCoordinates: String
get() = String.format("%.6f, %.6f", latitude, longitude)
}