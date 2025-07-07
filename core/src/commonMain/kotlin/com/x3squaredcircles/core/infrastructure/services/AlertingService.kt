// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/services/AlertingService.kt
package com.x3squaredcircles.core.infrastructure.services

import com.x3squaredcircles.core.mediator.IMediator
import com.x3squaredcircles.core.mediator.IRequest
import com.x3squaredcircles.core.presentation.IErrorDisplayService
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class AlertEvent(
    val message: String,
    val title: String,
    val alertType: AlertType
) : IRequest<Unit>

enum class AlertType {
    Info,
    Success,
    Warning,
    Error
}

interface IAlertService {
    suspend fun showInfoAlertAsync(message: String, title: String = "Information")
    suspend fun showSuccessAlertAsync(message: String, title: String = "Success")
    suspend fun showWarningAlertAsync(message: String, title: String = "Warning")
    suspend fun showErrorAlertAsync(message: String, title: String = "Error")
}

class AlertingService(
    private val mediator: IMediator
) : IAlertService {
    
    private val mutex = Mutex()
    private var isHandlingAlert = false

    override suspend fun showInfoAlertAsync(message: String, title: String) {
        mutex.withLock {
            if (isHandlingAlert) return@withLock
            
            try {
                isHandlingAlert = true
                val alertEvent = AlertEvent(message, title, AlertType.Info)
                mediator.send(alertEvent)
            } finally {
                isHandlingAlert = false
            }
        }
    }

    override suspend fun showSuccessAlertAsync(message: String, title: String) {
        mutex.withLock {
            if (isHandlingAlert) return@withLock
            
            try {
                isHandlingAlert = true
                val alertEvent = AlertEvent(message, title, AlertType.Success)
                mediator.send(alertEvent)
            } finally {
                isHandlingAlert = false
            }
        }
    }

    override suspend fun showWarningAlertAsync(message: String, title: String) {
        mutex.withLock {
            if (isHandlingAlert) return@withLock
            
            try {
                isHandlingAlert = true
                val alertEvent = AlertEvent(message, title, AlertType.Warning)
                mediator.send(alertEvent)
            } finally {
                isHandlingAlert = false
            }
        }
    }

    override suspend fun showErrorAlertAsync(message: String, title: String) {
        mutex.withLock {
            if (isHandlingAlert) return@withLock
            
            try {
                isHandlingAlert = true
                val alertEvent = AlertEvent(message, title, AlertType.Error)
                mediator.send(alertEvent)
            } finally {
                isHandlingAlert = false
            }
        }
    }
}