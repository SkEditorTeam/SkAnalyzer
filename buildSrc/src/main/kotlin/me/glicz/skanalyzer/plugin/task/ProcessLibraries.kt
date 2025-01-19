package me.glicz.skanalyzer.plugin.task

import me.glicz.skanalyzer.plugin.bootstrap.Assets
import me.glicz.skanalyzer.plugin.bootstrap.runtimeLibraries
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class ProcessLibraries : AbstractProcessTask() {
    override val assets
        get() = runtimeLibraries(project)
}
