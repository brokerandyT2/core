// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/di/CoreModule.kt
package com.x3squaredcircles.core.infrastructure.di
import com.x3squaredcircles.core.infrastructure.services.*
import com.x3squaredcircles.core.infrastructure.events.IEventBus
import com.x3squaredcircles.core.infrastructure.events.InMemoryEventBus
import com.x3squaredcircles.core.mediator.IMediator
import com.x3squaredcircles.core.mediator.Mediator
import com.x3squaredcircles.core.services.ErrorDisplayService
import com.x3squaredcircles.core.services.IErrorDisplayService
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Core Module DI Configuration
 * 
 * This module only contains CORE infrastructure dependencies that can be reused
 * across multiple applications. Business logic handlers belong in the app module.
 * 
 * Koin DI Mapping Reference:
 * - single<Interface> { Implementation() }  = Singleton (one instance for entire app)
 * - factory<Interface> { Implementation() } = Transient (new instance each time)
 * - scoped<Interface> { Implementation() }  = Scoped (one per scope/screen)
 * - get<Interface>() = Retrieve dependency from container
 */
object CoreModule {
    
    /**
     * Registers core infrastructure dependencies only.
     * Equivalent to .NET's ServiceCollection.AddApplication() for core services.
     */
    fun addCore(): Module = module {
        
        // Core Mediator Infrastructure
        // Equivalent to: services.AddSingleton<IMediator, Mediator>()
        single<IMediator> { Mediator() }
        
        // Core Event Bus Infrastructure  
        // Equivalent to: services.AddSingleton<IEventBus, InMemoryEventBus>()
        single<IEventBus> { InMemoryEventBus() }
        single<ILoggingService> { LoggingService() }
        // Core Error Display Service
        // Equivalent to: services.AddSingleton<IErrorDisplayService, ErrorDisplayService>()
        single<IErrorDisplayService> { ErrorDisplayService() }
        
        // NOTE: Business logic components (handlers, validators, repositories) 
        // are NOT registered here - they belong in the app module
        
    }
}