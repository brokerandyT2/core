// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/di/PhotographyModule.kt
package com.x3squaredcircles.photography.infrastructure.di

import com.x3squaredcircles.photography.infrastructure.services.ExifService
import com.x3squaredcircles.photography.services.IExifService
import org.koin.core.module.Module
import org.koin.dsl.module

object PhotographyModule {
    
    fun addPhotography(): Module = module {
        
        // EXIF Service
        // Equivalent to: services.AddScoped<IExifService, ExifService>()
        factory<IExifService> { ExifService(get()) }
        
    }
    
}