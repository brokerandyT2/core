// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/application/services/ISubscriptionService.kt
package com.x3squaredcircles.photography.application.services

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.application.commands.subscription.SubscriptionProductDto
import com.x3squaredcircles.photography.application.commands.subscription.ProcessSubscriptionResultDto
import com.x3squaredcircles.photography.application.queries.subscription.SubscriptionStatusDto

import kotlinx.coroutines.Job

interface ISubscriptionService {
    
    suspend fun initializeAsync(cancellationToken: Job = Job()): Result<Boolean>
    
    suspend fun getAvailableProductsAsync(cancellationToken: Job = Job()): Result<List<SubscriptionProductDto>>
    
    suspend fun purchaseSubscriptionAsync(productId: String, cancellationToken: Job = Job()): Result<ProcessSubscriptionResultDto>
    
    suspend fun storeSubscriptionAsync(subscriptionData: ProcessSubscriptionResultDto, cancellationToken: Job = Job()): Result<Boolean>
    
    suspend fun storeSubscriptionInSettingsAsync(subscriptionData: ProcessSubscriptionResultDto, cancellationToken: Job = Job()): Result<Boolean>
    
    suspend fun getCurrentSubscriptionStatusAsync(cancellationToken: Job = Job()): Result<SubscriptionStatusDto>
    
    suspend fun validateAndUpdateSubscriptionAsync(cancellationToken: Job = Job()): Result<Boolean>
    
    suspend fun disconnectAsync()
}