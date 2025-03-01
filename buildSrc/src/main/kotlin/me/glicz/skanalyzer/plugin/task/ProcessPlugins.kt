package me.glicz.skanalyzer.plugin.task

import me.glicz.skanalyzer.plugin.bootstrap.plugins

abstract class ProcessPlugins : AbstractProcessTask() {
    override val assets
        get() = plugins(project)
}
