package com.strange.dr.drstrange.widgets

import com.intellij.openapi.wm.CustomStatusBarWidget
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.*
import kotlin.concurrent.thread

class MemoryUsageIndicator : JPanel() {
    private var usedMemoryMB = 0
    private val totalMemoryMB = 1024  // Total memory in MB

    private val memoryLabel: JLabel = JLabel()

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
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2d = g as Graphics2D

        // Calculate used percentage
        val usedPercentage = usedMemoryMB.toDouble() / totalMemoryMB
        val barWidth = (width * usedPercentage).toInt() // Width of the filled portion

        // Draw background (default status bar color)
        g2d.color = JBColor.background() // IDE background color

        g2d.fillRect(0, 0, width, height)

        // Draw used memory as filled rectangle
        g2d.color = getColorForUsage(usedPercentage) // Set color based on usage
        g2d.fillRect(0, 0, barWidth, height) // Fill the bar to represent memory usage

        // Update label with used memory
        memoryLabel.text = "$usedMemoryMB of $totalMemoryMB MB"
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
        thread(start = true) {
            while (true) {
                // Simulate memory usage (increase by 50 MB, wrap around after exceeding total)
                usedMemoryMB = (usedMemoryMB + 50) % (totalMemoryMB + 1)

                // Update the component on the EDT
                SwingUtilities.invokeLater {
                    repaint() // Repaint to reflect the new memory usage
                }

                Thread.sleep(1000) // Update every second
            }
        }
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
