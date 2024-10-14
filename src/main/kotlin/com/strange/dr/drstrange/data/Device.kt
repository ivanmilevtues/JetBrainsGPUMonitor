package com.strange.dr.drstrange.data

data class Device(
    val name: String,
    val totalMemory: Long,
    val usedMemory: Long,
    val utilization: Long
    // TODO: Watt usage
)
