// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/behaviors/IPipelineBehavior.kt
package com.x3squaredcircles.core.infrastructure.behaviors

import com.x3squaredcircles.core.mediator.IRequest

/**
 * Pipeline behavior interface for MediatR-style request/response pipeline
 * 
 * Equivalent to .NET's IPipelineBehavior<TRequest, TResponse>
 * Allows for cross-cutting concerns like validation, logging, etc.
 */
interface IPipelineBehavior<TRequest : IRequest<TResponse>, TResponse> {
    /**
     * Handles the request and calls the next behavior in the pipeline
     * 
     * @param request The request object
     * @param next The next handler in the pipeline
     * @return The response from the pipeline
     */
    suspend fun handle(request: TRequest, next: suspend () -> TResponse): TResponse
}