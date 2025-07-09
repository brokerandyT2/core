// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/entities/Subscription.kt
package com.x3squaredcircles.photography.domain.entities

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Represents a subscription with its activity periods and status
 */
data class Subscription(
    val id: Int = 0,
    val createdAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    val updatedAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    val productId: String = "",
    val transactionId: String = "",
    val purchaseToken: String = "",
    val purchaseDate: LocalDateTime,
    val expirationDate: LocalDateTime,
    val status: SubscriptionStatus = SubscriptionStatus.Pending,
    val period: SubscriptionPeriod = SubscriptionPeriod.Monthly,
    val userId: String = ""
) {
    /**
     * Checks if subscription is currently active
     */
    val isActive: Boolean
        get() = status == SubscriptionStatus.Active && 
                expirationDate > Clock.System.now().toLocalDateTime(TimeZone.UTC)

    /**
     * Updates subscription status
     */
    fun updateStatus(newStatus: SubscriptionStatus): Subscription {
        return copy(
            status = newStatus,
            updatedAt = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        )
    }

    /**
     * Updates expiration date
     */
    fun updateExpiration(newExpirationDate: LocalDateTime): Subscription {
        return copy(
            expirationDate = newExpirationDate,
            updatedAt = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        )
    }

    /**
     * Checks if subscription is expiring soon (within default threshold of 3 days)
     */
    fun isExpiringSoon(): Boolean {
        return isExpiringSoon(3)
    }

    /**
     * Checks if subscription is expiring soon within specified days threshold
     */
    fun isExpiringSoon(daysThreshold: Int): Boolean {
        if (!isActive) return false
        
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val daysDifference = (expirationDate.date.toEpochDays() - now.date.toEpochDays())
        
        return daysDifference <= daysThreshold
    }

    /**
     * Gets number of days until expiration
     */
    fun daysUntilExpiration(): Int {
        if (!isActive) return 0
        
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val daysDifference = (expirationDate.date.toEpochDays() - now.date.toEpochDays()).toInt()
        
        return maxOf(0, daysDifference)
    }
}

/**
 * Subscription status enumeration
 */
enum class SubscriptionStatus(val value: Int) {
    Active(1),
    Expired(2),
    Cancelled(3),
    Pending(4),
    Failed(5);

    companion object {
        fun fromValue(value: Int): SubscriptionStatus {
            return entries.find { it.value == value } ?: Pending
        }
    }
}

/**
 * Subscription period enumeration
 */
enum class SubscriptionPeriod(val value: Int) {
    Monthly(1),
    Yearly(2);

    companion object {
        fun fromValue(value: Int): SubscriptionPeriod {
            return entries.find { it.value == value } ?: Monthly
        }
    }
}