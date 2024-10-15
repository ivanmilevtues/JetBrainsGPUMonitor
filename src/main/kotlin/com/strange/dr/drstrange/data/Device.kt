package com.strange.dr.drstrange.data

data class Device(
    val id: Int,
    val name: String,
    val utilizationPercent: Int,
    val powerUsageWatt: Double,
    val usedMemoryMB: Int,
    val totalMemoryMB: Int
)