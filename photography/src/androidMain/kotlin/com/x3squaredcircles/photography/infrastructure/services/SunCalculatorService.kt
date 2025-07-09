// com/x3squaredcircles/photography/infrastructure/services/SunCalculatorService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.photography.domain.services.ISunCalculatorService
import io.github.cosinekitty.astronomy.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.runBlocking
import kotlin.math.*

class SunCalculatorService : ISunCalculatorService {
    
    private val calculationCache = mutableMapOf<String, Pair<Any, Long>>()
    private val cacheTimeout = 30 * 60 * 1000L // 30 minutes in milliseconds
    private val cacheMutex = Mutex()
    
    private suspend fun <T> getCachedOrCalculate(cacheKey: String, calculation: () -> T): T {
        return cacheMutex.withLock {
            val cached = calculationCache[cacheKey]
            if (cached != null && System.currentTimeMillis() < cached.second) {
                @Suppress("UNCHECKED_CAST")
                cached.first as T
            } else {
                val result = calculation()
                calculationCache[cacheKey] = Pair(result as Any, System.currentTimeMillis() + cacheTimeout)
                result
            }
        }
    }
    
    private fun timestampToAstroTime(timestamp: Long): AstroTime {
        val julianDays = (timestamp / 86400000.0) + 2440587.5
        return AstroTime.fromTerrestrialTime(julianDays)
    }
    
    private fun astroTimeToTimestamp(astroTime: AstroTime): Long {
        val julianDays = astroTime.tt
        return ((julianDays - 2440587.5) * 86400000.0).toLong()
    }
    
    private fun createObserver(latitude: Double, longitude: Double, height: Double = 0.0): Observer {
        return Observer(latitude, longitude, height)
    }
    
    private fun searchSunEvent(observer: Observer, startTime: AstroTime, direction: Direction): AstroTime? {
        return try {
            searchRiseSet(Body.Sun, observer, direction, startTime, 1.0)?.time
        } catch (e: Exception) {
            null
        }
    }
    
    private fun searchTwilight(observer: Observer, startTime: AstroTime, direction: Direction, altitude: Double): AstroTime? {
        return try {
            searchAltitude(Body.Sun, observer, direction, startTime, 1.0, altitude)?.time
        } catch (e: Exception) {
            null
        }
    }
    
    override fun getSunrise(date: Long, latitude: Double, longitude: Double, timezone: String): Long {
        val cacheKey = "sunrise_${date / 86400000L}_${latitude}_${longitude}"
        return runBlocking {
            getCachedOrCalculate(cacheKey) {
                val observer = createObserver(latitude, longitude)
                val startTime = timestampToAstroTime(date)
                val sunriseTime = searchSunEvent(observer, startTime, Direction.Rise)
                
                sunriseTime?.let { astroTimeToTimestamp(it) } ?: date
            }
        }
    }
    
    override fun getSunriseEnd(date: Long, latitude: Double, longitude: Double, timezone: String): Long {
        val sunrise = getSunrise(date, latitude, longitude, timezone)
        return sunrise + (3 * 60 * 1000L) // Add 3 minutes
    }
    
    override fun getSunsetStart(date: Long, latitude: Double, longitude: Double, timezone: String): Long {
        val sunset = getSunset(date, latitude, longitude, timezone)
        return sunset - (3 * 60 * 1000L) // Subtract 3 minutes
    }
    
    override fun getSunset(date: Long, latitude: Double, longitude: Double, timezone: String): Long {
        val cacheKey = "sunset_${date / 86400000L}_${latitude}_${longitude}"
        return runBlocking {
            getCachedOrCalculate(cacheKey) {
                val observer = createObserver(latitude, longitude)
                val startTime = timestampToAstroTime(date)
                val sunsetTime = searchSunEvent(observer, startTime, Direction.Set)
                
                sunsetTime?.let { astroTimeToTimestamp(it) } ?: date
            }
        }
    }
    
    override fun getSolarNoon(date: Long, latitude: Double, longitude: Double, timezone: String): Long {
        val cacheKey = "solarnoon_${date / 86400000L}_${latitude}_${longitude}"
        return runBlocking {
            getCachedOrCalculate(cacheKey) {
                val observer = createObserver(latitude, longitude)
                val startTime = timestampToAstroTime(date)
                
                try {
                    val noonEvent = searchHourAngle(Body.Sun, observer, 0.0, startTime)
                    noonEvent?.let { astroTimeToTimestamp(it) } ?: date
                } catch (e: Exception) {
                    date
                }
            }
        }
    }
    
