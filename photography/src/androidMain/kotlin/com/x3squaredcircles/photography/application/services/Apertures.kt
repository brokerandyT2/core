// com/x3squaredcircles/photography/application/services/Apertures.kt
package com.x3squaredcircles.photography.application.services

object Apertures {
    val thirds = arrayOf(
        "f/1",
        "f/1.1",
        "f/1.3",
        "f/1.4",
        "f/1.6",
        "f/1.8",
        "f/2",
        "f/2.2",
        "f/2.5",
        "f/2.8",
        "f/3.2",
        "f/3.6",
        "f/4",
        "f/4.5",
        "f/5",
        "f/5.6",
        "f/6.3",
        "f/7.1",
        "f/8",
        "f/9",
        "f/10.1",
        "f/11",
        "f/12.7",
        "f/14.3",
        "f/16",
        "f/18",
        "f/20.2",
        "f/22",
        "f/25.4",
        "f/28.5",
        "f/32",
        "f/36",
        "f/40.3",
        "f/45",
        "f/50.8",
        "f/57",
        "f/64"
    )

    val halves = arrayOf(
        "f/1",
        "f/1.2",
        "f/1.4",
        "f/2",
        "f/2.4",
        "f/2.8",
        "f/3.4",
        "f/4",
        "f/4.8",
        "f/5.6",
        "f/6.7",
        "f/8",
        "f/9.5",
        "f/11",
        "f/13.5",
        "f/16",
        "f/19",
        "f/22",
        "f/26.9",
        "f/32",
        "f/38.1",
        "f/45",
        "f/53.8",
        "f/64"
    )

    val full = arrayOf(
        "f/1",
        "f/1.4",
        "f/2",
        "f/2.8",
        "f/4",
        "f/5.6",
        "f/8",
        "f/11",
        "f/16",
        "f/22",
        "f/32",
        "f/45",
        "f/64"
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