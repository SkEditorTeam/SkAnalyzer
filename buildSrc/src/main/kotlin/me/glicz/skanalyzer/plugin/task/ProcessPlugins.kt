package me.glicz.skanalyzer.plugin.task

import me.glicz.skanalyzer.plugin.bootstrap.Assets
import me.glicz.skanalyzer.plugin.bootstrap.plugins
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class ProcessPlugins : AbstractProcessTask() {
    override val assets
        get() = plugins(project)
}
