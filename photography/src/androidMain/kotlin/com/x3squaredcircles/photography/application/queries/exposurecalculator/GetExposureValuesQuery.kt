// com/x3squaredcircles/photography/application/queries/exposurecalculator/GetExposureValuesQuery.kt
package com.x3squaredcircles.photography.application.queries.exposurecalculator

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IQuery
import com.x3squaredcircles.core.mediator.IQueryHandler

import com.x3squaredcircles.photography.application.services.IExposureCalculatorService
import com.x3squaredcircles.photography.application.services.models.ExposureIncrements

data class GetExposureValuesQuery(
    val increments: ExposureIncrements
) : IQuery<Result<ExposureValuesDto>>

data class ExposureValuesDto(
    val shutterSpeeds: Array<String>,
    val apertures: Array<String>,
    val isos: Array<String>
)

class GetExposureValuesQueryHandler(
    private val exposureCalculatorService: IExposureCalculatorService
) : IQueryHandler<GetExposureValuesQuery, Result<ExposureValuesDto>> {

    override suspend fun handle(request: GetExposureValuesQuery): Result<ExposureValuesDto> {
        return try {
            val shutterSpeedsResult = exposureCalculatorService.getShutterSpeedsAsync(request.increments)
            val aperturesResult = exposureCalculatorService.getAperturesAsync(request.increments)
            val isosResult = exposureCalculatorService.getIsosAsync(request.increments)

            if (!shutterSpeedsResult.isSuccess || !aperturesResult.isSuccess || !isosResult.isSuccess) {
                val errorMessage = when {
                    !shutterSpeedsResult.isSuccess -> (shutterSpeedsResult as Result.Failure).errorMessage
                    !aperturesResult.isSuccess -> (aperturesResult as Result.Failure).errorMessage
                    !isosResult.isSuccess -> (isosResult as Result.Failure).errorMessage
                    else -> "ExposureCalculator_Error_RetrievingValues"
                }
                return Result.failure(errorMessage)
            }

            val result = ExposureValuesDto(
                shutterSpeeds = shutterSpeedsResult.data!!,
                apertures = aperturesResult.data!!,
                isos = isosResult.data!!
            )

            Result.success(result)
        } catch (ex: Exception) {
            Result.failure("ExposureCalculator_Error_RetrievingValues")
        }
    }
}