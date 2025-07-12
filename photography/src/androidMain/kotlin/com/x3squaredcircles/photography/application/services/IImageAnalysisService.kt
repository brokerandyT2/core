// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/application/services/IImageAnalysisService.kt
package com.x3squaredcircles.photography.application.services



import com.x3squaredcircles.photography.domain.models.ImageAnalysisResult
import com.x3squaredcircles.photography.domain.models.HistogramDisplayMode

interface IImageAnalysisService {
    
    /**
     * Analyzes an image and returns comprehensive analysis results
     */
    suspend fun analyzeImage(
        imageData: ByteArray,
        progressCallback: ((Double) -> Unit)? = null
    ): ImageAnalysisResult
    
    /**
     * Generates a histogram image for the specified display mode
     */
    suspend fun generateHistogramImage(
        histogram: DoubleArray,
        mode: HistogramDisplayMode,
        width: Int = 512,
        height: Int = 256
    ): String
    
    /**
     * Generates a stacked histogram image showing all color channels
     */
    suspend fun generateStackedHistogram(
        analysisResult: ImageAnalysisResult,
        width: Int = 512,
        height: Int = 256
    ): String
    
    /**
     * Clears the histogram cache to free up memory
     */
    fun clearHistogramCache()
    
    /**
     * Analyzes image metadata (EXIF data)
     */
    suspend fun analyzeImageMetadata(imageData: ByteArray): ImageMetadata?
    
    /**
     * Calculates white balance information from image
     */
    suspend fun calculateWhiteBalance(imageData: ByteArray): WhiteBalanceInfo
    
    /**
     * Detects clipping in highlights and shadows
     */
    suspend fun detectClipping(
        redHistogram: DoubleArray,
        greenHistogram: DoubleArray,
        blueHistogram: DoubleArray,
        threshold: Double = 0.01
    ): ClippingInfo
}