    override fun getNadir(date: Long, latitude: Double, longitude: Double, timezone: String): Long {
        val solarNoon = getSolarNoon(date, latitude, longitude, timezone)
        return solarNoon + (12 * 60 * 60 * 1000L) // Add 12 hours
    }
    
    override fun getCivilDawn(date: Long, latitude: Double, longitude: Double, timezone: String): Long {
        val cacheKey = "civildawn_${date / 86400000L}_${latitude}_${longitude}"
        return runBlocking {
            getCachedOrCalculate(cacheKey) {
                val observer = createObserver(latitude, longitude)
                val startTime = timestampToAstroTime(date)
                val civilDawnTime = searchTwilight(observer, startTime, Direction.Rise, -6.0)
                
                civilDawnTime?.let { astroTimeToTimestamp(it) } ?: date
            }
        }
    }
    
    override fun getCivilDusk(date: Long, latitude: Double, longitude: Double, timezone: String): Long {
        val cacheKey = "civildusk_${date / 86400000L}_${latitude}_${longitude}"
        return runBlocking {
            getCachedOrCalculate(cacheKey) {
                val observer = createObserver(latitude, longitude)
                val startTime = timestampToAstroTime(date)
                val civilDuskTime = searchTwilight(observer, startTime, Direction.Set, -6.0)
                
                civilDuskTime?.let { astroTimeToTimestamp(it) } ?: date
            }
        }
    }
    
    override fun getNauticalDawn(date: Long, latitude: Double, longitude: Double, timezone: String): Long {
        val cacheKey = "nauticaldawn_${date / 86400000L}_${latitude}_${longitude}"
        return runBlocking {
            getCachedOrCalculate(cacheKey) {
                val observer = createObserver(latitude, longitude)
                val startTime = timestampToAstroTime(date)
                val nauticalDawnTime = searchTwilight(observer, startTime, Direction.Rise, -12.0)
                
                nauticalDawnTime?.let { astroTimeToTimestamp(it) } ?: date
            }
        }
    }
    
    override fun getNauticalDusk(date: Long, latitude: Double, longitude: Double, timezone: String): Long {
        val cacheKey = "nauticaldusk_${date / 86400000L}_${latitude}_${longitude}"
        return runBlocking {
            getCachedOrCalculate(cacheKey) {
                val observer = createObserver(latitude, longitude)
                val startTime = timestampToAstroTime(date)
                val nauticalDuskTime = searchTwilight(observer, startTime, Direction.Set, -12.0)
                
                nauticalDuskTime?.let { astroTimeToTimestamp(it) } ?: date
            }
        }
    }
    
    override fun getAstronomicalDawn(date: Long, latitude: Double, longitude: Double, timezone: String): Long {
        val cacheKey = "astronomicaldawn_${date / 86400000L}_${latitude}_${longitude}"
        return runBlocking {
            getCachedOrCalculate(cacheKey) {
                val observer = createObserver(latitude, longitude)
                val startTime = timestampToAstroTime(date)
                val astronomicalDawnTime = searchTwilight(observer, startTime, Direction.Rise, -18.0)
                
                astronomicalDawnTime?.let { astroTimeToTimestamp(it) } ?: date
            }
        }
    }
    
    override fun getAstronomicalDusk(date: Long, latitude: Double, longitude: Double, timezone: String): Long {
        val cacheKey = "astronomicaldusk_${date / 86400000L}_${latitude}_${longitude}"
        return runBlocking {
            getCachedOrCalculate(cacheKey) {
                val observer = createObserver(latitude, longitude)
                val startTime = timestampToAstroTime(date)
                val astronomicalDuskTime = searchTwilight(observer, startTime, Direction.Set, -18.0)
                
                astronomicalDuskTime?.let { astroTimeToTimestamp(it) } ?: date
            }
        }
    }
    
    override fun getGoldenHourStart(date: Long, latitude: Double, longitude: Double, timezone: String): Long {
        val sunset = getSunset(date, latitude, longitude, timezone)
        return sunset - (60 * 60 * 1000L) // Subtract 1 hour
    }
    
    override fun getGoldenHourEnd(date: Long, latitude: Double, longitude: Double, timezone: String): Long {
        val sunrise = getSunrise(date, latitude, longitude, timezone)
        return sunrise + (60 * 60 * 1000L) // Add 1 hour
    }
    
    override fun getBlueHourStart(date: Long, latitude: Double, longitude: Double, timezone: String): Long {
        return getCivilDusk(date, latitude, longitude, timezone)
    }
    
    override fun getBlueHourEnd(date: Long, latitude: Double, longitude: Double, timezone: String): Long {
        return getCivilDawn(date, latitude, longitude, timezone)
    }
    
