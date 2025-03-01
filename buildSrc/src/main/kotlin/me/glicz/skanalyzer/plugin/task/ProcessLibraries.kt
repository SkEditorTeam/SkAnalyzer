package me.glicz.skanalyzer.plugin.task

import me.glicz.skanalyzer.plugin.bootstrap.runtimeLibraries

abstract class ProcessLibraries : AbstractProcessTask() {
    override val assets
        get() = runtimeLibraries(project)
}
