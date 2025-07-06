// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/valueobjects/Temperature.kt
package com.x3squaredcircles.core.domain.valueobjects

import kotlin.math.round

/**
 * Value object representing temperature with unit conversions
 */
class Temperature private constructor(
    private val celsius: Double
) : ValueObject() {
    
    val celsiusValue: Double get() = celsius
    val fahrenheit: Double get() = (celsius * 9.0 / 5.0) + 32.0
    val kelvin: Double get() = celsius + 273.15
    
    companion object {
        /**
         * Creates a Temperature instance from a specified temperature in degrees Celsius.
         */
        fun fromCelsius(celsius: Double): Temperature {
            return Temperature(round(celsius * 100.0) / 100.0)
        }
        
        /**
         * Creates a Temperature instance from a temperature value in degrees Fahrenheit.
         */
        fun fromFahrenheit(fahrenheit: Double): Temperature {
            val celsius = (fahrenheit - 32.0) * 5.0 / 9.0
            return Temperature(round(celsius * 100.0) / 100.0)
        }
        
        /**
         * Creates a Temperature instance from a temperature value in Kelvin.
         */
        fun fromKelvin(kelvin: Double): Temperature {
            val celsius = kelvin - 273.15
            return Temperature(round(celsius * 100.0) / 100.0)
        }
    }
    
    /**
     * Provides the components used to determine equality for the current object.
     */
    override fun getEqualityComponents(): List<Any?> {
        return listOf(celsius)
    }
    
    /**
     * Returns a string representation of the temperature in both Celsius and Fahrenheit.
     */
    override fun toString(): String {
        return "${String.format("%.1f", celsius)}°C / ${String.format("%.1f", fahrenheit)}°F"
    }
}