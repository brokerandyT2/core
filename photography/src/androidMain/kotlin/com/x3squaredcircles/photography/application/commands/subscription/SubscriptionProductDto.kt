// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/subscription/SubscriptionProductDto.kt
package com.x3squaredcircles.photography.application.commands.subscription

import com.x3squaredcircles.photography.domain.entities.SubscriptionPeriod

data class SubscriptionProductDto(
    val productId: String = "",
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val priceAmountMicros: String = "",
    val currencyCode: String = "",
    val period: SubscriptionPeriod = SubscriptionPeriod.Monthly
)