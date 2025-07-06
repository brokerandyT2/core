// core\src\commonMain\kotlin\com\x3squaredcircles\core\mediator\Mediator.kt
package com.x3squaredcircles.core.mediator

import com.x3squaredcircles.core.Result

// Base request interface
interface IRequest<TResponse>

// Handler interfaces
interface IRequestHandler<in TRequest : IRequest<TResponse>, TResponse> {
    suspend fun handle(request: TRequest): TResponse
}

// Command and Query base interfaces
interface ICommand<TResponse> : IRequest<TResponse>
interface IQuery<TResponse> : IRequest<TResponse>

// Handler type aliases for clarity
typealias ICommandHandler<TCommand, TResponse> = IRequestHandler<TCommand, TResponse>
typealias IQueryHandler<TQuery, TResponse> = IRequestHandler<TQuery, TResponse>

// Mediator interface
interface IMediator {
    suspend fun <TResponse> send(request: IRequest<TResponse>): TResponse
    fun <TRequest : IRequest<TResponse>, TResponse> registerHandler(
        requestClass: String,
        handler: IRequestHandler<TRequest, TResponse>
    )
}

// Simple mediator implementation using a registry
class Mediator : IMediator {
    
    private val handlers = mutableMapOf<String, IRequestHandler<*, *>>()
    
    override suspend fun <TResponse> send(request: IRequest<TResponse>): TResponse {
        return try {
            val requestClassName = request::class.simpleName ?: "Unknown"
            val handler = handlers[requestClassName] 
                ?: throw IllegalStateException("No handler registered for $requestClassName")
            
            @Suppress("UNCHECKED_CAST")
            val typedHandler = handler as IRequestHandler<IRequest<TResponse>, TResponse>
            typedHandler.handle(request)
        } catch (e: Exception) {
            // If TResponse appears to be a Result type, return a failure
            // This is a simplified check - in practice you'd have better type checking
            @Suppress("UNCHECKED_CAST")
            try {
                Result.failure<Any>("Mediator error: ${e.message}") as TResponse
            } catch (castException: Exception) {
                throw e
            }
        }
    }
    
    override fun <TRequest : IRequest<TResponse>, TResponse> registerHandler(
        requestClass: String,
        handler: IRequestHandler<TRequest, TResponse>
    ) {
        handlers[requestClass] = handler
    }
}