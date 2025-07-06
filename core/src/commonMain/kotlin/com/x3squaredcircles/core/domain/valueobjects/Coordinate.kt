// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/valueobjects/Coordinate.kt
package com.x3squaredcircles.core.domain.valueobjects

import kotlin.math.*

/**
 * PERFORMANCE OPTIMIZED: Value object representing geographic coordinates
 */
class Coordinate private constructor(
    val latitude: Double,
    val longitude: Double,
    private val preCalculatedHashCode: Int
) : ValueObject() {
    
    companion object {
        // PERFORMANCE: Pre-calculated constants for distance calculations
        private const val EARTH_RADIUS_KM = 6371.0
        private const val DEGREES_TO_RADIANS = PI / 180.0
        private const val RADIANS_TO_DEGREES = 180.0 / PI
        
        // PERFORMANCE: Cache for distance calculations between commonly used coordinates
        private val distanceCache = mutableMapOf<Pair<Pair<Double, Double>, Pair<Double, Double>>, Double>()
        
        // PERFORMANCE: Cache for string representations to avoid repeated formatting
        private val stringCache = mutableMapOf<Pair<Double, Double>, String>()
        
        // PERFORMANCE: Cache for validation results
        private val validationCache = mutableMapOf<Pair<Double, Double>, Boolean>()
        
        /**
         * Creates a new Coordinate instance with validation
         */
        fun create(latitude: Double, longitude: Double): Coordinate {
            validateCoordinates(latitude, longitude)
            val roundedLat = round(latitude * 1000000.0) / 1000000.0
            val roundedLon = round(longitude * 1000000.0) / 1000000.0
            val hashCode = calculateHashCode(roundedLat, roundedLon)
            return Coordinate(roundedLat, roundedLon, hashCode)
        }
        
        /**
         * Creates a new Coordinate instance with optional validation skip
         */
        fun createWithValidationSkip(latitude: Double, longitude: Double, skipValidation: Boolean = false): Coordinate {
            if (!skipValidation) {
                validateCoordinates(latitude, longitude)
            }
            val roundedLat = round(latitude * 1000000.0) / 1000000.0
            val roundedLon = round(longitude * 1000000.0) / 1000000.0
            val hashCode = calculateHashCode(roundedLat, roundedLon)
            return Coordinate(roundedLat, roundedLon, hashCode)
        }
        
        /**
         * PERFORMANCE: Static method for creating coordinates with validation caching
         */
        fun createValidated(latitude: Double, longitude: Double): Coordinate {
            val key = Pair(round(latitude * 1000000.0) / 1000000.0, round(longitude * 1000000.0) / 1000000.0)
            
            val isValid = validationCache.getOrPut(key) {
                isValidCoordinate(latitude, longitude)
            }
            
            if (!isValid) {
                throw IllegalArgumentException("Invalid coordinates: Latitude=$latitude, Longitude=$longitude")
            }
            
            return createWithValidationSkip(latitude, longitude, skipValidation = true)
        }
        
        /**
         * PERFORMANCE: Fast coordinate validation without exceptions
         */
        fun isValidCoordinate(latitude: Double, longitude: Double): Boolean {
            return latitude in -90.0..90.0 && longitude in -180.0..180.0
        }
        
        /**
         * PERFORMANCE: Batch coordinate creation for multiple points
         */
        fun createBatch(coordinates: List<Pair<Double, Double>>): List<Coordinate> {
            return coordinates.map { (lat, lon) ->
                createValidated(lat, lon)
            }
        }
        
        /**
         * PERFORMANCE: Calculate midpoint between two coordinates
         */
        fun midpoint(coord1: Coordinate, coord2: Coordinate): Coordinate {
            val lat1Rad = coord1.latitude * DEGREES_TO_RADIANS
            val lon1Rad = coord1.longitude * DEGREES_TO_RADIANS
            val lat2Rad = coord2.latitude * DEGREES_TO_RADIANS
            val deltaLonRad = (coord2.longitude - coord1.longitude) * DEGREES_TO_RADIANS
            
            val bx = cos(lat2Rad) * cos(deltaLonRad)
            val by = cos(lat2Rad) * sin(deltaLonRad)
            
            val lat3Rad = atan2(
                sin(lat1Rad) + sin(lat2Rad),
                sqrt((cos(lat1Rad) + bx) * (cos(lat1Rad) + bx) + by * by)
            )
            
            val lon3Rad = lon1Rad + atan2(by, cos(lat1Rad) + bx)
            
            val midLat = lat3Rad * RADIANS_TO_DEGREES
            val midLon = lon3Rad * RADIANS_TO_DEGREES
            
            return createWithValidationSkip(midLat, midLon, skipValidation = true)
        }
        
        /**
         * PERFORMANCE: Static cache cleanup method for memory management
         */
        fun clearCaches() {
            distanceCache.clear()
            stringCache.clear()
            validationCache.clear()
        }
        
        /**
         * PERFORMANCE: Get cache statistics for monitoring
         */
        fun getCacheStats(): Triple<Int, Int, Int> {
            return Triple(distanceCache.size, stringCache.size, validationCache.size)
        }
        
        private fun validateCoordinates(latitude: Double, longitude: Double) {
            if (latitude !in -90.0..90.0) {
                throw IllegalArgumentException("Latitude must be between -90 and 90")
            }
            if (longitude !in -180.0..180.0) {
                throw IllegalArgumentException("Longitude must be between -180 and 180")
            }
        }
        
        private fun calculateHashCode(latitude: Double, longitude: Double): Int {
            var hash = 17
            hash = hash * 23 + latitude.hashCode()
            hash = hash * 23 + longitude.hashCode()
            return hash
        }
        
        /**
         * PERFORMANCE: Highly optimized Haversine distance calculation
         */
        private fun calculateHaversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val lat1Rad = lat1 * DEGREES_TO_RADIANS
            val lon1Rad = lon1 * DEGREES_TO_RADIANS
            val lat2Rad = lat2 * DEGREES_TO_RADIANS
            val lon2Rad = lon2 * DEGREES_TO_RADIANS
            
            val dLat = lat2Rad - lat1Rad
            val dLon = lon2Rad - lon1Rad
            
            val sinDLat = sin(dLat * 0.5)
            val sinDLon = sin(dLon * 0.5)
            val cosLat1 = cos(lat1Rad)
            val cosLat2 = cos(lat2Rad)
            
            val a = sinDLat * sinDLat + cosLat1 * cosLat2 * sinDLon * sinDLon
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            
            return EARTH_RADIUS_KM * c
        }
        
        /**
         * PERFORMANCE: Bulk distance calculation for multiple coordinates
         */
        fun calculateDistances(from: Coordinate, destinations: List<Coordinate>): List<Double> {
            return destinations.map { from.distanceTo(it) }
        }
    }
    
    /**
     * PERFORMANCE: Optimized distance calculation with caching
     */
    fun distanceTo(other: Coordinate): Double {
        val cacheKey = Pair(Pair(latitude, longitude), Pair(other.latitude, other.longitude))
        
        return distanceCache.getOrPut(cacheKey) {
            calculateHaversineDistance(latitude, longitude, other.latitude, other.longitude)
        }
    }
    
    /**
     * PERFORMANCE: Fast distance check without full calculation for nearby coordinates
     */
    fun isWithinDistance(other: Coordinate, maxDistanceKm: Double): Boolean {
        val latDiff = abs(latitude - other.latitude)
        val lonDiff = abs(longitude - other.longitude)
        
        if (latDiff < 1.0 && lonDiff < 1.0) {
            val approximateDistance = sqrt(latDiff * latDiff + lonDiff * lonDiff) * 111.32
            if (approximateDistance > maxDistanceKm) return false
        }
        
        return distanceTo(other) <= maxDistanceKm
    }
    
    /**
     * PERFORMANCE: Find nearest coordinate from a collection
     */
    fun findNearest(candidates: List<Coordinate>): Coordinate {
        require(candidates.isNotEmpty()) { "Candidates collection cannot be empty" }
        
        var nearest = candidates[0]
        var minDistance = distanceTo(nearest)
        
        for (i in 1 until candidates.size) {
            val distance = distanceTo(candidates[i])
            if (distance < minDistance) {
                minDistance = distance
                nearest = candidates[i]
                
                if (distance < 0.001) break
            }
        }
        
        return nearest
    }
    
    /**
     * PERFORMANCE: Get coordinates within specified radius using spatial filtering
     */
    fun getCoordinatesWithinRadius(candidates: List<Coordinate>, radiusKm: Double): List<Coordinate> {
        val results = mutableListOf<Coordinate>()
        
        if (candidates.size > 100) {
            val boundingBox = calculateBoundingBox(radiusKm)
            
            for (candidate in candidates) {
                if (candidate.latitude >= boundingBox.first &&
                    candidate.latitude <= boundingBox.second &&
                    candidate.longitude >= boundingBox.third &&
                    candidate.longitude <= boundingBox.fourth) {
                    
                    if (isWithinDistance(candidate, radiusKm)) {
                        results.add(candidate)
                    }
                }
            }
        } else {
            for (candidate in candidates) {
                if (isWithinDistance(candidate, radiusKm)) {
                    results.add(candidate)
                }
            }
        }
        
        return results
    }
    
    /**
     * PERFORMANCE: Calculate bearing to another coordinate
     */
    fun bearingTo(other: Coordinate): Double {
        val lat1Rad = latitude * DEGREES_TO_RADIANS
        val lat2Rad = other.latitude * DEGREES_TO_RADIANS
        val deltaLonRad = (other.longitude - longitude) * DEGREES_TO_RADIANS
        
        val y = sin(deltaLonRad) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(deltaLonRad)
        
        val bearingRad = atan2(y, x)
        val bearingDeg = bearingRad * RADIANS_TO_DEGREES
        
        return (bearingDeg + 360) % 360
    }
    
    private fun calculateBoundingBox(radiusKm: Double): Quadruple<Double, Double, Double, Double> {
        val deltaLat = radiusKm / 111.32
        val deltaLon = radiusKm / (111.32 * cos(latitude * DEGREES_TO_RADIANS))
        
        return Quadruple(
            maxOf(-90.0, latitude - deltaLat),
            minOf(90.0, latitude + deltaLat),
            maxOf(-180.0, longitude - deltaLon),
            minOf(180.0, longitude + deltaLon)
        )
    }
    
    /**
     * PERFORMANCE: Optimized equality components with pre-calculated values
     */
    override fun getEqualityComponents(): List<Any?> {
        return listOf(latitude, longitude)
    }
    
    /**
     * PERFORMANCE: Use pre-calculated hash code
     */
    override fun hashCode(): Int = preCalculatedHashCode
    
    /**
     * PERFORMANCE: Cached string representation to avoid repeated formatting
     */
    override fun toString(): String {
        val key = Pair(latitude, longitude)
        return stringCache.getOrPut(key) {
            "${String.format("%.6f", latitude)}, ${String.format("%.6f", longitude)}"
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)