package com.strange.dr.drstrange.widgets
import kotlin.concurrent.thread

import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.wm.CustomStatusBarWidget
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.SwingUtilities

class MonitorStatusBarWidget : CustomStatusBarWidget {
    // Store the JLabel as a class property
    private val label = JLabel("GPU Monitor")  // Initial text for the label

    // A variable to simulate dynamic data (e.g., GPU usage)
    private var counter = 0

    init {
        // Start a background thread to update the label text every second
        startUpdatingLabel()
    }

    override fun ID(): String {
        return "GPUMonitor"
    }

    override fun getComponent(): JComponent {
        // Set the tooltip text for the label
        label.toolTipText = "Click to choose option"

        // Add a mouse listener to handle click events
        label.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                // Show dropdown-like popup when the label is clicked
                showListPopup(e)
            }
        })

        return label
    }

    override fun dispose() {
        // Clean up resources if necessary
    }

    // Method to dynamically update the text of the label
    fun updateLabelText(newText: String) {
        // Update label on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater {
            label.text = newText
        }
    }

    // Method to start a background thread that updates the label every second
    private fun startUpdatingLabel() {
        thread(start = true) {
            while (true) {
                // Simulate some dynamic data (e.g., GPU usage)
                counter++

                // Update the label text
                updateLabelText("GPU Usage: $counter%")

                // Sleep for 1 second before updating again
                Thread.sleep(1000)
            }
        }
    }

    private fun showListPopup(mouseEvent: MouseEvent) {
        // List of options (similar to the encoding options in UTF-8 popup)
        val items = listOf("Option 1", "Option 2", "Option 3", "Option 4")

        // Create a ListPopup
        val popup = com.intellij.openapi.ui.popup.JBPopupFactory.getInstance().createPopupChooserBuilder(items)
            .setTitle("Choose an Option")
            .setItemChosenCallback { selectedValue ->
                // Handle the selection and update the label text dynamically
                updateLabelText("Selected: $selectedValue")
            }
            .createPopup()

        // Show the popup at the location of the click
        popup.showInScreenCoordinates(mouseEvent.component, mouseEvent.locationOnScreen)
    }
}
