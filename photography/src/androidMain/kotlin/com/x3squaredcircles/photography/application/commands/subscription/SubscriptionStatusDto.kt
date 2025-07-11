// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/SubscriptionStatusDto.kt
package com.x3squaredcircles.photography.application.queries.subscription

import com.x3squaredcircles.photography.domain.entities.SubscriptionStatus
import com.x3squaredcircles.photography.domain.entities.SubscriptionPeriod
import kotlinx.datetime.LocalDateTime

/**
 * Data transfer object for subscription status information
 */
data class SubscriptionStatusDto(
    val hasActiveSubscription: Boolean = false,
    val productId: String = "",
    val status: SubscriptionStatus = SubscriptionStatus.Pending,
    val expirationDate: LocalDateTime? = null,
    val purchaseDate: LocalDateTime? = null,
    val period: SubscriptionPeriod = SubscriptionPeriod.Monthly,
    val isExpiringSoon: Boolean = false,
    val daysUntilExpiration: Int = 0
)