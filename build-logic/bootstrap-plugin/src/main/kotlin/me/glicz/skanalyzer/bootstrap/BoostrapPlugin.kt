package me.glicz.skanalyzer.bootstrap

import me.glicz.skanalyzer.bootstrap.task.BundleAssets
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.kotlin.dsl.get

class BoostrapPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val plugin by project.configurations.registering {
            isTransitive = false
        }

        val bundleLibraries by project.tasks.registering(BundleAssets::class) {
            group = "skanalyzer"

            bundleName.set("libraries")
            configuration.set(project.configurations["runtimeClasspath"])
        }

        val bundlePlugins by project.tasks.registering(BundleAssets::class) {
            group = "skanalyzer"

            bundleName.set("plugins")
            assetIdResolver.set { artifact -> artifact.name }
            assetPathResolver.set { artifact -> "${artifact.name}.jar" }
            configuration.set(plugin)
        }

        project.tasks.withType<Jar> {
            from(bundleLibraries) {
                into("META-INF")
            }

            from(bundlePlugins) {
                into("META-INF")
            }
        }
    }
}
