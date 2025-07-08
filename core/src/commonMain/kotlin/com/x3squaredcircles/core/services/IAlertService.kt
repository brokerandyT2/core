// core/src/commonMain/kotlin/com/x3squaredcircles/core/services/IAlertService.kt
package com.x3squaredcircles.core.services
interface IAlertService {
    suspend fun showInfoAlertAsync(message: String, title: String? = null)
    suspend fun showSuccessAlertAsync(message: String, title: String? = null)
    suspend fun showWarningAlertAsync(message: String, title: String? = null)
    suspend fun showErrorAlertAsync(message: String, title: String? = null)
}

object AlertServiceExtensions {
    suspend fun IAlertService.showInfoAlertAsync(message: String, title: String? = null) {
        showInfoAlertAsync(message, title ?: "Information")
    }
    suspend fun IAlertService.showSuccessAlertAsync(message: String, title: String? = null) {
        showSuccessAlertAsync(message, title ?: "Success")
    }

    suspend fun IAlertService.showWarningAlertAsync(message: String, title: String? = null) {
        showWarningAlertAsync(message, title ?: "Warning")
    }

    suspend fun IAlertService.showErrorAlertAsync(message: String, title: String? = null) {
        showErrorAlertAsync(message, title ?: "Error")
    }
}
