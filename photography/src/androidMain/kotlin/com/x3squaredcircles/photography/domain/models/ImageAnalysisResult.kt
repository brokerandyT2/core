// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/ImageAnalysisResult.kt
package com.x3squaredcircles.photography.domain.models

/**
 * Comprehensive image analysis result containing all statistical and visual information
 */
data class ImageAnalysisResult(
    // Basic image information
    val width: Int,
    val height: Int,
    val totalPixels: Int,
    
    // Color channel histograms (256 bins each)
    val redHistogram: HistogramData,
    val greenHistogram: HistogramData,
    val blueHistogram: HistogramData,
    val luminanceHistogram: HistogramData,
    
    // White balance information
    val whiteBalance: WhiteBalanceAnalysis,
    
    // Contrast analysis
    val contrast: ContrastAnalysis,
    
    // Exposure analysis
    val exposure: ExposureAnalysis,
    
    // Clipping detection
    val hasHighlightClipping: Boolean,
    val hasShadowClipping: Boolean,
    val highlightClippingPercentage: Double,
    val shadowClippingPercentage: Double,
    
    // Statistical information
    val statistics: ImageStatistics,
    
    // Quality metrics
    val quality: QualityMetrics
)

/**
 * Histogram data with statistical information
 */
data class HistogramData(
    val bins: DoubleArray,
    val mean: Double,
    val standardDeviation: Double,
    val min: Int,
    val max: Int,
    val median: Double,
    val mode: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as HistogramData

        if (!bins.contentEquals(other.bins)) return false
        if (mean != other.mean) return false
        if (standardDeviation != other.standardDeviation) return false
        if (min != other.min) return false
        if (max != other.max) return false
        if (median != other.median) return false
        if (mode != other.mode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bins.contentHashCode()
        result = 31 * result + mean.hashCode()
        result = 31 * result + standardDeviation.hashCode()
        result = 31 * result + min
        result = 31 * result + max
        result = 31 * result + median.hashCode()
        result = 31 * result + mode
        return result
    }
}

/**
 * White balance analysis information
 */
data class WhiteBalanceAnalysis(
    val colorTemperature: Double,
    val tint: Double,
    val confidence: Double,
    val suggestedAdjustments: WhiteBalanceSuggestion
)

/**
 * White balance adjustment suggestions
 */
data class WhiteBalanceSuggestion(
    val temperatureAdjustment: Double,
    val tintAdjustment: Double,
    val confidence: Double,
    val description: String
)

/**
 * Contrast analysis information
 */
data class ContrastAnalysis(
    val rmsContrast: Double,
    val michelsonContrast: Double,
    val dynamicRange: Double,
    val contrastRating: ContrastRating,
    val suggestions: List<String>
)

/**
 * Contrast rating enumeration
 */
enum class ContrastRating {
    VeryLow,
    Low,
    Moderate,
    High,
    VeryHigh
}

/**
 * Exposure analysis information
 */
data class ExposureAnalysis(
    val overallBrightness: Double,
    val exposureValue: Double,
    val exposureRating: ExposureRating,
    val recommendations: List<String>,
    val isUnderexposed: Boolean,
    val isOverexposed: Boolean,
    val optimalExposureAdjustment: Double
)

/**
 * Exposure rating enumeration
 */
enum class ExposureRating {
    Underexposed,
    SlightlyUnderexposed,
    Optimal,
    SlightlyOverexposed,
    Overexposed
}

/**
 * Overall image statistics
 */
data class ImageStatistics(
    val averageRed: Double,
    val averageGreen: Double,
    val averageBlue: Double,
    val averageLuminance: Double,
    val colorfulness: Double,
    val saturation: Double,
    val dominantColors: List<DominantColor>
)

/**
 * Dominant color information
 */
data class DominantColor(
    val red: Int,
    val green: Int,
    val blue: Int,
    val percentage: Double,
    val colorName: String
)

/**
 * Image quality metrics
 */
data class QualityMetrics(
    val sharpness: Double,
    val noise: Double,
    val overallQuality: QualityRating,
    val recommendedImprovements: List<String>
)

/**
 * Quality rating enumeration
 */
enum class QualityRating {
    Poor,
    Fair,
    Good,
    Excellent
}