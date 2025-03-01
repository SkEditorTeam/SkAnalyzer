package me.glicz.skanalyzer.plugin

import me.glicz.skanalyzer.plugin.task.ProcessLibraries
import me.glicz.skanalyzer.plugin.task.ProcessPlugins
import me.glicz.skanalyzer.plugin.util.plugin
import me.glicz.skanalyzer.plugin.util.runtimeClasspath
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.registering
import org.gradle.kotlin.dsl.withType

class SkAnalyzerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val plugin by project.configurations.registering {
            isTransitive = false
        }

        val processLibraries by project.tasks.registering(ProcessLibraries::class) {
            group = "skanalyzer"

            dependsOn(project.configurations.runtimeClasspath.buildDependencies)

            output.set(project.layout.buildDirectory.dir("libraries"))
        }

        val processPlugins by project.tasks.registering(ProcessPlugins::class) {
            group = "skanalyzer"

            dependsOn(project.configurations.plugin.buildDependencies)

            output.set(project.layout.buildDirectory.dir("plugins"))
        }

        project.tasks.withType<Jar> {
            from(processLibraries.map { it.output })
            from(processPlugins.map { it.output })
        }
    }
}
