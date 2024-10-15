package com.strange.dr

import com.strange.dr.drstrange.data.Device
import java.io.BufferedReader
import java.io.InputStreamReader

class GpuStatsManager {

    fun getGpuStats(): List<Device> {
        val gpuStatsList = mutableListOf<Device>()
        try {
            // Execute nvidia-smi command to get GPU stats in a formatted way
            val process = ProcessBuilder(
                "nvidia-smi",
                "--query-gpu=name,utilization.gpu,power.draw,memory.used,memory.total",
                "--format=csv,noheader"
            ).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?

            // Read the output line by line
            var lineId = 0
            while (reader.readLine().also { line = it } != null) {
                val parts = line!!.split(",").map { it.trim() } // Split and trim each part
                if (parts.size == 5) {
                    // Create a GpuStats object with the retrieved information
                    val gpuStat = Device(
                        id = lineId,
                        name = parts[0],
                        utilizationPercent = parts[1].splitToInt(),
                        powerUsageWatt = parts[2].splitToDouble(),
                        usedMemoryMB = parts[3].splitToInt(),
                        totalMemoryMB = parts[4].splitToInt(),
                    )
                    lineId += 1
                    gpuStatsList.add(gpuStat)
                }
            }

            // Wait for the process to finish
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return gpuStatsList
    }

    fun getDeviceNames(): List<String> {
        val gpuNames = mutableListOf<String>()
        try {
            // Execute nvidia-smi command
            val process = ProcessBuilder("nvidia-smi", "--query-gpu=name", "--format=csv,noheader").start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?

            // Read the output line by line
            while (reader.readLine().also { line = it } != null) {
                gpuNames.add(line!!)
            }

            // Wait for the process to finish
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return gpuNames
    }
}

private fun String.splitToDouble() = this.split(" ")[0].toDouble()
private fun String.splitToInt() = this.split(" ")[0].toInt()