    override fun getSolarAzimuth(dateTime: Long, latitude: Double, longitude: Double, timezone: String): Double {
        val cacheKey = "azimuth_${dateTime}_${latitude}_${longitude}"
        return runBlocking {
            getCachedOrCalculate(cacheKey) {
                val observer = createObserver(latitude, longitude)
                val astroTime = timestampToAstroTime(dateTime)
                
                try {
                    val horizontal = horizon(astroTime, observer, Body.Sun, Refraction.Normal)
                    horizontal.azimuth
                } catch (e: Exception) {
                    0.0
                }
            }
        }
    }
    
    override fun getSolarElevation(dateTime: Long, latitude: Double, longitude: Double, timezone: String): Double {
        val cacheKey = "elevation_${dateTime}_${latitude}_${longitude}"
        return runBlocking {
            getCachedOrCalculate(cacheKey) {
                val observer = createObserver(latitude, longitude)
                val astroTime = timestampToAstroTime(dateTime)
                
                try {
                    val horizontal = horizon(astroTime, observer, Body.Sun, Refraction.Normal)
                    horizontal.altitude
                } catch (e: Exception) {
                    0.0
                }
            }
        }
    }
    
    override fun getMoonrise(date: Long, latitude: Double, longitude: Double, timezone: String): Long? {
        val cacheKey = "moonrise_${date / 86400000L}_${latitude}_${longitude}"
        return runBlocking {
            getCachedOrCalculate(cacheKey) {
                val observer = createObserver(latitude, longitude)
                val startTime = timestampToAstroTime(date)
                
                try {
                    val moonriseTime = searchRiseSet(Body.Moon, observer, Direction.Rise, startTime, 1.0)?.time
                    moonriseTime?.let { astroTimeToTimestamp(it) }
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
    
    override fun getMoonset(date: Long, latitude: Double, longitude: Double, timezone: String): Long? {
        val cacheKey = "moonset_${date / 86400000L}_${latitude}_${longitude}"
        return runBlocking {
            getCachedOrCalculate(cacheKey) {
                val observer = createObserver(latitude, longitude)
                val startTime = timestampToAstroTime(date)
                
                try {
                    val moonsetTime = searchRiseSet(Body.Moon, observer, Direction.Set, startTime, 1.0)?.time
                    moonsetTime?.let { astroTimeToTimestamp(it) }
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
    
    override fun getMoonPhase(date: Long, latitude: Double, longitude: Double, timezone: String): Double {
        val cacheKey = "moonphase_${date / 86400000L}"
        return runBlocking {
            getCachedOrCalculate(cacheKey) {
                val astroTime = timestampToAstroTime(date)
                
                try {
                    moonPhase(astroTime)
                } catch (e: Exception) {
                    0.0
                }
            }
        }
    }
    
    override fun getMoonIllumination(date: Long, latitude: Double, longitude: Double, timezone: String): Double {
        val cacheKey = "moonillumination_${date / 86400000L}"
        return runBlocking {
            getCachedOrCalculate(cacheKey) {
                val astroTime = timestampToAstroTime(date)
                
                try {
                    val illumination = illumination(Body.Moon, astroTime)
                    illumination.phaseFraction
                } catch (e: Exception) {
                    0.0
                }
            }
        }
    }
    
    override fun getMoonAzimuth(dateTime: Long, latitude: Double, longitude: Double, timezone: String): Double {
        val cacheKey = "moonazimuth_${dateTime}_${latitude}_${longitude}"
        return runBlocking {
            getCachedOrCalculate(cacheKey) {
                val observer = createObserver(latitude, longitude)
                val astroTime = timestampToAstroTime(dateTime)
                
                try {
                    val horizontal = horizon(astroTime, observer, Body.Moon, Refraction.Normal)
                    horizontal.azimuth
                } catch (e: Exception) {
                    0.0
                }
            }
        }
    }
    
    override fun getMoonElevation(dateTime: Long, latitude: Double, longitude: Double, timezone: String): Double {
        val cacheKey = "moonelevation_${dateTime}_${latitude}_${longitude}"
        return runBlocking {
            getCachedOrCalculate(cacheKey) {
                val observer = createObserver(latitude, longitude)
                val astroTime = timestampToAstroTime(dateTime)
                
                try {
                    val horizontal = horizon(astroTime, observer, Body.Moon, Refraction.Normal)
                    horizontal.altitude
                } catch (e: Exception) {
                    0.0
                }
            }
        }
    }
    
    override fun isMoonUp(dateTime: Long, latitude: Double, longitude: Double, timezone: String): Boolean {
        val elevation = getMoonElevation(dateTime, latitude, longitude, timezone)
        return elevation > 0.0
    }
}