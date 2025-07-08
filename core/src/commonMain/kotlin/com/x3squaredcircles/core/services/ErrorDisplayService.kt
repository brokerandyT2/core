// core/src/commonMain/kotlin/com/x3squaredcircles/core/services/ErrorDisplayService.kt
package com.x3squaredcircles.core.services

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import com.x3squaredcircles.core.events.DomainErrorEvent
data class ErrorDisplayEventArgs(
val errors: List<DomainErrorEvent>,
val displayMessage: String
) {
val isSingleError: Boolean get() = errors.size == 1
}
interface IErrorDisplayService {
val errorsReady: SharedFlow<ErrorDisplayEventArgs>
suspend fun triggerErrorDisplayAsync(errors: List<DomainErrorEvent>)
}
class ErrorDisplayService : IErrorDisplayService {
companion object {
    private const val ERROR_AGGREGATION_WINDOW_MS = 500L
    private const val MAX_ERRORS_PER_BATCH = 10
    private const val CHANNEL_CAPACITY = 1000
}

private val errorChannel = Channel<DomainErrorEvent>(CHANNEL_CAPACITY)
private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

private val _errorsReady = MutableSharedFlow<ErrorDisplayEventArgs>()
override val errorsReady: SharedFlow<ErrorDisplayEventArgs> = _errorsReady.asSharedFlow()

init {
    // Start background error processing
    scope.launch {
        processErrorsAsync()
    }
}

suspend fun publishErrorAsync(errorEvent: DomainErrorEvent) {
    // Try to send without blocking
    if (!errorChannel.trySend(errorEvent).isSuccess) {
        // Channel is full - try with timeout for important errors
        withTimeoutOrNull(50) {
            errorChannel.send(errorEvent)
        }
    }
}

override suspend fun triggerErrorDisplayAsync(errors: List<DomainErrorEvent>) {
    if (errors.isEmpty()) return
    processErrorBatch(errors)
}

private suspend fun processErrorsAsync() {
    val errorBatch = mutableListOf<DomainErrorEvent>()
    
    while (!scope.coroutineContext[Job]?.isCancelled!!) {
        try {
            errorBatch.clear()
            
            // Collect errors for aggregation window
            val deadline = System.currentTimeMillis() + ERROR_AGGREGATION_WINDOW_MS
            
            while (System.currentTimeMillis() < deadline && errorBatch.size < MAX_ERRORS_PER_BATCH) {
                val remainingTime = deadline - System.currentTimeMillis()
                if (remainingTime <= 0) break
                
                val error = withTimeoutOrNull(remainingTime) {
                    errorChannel.receive()
                }
                
                if (error != null) {
                    errorBatch.add(error)
                } else {
                    break
                }
            }
            
            // Process batch if we have errors
            if (errorBatch.isNotEmpty()) {
                processErrorBatch(errorBatch.toList())
            }
            
            // Wait for next processing cycle if no errors were found
            if (errorBatch.isEmpty()) {
                delay(ERROR_AGGREGATION_WINDOW_MS)
            }
        } catch (e: Exception) {
            // Swallow exceptions to keep background processor running
            delay(ERROR_AGGREGATION_WINDOW_MS)
        }
    }
}

private suspend fun processErrorBatch(errors: List<DomainErrorEvent>) {
    try {
        val displayMessage = generateDisplayMessage(errors)
        val eventArgs = ErrorDisplayEventArgs(errors, displayMessage)
        
        _errorsReady.emit(eventArgs)
    } catch (e: Exception) {
        // Swallow exceptions to keep processor running
    }
}

private fun generateDisplayMessage(errors: List<DomainErrorEvent>): String {
    return when {
        errors.isEmpty() -> "No errors"
        errors.size == 1 -> {
            val error = errors.first()
            when (error.resourceKey) {
                "Location_Error_NotFound" -> "Location not found"
                "Location_Error_SaveFailed" -> "Failed to save location"
                "Weather_Error_UpdateFailed" -> "Weather update failed"
                "Weather_Error_ApiUnavailable" -> "Weather service unavailable"
                "Weather_Error_NetworkTimeout" -> "Weather service timeout"
                "Validation_Error_Single" -> "Validation error: Invalid input"
                "Setting_Error_DuplicateKey" -> "Setting already exists"
                "TipType_Error_DuplicateName" -> "Tip type already exists"
                else -> "An error occurred, please try again"
            }
        }
        else -> "Multiple errors occurred (${errors.size} errors)"
    }
}

fun dispose() {
    scope.cancel()
    errorChannel.close()
}
}