// core\src\commonMain\kotlin\com\x3squaredcircles\core\presentation\TipsViewModel.kt
package com.x3squaredcircles.core.presentation
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IMediator
import com.x3squaredcircles.core.queries.GetAllTipTypesQuery
import com.x3squaredcircles.core.queries.GetTipsByTypeQuery
import com.x3squaredcircles.core.dtos.TipTypeDto
import com.x3squaredcircles.core.dtos.TipDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class TipsViewModel(
private val mediator: IMediator,
errorDisplayService: IErrorDisplayService? = null
) : BaseViewModel(null, errorDisplayService), INavigationAware {
private val _selectedTipTypeId = MutableStateFlow(0)
val selectedTipTypeId: StateFlow<Int> = _selectedTipTypeId.asStateFlow()

private val _selectedTipType = MutableStateFlow<TipTypeItemViewModel?>(null)
val selectedTipType: StateFlow<TipTypeItemViewModel?> = _selectedTipType.asStateFlow()

private val _tips = MutableStateFlow<List<TipItemViewModel>>(emptyList())
val tips: StateFlow<List<TipItemViewModel>> = _tips.asStateFlow()

private val _tipTypes = MutableStateFlow<List<TipTypeItemViewModel>>(emptyList())
val tipTypes: StateFlow<List<TipTypeItemViewModel>> = _tipTypes.asStateFlow()

suspend fun loadTipTypesAsync() {
    try {
        setIsBusy(true)
        clearErrors()

        _tipTypes.value = emptyList()

        val query = GetAllTipTypesQuery()
        val result: Result<List<TipTypeDto>> = mediator.send(query)

        when (result) {
            is Result.Success -> {
                val tipTypeViewModels = result.data?.map { item: TipTypeDto ->
                    TipTypeItemViewModel(
                        id = item.id,
                        name = item.name,
                        i8n = item.i8n
                    )
                }
                _tipTypes.value = tipTypeViewModels!!

                if (tipTypeViewModels.isNotEmpty()) {
                    val firstTipType = tipTypeViewModels.first()
                    _selectedTipType.value = firstTipType
                    _selectedTipTypeId.value = firstTipType.id
                    loadTipsByTypeAsync(firstTipType.id)
                }
            }
            is Result.Failure -> {
                onSystemError(result.errorMessage)
            }
        }
    } catch (e: Exception) {
        onSystemError("Error loading tip types: ${e.message}")
    } finally {
        setIsBusy(false)
    }
}

suspend fun loadTipsByTypeAsync(tipTypeId: Int) {
    try {
        if (tipTypeId <= 0) {
            setValidationError("Please select a valid tip type")
            return
        }

        setIsBusy(true)
        clearErrors()

        _tips.value = emptyList()

        val query = GetTipsByTypeQuery(tipTypeId = tipTypeId)
        val result: Result<List<TipDto>> = mediator.send(query)

        when (result) {
            is Result.Success -> {
                val tipViewModels = result.data?.map { item: TipDto ->
                    TipItemViewModel(
                        id = item.id,
                        tipTypeId = item.tipTypeId,
                        title = item.title,
                        content = item.content,
                        fstop = item.fstop,
                        shutterSpeed = item.shutterSpeed,
                        iso = item.iso,
                        i8n = item.i8n
                    )
                }
                _tips.value = tipViewModels!!
            }
            is Result.Failure -> {
                onSystemError(result.errorMessage)
            }
        }
    } catch (e: Exception) {
        onSystemError("Error loading tips: ${e.message}")
    } finally {
        setIsBusy(false)
    }
}

fun onSelectedTipTypeChanged(tipType: TipTypeItemViewModel?) {
    if (tipType != null) {
        _selectedTipTypeId.value = tipType.id
        viewModelScope.launch {
            loadTipsByTypeAsync(tipType.id)
        }
    }
}

override suspend fun onNavigatedToAsync() {
    loadTipTypesAsync()
}

override suspend fun onNavigatedFromAsync() {
    // No cleanup needed
}
}
data class TipItemViewModel(
val id: Int,
val tipTypeId: Int,
val title: String,
val content: String,
val fstop: String,
val shutterSpeed: String,
val iso: String,
val i8n: String
) {
val hasCameraSettings: Boolean
get() = fstop.isNotEmpty() || shutterSpeed.isNotEmpty() || iso.isNotEmpty()
val cameraSettingsDisplay: String
    get() {
        val parts = mutableListOf<String>()
        if (fstop.isNotEmpty()) parts.add("F: $fstop")
        if (shutterSpeed.isNotEmpty()) parts.add("Shutter: $shutterSpeed")
        if (iso.isNotEmpty()) parts.add("ISO: $iso")
        return parts.joinToString(" ")
    }
}
data class TipTypeItemViewModel(
val id: Int,
val name: String,
val i8n: String
)