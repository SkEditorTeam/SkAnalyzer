package me.glicz.skanalyzer.plugin.task

import me.glicz.skanalyzer.plugin.bootstrap.Assets
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class AbstractProcessTask : DefaultTask() {
    @get:OutputDirectory
    abstract val output: DirectoryProperty

    @get:Internal
    abstract val assets: Assets<*>

    @TaskAction
    fun run() {
        val metaInf = output.dir("META-INF").get().asFile.apply {
            deleteRecursively()
            mkdirs()
        }

        assets.write(metaInf)
    }
}
