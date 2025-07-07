// core/src/commonMain/kotlin/com/x3squaredcircles/core/validators/IValidator.kt
package com.x3squaredcircles.core.validators
interface IValidator<T> {
fun validate(value: T): ValidationResult
}
