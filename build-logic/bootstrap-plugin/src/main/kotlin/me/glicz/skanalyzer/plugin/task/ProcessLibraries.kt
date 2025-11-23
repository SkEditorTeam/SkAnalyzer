package me.glicz.skanalyzer.plugin.task

import me.glicz.skanalyzer.plugin.bootstrap.runtimeLibraries
import org.gradle.api.tasks.UntrackedTask

@UntrackedTask(because = "process task")
abstract class ProcessLibraries : AbstractProcessTask() {
    override val assets
        get() = runtimeLibraries(project)
}
