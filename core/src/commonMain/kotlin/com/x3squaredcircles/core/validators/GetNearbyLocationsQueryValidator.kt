// core/src/commonMain/kotlin/com/x3squaredcircles/core/validators/GetNearbyLocationsQueryValidator.kt
package com.x3squaredcircles.core.validators

import com.x3squaredcircles.core.queries.GetNearbyLocationsQuery

class GetNearbyLocationsQueryValidator : IValidator<GetNearbyLocationsQuery> {
    override fun validate(value: GetNearbyLocationsQuery): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (value.latitude < -90.0 || value.latitude > 90.0) {
            errors.add("Location_ValidationError_LatitudeRange")
        }
        
        if (value.longitude < -180.0 || value.longitude > 180.0) {
            errors.add("Location_ValidationError_LongitudeRange")
        }
        
        if (value.distanceKm <= 0.0) {
            errors.add("Location_ValidationError_DistanceRequired")
        } else if (value.distanceKm > 100.0) {
            errors.add("Location_ValidationError_DistanceMaximum")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
}