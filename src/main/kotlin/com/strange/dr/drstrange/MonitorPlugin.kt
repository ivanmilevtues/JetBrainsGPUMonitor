package com.strange.dr.drstrange

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.WindowManager
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.components.ProjectComponent
import com.strange.dr.drstrange.widgets.MonitorStatusBarWidget


class MonitorPlugin(private val project: Project) : ProjectComponent {

    override fun projectOpened() {
        // Get the status bar and add your custom widget
        val statusBar: StatusBar? = WindowManager.getInstance().getStatusBar(project)
        statusBar?.addWidget(MonitorStatusBarWidget(), "after PositionWidget", project)
    }

    override fun projectClosed() {
        // Cleanup if necessary
    }
}
