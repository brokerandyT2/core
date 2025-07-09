// com/x3squaredcircles/photography/application/services/ExposureSettingsDto.kt
package com.x3squaredcircles.photography.application.services

data class ExposureSettingsDto(
    val shutterSpeed: String = "",
    val aperture: String = "",
    val iso: String = "",
    val errorMessage: String = ""
)