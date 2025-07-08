// core/src/commonMain/kotlin/com/x3squaredcircles/core/validators/GetLocationsQueryValidator.kt
package com.x3squaredcircles.core.validators

import com.x3squaredcircles.core.queries.GetLocationsQuery

class GetLocationsQueryValidator : IValidator<GetLocationsQuery> {
    override fun validate(value: GetLocationsQuery): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (value.pageNumber <= 0) {
            errors.add("Pagination_ValidationError_PageNumberRequired")
        }
        
        if (value.pageSize <= 0) {
            errors.add("Pagination_ValidationError_PageSizeRequired")
        } else if (value.pageSize > 100) {
            errors.add("Pagination_ValidationError_PageSizeMaximum")
        }
        
        if (!value.searchTerm.isNullOrBlank() && value.searchTerm.length > 100) {
            errors.add("Search_ValidationError_SearchTermMaxLength")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }
}