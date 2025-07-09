// com/x3squaredcircles/photography/application/services/ISOs.kt
package com.x3squaredcircles.photography.application.services

object ISOs {
    val thirds = arrayOf(
        "50",
        "70",
        "100",
        "125",
        "160",
        "200",
        "250",
        "320",
        "400",
        "500",
        "640",
        "800",
        "1000",
        "1250",
        "1600",
        "2000",
        "2500",
        "3200",
        "4000",
        "5000",
        "6400",
        "8000",
        "10000",
        "12800",
        "16000",
        "20000",
        "25600"
    )

    val halves = arrayOf(
        "50",
        "70",
        "100",
        "140",
        "200",
        "280",
        "400",
        "560",
        "800",
        "1100",
        "1600",
        "2200",
        "3200",
        "3600",
        "4400",
        "6400",
        "8800",
        "12800",
        "17600",
        "25600"
    )

    val full = arrayOf(
        "50",
        "100",
        "200",
        "400",
        "800",
        "1600",
        "3200",
        "6400",
        "12800",
        "25600"
    )

    fun getScale(step: String): Array<String> {
        return when (step) {
            "Full" -> full
            "Half" -> halves
            "Third" -> thirds
            else -> full
        }
    }
}