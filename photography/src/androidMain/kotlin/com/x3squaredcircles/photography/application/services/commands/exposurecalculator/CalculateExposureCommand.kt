// com/x3squaredcircles/photography/application/commands/exposurecalculator/CalculateExposureCommand.kt
package com.x3squaredcircles.photography.application.commands.exposurecalculator

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommand
import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.photography.application.services.models.ExposureTriangleDto
import com.x3squaredcircles.photography.application.services.models.ExposureSettingsDto
import com.x3squaredcircles.photography.application.services.models.ExposureIncrements
import com.x3squaredcircles.photography.application.services.FixedValue
import com.x3squaredcircles.photography.application.services.IExposureCalculatorService

data class CalculateExposureCommand(
    val baseExposure: ExposureTriangleDto,
    val targetAperture: String,
    val targetShutterSpeed: String,
    val targetIso: String,
    val increments: ExposureIncrements,
    val toCalculate: FixedValue,
    val evCompensation: Double
) : ICommand<Result<ExposureSettingsDto>>

class CalculateExposureCommandHandler(
    private val exposureCalculatorService: IExposureCalculatorService
) : ICommandHandler<CalculateExposureCommand, Result<ExposureSettingsDto>> {

    override suspend fun handle(request: CalculateExposureCommand): Result<ExposureSettingsDto> {
        return try {
            when (request.toCalculate) {
                FixedValue.ShutterSpeeds -> {
                    exposureCalculatorService.calculateShutterSpeedAsync(
                        request.baseExposure,
                        request.targetAperture,
                        request.targetIso,
                        request.increments,
                        evCompensation = request.evCompensation
                    )
                }
                FixedValue.Aperture -> {
                    exposureCalculatorService.calculateApertureAsync(
                        request.baseExposure,
                        request.targetShutterSpeed,
                        request.targetIso,
                        request.increments,
                        evCompensation = request.evCompensation
                    )
                }
                FixedValue.ISO -> {
                    exposureCalculatorService.calculateIsoAsync(
                        request.baseExposure,
                        request.targetShutterSpeed,
                        request.targetAperture,
                        request.increments,
                        evCompensation = request.evCompensation
                    )
                }
                else -> {
                    Result.failure("ExposureCalculator_Error_InvalidCalculationType")
                }
            }
        } catch (ex: Exception) {
            Result.failure("ExposureCalculator_Error_CalculatingExposure")
        }
    }
}