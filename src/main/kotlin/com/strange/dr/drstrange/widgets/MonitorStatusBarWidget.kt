package com.strange.dr.drstrange.widgets

import com.intellij.openapi.wm.CustomStatusBarWidget
import com.intellij.openapi.wm.StatusBar
import com.intellij.ui.JBColor
import com.strange.dr.GpuStatsManager
import com.strange.dr.drstrange.data.Device
import org.jetbrains.debugger.createVariablesList
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import kotlin.concurrent.thread

class MemoryUsageIndicator : JPanel() {
    private val memoryLabel: JLabel = JLabel()

    private val gpuStatsManager
        get() = GpuStatsManager()

    val popupMenu: JPopupMenu = JPopupMenu()

    private val devicesInfo: MutableList<JLabel> = mutableListOf()

    init {
        preferredSize = Dimension(200, 30)  // Set preferred width; height will be determined by the status bar
        layout = null // Use absolute positioning
        ToolTipManager.sharedInstance().registerComponent(this) // Register this component for tooltip
        toolTipText = "GPU Memory Usage" // Set tooltip text

        // Configure label
        memoryLabel.foreground = JBColor.foreground()
        add(memoryLabel)

        // Start a background thread to simulate memory updates
        startUpdatingMemoryUsage()

        initializePopupMenu()

        // Add a mouse listener to handle clicks
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.button == MouseEvent.BUTTON1) { // Show popup on left-click
                    popupMenu.show(this@MemoryUsageIndicator, 0, -popupMenu.preferredSize.height)
                }
            }
        })

    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if(!gpuStatsManager.isCompatible()) {
            memoryLabel.text = "Incompatible device."
            centerLabel(memoryLabel)
            return
        }
        val g2d = g as Graphics2D
        val devices = gpuStatsManager.getGpuStats()

        var highestDevice = devices[0]
        for (device in devices) {
            if (device.usedMemoryMB / device.totalMemoryMB > highestDevice.usedMemoryMB / highestDevice.totalMemoryMB) {
                highestDevice = device
            }
        }

        // Calculate used percentage
        val usedPercentage = highestDevice.usedMemoryMB.toDouble() / highestDevice.totalMemoryMB
        val barWidth = (width * usedPercentage).toInt() // Width of the filled portion

        // Draw background (default status bar color)
        g2d.color = JBColor.background() // IDE background color

        g2d.fillRect(0, 0, width, height)

        // Draw used memory as filled rectangle
        g2d.color = getColorForUsage(usedPercentage) // Set color based on usage
        g2d.fillRect(0, 0, barWidth, height) // Fill the bar to represent memory usage

        // Update label with used memory
        memoryLabel.text = "Dev ${highestDevice.id}: ${highestDevice.usedMemoryMB} of ${highestDevice.totalMemoryMB} MB"
        // Center the label in the panel
        centerLabel(memoryLabel)
    }

    // Center the label in the JPanel
    private fun centerLabel(label: JLabel) {
        val labelWidth = label.preferredSize.width
        val labelHeight = label.preferredSize.height
        label.setBounds((width - labelWidth) / 2, (height - labelHeight) / 2, labelWidth, labelHeight)
    }

    // Get color based on usage percentage
    private fun getColorForUsage(usage: Double): Color {
        return JBColor.GRAY
    }

    // Simulate memory usage updates in a background thread
    private fun startUpdatingMemoryUsage() {
        if (!gpuStatsManager.isCompatible()) {
            devicesInfo.forEach { label ->
                label.text = "Incompatible device."
            }
            return
        }
        thread(start = true) {
            while (true) {
                val devices = gpuStatsManager.getGpuStats()

                // Show only highest use on standard view:
                SwingUtilities.invokeLater {
                    devicesInfo.forEachIndexed { index, label ->
                        label.text = deviceToLabel(devices[index]).text
                    }
                    repaint() // Repaint to reflect the new memory usage
                }

                Thread.sleep(1000) // Update every second
            }
        }
    }

    private fun initializePopupMenu() {
        if (!gpuStatsManager.isCompatible()) {
            devicesInfo.add(JLabel("It looks like your GPU or OS isn't currently supported. We're working on adding integration for your setup—stay tuned!"))
        }

        for (device in gpuStatsManager.getGpuStats()) {
            devicesInfo.add(deviceToLabel(device))
        }

        for (deviceInfoLabel in devicesInfo) {
            popupMenu.add(deviceInfoLabel)
        }
    }

    private fun deviceToLabel(device: Device) =
        JLabel("Dev ${device.id} [${device.name}]. Mem Usage: ${device.usedMemoryMB}/${device.totalMemoryMB} MB. Utilization: ${device.utilizationPercent}%. Power Usage: ${device.powerUsageWatt} W").apply {
            border = BorderFactory.createEmptyBorder(5, 0, 5, 0)
        }
}

// Custom StatusBar Widget using the MemoryUsageIndicator
class MonitorStatusBarWidget : CustomStatusBarWidget {
    private val memoryUsageIndicator = MemoryUsageIndicator()

    override fun ID(): String {
        return "MemoryUsageIndicator"
    }

    override fun getComponent(): JComponent {
        return memoryUsageIndicator // Return the memory usage indicator directly
    }

    override fun dispose() {
        // Clean up resources if necessary
    }
}
