// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/dtos/MountTypeDto.kt
package com.x3squaredcircles.photography.dtos

import com.x3squaredcircles.photography.domain.enums.MountType

data class MountTypeDto(
    val value: MountType = MountType.Other,
    val displayName: String = "",
    val brand: String = ""
) {
    companion object {
        fun fromMountType(mountType: MountType): MountTypeDto {
            return MountTypeDto(
                value = mountType,
                displayName = MountType.getDisplayName(mountType),
                brand = MountType.getBrandName(mountType)
            )
        }

        fun getAllMountTypes(): List<MountTypeDto> {
            return MountType.entries.map { fromMountType(it) }
        }

        fun getByBrand(brand: String): List<MountTypeDto> {
            return MountType.entries
                .filter { MountType.getBrandName(it) == brand }
                .map { fromMountType(it) }
        }
    }
}
