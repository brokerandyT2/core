// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/behaviors/ValidationBehavior.kt
package com.x3squaredcircles.core.infrastructure.behaviors
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IMediator
import com.x3squaredcircles.core.mediator.IRequest
import com.x3squaredcircles.core.validators.IValidator
import com.x3squaredcircles.core.validators.ValidationResult
interface IPipelineBehavior<TRequest : IRequest<TResponse>, TResponse> {
suspend fun handle(request: TRequest, next: suspend () -> TResponse): TResponse
}
class ValidationBehavior<TRequest : IRequest<TResponse>, TResponse>(
private val validators: List<IValidator<TRequest>>,
private val mediator: IMediator
) : IPipelineBehavior<TRequest, TResponse> where TResponse : Result<*> {
override suspend fun handle(request: TRequest, next: suspend () -> TResponse): TResponse {
    if (validators.isEmpty()) {
        return next()
    }

    val validationErrors = mutableListOf<String>()

    for (validator in validators) {
        val validationResult = validator.validate(request)
        if (validationResult is ValidationResult.Failure) {
            validationErrors.addAll(validationResult.errors)
        }
    }

    if (validationErrors.isNotEmpty()) {
        @Suppress("UNCHECKED_CAST")
        return Result.failure<Any>(validationErrors.joinToString("; ")) as TResponse
    }

    return next()
}
}