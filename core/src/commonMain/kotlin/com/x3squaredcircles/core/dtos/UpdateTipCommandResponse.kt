// core/src/commonMain/kotlin/com/x3squaredcircles/core/dtos/UpdateTipCommandResponse.kt
package com.x3squaredcircles.core.dtos

data class UpdateTipCommandResponse(
    val id: Int,
    val tipTypeId: Int,
    val title: String = "",
    val content: String = "",
    val fstop: String = "",
    val shutterSpeed: String = "",
    val iso: String = "",
    val i8n: String = "en-US"
)