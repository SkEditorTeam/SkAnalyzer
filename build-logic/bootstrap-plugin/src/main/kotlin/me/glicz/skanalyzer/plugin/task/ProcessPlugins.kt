package me.glicz.skanalyzer.plugin.task

import me.glicz.skanalyzer.plugin.bootstrap.plugins
import org.gradle.api.tasks.UntrackedTask

@UntrackedTask(because = "process plugins")
abstract class ProcessPlugins : AbstractProcessTask() {
    override val assets
        get() = plugins(project)
}
