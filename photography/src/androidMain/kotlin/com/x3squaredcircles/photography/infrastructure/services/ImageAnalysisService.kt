// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/ImageAnalysisService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.photography.application.services.IImageAnalysisService
import com.x3squaredcircles.photography.application.services.ImageMetadata
import com.x3squaredcircles.photography.application.services.WhiteBalanceInfo
import com.x3squaredcircles.photography.application.services.ClippingInfo
import com.x3squaredcircles.photography.domain.models.*
import com.x3squaredcircles.core.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.datetime.Clock
import korlibs.image.format.*
import korlibs.image.bitmap.*
import kotlin.math.*

class ImageAnalysisService : IImageAnalysisService {

    private val histogramCache = mutableMapOf<String, String>()
    
    companion object {
        private const val CALIBRATION_CONSTANT = 1.0
        private const val CACHE_HISTOGRAM_WIDTH = 512
        private const val CACHE_HISTOGRAM_HEIGHT = 256
        private const val HISTOGRAM_BINS = 256
    }

    override suspend fun analyzeImage(
        imageData: ByteArray,
        progressCallback: ((Double) -> Unit)?
    ): ImageAnalysisResult = withContext(Dispatchers.Default) {
        
        progressCallback?.invoke(0.1)
        
        // Use Korim to decode the image
        val bitmap = try {
            imageData.decodeBitmap()
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to decode image: ${e.message}", e)
        }
        
        val width = bitmap.width
        val height = bitmap.height
        val totalPixels = width * height
        
        progressCallback?.invoke(0.3)
        
        // Get RGBA pixel data from Korim
        val rgba32 = bitmap.toBMP32()
        
        // Calculate histograms for each channel
        val redHistogram = DoubleArray(HISTOGRAM_BINS)
        val greenHistogram = DoubleArray(HISTOGRAM_BINS)
        val blueHistogram = DoubleArray(HISTOGRAM_BINS)
        val luminanceValues = mutableListOf<Double>()
        
        var redSum = 0.0
        var greenSum = 0.0
        var blueSum = 0.0
        
        // Process pixels and build histograms
        for (y in 0 until height) {
            for (x in 0 until width) {
                ensureActive()
                
                val pixel = rgba32[x, y]
                val red = pixel.r
                val green = pixel.g
                val blue = pixel.b
                
                redHistogram[red]++
                greenHistogram[green]++
                blueHistogram[blue]++
                
                redSum += red
                greenSum += green
                blueSum += blue
                
                // Calculate luminance using ITU-R BT.709 coefficients
                val luminance = calculateLuminance(red.toDouble(), green.toDouble(), blue.toDouble())
                luminanceValues.add(luminance)
            }
        }

        progressCallback?.invoke(0.6)

        // Build histogram data objects
        val redHistogramData = createHistogramData(redHistogram, totalPixels)
        val greenHistogramData = createHistogramData(greenHistogram, totalPixels)
        val blueHistogramData = createHistogramData(blueHistogram, totalPixels)

        // Calculate luminance histogram
        val luminanceHistogram = DoubleArray(HISTOGRAM_BINS)
        luminanceValues.forEach { luminance ->
            val bin = (luminance * 255).toInt().coerceIn(0, 255)
            luminanceHistogram[bin]++
        }
        val luminanceHistogramData = createHistogramData(luminanceHistogram, totalPixels)

        progressCallback?.invoke(0.8)

        // Calculate average color values
        val avgRed = redSum / totalPixels
        val avgGreen = greenSum / totalPixels
        val avgBlue = blueSum / totalPixels

        // Calculate white balance
        val whiteBalance = calculateWhiteBalance(avgRed, avgGreen, avgBlue)

        // Calculate contrast metrics
        val contrast = calculateContrastAnalysis(luminanceValues)

        // Calculate exposure analysis
        val exposure = calculateExposureAnalysis(luminanceValues, luminanceHistogramData)

        // Calculate clipping
        val shadowClippingPercentage = redHistogram[0] / totalPixels * 100
        val highlightClippingPercentage = redHistogram[255] / totalPixels * 100
        val hasHighlightClipping = highlightClippingPercentage > 1.0
        val hasShadowClipping = shadowClippingPercentage > 1.0

        // Calculate statistics
        val statistics = ImageStatistics(
            averageRed = avgRed,
            averageGreen = avgGreen,
            averageBlue = avgBlue,
            averageLuminance = luminanceValues.average(),
            colorfulness = calculateColorfulness(avgRed, avgGreen, avgBlue),
            saturation = calculateSaturation(avgRed, avgGreen, avgBlue),
            dominantColors = calculateDominantColors(redHistogram, greenHistogram, blueHistogram)
        )

        // Calculate quality metrics
        val quality = calculateQualityMetrics(contrast, exposure, hasHighlightClipping, hasShadowClipping)

        progressCallback?.invoke(1.0)

        ImageAnalysisResult(
            width = width,
            height = height,
            totalPixels = totalPixels,
            redHistogram = redHistogramData,
            greenHistogram = greenHistogramData,
            blueHistogram = blueHistogramData,
            luminanceHistogram = luminanceHistogramData,
            whiteBalance = whiteBalance,
            contrast = contrast,
            exposure = exposure,
            hasHighlightClipping = hasHighlightClipping,
            hasShadowClipping = hasShadowClipping,
            highlightClippingPercentage = highlightClippingPercentage,
            shadowClippingPercentage = shadowClippingPercentage,
            statistics = statistics,
            quality = quality
        )
    }

