// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/application/services/models/ExposureSettingsDto.kt
package com.x3squaredcircles.photography.application.services.models

data class ExposureSettingsDto(
    val shutterSpeed: String = "",
    val aperture: String = "",
    val iso: String = "",
    val errorMessage: String = ""
)