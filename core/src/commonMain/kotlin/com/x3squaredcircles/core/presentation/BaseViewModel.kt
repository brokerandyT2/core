// core\src\commonMain\kotlin\com\x3squaredcircles\core\presentation\BaseViewModel.kt
package com.x3squaredcircles.core.presentation

import com.x3squaredcircles.core.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
//import kotlin.native.concurrent.ThreadLocal
import java.lang.ref.WeakReference

// Event interfaces (simplified versions based on C# usage)
interface IEventBus {
    suspend fun publishAsync(event: Any)
}

interface IErrorDisplayService {
    val errorsReady: kotlinx.coroutines.flow.Flow<ErrorDisplayEventArgs>
}

data class ErrorDisplayEventArgs(
    val displayMessage: String
)

data class OperationErrorEventArgs(
    val message: String
)

// Command interface to match C# IAsyncRelayCommand pattern
interface IAsyncCommand {
    val canExecute: StateFlow<Boolean>
    suspend fun executeAsync(parameter: Any? = null)
}

abstract class BaseViewModel(
    private val eventBus: IEventBus? = null,
    private val errorDisplayService: IErrorDisplayService? = null
) {
    
    // Create own coroutine scope for cross-platform compatibility
    protected val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    
    // Thread-safe boolean flags
    @Volatile
    private var _isBusy = false
    @Volatile
    private var _isError = false
    @Volatile
    private var _hasActiveErrors = false
    @Volatile
    private var _isDisposed = false
    
    // StateFlow for UI state
    private val _isBusyFlow = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusyFlow.asStateFlow()
    
    private val _isErrorFlow = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isErrorFlow.asStateFlow()
    
    private val _errorMessageFlow = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessageFlow.asStateFlow()
    
    private val _hasActiveErrorsFlow = MutableStateFlow(false)
    val hasActiveErrors: StateFlow<Boolean> = _hasActiveErrorsFlow.asStateFlow()
    
    // Channel for one-time events
    private val _errorOccurredChannel = Channel<OperationErrorEventArgs>(Channel.BUFFERED)
    val errorOccurred = _errorOccurredChannel.receiveAsFlow()
    
    // Command tracking with weak references
    private var lastCommandRef: WeakReference<IAsyncCommand>? = null
    private var lastCommandParameter: Any? = null
    
    // Object pool for event args to reduce allocations
    private val eventArgsPool = mutableMapOf<String, OperationErrorEventArgs>()
    
    init {
        // Subscribe to error display service
        subscribeToErrorDisplayService()
    }
    
    // Property setters with StateFlow updates
    fun setIsBusy(value: Boolean) {
        if (_isBusy != value) {
            _isBusy = value
            _isBusyFlow.value = value
        }
    }
    
    fun setIsError(value: Boolean) {
        if (_isError != value) {
            _isError = value
            _isErrorFlow.value = value
            if (value && _errorMessageFlow.value.isNotEmpty()) {
                // ViewModel validation errors stay in UI - no event needed
            }
        }
    }
    
    protected fun setErrorMessage(value: String) {
        _errorMessageFlow.value = value
    }
    
    protected fun setHasActiveErrors(value: Boolean) {
        if (_hasActiveErrors != value) {
            _hasActiveErrors = value
            _hasActiveErrorsFlow.value = value
        }
    }
    
    // Command tracking
    protected fun trackCommand(command: IAsyncCommand, parameter: Any? = null) {
        if (_isDisposed) return
        
        lastCommandRef = WeakReference(command)
        lastCommandParameter = parameter
    }
    
    // Execute and track command
    suspend fun executeAndTrackAsync(command: IAsyncCommand, parameter: Any? = null) {
        if (_isDisposed) return
        
        trackCommand(command, parameter)
        command.executeAsync(parameter)
    }
    
    // Retry last command
    suspend fun retryLastCommandAsync() {
        if (_isDisposed) return
        
        lastCommandRef?.get()?.let { command ->
            if (command.canExecute.value) {
                command.executeAsync(lastCommandParameter)
            }
        }
    }
    
    // System error handling with event pooling
    open fun onSystemError(message: String) {
        if (_isDisposed) return
        
        viewModelScope.launch {
            // Reuse event args object when possible
            val args = eventArgsPool.getOrPut(message) { OperationErrorEventArgs(message) }
            _errorOccurredChannel.trySend(args)
        }
    }
    
    // Validation error setter
    protected fun setValidationError(message: String) {
        setErrorMessage(message)
        setIsError(true)
    }
    
    // Clear errors with batched updates
    protected fun clearErrors() {
        val wasError = _isError
        val hadActiveErrors = _hasActiveErrors
        val hadErrorMessage = _errorMessageFlow.value.isNotEmpty()
        
        if (wasError || hadActiveErrors || hadErrorMessage) {
            _isError = false
            setErrorMessage("")
            _hasActiveErrors = false
            
            // Update StateFlows
            _isErrorFlow.value = false
            _errorMessageFlow.value = ""
            _hasActiveErrorsFlow.value = false
        }
    }
    
    // Subscribe to error display service using coroutines
    private fun subscribeToErrorDisplayService() {
        errorDisplayService?.let { service ->
            viewModelScope.launch {
                service.errorsReady.collect { errorArgs ->
                    if (_isDisposed) return@collect
                    
                    setHasActiveErrors(true)
                    try {
                        onSystemError(errorArgs.displayMessage)
                    } finally {
                        setHasActiveErrors(false)
                    }
                }
            }
        }
    }
    
    open fun dispose() {
        if (!_isDisposed) {
            _isDisposed = true
            
            // Clear weak references
            lastCommandRef = null
            lastCommandParameter = null
            
            // Clear event args pool
            eventArgsPool.clear()
            
            // Close channels
            _errorOccurredChannel.close()
            
            // Cancel coroutine scope
            viewModelScope.cancel()
        }
    }
}