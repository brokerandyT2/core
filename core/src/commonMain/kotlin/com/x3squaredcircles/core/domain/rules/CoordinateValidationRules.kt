// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/rules/CoordinateValidationRules.kt
package com.x3squaredcircles.core.domain.rules

import com.x3squaredcircles.core.domain.valueobjects.Coordinate

/**
 * Business rules for coordinate validation
 */
object CoordinateValidationRules {
    
    /**
     * Validates the specified latitude and longitude values and determines if they represent a valid geographic location.
     */
    fun isValid(latitude: Double, longitude: Double): Pair<Boolean, List<String>> {
        val errors = mutableListOf<String>()
        
        if (latitude < -90 || latitude > 90) {
            errors.add("Latitude $latitude is out of valid range (-90 to 90)")
        }
        
        if (longitude < -180 || longitude > 180) {
            errors.add("Longitude $longitude is out of valid range (-180 to 180)")
        }
        
        // Check for specific invalid coordinates
        if (latitude == 0.0 && longitude == 0.0) {
            errors.add("Null Island (0,0) is not a valid location")
        }
        
        return Pair(errors.isEmpty(), errors)
    }
    
    /**
     * Determines whether the distance between two coordinates is within the specified maximum distance.
     */
    fun isValidDistance(from: Coordinate?, to: Coordinate?, maxDistanceKm: Double): Boolean {
        if (from == null || to == null) {
            return false
        }
        
        val distance = from.distanceTo(to)
        return distance <= maxDistanceKm
    }
}