// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/application/services/IMeteorShowerService.kt
package com.x3squaredcircles.photography.application.services


import kotlinx.datetime.LocalDate
import com.x3squaredcircles.photography.domain.entities.MeteorShower

interface IMeteorShowerService {
    suspend fun getActiveShowersAsync(date: LocalDate): List<MeteorShower>
    suspend fun getShowerByCodeAsync(code: String): MeteorShower?
}