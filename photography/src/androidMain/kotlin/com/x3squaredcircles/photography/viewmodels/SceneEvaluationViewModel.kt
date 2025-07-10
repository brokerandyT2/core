// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/SceneEvaluationViewModel.kt
package com.x3squaredcircles.photography.viewmodels

import com.x3squaredcircles.core.presentation.BaseViewModel
import com.x3squaredcircles.core.presentation.IErrorDisplayService
import com.x3squaredcircles.photography.application.services.IImageAnalysisService
import com.x3squaredcircles.photography.domain.models.ImageAnalysisResult
import com.x3squaredcircles.photography.domain.models.HistogramDisplayMode

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

class SceneEvaluationViewModel(
    private val imageAnalysisService: IImageAnalysisService,
    errorDisplayService: IErrorDisplayService? = null
) : BaseViewModel(null, errorDisplayService) {

    // Threading and resource management
    private var cancellationTokenSource = Job()
    private val processingLock = Mutex()
    private var disposed = false

    // Caching for repeated operations
    private val analysisCache = mutableMapOf<String, ImageAnalysisResult>()
    private val histogramImageCache = mutableMapOf<HistogramDisplayMode, String>()
    private var lastAnalyzedImageHash = ""
    private var stackedHistogramImagePath = ""

    // Analysis result properties
    private val _analysisResult = MutableStateFlow<ImageAnalysisResult?>(null)
    val analysisResult: StateFlow<ImageAnalysisResult?> = _analysisResult.asStateFlow()

    private val _selectedHistogramMode = MutableStateFlow(HistogramDisplayMode.Red)
    val selectedHistogramMode: StateFlow<HistogramDisplayMode> = _selectedHistogramMode.asStateFlow()

    private val _showExposureWarnings = MutableStateFlow(true)
    val showExposureWarnings: StateFlow<Boolean> = _showExposureWarnings.asStateFlow()

    private val _currentHistogramImage = MutableStateFlow("")
    val currentHistogramImage: StateFlow<String> = _currentHistogramImage.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _colorTemperature = MutableStateFlow(5500.0)
    val colorTemperature: StateFlow<Double> = _colorTemperature.asStateFlow()

    private val _tintValue = MutableStateFlow(0.0)
    val tintValue: StateFlow<Double> = _tintValue.asStateFlow()

    private val _displayAll = MutableStateFlow(false)
    val displayAll: StateFlow<Boolean> = _displayAll.asStateFlow()

    // Professional analysis properties
    private val _dynamicRange = MutableStateFlow(0.0)
    val dynamicRange: StateFlow<Double> = _dynamicRange.asStateFlow()

    private val _exposureRecommendation = MutableStateFlow("")
    val exposureRecommendation: StateFlow<String> = _exposureRecommendation.asStateFlow()

    private val _hasClippingWarning = MutableStateFlow(false)
    val hasClippingWarning: StateFlow<Boolean> = _hasClippingWarning.asStateFlow()

    private val _clippingWarningMessage = MutableStateFlow("")
    val clippingWarningMessage: StateFlow<String> = _clippingWarningMessage.asStateFlow()

    private val _rmsContrast = MutableStateFlow(0.0)
    val rmsContrast: StateFlow<Double> = _rmsContrast.asStateFlow()

    private val _redMean = MutableStateFlow(0.0)
    val redMean: StateFlow<Double> = _redMean.asStateFlow()

    private val _greenMean = MutableStateFlow(0.0)
    val greenMean: StateFlow<Double> = _greenMean.asStateFlow()

    private val _blueMean = MutableStateFlow(0.0)
    val blueMean: StateFlow<Double> = _blueMean.asStateFlow()

    // Histogram visibility properties
    private val _isRedHistogramVisible = MutableStateFlow(true)
    val isRedHistogramVisible: StateFlow<Boolean> = _isRedHistogramVisible.asStateFlow()

    private val _isGreenHistogramVisible = MutableStateFlow(false)
    val isGreenHistogramVisible: StateFlow<Boolean> = _isGreenHistogramVisible.asStateFlow()

    private val _isBlueHistogramVisible = MutableStateFlow(false)
    val isBlueHistogramVisible: StateFlow<Boolean> = _isBlueHistogramVisible.asStateFlow()

    private val _isLuminanceHistogramVisible = MutableStateFlow(false)
    val isLuminanceHistogramVisible: StateFlow<Boolean> = _isLuminanceHistogramVisible.asStateFlow()

    // Progress tracking
    private val _processingStatus = MutableStateFlow("")
    val processingStatus: StateFlow<String> = _processingStatus.asStateFlow()

    private val _processingProgress = MutableStateFlow(0.0)
    val processingProgress: StateFlow<Double> = _processingProgress.asStateFlow()

    fun setSelectedHistogramMode(mode: HistogramDisplayMode) {
        _selectedHistogramMode.value = mode
        viewModelScope.launch {
            updateDisplayOptimized()
            updateHistogramVisibility()
        }
    }

    fun setShowExposureWarnings(show: Boolean) {
        _showExposureWarnings.value = show
    }

    fun setColorTemperature(temperature: Double) {
        _colorTemperature.value = temperature
    }

    fun setTintValue(tint: Double) {
        _tintValue.value = tint
    }

    fun setDisplayAll(display: Boolean) {
        _displayAll.value = display
    }

    /**
     * Evaluate scene command function
     */
    fun evaluateScene() {
        viewModelScope.launch {
            evaluateSceneAsync()
        }
    }

    /**
     * Change histogram mode command function
     */
    fun changeHistogramMode(mode: String) {
        viewModelScope.launch {
            try {
                val histogramMode = HistogramDisplayMode.valueOf(mode)
                setSelectedHistogramMode(histogramMode)
            } catch (e: IllegalArgumentException) {
                onSystemError("Invalid histogram mode: $mode")
            }
        }
    }

    /**
     * Clear previous analysis data when starting new analysis
     */
    private fun clearPreviousAnalysisData() {
        histogramImageCache.clear()
        stackedHistogramImagePath = ""
        lastAnalyzedImageHash = ""
    }

    /**
     * PERFORMANCE OPTIMIZATION: Streamlined scene evaluation with progress tracking
     */
    private suspend fun evaluateSceneAsync() {
        // Clear previous analysis data when starting new analysis
        clearPreviousAnalysisData()

        // Prevent concurrent processing
        if (!processingLock.tryLock()) {
            setValidationError("Analysis already in progress. Please wait.")
            return
        }

        try {
            executeSceneEvaluationOptimized()
        } finally {
            processingLock.unlock()
        }
    }

    /**
     * Execute scene evaluation with optimizations
     */
    private suspend fun executeSceneEvaluationOptimized() {
        try {
            imageAnalysisService.clearHistogramCache()

            // Cancel any existing operation
            cancellationTokenSource.cancel()
            cancellationTokenSource = Job()

            // Start progress tracking
            withContext(Dispatchers.Main) {
                setIsBusy(true)
                _isProcessing.value = true
                _processingProgress.value = 0.0
                _processingStatus.value = "Initializing camera..."
                clearErrors()
            }

            // Phase 1: Capture photo with timeout and progress
            val photo = capturePhotoOptimized { status ->
                viewModelScope.launch(Dispatchers.Main) {
                    _processingStatus.value = status
                }
            }

            if (photo == null) {
                withContext(Dispatchers.Main) {
                    setIsBusy(false)
                    _isProcessing.value = false
                    _processingStatus.value = "Photo capture cancelled"
                }
                return
            }

            withContext(Dispatchers.Main) {
                _processingProgress.value = 25.0
                _processingStatus.value = "Processing image..."
            }

            // Phase 2: Generate image hash for caching
            val imageHash = generateImageHash(photo)

            // Check cache first
            if (imageHash.isNotEmpty() && analysisCache.containsKey(imageHash)) {
                val cachedResult = analysisCache[imageHash]!!
                withContext(Dispatchers.Main) {
                    _processingProgress.value = 90.0
                    _processingStatus.value = "Loading cached analysis..."
                    _analysisResult.value = cachedResult
                    updateDisplayOptimized()
                    _processingProgress.value = 100.0
                    _processingStatus.value = "Analysis complete (cached)"
                    setIsBusy(false)
                    _isProcessing.value = false
                }
                return
            }

            // Phase 3: Perform image analysis on background thread
            val analysisResult = withContext(Dispatchers.Default) {
                try {
                    // Perform image analysis
                    imageAnalysisService.analyzeImage(photo) { progress ->
                        viewModelScope.launch(Dispatchers.Main) {
                            _processingProgress.value = 25.0 + (progress * 0.4) // 25-65%
                        }
                    }
                } catch (ex: Exception) {
                    throw RuntimeException("Image analysis failed: ${ex.message}", ex)
                }
            }

            withContext(Dispatchers.Main) {
                _processingProgress.value = 65.0
                _processingStatus.value = "Generating histograms..."
            }

            // Phase 4: Generate enhanced data
            val enhancedData = withContext(Dispatchers.Default) {
                try {
                    generateEnhancedAnalysisData(analysisResult)
                } catch (ex: Exception) {
                    throw RuntimeException("Enhancement processing failed: ${ex.message}", ex)
                }
            }

            // Phase 5: Update UI with all results
            withContext(Dispatchers.Main) {
                try {
                    // Store in cache
                    if (imageHash.isNotEmpty()) {
                        analysisCache[imageHash] = analysisResult
                        lastAnalyzedImageHash = imageHash

                        // Cleanup old cache entries (keep only last 3)
                        if (analysisCache.size > 3) {
                            val oldestKey = analysisCache.keys.first()
                            analysisCache.remove(oldestKey)
                        }
                    }

                    // Update all properties
                    _analysisResult.value = analysisResult
                    _exposureRecommendation.value = enhancedData.recommendations

                    // Cache histogram images
                    enhancedData.histogramImages.forEach { (mode, imagePath) ->
                        histogramImageCache[mode] = imagePath
                    }

                    // Cache stacked histogram
                    stackedHistogramImagePath = enhancedData.stackedHistogramPath

                    _processingProgress.value = 90.0
                    _processingStatus.value = "Finalizing display..."

                    updateDisplayOptimized()

                    _processingProgress.value = 100.0
                    _processingStatus.value = "Analysis complete"

                    // Extract key metrics
                    updateAnalysisMetrics(analysisResult)

                    // Update histogram visibility
                    updateHistogramVisibility()

                    delay(1000) // Show completion for 1 second
                    _processingStatus.value = ""
                    _processingProgress.value = 0.0
                } catch (ex: Exception) {
                    onSystemError("Error updating analysis results: ${ex.message}")
                } finally {
                    setIsBusy(false)
                    _isProcessing.value = false
                }
            }

        } catch (ex: Exception) {
            withContext(Dispatchers.Main) {
                onSystemError("Scene evaluation failed: ${ex.message}")
                setIsBusy(false)
                _isProcessing.value = false
                _processingStatus.value = "Analysis failed"
                _processingProgress.value = 0.0
            }
        }
    }

    /**
     * Capture photo (platform-specific implementation needed)
     */
    private suspend fun capturePhotoOptimized(progressCallback: (String) -> Unit): ByteArray? {
        return withContext(Dispatchers.Default) {
            try {
                progressCallback("Accessing camera...")
                delay(500) // Simulate camera access time
                
                progressCallback("Capturing image...")
                delay(1000) // Simulate capture time
                
                // Platform-specific camera capture implementation needed
                // Return dummy data for now
                ByteArray(1024) { it.toByte() }
            } catch (ex: Exception) {
                null
            }
        }
    }

    /**
     * Generate image hash for caching
     */
    private suspend fun generateImageHash(imageData: ByteArray): String {
        return withContext(Dispatchers.Default) {
            try {
                // Simple hash based on image data
                imageData.contentHashCode().toString()
            } catch (ex: Exception) {
                ""
            }
        }
    }

    /**
     * Generate enhanced analysis data
     */
    private suspend fun generateEnhancedAnalysisData(analysisResult: ImageAnalysisResult): EnhancedAnalysisData {
        return withContext(Dispatchers.Default) {
            val histogramImages = mutableMapOf<HistogramDisplayMode, String>()
            
            // Generate histogram images for each mode
            HistogramDisplayMode.values().forEach { mode ->
                val imagePath = imageAnalysisService.generateHistogramImage(
                    histogram = when (mode) {
                        HistogramDisplayMode.Red -> analysisResult.redHistogram.bins
                        HistogramDisplayMode.Green -> analysisResult.greenHistogram.bins
                        HistogramDisplayMode.Blue -> analysisResult.blueHistogram.bins
                        HistogramDisplayMode.Luminance -> analysisResult.luminanceHistogram.bins
                        HistogramDisplayMode.RGB -> analysisResult.luminanceHistogram.bins // Use luminance for RGB composite
                        HistogramDisplayMode.All -> analysisResult.luminanceHistogram.bins // Use luminance as base for

                    },
                    mode = mode
                )
                histogramImages[mode] = imagePath
            }

            val stackedPath = imageAnalysisService.generateStackedHistogram(analysisResult)
            val recommendations = generateRecommendations(analysisResult)

            EnhancedAnalysisData(
                histogramImages = histogramImages,
                stackedHistogramPath = stackedPath,
                recommendations = recommendations
            )
        }
    }

    /**
     * Generate exposure recommendations
     */
    private fun generateRecommendations(analysisResult: ImageAnalysisResult): String {
        val recommendations = mutableListOf<String>()
        
        // Check for clipping
        if (analysisResult.hasHighlightClipping) {
            recommendations.add("Reduce exposure to prevent highlight clipping")
        }
        
        if (analysisResult.hasShadowClipping) {
            recommendations.add("Increase exposure to preserve shadow detail")
        }
        
        // Check contrast
        if (analysisResult.contrast.rmsContrast < 0.3) {
            recommendations.add("Consider increasing contrast in post-processing")
        }
        
        // Check dynamic range
        val dynamicRange = analysisResult.luminanceHistogram.max - analysisResult.luminanceHistogram.min
        if (dynamicRange < 150) {
            recommendations.add("Scene has low dynamic range - good for single exposure")
        } else if (dynamicRange > 200) {
            recommendations.add("High dynamic range scene - consider HDR bracketing")
        }
        
        return if (recommendations.isNotEmpty()) {
            recommendations.joinToString(" • ")
        } else {
            "Exposure looks good - no major adjustments needed"
        }
    }

    /**
     * Update analysis metrics from result
     */
    private fun updateAnalysisMetrics(result: ImageAnalysisResult) {
        _dynamicRange.value = (result.luminanceHistogram.max - result.luminanceHistogram.min).toDouble()
        _rmsContrast.value = result.contrast.rmsContrast
        _redMean.value = result.redHistogram.mean
        _greenMean.value = result.greenHistogram.mean
        _blueMean.value = result.blueHistogram.mean
        _colorTemperature.value = result.whiteBalance.colorTemperature
        _tintValue.value = result.whiteBalance.tint
        
        // Check for clipping warnings
        _hasClippingWarning.value = result.hasHighlightClipping || result.hasShadowClipping
        _clippingWarningMessage.value = when {
            result.hasHighlightClipping && result.hasShadowClipping -> "Highlight and shadow clipping detected"
            result.hasHighlightClipping -> "Highlight clipping detected"
            result.hasShadowClipping -> "Shadow clipping detected"
            else -> ""
        }
    }

    /**
     * Update display with current histogram mode
     */
    private suspend fun updateDisplayOptimized() {
        try {
            val mode = _selectedHistogramMode.value
            val imagePath = histogramImageCache[mode]
            
            if (!imagePath.isNullOrEmpty()) {
                _currentHistogramImage.value = imagePath
            } else if (_displayAll.value && stackedHistogramImagePath.isNotEmpty()) {
                _currentHistogramImage.value = stackedHistogramImagePath
            }
        } catch (ex: Exception) {
            onSystemError("Error updating histogram display: ${ex.message}")
        }
    }

    /**
     * Update histogram visibility based on current mode
     */
    private fun updateHistogramVisibility() {
        val mode = _selectedHistogramMode.value
        
        _isRedHistogramVisible.value = mode == HistogramDisplayMode.Red || _displayAll.value
        _isGreenHistogramVisible.value = mode == HistogramDisplayMode.Green || _displayAll.value
        _isBlueHistogramVisible.value = mode == HistogramDisplayMode.Blue || _displayAll.value
        _isLuminanceHistogramVisible.value = mode == HistogramDisplayMode.Luminance || _displayAll.value
    }

    override fun dispose() {
        if (!disposed) {
            disposed = true
            cancellationTokenSource.cancel()
            analysisCache.clear()
            histogramImageCache.clear()
        }
        super.dispose()
    }
}

/**
 * Enhanced analysis data container
 */
data class EnhancedAnalysisData(
    val histogramImages: Map<HistogramDisplayMode, String>,
    val stackedHistogramPath: String,
    val recommendations: String
)