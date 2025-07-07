// core\src\commonMain\kotlin\com\x3squaredcircles\core\Result.kt
package com.x3squaredcircles.core

sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Failure<T>(val errorMessage: String) : Result<T>()
    
    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure
    val data: T?
        get() = when (this) {
            is Success -> value
            is Failure -> null
        }   
    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun <T> failure(errorMessage: String): Result<T> = Failure(errorMessage)
    }
}