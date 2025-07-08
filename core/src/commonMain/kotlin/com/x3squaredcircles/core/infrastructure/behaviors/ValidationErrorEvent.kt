// core/src/commonMain/kotlin/com/x3squaredcircles/core/events/ValidationErrorEvent.kt
package com.x3squaredcircles.core.events

import com.x3squaredcircles.core.common.models.Error

class ValidationErrorEvent(
    val entityType: String,
    val validationErrors: Map<String, List<String>>,
    source: String
) : DomainErrorEvent(source) {
    
    constructor(entityType: String, errors: List<Error>, source: String) : this(
        entityType,
        errors
            .filter { !it.propertyName.isNullOrEmpty() }
            .groupBy { it.propertyName!! }
            .mapValues { entry -> entry.value.map { it.message } },
        source
    )
    
    override val resourceKey: String
        get() = if (validationErrors.size == 1) {
            "Validation_Error_Single"
        } else {
            "Validation_Error_Multiple"
        }
    
    override val parameters: Map<String, Any>
        get() {
            val params = mutableMapOf<String, Any>(
                "EntityType" to entityType,
                "ErrorCount" to validationErrors.size
            )
            
            if (validationErrors.size == 1) {
                val firstError = validationErrors.entries.first()
                params["PropertyName"] = firstError.key
                params["ErrorMessage"] = firstError.value.first()
            }
            
            return params
        }
    
    override val severity: ErrorSeverity
        get() = ErrorSeverity.Warning
}