    override suspend fun generateHistogramImage(
        histogram: DoubleArray,
        mode: HistogramDisplayMode,
        width: Int,
        height: Int
    ): String = withContext(Dispatchers.Default) {

        // Generate a unique cache key
        val cacheKey = "${mode.name}_${histogram.contentHashCode()}_${width}x$height"

        // Check cache first
        histogramCache[cacheKey]?.let { return@withContext it }

        // Generate timestamp for unique filename
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val fileName = "${mode.name.lowercase()}_histogram_$timestamp.png"

        // Create histogram image
        val imagePath = createHistogramImageFile(histogram, mode, width, height, fileName)

        // Cache the result
        histogramCache[cacheKey] = imagePath

        imagePath
    }

    override suspend fun generateStackedHistogram(
        analysisResult: ImageAnalysisResult,
        width: Int,
        height: Int
    ): String = withContext(Dispatchers.Default) {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val fileName = "stacked_histogram_$timestamp.png"

        // Create stacked histogram image
        createStackedHistogramImageFile(
            redHistogram = analysisResult.redHistogram.bins,
            greenHistogram = analysisResult.greenHistogram.bins,
            blueHistogram = analysisResult.blueHistogram.bins,
            luminanceHistogram = analysisResult.luminanceHistogram.bins,
            width = width,
            height = height,
            fileName = fileName
        )
    }

    override fun clearHistogramCache() {
        histogramCache.clear()
    }

