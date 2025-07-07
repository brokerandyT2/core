// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/services/DirectAlertingService.kt
package com.x3squaredcircles.core.infrastructure.services

class DirectAlertingService : IAlertService {

    override suspend fun showInfoAlertAsync(message: String, title: String) {
        // Simple logging implementation for infrastructure components
        // This avoids circular dependencies with the mediator pattern
        println("Info Alert: $title - $message")
    }

    override suspend fun showSuccessAlertAsync(message: String, title: String) {
        println("Success Alert: $title - $message")
    }

    override suspend fun showWarningAlertAsync(message: String, title: String) {
        println("Warning Alert: $title - $message")
    }

    override suspend fun showErrorAlertAsync(message: String, title: String) {
        println("Error Alert: $title - $message")
    }
}