// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/behaviors/LoggingBehavior.kt
package com.x3squaredcircles.core.infrastructure.behaviors

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IMediator
import com.x3squaredcircles.core.mediator.IRequest
import kotlinx.coroutines.withTimeoutOrNull

class LoggingBehavior<TRequest : IRequest<TResponse>, TResponse>(
    private val mediator: IMediator
) : IPipelineBehavior<TRequest, TResponse> where TResponse : Result<*> {
    
    companion object {
        private const val MAX_SERIALIZATION_LENGTH = 2048
        private const val SLOW_REQUEST_THRESHOLD_MS = 500L
        private const val SERIALIZATION_TIMEOUT_MS = 100L
    }
    
    override suspend fun handle(request: TRequest, next: suspend () -> TResponse): TResponse {
        val requestName = request::class.simpleName ?: "Unknown"
        val requestGuid = generateRequestId()
        val startTime = System.currentTimeMillis()
        
        // Log request start
        logInfo("Request started $requestGuid $requestName")
        
        // Log request details if debug enabled
        if (isDebugEnabled()) {
            val serializedRequest = serializeRequest(request)
            logDebug("Request details $requestGuid $requestName: $serializedRequest")
        }
        
        return try {
            val response = next()
            val elapsedMs = System.currentTimeMillis() - startTime
            
            // Log successful completion
            logInfo("Request completed successfully $requestGuid $requestName in ${elapsedMs}ms")
            
            // Check for slow operations
            if (elapsedMs > SLOW_REQUEST_THRESHOLD_MS) {
                logWarning("Slow operation detected $requestGuid $requestName took ${elapsedMs}ms")
            }
            
            // Check if response indicates failure
            if (!response.isSuccess) {
                logWarning("Request completed with failure $requestGuid $requestName")
                
                if (isDebugEnabled() && response is Result.Failure<*>) {
                    logDebug("Request error $requestGuid: ${response.errorMessage}")
                }
            }
            
            response
        } catch (ex: Exception) {
            val elapsedMs = System.currentTimeMillis() - startTime
            
            logError("Request failed $requestGuid $requestName after ${elapsedMs}ms", ex)
            
            // Publish error event for unexpected exceptions (fire-and-forget)
            try {
                // Note: In a full implementation, you'd publish a ValidationErrorEvent here
                // For now, we'll just re-throw the exception
            } catch (publishEx: Exception) {
                logError("Failed to publish error event for request $requestGuid", publishEx)
            }
            
            throw ex
        }
    }
    
    private fun generateRequestId(): String {
        return "${System.currentTimeMillis()}-${(1000..9999).random()}"
    }
    
    private suspend fun serializeRequest(request: TRequest): String {
        return try {
            withTimeoutOrNull(SERIALIZATION_TIMEOUT_MS) {
                val serialized = request.toString()
                if (serialized.length > MAX_SERIALIZATION_LENGTH) {
                    "${serialized.take(MAX_SERIALIZATION_LENGTH)}... (truncated)"
                } else {
                    serialized
                }
            } ?: "Serialization timeout"
        } catch (ex: Exception) {
            "Serialization failed: ${ex.message}"
        }
    }
    
    private fun isDebugEnabled(): Boolean {
        // Simple debug check - in a full implementation, this would check actual log levels
        return false // Set to true for debug logging
    }
    
    private fun logInfo(message: String) {
        println("INFO: $message")
    }
    
    private fun logDebug(message: String) {
        if (isDebugEnabled()) {
            println("DEBUG: $message")
        }
    }
    
    private fun logWarning(message: String) {
        println("WARN: $message")
    }
    
    private fun logError(message: String, exception: Exception? = null) {
        println("ERROR: $message")
        exception?.let { 
            println("Exception: ${it.message}")
            it.printStackTrace()
        }
    }
}