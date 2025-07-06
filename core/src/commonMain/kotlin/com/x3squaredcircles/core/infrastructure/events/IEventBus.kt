// infrastructure/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/events/IEventBus.kt
package com.x3squaredcircles.core.infrastructure.events

import com.x3squaredcircles.core.domain.interfaces.IDomainEvent
import kotlinx.coroutines.Job

interface IEventHandler<in TEvent> {
    suspend fun handleAsync(event: TEvent)
}

interface IEventBus {
    suspend fun publishAsync(domainEvent: IDomainEvent, cancellationToken: Job = Job())
    suspend fun publishAllAsync(domainEvents: Array<IDomainEvent>, cancellationToken: Job = Job())
    suspend fun <TEvent> publishAsync(event: TEvent) where TEvent : Any
    suspend fun <TEvent> subscribeAsync(handler: IEventHandler<TEvent>) where TEvent : Any
    suspend fun <TEvent> unsubscribeAsync(handler: IEventHandler<TEvent>) where TEvent : Any
}