    override suspend fun analyzeImageMetadata(imageData: ByteArray): ImageMetadata? {
        return try {
            val bitmap = imageData.decodeBitmap()
            ImageMetadata(
                width = bitmap.width,
                height = bitmap.height,
                aperture = null, // Would need EXIF parsing
                shutterSpeed = null,
                iso = null,
                focalLength = null,
                camera = null,
                lens = null,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun calculateWhiteBalance(imageData: ByteArray): WhiteBalanceInfo {
        // For this implementation, we'll extract basic white balance from the full analysis
        val analysisResult = analyzeImage(imageData)
        return WhiteBalanceInfo(
            colorTemperature = analysisResult.whiteBalance.colorTemperature,
            tint = analysisResult.whiteBalance.tint,
            confidence = analysisResult.whiteBalance.confidence
        )
    }

    override suspend fun detectClipping(
        redHistogram: DoubleArray,
        greenHistogram: DoubleArray,
        blueHistogram: DoubleArray,
        threshold: Double
    ): ClippingInfo {
        val totalPixels = redHistogram.sum()

        val highlightClippingPercentage = (redHistogram[255] + greenHistogram[255] + blueHistogram[255]) / (totalPixels * 3) * 100
        val shadowClippingPercentage = (redHistogram[0] + greenHistogram[0] + blueHistogram[0]) / (totalPixels * 3) * 100

        return ClippingInfo(
            hasHighlightClipping = highlightClippingPercentage > threshold,
            hasShadowClipping = shadowClippingPercentage > threshold,
            highlightClippingPercentage = highlightClippingPercentage,
            shadowClippingPercentage = shadowClippingPercentage
        )
    }

    // Private helper methods

    private fun createHistogramData(histogram: DoubleArray, totalPixels: Int): HistogramData {
        val mean = calculateHistogramMean(histogram)
        val standardDeviation = calculateHistogramStandardDeviation(histogram, mean)
        val median = calculateHistogramMedian(histogram)
        val mode = histogram.indices.maxByOrNull { histogram[it] } ?: 0
        val min = histogram.indices.firstOrNull { histogram[it] > 0 } ?: 0
        val max = histogram.indices.lastOrNull { histogram[it] > 0 } ?: 255

        return HistogramData(
            bins = histogram,
            mean = mean,
            standardDeviation = standardDeviation,
            min = min,
            max = max,
            median = median,
            mode = mode
        )
    }

    private fun calculateLuminance(red: Double, green: Double, blue: Double): Double {
        // Convert to linear RGB
        val linearR = sRGBToLinear(red / 255.0)
        val linearG = sRGBToLinear(green / 255.0)
        val linearB = sRGBToLinear(blue / 255.0)

        // ITU-R BT.709 luminance coefficients
        return 0.2126 * linearR + 0.7152 * linearG + 0.0722 * linearB
    }

    private fun sRGBToLinear(srgb: Double): Double {
        return if (srgb <= 0.04045) {
            srgb / 12.92
        } else {
            ((srgb + 0.055) / 1.055).pow(2.4)
        }
    }

    private fun calculateWhiteBalance(avgRed: Double, avgGreen: Double, avgBlue: Double): WhiteBalanceAnalysis {
        // Convert RGB to CIE XYZ color space
        val (x, y, z) = rgbToXYZ(avgRed, avgGreen, avgBlue)

        // Convert XYZ to chromaticity coordinates
        val totalXYZ = x + y + z
        if (totalXYZ == 0.0) {
            return WhiteBalanceAnalysis(
                colorTemperature = 5500.0,
                tint = 0.0,
                confidence = 0.0,
                suggestedAdjustments = WhiteBalanceSuggestion(0.0, 0.0, 0.0, "Unable to determine white balance")
            )
        }

        val chromaticityX = x / totalXYZ
        val chromaticityY = y / totalXYZ

        // McCamy's approximation formula
        val n = (chromaticityX - 0.3320) / (0.1858 - chromaticityY)
        val cct = 449 * n.pow(3) + 3525 * n.pow(2) + 6823.3 * n + 5520.33

        val temperature = cct.coerceIn(2000.0, 25000.0)
        val tint = calculateTintValue(avgRed, avgGreen, avgBlue)
        val confidence = calculateWhiteBalanceConfidence(temperature, tint)

        val suggestion = generateWhiteBalanceSuggestion(temperature, tint, confidence)

        return WhiteBalanceAnalysis(
            colorTemperature = temperature,
            tint = tint,
            confidence = confidence,
            suggestedAdjustments = suggestion
        )
    }

    private fun rgbToXYZ(r: Double, g: Double, b: Double): Triple<Double, Double, Double> {
        // Normalize to 0-1 and apply gamma correction
        val linearR = sRGBToLinear(r / 255.0)
        val linearG = sRGBToLinear(g / 255.0)
        val linearB = sRGBToLinear(b / 255.0)

        // sRGB to XYZ transformation matrix (D65 illuminant)
        val x = linearR * 0.4124564 + linearG * 0.3575761 + linearB * 0.1804375
        val y = linearR * 0.2126729 + linearG * 0.7151522 + linearB * 0.0721750
        val z = linearR * 0.0193339 + linearG * 0.1191920 + linearB * 0.9503041

        return Triple(x, y, z)
    }

    private fun calculateTintValue(avgRed: Double, avgGreen: Double, avgBlue: Double): Double {
        val greenMagentaRatio = avgGreen / ((avgRed + avgBlue) / 2)
        return ((greenMagentaRatio - 1.0) * 2.0).coerceIn(-1.0, 1.0)
    }

    private fun calculateWhiteBalanceConfidence(temperature: Double, tint: Double): Double {
        val tempConfidence = when {
            temperature in 3000.0..7000.0 -> 0.9
            temperature in 2500.0..9000.0 -> 0.7
            else -> 0.5
        }

        val tintConfidence = when {
            abs(tint) < 0.1 -> 0.9
            abs(tint) < 0.3 -> 0.7
            else -> 0.5
        }

        return (tempConfidence + tintConfidence) / 2.0
    }

    private fun generateWhiteBalanceSuggestion(temperature: Double, tint: Double, confidence: Double): WhiteBalanceSuggestion {
        val description = when {
            temperature < 3000 -> "Very warm - consider cooling"
            temperature < 4000 -> "Warm - slight cooling recommended"
            temperature > 7000 -> "Very cool - consider warming"
            temperature > 6000 -> "Cool - slight warming recommended"
            else -> "Neutral temperature"
        }

        val tempAdjustment = when {
            temperature < 4000 -> 500.0
            temperature > 6000 -> -500.0
            else -> 0.0
        }

        val tintAdjustment = when {
            tint > 0.2 -> -0.1
            tint < -0.2 -> 0.1
            else -> 0.0
        }

        return WhiteBalanceSuggestion(
            temperatureAdjustment = tempAdjustment,
            tintAdjustment = tintAdjustment,
            confidence = confidence,
            description = description
        )
    }

    private fun calculateContrastAnalysis(luminanceValues: List<Double>): ContrastAnalysis {
        if (luminanceValues.isEmpty()) {
            return ContrastAnalysis(
                rmsContrast = 0.0,
                michelsonContrast = 0.0,
                dynamicRange = 0.0,
                contrastRating = ContrastRating.VeryLow,
                suggestions = listOf("No data available for contrast analysis")
            )
        }

        val mean = luminanceValues.average()
        val min = luminanceValues.minOrNull() ?: 0.0
        val max = luminanceValues.maxOrNull() ?: 1.0

        val rmsContrast = sqrt(luminanceValues.map { (it - mean).pow(2) }.average())
        val michelsonContrast = if (max + min > 0) (max - min) / (max + min) else 0.0
        val dynamicRange = if (min > 0) log10(max / min) * 3.32 else 0.0 // Convert to stops

        val contrastRating = when {
            rmsContrast < 0.1 -> ContrastRating.VeryLow
            rmsContrast < 0.2 -> ContrastRating.Low
            rmsContrast < 0.4 -> ContrastRating.Moderate
            rmsContrast < 0.6 -> ContrastRating.High
            else -> ContrastRating.VeryHigh
        }

        val suggestions = generateContrastSuggestions(contrastRating, dynamicRange)

        return ContrastAnalysis(
            rmsContrast = rmsContrast,
            michelsonContrast = michelsonContrast,
            dynamicRange = dynamicRange,
            contrastRating = contrastRating,
            suggestions = suggestions
        )
    }

    private fun generateContrastSuggestions(rating: ContrastRating, dynamicRange: Double): List<String> {
        return when (rating) {
            ContrastRating.VeryLow -> listOf("Increase contrast", "Consider HDR techniques")
            ContrastRating.Low -> listOf("Slight contrast adjustment may improve image")
            ContrastRating.Moderate -> listOf("Good contrast levels")
            ContrastRating.High -> listOf("Excellent contrast")
            ContrastRating.VeryHigh -> listOf("Very high contrast", "Check for clipping")
        }
    }

    private fun calculateExposureAnalysis(luminanceValues: List<Double>, histogramData: HistogramData): ExposureAnalysis {
        if (luminanceValues.isEmpty()) {
            return ExposureAnalysis(
                overallBrightness = 0.0,
                exposureValue = 0.0,
                exposureRating = ExposureRating.Underexposed,
                recommendations = listOf("No data available for exposure analysis"),
                isUnderexposed = true,
                isOverexposed = false,
                optimalExposureAdjustment = 0.0
            )
        }

        val mean = luminanceValues.average()
        val median = histogramData.median / 255.0

        val exposureValue = log(mean * CALIBRATION_CONSTANT, 2.0)
        val targetEV = log(0.18 * CALIBRATION_CONSTANT, 2.0) // 18% gray target

        val isUnderexposed = mean < 0.1
        val isOverexposed = mean > 0.8

        val exposureRating = when {
            mean < 0.1 -> ExposureRating.Underexposed
            mean < 0.3 -> ExposureRating.SlightlyUnderexposed
            mean > 0.8 -> ExposureRating.Overexposed
            mean > 0.6 -> ExposureRating.SlightlyOverexposed
            else -> ExposureRating.Optimal
        }

        val optimalAdjustment = targetEV - exposureValue
        val recommendations = generateExposureRecommendations(exposureRating, isUnderexposed, isOverexposed)

        return ExposureAnalysis(
            overallBrightness = mean,
            exposureValue = exposureValue,
            exposureRating = exposureRating,
            recommendations = recommendations,
            isUnderexposed = isUnderexposed,
            isOverexposed = isOverexposed,
            optimalExposureAdjustment = optimalAdjustment
        )
    }

    private fun generateExposureRecommendations(rating: ExposureRating, isUnderexposed: Boolean, isOverexposed: Boolean): List<String> {
        val recommendations = mutableListOf<String>()

        when (rating) {
            ExposureRating.Underexposed -> recommendations.add("Increase exposure by 1-2 stops")
            ExposureRating.SlightlyUnderexposed -> recommendations.add("Increase exposure by 0.5-1 stop")
            ExposureRating.Optimal -> recommendations.add("Excellent exposure")
            ExposureRating.SlightlyOverexposed -> recommendations.add("Decrease exposure by 0.5-1 stop")
            ExposureRating.Overexposed -> recommendations.add("Decrease exposure by 1-2 stops")
        }

        if (isUnderexposed) recommendations.add("Check for shadow clipping")
        if (isOverexposed) recommendations.add("Check for highlight clipping")

        return recommendations
    }

    private fun calculateColorfulness(avgRed: Double, avgGreen: Double, avgBlue: Double): Double {
        val max = maxOf(avgRed, avgGreen, avgBlue)
        val min = minOf(avgRed, avgGreen, avgBlue)
        return if (max > 0) (max - min) / max else 0.0
    }

    private fun calculateSaturation(avgRed: Double, avgGreen: Double, avgBlue: Double): Double {
        val max = maxOf(avgRed, avgGreen, avgBlue)
        val min = minOf(avgRed, avgGreen, avgBlue)
        val sum = avgRed + avgGreen + avgBlue
        return if (sum > 0) (max - min) / (sum / 3) else 0.0
    }

    private fun calculateDominantColors(redHist: DoubleArray, greenHist: DoubleArray, blueHist: DoubleArray): List<DominantColor> {
        // Simplified dominant color calculation
        val totalPixels = redHist.sum()
        val redPeak = redHist.indices.maxByOrNull { redHist[it] } ?: 0
        val greenPeak = greenHist.indices.maxByOrNull { greenHist[it] } ?: 0
        val bluePeak = blueHist.indices.maxByOrNull { blueHist[it] } ?: 0

        return listOf(
            DominantColor(
                red = redPeak,
                green = greenPeak,
                blue = bluePeak,
                percentage = (redHist[redPeak] / totalPixels) * 100,
                colorName = getColorName(redPeak, greenPeak, bluePeak)
            )
        )
    }

    private fun getColorName(red: Int, green: Int, blue: Int): String {
        return when {
            red > green && red > blue -> "Red-dominant"
            green > red && green > blue -> "Green-dominant"
            blue > red && blue > green -> "Blue-dominant"
            else -> "Neutral"
        }
    }

    private fun calculateQualityMetrics(contrast: ContrastAnalysis, exposure: ExposureAnalysis, hasHighlightClipping: Boolean, hasShadowClipping: Boolean): QualityMetrics {
        val contrastScore = when (contrast.contrastRating) {
            ContrastRating.VeryLow -> 1.0
            ContrastRating.Low -> 2.0
            ContrastRating.Moderate -> 4.0
            ContrastRating.High -> 5.0
            ContrastRating.VeryHigh -> 3.0
        }

        val exposureScore = when (exposure.exposureRating) {
            ExposureRating.Underexposed -> 1.0
            ExposureRating.SlightlyUnderexposed -> 3.0
            ExposureRating.Optimal -> 5.0
            ExposureRating.SlightlyOverexposed -> 3.0
            ExposureRating.Overexposed -> 1.0
        }

        val clippingPenalty = if (hasHighlightClipping || hasShadowClipping) 1.0 else 0.0
        val overallScore = (contrastScore + exposureScore - clippingPenalty) / 2.0

        val overallQuality = when {
            overallScore >= 4.5 -> QualityRating.Excellent
            overallScore >= 3.5 -> QualityRating.Good
            overallScore >= 2.5 -> QualityRating.Fair
            else -> QualityRating.Poor
        }

        val improvements = mutableListOf<String>()
        if (hasHighlightClipping) improvements.add("Reduce highlight clipping")
        if (hasShadowClipping) improvements.add("Reduce shadow clipping")
        if (contrast.contrastRating == ContrastRating.VeryLow) improvements.add("Increase contrast")
        if (exposure.exposureRating != ExposureRating.Optimal) improvements.add("Adjust exposure")

        return QualityMetrics(
            sharpness = 0.0, // Would need edge detection for real sharpness calculation
            noise = 0.0, // Would need noise analysis
            overallQuality = overallQuality,
            recommendedImprovements = improvements
        )
    }

    private fun calculateHistogramMean(histogram: DoubleArray): Double {
        var sum = 0.0
        var totalCount = 0.0
        for (i in histogram.indices) {
            sum += i * histogram[i]
            totalCount += histogram[i]
        }
        return if (totalCount > 0) sum / totalCount else 0.0
    }

    private fun calculateHistogramStandardDeviation(histogram: DoubleArray, mean: Double): Double {
        var sumSquaredDifferences = 0.0
        var totalCount = 0.0
        for (i in histogram.indices) {
            val diff = i - mean
            sumSquaredDifferences += diff * diff * histogram[i]
            totalCount += histogram[i]
        }
        return if (totalCount > 0) sqrt(sumSquaredDifferences / totalCount) else 0.0
    }

    private fun calculateHistogramMedian(histogram: DoubleArray): Double {
        val totalCount = histogram.sum()
        var cumulativeCount = 0.0
        val halfCount = totalCount / 2.0

        for (i in histogram.indices) {
            cumulativeCount += histogram[i]
            if (cumulativeCount >= halfCount) {
                return i.toDouble()
            }
        }
        return 0.0
    }

    // Platform-specific image creation methods
    private suspend fun createHistogramImageFile(
        histogram: DoubleArray,
        mode: HistogramDisplayMode,
        width: Int,
        height: Int,
        fileName: String
    ): String = withContext(Dispatchers.Default) {

        // Create file path
        val filePath = getAppDataPath() + "/" + fileName

        // Create a simple histogram image using basic drawing
        val imageData = createHistogramImageData(histogram, mode, width, height)

        // Write PNG file
        writePNGFile(filePath, imageData, width, height)

        filePath
    }

    private suspend fun createStackedHistogramImageFile(
        redHistogram: DoubleArray,
        greenHistogram: DoubleArray,
        blueHistogram: DoubleArray,
        luminanceHistogram: DoubleArray,
        width: Int,
        height: Int,
        fileName: String
    ): String = withContext(Dispatchers.Default) {
        val filePath = getAppDataPath() + "/" + fileName

        // Create stacked histogram image
        val imageData = createStackedHistogramImageData(
            redHistogram, greenHistogram, blueHistogram, luminanceHistogram, width, height
        )

        // Write PNG file
        writePNGFile(filePath, imageData, width, height)

        filePath
    }

    private fun createHistogramImageData(
        histogram: DoubleArray,
        mode: HistogramDisplayMode,
        width: Int,
        height: Int
    ): ByteArray {
        val imageData = ByteArray(width * height * 4) // RGBA
        val margin = 10
        val graphWidth = width - (2 * margin)
        val graphHeight = height - (2 * margin)

        // Fill background with white
        for (i in imageData.indices step 4) {
            imageData[i] = 255.toByte() // R
            imageData[i + 1] = 255.toByte() // G
            imageData[i + 2] = 255.toByte() // B
            imageData[i + 3] = 255.toByte() // A
        }

        // Find max value for scaling
        val maxValue = histogram.maxOrNull() ?: 1.0

        // Get color for histogram mode
        val (red, green, blue) = when (mode) {
            HistogramDisplayMode.Red -> Triple(255, 0, 0)
            HistogramDisplayMode.Green -> Triple(0, 255, 0)
            HistogramDisplayMode.Blue -> Triple(0, 0, 255)
            HistogramDisplayMode.Luminance -> Triple(128, 128, 128)
            HistogramDisplayMode.RGB -> Triple(64, 64, 64)
            HistogramDisplayMode.All -> Triple(32, 32, 32)
        }

        // Draw histogram bars
        for (i in histogram.indices) {
            val x = margin + (i * graphWidth / histogram.size)
            val barHeight = (histogram[i] / maxValue * graphHeight).toInt()
            val barTop = height - margin - barHeight

            // Draw vertical bar
            for (y in barTop until height - margin) {
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    val pixelIndex = (y * width + x) * 4
                    if (pixelIndex < imageData.size - 3) {
                        imageData[pixelIndex] = red.toByte()
                        imageData[pixelIndex + 1] = green.toByte()
                        imageData[pixelIndex + 2] = blue.toByte()
                        imageData[pixelIndex + 3] = 255.toByte()
                    }
                }
            }
        }

        // Draw axes
        drawAxis(imageData, width, height, margin)

        return imageData
    }

    private fun createStackedHistogramImageData(
        redHistogram: DoubleArray,
        greenHistogram: DoubleArray,
        blueHistogram: DoubleArray,
        luminanceHistogram: DoubleArray,
        width: Int,
        height: Int
    ): ByteArray {
        val imageData = ByteArray(width * height * 4) // RGBA
        val margin = 10
        val graphWidth = width - (2 * margin)
        val graphHeight = height - (2 * margin)

        // Fill background with white
        for (i in imageData.indices step 4) {
            imageData[i] = 255.toByte()
            imageData[i + 1] = 255.toByte()
            imageData[i + 2] = 255.toByte()
            imageData[i + 3] = 255.toByte()
        }

        // Find max value across all histograms for consistent scaling
        val maxValue = maxOf(
            redHistogram.maxOrNull() ?: 0.0,
            greenHistogram.maxOrNull() ?: 0.0,
            blueHistogram.maxOrNull() ?: 0.0,
            luminanceHistogram.maxOrNull() ?: 0.0
        )

        if (maxValue <= 0) return imageData

        // Draw all histograms with transparency
        val histograms = arrayOf(redHistogram, greenHistogram, blueHistogram, luminanceHistogram)
        val colors = arrayOf(
            Triple(255, 0, 0),    // Red
            Triple(0, 255, 0),    // Green
            Triple(0, 0, 255),    // Blue
            Triple(128, 128, 128) // Luminance
        )

        for (histIndex in histograms.indices) {
            val histogram = histograms[histIndex]
            val (red, green, blue) = colors[histIndex]

            for (i in histogram.indices) {
                val x = margin + (i * graphWidth / histogram.size)
                val barHeight = (histogram[i] / maxValue * graphHeight).toInt()
                val barTop = height - margin - barHeight

                // Draw with transparency by blending colors
                for (y in barTop until height - margin) {
                    if (x >= 0 && x < width && y >= 0 && y < height) {
                        val pixelIndex = (y * width + x) * 4
                        if (pixelIndex < imageData.size - 3) {
                            // Blend with existing pixel (additive blending with alpha)
                            val alpha = 80 // Transparency
                            val existingR = imageData[pixelIndex].toUByte().toInt()
                            val existingG = imageData[pixelIndex + 1].toUByte().toInt()
                            val existingB = imageData[pixelIndex + 2].toUByte().toInt()

                            val newR = (existingR * (255 - alpha) + red * alpha) / 255
                            val newG = (existingG * (255 - alpha) + green * alpha) / 255
                            val newB = (existingB * (255 - alpha) + blue * alpha) / 255

                            imageData[pixelIndex] = newR.coerceIn(0, 255).toByte()
                            imageData[pixelIndex + 1] = newG.coerceIn(0, 255).toByte()
                            imageData[pixelIndex + 2] = newB.coerceIn(0, 255).toByte()
                            imageData[pixelIndex + 3] = 255.toByte()
                        }
                    }
                }
            }
        }

        // Draw axes
        drawAxis(imageData, width, height, margin)

        return imageData
    }

    private fun drawAxis(imageData: ByteArray, width: Int, height: Int, margin: Int) {
        // Draw horizontal axis
        val axisY = height - margin
        for (x in margin until width - margin) {
            val pixelIndex = (axisY * width + x) * 4
            if (pixelIndex < imageData.size - 3) {
                imageData[pixelIndex] = 0      // R
                imageData[pixelIndex + 1] = 0  // G
                imageData[pixelIndex + 2] = 0  // B
                imageData[pixelIndex + 3] = 255.toByte() // A
            }
        }

        // Draw vertical axis
        val axisX = margin
        for (y in margin until height - margin) {
            val pixelIndex = (y * width + axisX) * 4
            if (pixelIndex < imageData.size - 3) {
                imageData[pixelIndex] = 0      // R
                imageData[pixelIndex + 1] = 0  // G
                imageData[pixelIndex + 2] = 0  // B
                imageData[pixelIndex + 3] = 255.toByte() // A
            }
        }
    }

    private fun writePNGFile(filePath: String, imageData: ByteArray, width: Int, height: Int) {
        // Create a simple PNG file
        val pngData = createSimplePNG(imageData, width, height)
        
        // Write to file
        writeToFile(filePath, pngData)
    }

    private fun createSimplePNG(imageData: ByteArray, width: Int, height: Int): ByteArray {
        // Very simplified PNG creation
        val header = byteArrayOf(
            0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A // PNG signature
        )

        // Create IHDR chunk (simplified)
        val ihdr = createIHDRChunk(width, height)

        // Create IDAT chunk with compressed image data (simplified)
        val idat = createIDATChunk(imageData, width, height)

        // Create IEND chunk
        val iend = createIENDChunk()

        return header + ihdr + idat + iend
    }

    private fun createIHDRChunk(width: Int, height: Int): ByteArray {
        val data = ByteArray(25) // 4 + 4 + 13 + 4 bytes
        var offset = 0

        // Chunk length (13 bytes)
        writeInt32BE(data, offset, 13)
        offset += 4

        // Chunk type "IHDR"
        data[offset++] = 'I'.code.toByte()
        data[offset++] = 'H'.code.toByte()
        data[offset++] = 'D'.code.toByte()
        data[offset++] = 'R'.code.toByte()

        // Width
        writeInt32BE(data, offset, width)
        offset += 4

        // Height
        writeInt32BE(data, offset, height)
        offset += 4

        // Bit depth (8)
        data[offset++] = 8

        // Color type (RGBA = 6)
        data[offset++] = 6

        // Compression method (0)
        data[offset++] = 0

        // Filter method (0)
        data[offset++] = 0

        // Interlace method (0)
        data[offset++] = 0

        // CRC (simplified - just use zeros)
        writeInt32BE(data, offset, 0)

        return data
    }

    private fun createIDATChunk(imageData: ByteArray, width: Int, height: Int): ByteArray {
        // Simplified IDAT creation (normally would use zlib compression)
        val chunkSize = 8 + imageData.size
        val data = ByteArray(chunkSize)
        var offset = 0

        // Chunk length
        writeInt32BE(data, offset, imageData.size)
        offset += 4

        // Chunk type "IDAT"
        data[offset++] = 'I'.code.toByte()
        data[offset++] = 'D'.code.toByte()
        data[offset++] = 'A'.code.toByte()
        data[offset++] = 'T'.code.toByte()

        // Image data (normally compressed)
        imageData.copyInto(data, offset)
        offset += imageData.size

        // CRC (simplified)
        writeInt32BE(data, offset, 0)

        return data
    }

    private fun createIENDChunk(): ByteArray {
        return byteArrayOf(
            0, 0, 0, 0, // Length (0)
            'I'.code.toByte(), 'E'.code.toByte(), 'N'.code.toByte(), 'D'.code.toByte(), // Type
            0, 0, 0, 0  // CRC (simplified)
        )
    }

    private fun writeInt32BE(data: ByteArray, offset: Int, value: Int) {
        data[offset] = (value shr 24).toByte()
        data[offset + 1] = (value shr 16).toByte()
        data[offset + 2] = (value shr 8).toByte()
        data[offset + 3] = value.toByte()
    }

    private fun getAppDataPath(): String {
        // Use system temp directory as fallback that works on all platforms
        return System.getProperty("java.io.tmpdir") ?: "/tmp"
    }

    private fun writeToFile(filePath: String, data: ByteArray) {
        try {
            // Create parent directories if they don't exist
            val file = java.io.File(filePath)
            file.parentFile?.mkdirs()

            // Write the data to file
            java.io.FileOutputStream(filePath).use { fos ->
                fos.write(data)
                fos.flush()
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to write file: $filePath", e)
        }
    }
}