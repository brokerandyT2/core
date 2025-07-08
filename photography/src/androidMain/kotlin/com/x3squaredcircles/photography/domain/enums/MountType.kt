// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/enums/MountType.kt
package com.x3squaredcircles.photography.domain.enums

enum class MountType(val value: Int) {
    // Canon
    CanonEF(1),
    CanonEFS(2),
    CanonEFM(3),
    CanonRF(4),
    CanonFD(5),

    // Nikon
    NikonF(10),
    NikonZ(11),
    Nikon1(12),

    // Sony
    SonyE(20),
    SonyFE(21),
    SonyA(22),

    // Fujifilm
    FujifilmX(30),
    FujifilmGFX(31),

    // Pentax
    PentaxK(40),
    PentaxQ(41),

    // Micro Four Thirds
    MicroFourThirds(50),

    // Leica
    LeicaM(60),
    LeicaL(61),
    LeicaSL(62),
    LeicaTL(63),

    // Olympus
    OlympusFourThirds(70),

    // Panasonic
    PanasonicL(80),

    // Sigma
    SigmaSA(90),

    // Tamron
    TamronAdaptall(100),

    // Generic/Other
    C(200),
    CS(201),
    M42(202),
    T2(203),
    Other(999);

    companion object {
        fun fromValue(value: Int): MountType {
            return entries.find { it.value == value } ?: Other
        }

        fun getDisplayName(mountType: MountType): String {
            return when (mountType) {
                CanonEF -> "Canon EF"
                CanonEFS -> "Canon EF-S"
                CanonEFM -> "Canon EF-M"
                CanonRF -> "Canon RF"
                CanonFD -> "Canon FD"
                NikonF -> "Nikon F"
                NikonZ -> "Nikon Z"
                Nikon1 -> "Nikon 1"
                SonyE -> "Sony E"
                SonyFE -> "Sony FE"
                SonyA -> "Sony A"
                FujifilmX -> "Fujifilm X"
                FujifilmGFX -> "Fujifilm GFX"
                PentaxK -> "Pentax K"
                PentaxQ -> "Pentax Q"
                MicroFourThirds -> "Micro Four Thirds"
                LeicaM -> "Leica M"
                LeicaL -> "Leica L"
                LeicaSL -> "Leica SL"
                LeicaTL -> "Leica TL"
                OlympusFourThirds -> "Four Thirds"
                PanasonicL -> "Panasonic L"
                SigmaSA -> "Sigma SA"
                TamronAdaptall -> "Tamron Adaptall"
                C -> "C Mount"
                CS -> "CS Mount"
                M42 -> "M42"
                T2 -> "T2"
                Other -> "Other"
            }
        }

        fun getBrandName(mountType: MountType): String {
            return when (mountType) {
                CanonEF, CanonEFS, CanonEFM, CanonRF, CanonFD -> "Canon"
                NikonF, NikonZ, Nikon1 -> "Nikon"
                SonyE, SonyFE, SonyA -> "Sony"
                FujifilmX, FujifilmGFX -> "Fujifilm"
                PentaxK, PentaxQ -> "Pentax"
                MicroFourThirds -> "M4/3"
                LeicaM, LeicaL, LeicaSL, LeicaTL -> "Leica"
                OlympusFourThirds -> "Olympus"
                PanasonicL -> "Panasonic"
                SigmaSA -> "Sigma"
                TamronAdaptall -> "Tamron"
                C, CS, M42, T2, Other -> "Generic"
            }
        }
    }
}