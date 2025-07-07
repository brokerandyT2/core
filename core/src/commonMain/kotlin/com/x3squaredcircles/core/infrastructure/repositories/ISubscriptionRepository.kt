// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/repositories/ISubscriptionRepository.kt
package com.x3squaredcircles.core.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.domain.entities.Subscription

interface ISubscriptionRepository {
    suspend fun createAsync(subscription: Subscription): Result<Subscription>
    suspend fun getActiveSubscriptionAsync(userId: String): Result<Subscription>
    suspend fun getByTransactionIdAsync(transactionId: String): Result<Subscription>
    suspend fun updateAsync(subscription: Subscription): Result<Subscription>
    suspend fun getByPurchaseTokenAsync(purchaseToken: String): Result<Subscription>
}
