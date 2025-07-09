// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/AstroTarget.kt
package com.x3squaredcircles.photography.domain.models

enum class AstroTarget {
    // General categories
    Moon,
    Planets,
    MilkyWayCore,
    DeepSkyObjects,
    StarTrails,
    Comets,
    MeteorShowers,
    PolarAlignment,
    Constellations,
    NorthernLights,

    // Individual planets
    Mercury,
    Venus,
    Mars,
    Jupiter,
    Saturn,
    Uranus,
    Neptune,
    Pluto,

    // Meteor showers
    Quadrantids,
    EtaAquariids,
    Leonids,
    Geminids,
    Perseids,

    // Constellations
    Leo,
    Scorpius,
    Cygnus,
    BigDipper,
    Cassiopeia,
    Orion,
    Constellation_Sagittarius,
    Constellation_Orion,
    Constellation_Cassiopeia,
    Constellation_UrsaMajor,
    Constellation_Cygnus,
    Constellation_Scorpius,
    Constellation_Leo,

    // Deep Sky Objects
    CrabNebula,
    LagoonNebula,
    EagleNebula,
    RingNebula,
    WhirlpoolGalaxy,
    Pleiades,
    OrionNebula,
    AndromedaGalaxy,
    M31_Andromeda,
    M104_Sombrero,
    M81_Bodes,
    M57_Ring,
    M27_Dumbbell,
    M13_Hercules,
    M51_Whirlpool,
    M42_Orion,

    // Special targets
    ISS
}