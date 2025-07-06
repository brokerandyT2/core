// infrastructure/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/events/InMemoryEventBus.kt
package com.x3squaredcircles.core.infrastructure.events

import com.x3squaredcircles.core.domain.interfaces.IDomainEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KClass

class InMemoryEventBus : IEventBus {
    
    private val handlers = mutableMapOf<KClass<*>, MutableList<Any>>()
    private val mutex = Mutex()
    
    override suspend fun publishAsync(domainEvent: IDomainEvent, cancellationToken: Job) {
        mutex.withLock {
            val eventType = domainEvent::class
            handlers[eventType]?.let { handlerList ->
                handlerList.toList().forEach { handler ->
                    try {
                        if (handler is IEventHandler<*>) {
                            @Suppress("UNCHECKED_CAST")
                            val typedHandler = handler as IEventHandler<IDomainEvent>
                            typedHandler.handleAsync(domainEvent)
                        }
                    } catch (ex: Exception) {
                        // Continue processing other handlers even if one fails
                    }
                }
            }
        }
    }
    
    override suspend fun publishAllAsync(domainEvents: Array<IDomainEvent>, cancellationToken: Job) {
        domainEvents.forEach { domainEvent ->
            publishAsync(domainEvent, cancellationToken)
        }
    }
    
    override suspend fun <TEvent> publishAsync(event: TEvent) where TEvent : Any {
        mutex.withLock {
            val eventType = event::class
            handlers[eventType]?.let { handlerList ->
                handlerList.toList().forEach { handler ->
                    try {
                        if (handler is IEventHandler<*>) {
                            @Suppress("UNCHECKED_CAST")
                            val typedHandler = handler as IEventHandler<TEvent>
                            typedHandler.handleAsync(event)
                        }
                    } catch (ex: Exception) {
                        // Continue processing other handlers even if one fails
                    }
                }
            }
        }
    }
    
    override suspend fun <TEvent> subscribeAsync(handler: IEventHandler<TEvent>) where TEvent : Any {
        // Note: This requires explicit type registration via subscribe() method
        throw UnsupportedOperationException("Use subscribe(eventType, handler) method instead")
    }
    
    override suspend fun <TEvent> unsubscribeAsync(handler: IEventHandler<TEvent>) where TEvent : Any {
        // Note: This requires explicit type registration via unsubscribe() method
        throw UnsupportedOperationException("Use unsubscribe(eventType, handler) method instead")
    }
    
    fun <TEvent> subscribe(eventType: KClass<TEvent>, handler: IEventHandler<TEvent>) where TEvent : Any {
        if (!handlers.containsKey(eventType)) {
            handlers[eventType] = mutableListOf()
        }
        handlers[eventType]?.add(handler)
    }
    
    fun <TEvent> unsubscribe(eventType: KClass<TEvent>, handler: IEventHandler<TEvent>) where TEvent : Any {
        handlers[eventType]?.remove(handler)
        if (handlers[eventType]?.isEmpty() == true) {
            handlers.remove(eventType)
        }
    }
}