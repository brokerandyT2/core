// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/MeteorShowerDataService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.core.infrastructure.services.ILoggingService
import com.x3squaredcircles.photography.application.common.interfaces.IMeteorShowerDataService
import com.x3squaredcircles.photography.domain.entities.MeteorShower
import com.x3squaredcircles.photography.domain.entities.MeteorShowerData
import kotlinx.coroutines.Job
import kotlinx.datetime.LocalDate

class MeteorShowerDataService(
    private val loggingService: ILoggingService,
    private val parser: StellariumMeteorShowerParser
) : IMeteorShowerDataService {

    companion object {
        private const val RESOURCE_PATH = "meteor_showers.json"
        
        // Lazy-loaded data with thread safety
        private val meteorShowerData by lazy { loadMeteorShowerData() }
        
        private fun loadMeteorShowerData(): MeteorShowerData {
            return try {
                // Load embedded JSON resource
                val jsonContent = loadEmbeddedResource(RESOURCE_PATH)
                
                if (jsonContent.isNullOrEmpty()) {
                    return MeteorShowerData()
                }

                // Parse using the Stellarium parser
                val tempParser = StellariumMeteorShowerParser(
                    object : ILoggingService {
                        override suspend fun logToDatabaseAsync(level: com.x3squaredcircles.core.enums.LogLevel, message: String, exception: Exception?) {}
                        override suspend fun getLogsAsync(count: Int) = emptyList<com.x3squaredcircles.core.infrastructure.external.models.LogEntry>()
                        override suspend fun clearLogsAsync() {}
                        override fun logInfo(message: String) { println("INFO: $message") }
                        override fun logDebug(message: String) { println("DEBUG: $message") }
                        override fun logWarning(message: String, exception: Exception?) { println("WARN: $message") }
                        override fun logError(message: String, exception: Exception?) { println("ERROR: $message") }
                    }
                )

                val data = kotlinx.coroutines.runBlocking { tempParser.parseStellariumData(jsonContent) }
                data
            } catch (ex: Exception) {
                println("Critical error loading meteor shower data from embedded resource: ${ex.message}")
                MeteorShowerData()
            }
        }

        private fun loadEmbeddedResource(resourcePath: String): String? {
            return try {
                // In KMM, we would typically use platform-specific resource loading
                // This is a placeholder that would need platform-specific implementation
                this::class.java.classLoader?.getResourceAsStream(resourcePath)?.use { stream ->
                    stream.bufferedReader().readText()
                }
            } catch (ex: Exception) {
                println("Failed to load embedded resource $resourcePath: ${ex.message}")
                null
            }
        }
    }

    override suspend fun getActiveShowersAsync(date: LocalDate, cancellationToken: Job): List<MeteorShower> {
        return try {
            val data = getMeteorShowerDataAsync()
            val activeShowers = data.getActiveShowers(date)

            loggingService.logDebug("Found ${activeShowers.size} active meteor showers for $date")
            activeShowers
        } catch (ex: Exception) {
            loggingService.logError("Error getting active showers for date $date", ex)
            emptyList()
        }
    }

    override suspend fun getActiveShowersAsync(date: LocalDate, minZHR: Int, cancellationToken: Job): List<MeteorShower> {
        return try {
            val data = getMeteorShowerDataAsync()
            val activeShowers = data.getActiveShowers(date, minZHR)

            loggingService.logDebug("Found ${activeShowers.size} active meteor showers for $date with ZHR >= $minZHR")
            activeShowers
        } catch (ex: Exception) {
            loggingService.logError("Error getting active showers for date $date with ZHR >= $minZHR", ex)
            emptyList()
        }
    }

    override suspend fun getShowerByCodeAsync(code: String, cancellationToken: Job): MeteorShower? {
        return try {
            if (code.isBlank()) {
                loggingService.logWarning("getShowerByCodeAsync called with null or empty code")
                return null
            }

            val data = getMeteorShowerDataAsync()
            val shower = data.getShowerByCode(code)

            if (shower != null) {
                loggingService.logDebug("Found meteor shower: $code - ${shower.designation}")
            } else {
                loggingService.logDebug("Meteor shower not found for code: $code")
            }

            shower
        } catch (ex: Exception) {
            loggingService.logError("Error getting shower by code $code", ex)
            null
        }
    }

    override suspend fun getAllShowersAsync(cancellationToken: Job): List<MeteorShower> {
        return try {
            val data = getMeteorShowerDataAsync()
            loggingService.logDebug("Retrieved ${data.showers.size} total meteor showers")
            data.showers
        } catch (ex: Exception) {
            loggingService.logError("Error getting all meteor showers", ex)
            emptyList()
        }
    }

    override suspend fun getShowersInDateRangeAsync(startDate: LocalDate, endDate: LocalDate, cancellationToken: Job): List<MeteorShower> {
        return try {
            val data = getMeteorShowerDataAsync()
            val showersInRange = mutableListOf<MeteorShower>()
            val showerCodes = mutableSetOf<String>()
            
            // Use simple day counting instead of date arithmetic
            val startYear = startDate.year
            val startDayOfYear = startDate.dayOfYear
            val endYear = endDate.year
            val endDayOfYear = endDate.dayOfYear
            
            // For simplicity, check each shower's activity period against the range
            data.showers.forEach { shower ->
                // Check if shower overlaps with date range by checking activity periods
                val isInRange = checkShowerInDateRange(shower, startDate, endDate)
                if (isInRange && showerCodes.add(shower.code)) {
                    showersInRange.add(shower)
                }
            }
            
            loggingService.logDebug("Found ${showersInRange.size} meteor showers active between $startDate and $endDate")
            showersInRange.sortedBy { it.designation }
        } catch (ex: Exception) {
            loggingService.logError("Error getting showers in date range $startDate to $endDate", ex)
            emptyList()
        }
    }

    private fun checkShowerInDateRange(shower: MeteorShower, startDate: LocalDate, endDate: LocalDate): Boolean {
        // Simple check - if activity is active on start or end date, include it
        return shower.activity.isActiveOn(startDate) || shower.activity.isActiveOn(endDate)
    }

    override suspend fun isShowerActiveAsync(showerCode: String, date: LocalDate, cancellationToken: Job): Boolean {
        return try {
            val shower = getShowerByCodeAsync(showerCode, cancellationToken)
            val isActive = shower?.let { 
                // Check if shower is active by checking its activity period
                it.activity.isActiveOn(date)
            } ?: false
            
            loggingService.logDebug("Shower $showerCode is ${if (isActive) "active" else "inactive"} on $date")
            isActive
        } catch (ex: Exception) {
            loggingService.logError("Error checking if shower $showerCode is active on $date", ex)
            false
        }
    }

    override suspend fun getExpectedZHRAsync(showerCode: String, date: LocalDate, cancellationToken: Job): Double {
        return try {
            val shower = getShowerByCodeAsync(showerCode, cancellationToken)
            shower?.let {
                // Calculate expected ZHR based on activity period
                it.activity.getExpectedZHR(date).toDouble()
            } ?: 0.0
        } catch (ex: Exception) {
            loggingService.logError("Error getting expected ZHR for shower $showerCode on $date", ex)
            0.0
        }
    }

    private suspend fun getMeteorShowerDataAsync(): MeteorShowerData {
        return meteorShowerData
    }
}