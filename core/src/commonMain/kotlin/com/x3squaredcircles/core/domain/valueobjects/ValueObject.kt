// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/valueobjects/ValueObject.kt
package com.x3squaredcircles.core.domain.valueobjects

/**
 * Base class for value objects
 */
abstract class ValueObject {
    
    /**
     * Provides the components that define equality for the derived type.
     */
    protected abstract fun getEqualityComponents(): List<Any?>
    
    /**
     * Determines whether the specified object is equal to the current object.
     */
    override fun equals(other: Any?): Boolean {
        if (other == null || other::class != this::class) {
            return false
        }
        
        val otherValueObject = other as ValueObject
        return getEqualityComponents() == otherValueObject.getEqualityComponents()
    }
    
    /**
     * Returns a hash code for the current object based on its equality components.
     */
    override fun hashCode(): Int {
        return getEqualityComponents()
            .map { it?.hashCode() ?: 0 }
            .fold(0) { acc, hash -> acc xor hash }
    }
}