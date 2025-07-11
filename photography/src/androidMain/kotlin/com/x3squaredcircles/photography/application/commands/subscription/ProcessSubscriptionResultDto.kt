// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/subscription/ProcessSubscriptionResultDto.kt
package com.x3squaredcircles.photography.application.commands.subscription

import com.x3squaredcircles.photography.domain.entities.SubscriptionStatus

data class ProcessSubscriptionResultDto(
    val isSuccessful: Boolean = false,
    val transactionId: String = "",
    val purchaseToken: String = "",
    val purchaseDate: Long = 0L,
    val expirationDate: Long = 0L,
    val productId: String = "",
    val status: SubscriptionStatus = SubscriptionStatus.Failed,
    val errorMessage: String = ""
)