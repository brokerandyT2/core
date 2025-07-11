// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/SubscriptionSignUpViewModel.kt
package com.x3squaredcircles.photography.viewmodels

import com.x3squaredcircles.core.presentation.BaseViewModel
import com.x3squaredcircles.core.presentation.IErrorDisplayService
import com.x3squaredcircles.core.mediator.IMediator
import com.x3squaredcircles.photography.domain.entities.SubscriptionPeriod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SubscriptionSignUpViewModel : BaseViewModel {
    
    private lateinit var mediator: IMediator
    private val operationMutex = Mutex()
    private var cancellationJob: Job? = null

    private val _subscriptionProducts = MutableStateFlow<List<SubscriptionProductViewModel>>(emptyList())
    val subscriptionProducts: StateFlow<List<SubscriptionProductViewModel>> = _subscriptionProducts.asStateFlow()

    private val _selectedProduct = MutableStateFlow<SubscriptionProductViewModel?>(null)
    val selectedProduct: StateFlow<SubscriptionProductViewModel?> = _selectedProduct.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _hasError = MutableStateFlow(false)
    val hasError: StateFlow<Boolean> = _hasError.asStateFlow()

    private val _subscriptionCompletedFlow = MutableSharedFlow<Unit>()
    val subscriptionCompleted: SharedFlow<Unit> = _subscriptionCompletedFlow.asSharedFlow()

    private val _notNowSelectedFlow = MutableSharedFlow<Unit>()
    val notNowSelected: SharedFlow<Unit> = _notNowSelectedFlow.asSharedFlow()

    constructor() : super(null, null) {
        throw IllegalStateException("Use constructor with mediator parameter")
    }

    constructor(mediator: IMediator, errorDisplayService: IErrorDisplayService? = null) : super(null, errorDisplayService) {
        this.mediator = mediator
    }

    suspend fun initializeAsync() {
        if (!operationMutex.tryLock()) {
            return
        }

        try {
            cancellationJob?.cancel()
            cancellationJob = Job()

            _hasError.value = false
            clearErrors()

            val initResult = withContext(Dispatchers.Default) {
                try {
                    val initCommand = InitializeSubscriptionCommand()
                    mediator.send(initCommand)
                } catch (e: Exception) {
                    throw IllegalStateException("Subscription initialization failed: ${e.message}", e)
                }
            }

           if (!initResult.isSuccess) {
                val errorMsg = if (initResult is com.x3squaredcircles.core.Result.Failure) {
                    initResult.errorMessage
                } else {
                    "Failed to initialize subscription service"
                }
                onSystemError(errorMsg)
                return
            }

       

            val products = mutableListOf<SubscriptionProductViewModel>()
            initResult.data?.products?.forEach { product ->
                products.add(SubscriptionProductViewModel(
                    productId = product.productId,
                    title = product.title,
                    description = product.description,
                    price = product.price,
                    period = product.period,
                    isSelected = false
                ))
            }

            _subscriptionProducts.value = products
            _isInitialized.value = true

 

        } catch (e: Exception) {
            if (e !is kotlinx.coroutines.CancellationException) {
                onSystemError("Error initializing subscription: ${e.message}")
            }
        } finally {
            operationMutex.unlock()
        }
    }

    suspend fun purchaseSubscriptionAsync() {
        val currentSelectedProduct = _selectedProduct.value
        if (currentSelectedProduct == null) {
            setValidationError("Please select a subscription plan")
            return
        }

        if (!operationMutex.tryLock()) {
            return
        }

        try {
            cancellationJob?.cancel()
            cancellationJob = Job()

            _hasError.value = false
            clearErrors()

            val purchaseResult = withContext(Dispatchers.Default) {
                try {
                    val purchaseCommand = ProcessSubscriptionCommand(
                        productId = currentSelectedProduct.productId,
                        period = currentSelectedProduct.period
                    )
                    mediator.send(purchaseCommand)
                } catch (e: Exception) {
                    throw IllegalStateException("Subscription purchase failed: ${e.message}", e)
                }
            }

         
            if (!purchaseResult.isSuccess) {
                val errorMsg = if (purchaseResult is com.x3squaredcircles.core.Result.Failure) {
                    purchaseResult.errorMessage
                } else {
                    "Failed to initialize subscription service"
                }
                onSystemError(errorMsg)
                return
            }

            if (purchaseResult.data?.isSuccessful == true) {
                onSubscriptionCompleted()
            } else {
                onSystemError("There was an error processing your request, please try again")
            }

        } catch (e: Exception) {
            if (e !is kotlinx.coroutines.CancellationException) {
                onSystemError("Error processing subscription: ${e.message}")
            }
        } finally {
            operationMutex.unlock()
        }
    }

    fun selectProduct(product: SubscriptionProductViewModel?) {
        if (product == null) return

        try {
     

            val updatedProducts = _subscriptionProducts.value.map { p ->
                p.copy(isSelected = p.productId == product.productId)
            }

            _subscriptionProducts.value = updatedProducts
            _selectedProduct.value = product.copy(isSelected = true)

       
        } catch (e: Exception) {
            onSystemError("Error selecting product: ${e.message}")
         
        }
    }

    fun notNow() {
        onNotNowSelected()
    }

    override fun onSystemError(message: String) {
        super.onSystemError(message)
        _hasError.value = true
    }

    private fun onSubscriptionCompleted() {
        viewModelScope.launch {
            _subscriptionCompletedFlow.emit(Unit)
        }
    }

    private fun onNotNowSelected() {
        viewModelScope.launch {
            _notNowSelectedFlow.emit(Unit)
        }
    }



    override fun dispose() {
        cancellationJob?.cancel()
        super.dispose()
    }
}

data class SubscriptionProductViewModel(
    val productId: String = "",
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val period: SubscriptionPeriod = SubscriptionPeriod.Monthly,
    val isSelected: Boolean = false
) {
    val periodText: String
        get() = when (period) {
            SubscriptionPeriod.Monthly -> "Monthly"
            SubscriptionPeriod.Yearly -> "Yearly"
        }
}

// Command classes
data class InitializeSubscriptionCommand(
    val dummy: Unit = Unit
) : com.x3squaredcircles.core.mediator.IRequest<com.x3squaredcircles.core.Result<InitializeSubscriptionResultDto>>

data class InitializeSubscriptionResultDto(
    val products: List<InitializeSubscriptionProductDto> = emptyList(),
    val isConnected: Boolean = false,
    val errorMessage: String = ""
)

data class InitializeSubscriptionProductDto(
    val productId: String = "",
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val priceAmountMicros: String = "",
    val currencyCode: String = "",
    val period: SubscriptionPeriod = SubscriptionPeriod.Monthly
)

data class ProcessSubscriptionCommand(
    val productId: String = "",
    val period: SubscriptionPeriod = SubscriptionPeriod.Monthly
) : com.x3squaredcircles.core.mediator.IRequest<com.x3squaredcircles.core.Result<ProcessSubscriptionResultDto>>

data class ProcessSubscriptionResultDto(
    val isSuccessful: Boolean = false,
    val transactionId: String = "",
    val purchaseToken: String = "",
    val purchaseDate: kotlinx.datetime.Instant = kotlinx.datetime.Clock.System.now(),
    val expirationDate: kotlinx.datetime.Instant = kotlinx.datetime.Clock.System.now(),
    val productId: String = "",
    val status: SubscriptionStatus = SubscriptionStatus.PENDING,
    val errorMessage: String = ""
)

enum class SubscriptionStatus {
    PENDING,
    ACTIVE,
    EXPIRED,
    CANCELED,
    GRACE_PERIOD,
    ON_HOLD
}