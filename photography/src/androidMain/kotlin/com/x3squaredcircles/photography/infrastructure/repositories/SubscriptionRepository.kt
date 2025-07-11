Now let me search for the InvalidateCacheForUser method and other helper methods:Searched project for “SubscriptionRepository InvalidateCacheForUser helper methods cache management complete”4 resultsLocation/Location.Photography.Infrastructure/Repositories/SubscriptionRepository.cs5 relevant sectionstextLocation/Location.Photography.Infrastructure/DependencyInjection.cstextLocation/Location.Photography.ViewModels/SubscriptionAwareViewModelBase.cstextLocation/Location.Photography.Infrastructure/Services/SubscriptionService.cstextPerfect! Now I have the complete SubscriptionRepository implementation. Let me migrate it to Kotlin following the rules:
// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/SubscriptionRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.infrastructure.database.IDatabaseContext
import com.x3squaredcircles.photography.domain.entities.Subscription
import com.x3squaredcircles.photography.repositories.ISubscriptionRepository
import com.x3squaredcircles.core.infrastructure.services.ILoggingService
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.builtins.serializer
import java.util.concurrent.ConcurrentHashMap

class SubscriptionRepository(
private val context: IDatabaseContext,
private val logger: ILoggingService
) : ISubscriptionRepository {
companion object {
    private const val SUBSCRIPTION_ERROR_CANNOT_BE_NULL = "Subscription cannot be null"
    private const val SUBSCRIPTION_ERROR_NOT_FOUND = "Subscription not found"
    private const val SUBSCRIPTION_ERROR_NO_ACTIVE = "No active subscription found"
    private const val SUBSCRIPTION_ERROR_USER_ID_NULL = "User ID cannot be null or empty"
    private const val SUBSCRIPTION_ERROR_TRANSACTION_ID_NULL = "Transaction ID cannot be null or empty"
    private const val SUBSCRIPTION_ERROR_PURCHASE_TOKEN_NULL = "Purchase token cannot be null or empty"
    private const val SUBSCRIPTION_ERROR_CREATING = "Error creating subscription: %s"
    private const val SUBSCRIPTION_ERROR_GETTING_ACTIVE = "Error retrieving active subscription: %s"
    private const val SUBSCRIPTION_ERROR_GETTING_TRANSACTION = "Error retrieving subscription: %s"
    private const val SUBSCRIPTION_ERROR_UPDATING = "Error updating subscription: %s"
    private const val SUBSCRIPTION_ERROR_GETTING_TOKEN = "Error retrieving subscription: %s"
    private const val SUBSCRIPTION_ERROR_CHECKING_STATUS = "Error checking subscription status: %s"
    private val CACHE_TIMEOUT_MINUTES = 5L
}

// Cache for frequently accessed subscriptions to reduce database calls
private val subscriptionCache = ConcurrentHashMap<String, Pair<Subscription?, Instant>>()

override suspend fun createAsync(subscription: Subscription, cancellationToken: Job): Result<Subscription> {
    return try {
        cancellationToken.ensureActive()

        withContext(Dispatchers.IO) {
            context.insertAsync(subscription)
        }

        // Clear cache for this user since we've added a new subscription
        invalidateCacheForUser(subscription.userId)

        logger.logInfo("Created subscription with ID: ${subscription.id}")
        Result.success(subscription)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error creating subscription", e)
        Result.failure(String.format(SUBSCRIPTION_ERROR_CREATING, e.message))
    }
}

override suspend fun getActiveSubscriptionAsync(userId: String, cancellationToken: Job): Result<Subscription> {
    return try {
        cancellationToken.ensureActive()

        if (userId.isBlank()) {
            return Result.failure(SUBSCRIPTION_ERROR_USER_ID_NULL)
        }

        // Check cache first to reduce database calls
        val cacheKey = "active_$userId"
        val cached = subscriptionCache[cacheKey]
        if (cached != null && Clock.System.now() < cached.second) {
            cached.first?.let {
                return Result.success(it)
            }
        }

        // Execute database query on background thread to prevent UI blocking
        val subscription = withContext(Dispatchers.IO) {
            val subscriptions = context.table<Subscription>()
                .where { 
                    it.userId == userId && 
                    it.isActive && 
                    it.expirationDate != null && 
                    it.expirationDate!! > Instant.fromEpochMilliseconds(Clock.System.now().epochSeconds).toLocalDateTime(TimeZone.currentSystemDefault())
                }

               as List<Subscription>()
            
            subscriptions.firstOrNull()
        }

        if (subscription == null) {
            return Result.failure(SUBSCRIPTION_ERROR_NO_ACTIVE)
        }

        // Cache the result for future calls
        val cacheExpiry = Clock.System.now().plus(CACHE_TIMEOUT_MINUTES, DateTimeUnit.MINUTE)
        subscriptionCache[cacheKey] = Pair(subscription, cacheExpiry)

        Result.success(subscription)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error getting active subscription for user: $userId", e)
        Result.failure(String.format(SUBSCRIPTION_ERROR_GETTING_ACTIVE, e.message))
    }
}

override suspend fun getByTransactionIdAsync(transactionId: String, cancellationToken: Job): Result<Subscription> {
    return try {
        cancellationToken.ensureActive()

        if (transactionId.isBlank()) {
            return Result.failure(SUBSCRIPTION_ERROR_TRANSACTION_ID_NULL)
        }

        // Check cache first
        val cacheKey = "transaction_$transactionId"
        val cached = subscriptionCache[cacheKey]
        if (cached != null && Clock.System.now() < cached.second) {
            cached.first?.let {
                return Result.success(it)
            }
        }

        // Execute database query on background thread to prevent UI blocking
        val subscription = withContext(Dispatchers.IO) {
            val subscriptions = context.table<Subscription>()
                .where { it.transactionId == transactionId }
                as List<Subscription>()
            
            subscriptions.firstOrNull()
        }

        if (subscription == null) {
            return Result.failure(SUBSCRIPTION_ERROR_NOT_FOUND)
        }

        // Cache the result
        val cacheExpiry = Clock.System.now().plus(CACHE_TIMEOUT_MINUTES, DateTimeUnit.MINUTE)
        subscriptionCache[cacheKey] = Pair(subscription, cacheExpiry)

        Result.success(subscription)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error getting subscription by transaction ID: $transactionId", e)
        Result.failure(String.format(SUBSCRIPTION_ERROR_GETTING_TRANSACTION, e.message))
    }
}

override suspend fun updateAsync(subscription: Subscription, cancellationToken: Job): Result<Subscription> {
    return try {
        cancellationToken.ensureActive()

        withContext(Dispatchers.IO) {
            context.updateAsync(subscription)
        }

        // Clear cache for this user since subscription data has changed
        invalidateCacheForUser(subscription.userId)

        logger.logInfo("Updated subscription with ID: ${subscription.id}")
        Result.success(subscription)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error updating subscription with ID: ${subscription.id}", e)
        Result.failure(String.format(SUBSCRIPTION_ERROR_UPDATING, e.message))
    }
}

override suspend fun getByPurchaseTokenAsync(purchaseToken: String, cancellationToken: Job): Result<Subscription> {
    return try {
        cancellationToken.ensureActive()

        if (purchaseToken.isBlank()) {
            return Result.failure(SUBSCRIPTION_ERROR_PURCHASE_TOKEN_NULL)
        }

        // Check cache first
        val cacheKey = "token_$purchaseToken"
        val cached = subscriptionCache[cacheKey]
        if (cached != null && Clock.System.now() < cached.second) {
            cached.first?.let {
                return Result.success(it)
            }
        }

        // Execute database query on background thread to prevent UI blocking
        val subscription = withContext(Dispatchers.IO) {
            val subscriptions = context.table<Subscription>()
                .where { it.purchaseToken == purchaseToken }
                as List<Subscription>()
            
            subscriptions.firstOrNull()
        }

        if (subscription == null) {
            return Result.failure(SUBSCRIPTION_ERROR_NOT_FOUND)
        }

        // Cache the result
        val cacheExpiry = Clock.System.now().plus(CACHE_TIMEOUT_MINUTES, DateTimeUnit.MINUTE)
        subscriptionCache[cacheKey] = Pair(subscription, cacheExpiry)

        Result.success(subscription)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error getting subscription by purchase token: $purchaseToken", e)
        Result.failure(String.format(SUBSCRIPTION_ERROR_GETTING_TOKEN, e.message))
    }
}

/**
 * Bulk operation to retrieve multiple subscriptions efficiently
 */
suspend fun getSubscriptionsByUserIdsAsync(
    userIds: List<String>,
    cancellationToken: Job = Job()
): Result<List<Subscription>> {
    return try {
        cancellationToken.ensureActive()

        if (userIds.isEmpty()) {
            return Result.success(emptyList())
        }

        // Execute bulk query on background thread
        val subscriptions = withContext(Dispatchers.IO) {
            val allSubscriptions = mutableListOf<Subscription>()

            // Process in batches to avoid potential query size limits
            val batchSize = 100
            userIds.chunked(batchSize).forEach { batch ->
                val batchSubscriptions = context.table<Subscription>()
                    .where { subscription -> batch.contains(subscription.userId) }
                    as List<Subscription>()

                allSubscriptions.addAll(batchSubscriptions)
            }

            allSubscriptions
        }

        Result.success(subscriptions)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error retrieving subscriptions for multiple users", e)
        Result.failure("Error retrieving subscriptions: ${e.message}")
    }
}

/**
 * Efficiently check if user has any active subscription without loading full entity
 */
suspend fun hasActiveSubscriptionAsync(userId: String, cancellationToken: Job = Job()): Result<Boolean> {
    return try {
        cancellationToken.ensureActive()

        if (userId.isBlank()) {
            return Result.success(false)
        }

        // Check cache first for performance
        val cacheKey = "hasactive_$userId"
        val cached = subscriptionCache[cacheKey]
        if (cached != null && Clock.System.now() < cached.second) {
            return Result.success(cached.first != null)
        }

        // Execute efficient count query on background thread
 val hasActiveSubscription = withContext(Dispatchers.IO) {
            val now = Clock.System.now()
            val y = Instant.fromEpochMilliseconds(now.epochSeconds).toLocalDateTime(TimeZone.currentSystemDefault())

            val count = context.table<Subscription>()
                .where { 
                    it.userId == userId && 
                    it.isActive && 
                    it.expirationDate > y
                } as List<Subscription>()
            count.size > 0
        } as Boolean

        Result.success(hasActiveSubscription)
    } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) throw e
        logger.logError("Error checking if user has active subscription: $userId", e)
        Result.failure(String.format(SUBSCRIPTION_ERROR_CHECKING_STATUS, e.message))
    }
}

/**
 * Clear cache entries for a specific user when their subscription data changes
 */
private fun invalidateCacheForUser(userId: String) {
    if (userId.isBlank()) return

    val keysToRemove = subscriptionCache.keys.filter { key -> key.contains(userId) }

    keysToRemove.forEach { key ->
        subscriptionCache.remove(key)
    }
}

/**
 * Periodic cleanup of expired cache entries to prevent memory leaks
 */
fun cleanupExpiredCache() {
    val now = Clock.System.now()
    val expiredKeys = subscriptionCache.entries
        .filter { (_, value) -> now >= value.second }
        .map { it.key }

    expiredKeys.forEach { key ->
        subscriptionCache.remove(key)
    }

    if (expiredKeys.isNotEmpty()) {
        logger.logInfo("Cleaned up ${expiredKeys.size} expired cache entries")
    }
}
}