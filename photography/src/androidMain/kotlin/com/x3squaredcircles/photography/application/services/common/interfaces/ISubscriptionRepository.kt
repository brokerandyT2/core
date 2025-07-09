// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/application/common/interfaces/ISubscriptionRepository.kt
package com.x3squaredcircles.photography.application.common.interfaces

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.domain.entities.Subscription

import kotlinx.coroutines.Job

interface ISubscriptionRepository {
    /**
     * Creates a new subscription record
     */
    suspend fun createAsync(
        subscription: Subscription,
        cancellationToken: Job = Job()
    ): Result<Subscription>

    /**
     * Gets the current active subscription for a user
     */
    suspend fun getActiveSubscriptionAsync(
        userId: String,
        cancellationToken: Job = Job()
    ): Result<Subscription>

    /**
     * Gets subscription by transaction ID
     */
    suspend fun getByTransactionIdAsync(
        transactionId: String,
        cancellationToken: Job = Job()
    ): Result<Subscription>

    /**
     * Updates an existing subscription
     */
    suspend fun updateAsync(
        subscription: Subscription,
        cancellationToken: Job = Job()
    ): Result<Subscription>

    /**
     * Gets subscription by purchase token
     */
    suspend fun getByPurchaseTokenAsync(
        purchaseToken: String,
        cancellationToken: Job = Job()
    ): Result<Subscription>
}