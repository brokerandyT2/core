// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/valueobjects/Address.kt
package com.x3squaredcircles.core.domain.valueobjects

/**
 * Value object representing a physical address
 */
class Address(
    val city: String,
    val state: String
) : ValueObject() {
    
    /**
     * Provides the components used to determine equality for the current object.
     */
    override fun getEqualityComponents(): List<Any?> {
        return listOf(city.uppercase(), state.uppercase())
    }
    
    /**
     * Returns a string representation of the location, formatted as "City, State".
     */
    override fun toString(): String {
        return when {
            city.isBlank() && state.isBlank() -> ""
            state.isBlank() -> city
            city.isBlank() -> state
            else -> "$city, $state"
        }
    }
}