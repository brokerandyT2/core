// core\src\commonMain\kotlin\com\x3squaredcircles\core\presentation\INavigationAware.kt
package com.x3squaredcircles.core.presentation

interface INavigationAware {
    suspend fun onNavigatedToAsync()
    suspend fun onNavigatedFromAsync()
}