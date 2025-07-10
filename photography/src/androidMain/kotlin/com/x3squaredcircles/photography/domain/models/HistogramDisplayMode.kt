// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/HistogramDisplayMode.kt
package com.x3squaredcircles.photography.domain.models

/**
 * Enumeration for different histogram display modes
 */
enum class HistogramDisplayMode(val displayName: String, val colorCode: String) {
    /**
     * Red channel histogram
     */
    Red("Red Channel", "#FF0000"),
    
    /**
     * Green channel histogram
     */
    Green("Green Channel", "#00FF00"),
    
    /**
     * Blue channel histogram
     */
    Blue("Blue Channel", "#0000FF"),
    
    /**
     * Luminance (brightness) histogram
     */
    Luminance("Luminance", "#FFFFFF"),
    
    /**
     * RGB composite histogram
     */
    RGB("RGB Composite", "#888888"),
    
    /**
     * All channels overlaid
     */
    All("All Channels", "#FFFFFF");
    
    /**
     * Get the corresponding color for histogram visualization
     */
    fun getVisualizationColor(): Int {
        return when (this) {
            Red -> 0xFFFF0000.toInt()
            Green -> 0xFF00FF00.toInt()
            Blue -> 0xFF0000FF.toInt()
            Luminance -> 0xFFFFFFFF.toInt()
            RGB -> 0xFF888888.toInt()
            All -> 0xFFFFFFFF.toInt()
        }
    }
    
    /**
     * Get alpha value for overlay mode
     */
    fun getAlphaValue(): Float {
        return when (this) {
            All -> 0.7f
            else -> 1.0f
        }
    }
    
    /**
     * Check if this mode should show multiple channels
     */
    fun isMultiChannel(): Boolean {
        return this == RGB || this == All
    }
    
    /**
     * Get the histogram data selector function
     */
    fun getHistogramSelector(): (ImageAnalysisResult) -> HistogramData {
        return when (this) {
            Red -> { result -> result.redHistogram }
            Green -> { result -> result.greenHistogram }
            Blue -> { result -> result.blueHistogram }
            Luminance -> { result -> result.luminanceHistogram }
            RGB -> { result -> result.luminanceHistogram } // Use luminance for RGB composite
            All -> { result -> result.luminanceHistogram } // Use luminance as base for all channels
        }
    }
    
    companion object {
        /**
         * Get all single channel modes
         */
        fun getSingleChannelModes(): List<HistogramDisplayMode> {
            return listOf(Red, Green, Blue, Luminance)
        }
        
        /**
         * Get all composite modes
         */
        fun getCompositeModes(): List<HistogramDisplayMode> {
            return listOf(RGB, All)
        }
        
        /**
         * Parse mode from string (case-insensitive)
         */
        fun fromString(value: String): HistogramDisplayMode? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
        
        /**
         * Get default mode
         */
        fun getDefault(): HistogramDisplayMode = Luminance
    }
}