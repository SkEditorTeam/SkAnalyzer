package me.glicz.skanalyzer.bundler

import me.glicz.skanalyzer.bundler.task.BundleAssets
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.bundling.Zip
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.*

class BundlerPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        val library by configurations.registering
        val plugin by configurations.registering {
            isTransitive = false
        }

        val bundleLibraries by tasks.registering(BundleAssets::class) {
            group = "skanalyzer internal"

            bundleName.set("libraries")
            configuration.set(library)
        }

        val bundlePlugins by tasks.registering(BundleAssets::class) {
            group = "skanalyzer internal"

            bundleName.set("plugins")
            assetIdResolver.set { artifact -> artifact.name }
            assetPathResolver.set { artifact -> "${artifact.name}.jar" }
            configuration.set(plugin)
        }

        val bundlerJar by tasks.registering(Zip::class) {
            group = "skanalyzer"
            doNotTrackState(name)

            archiveBaseName = "${project.name}-bundler"
            archiveExtension = Jar.DEFAULT_EXTENSION
            setMetadataCharset(Charsets.UTF_8.name())

            from(bundleLibraries) {
                into("META-INF")
            }

            from(bundlePlugins) {
                into("META-INF")
            }
        }

        tasks.registerRunTask("runDev") {
            classpath(library)

            plugin {
                files.forEach {
                    args("--add-plugin=${it.absolutePath}")
                }
            }
            dependsOn(plugin)

            debugOptions {
                suspend = false
                server = true
                port = 5005
                host = "*"
            }

            jvmArgs("-XX:+AllowEnhancedClassRedefinition")
        }

        tasks.registerRunTask("runBundler") {
            classpath(bundlerJar)
        }
    }

    private fun TaskContainer.registerRunTask(
        name: String,
        configurationAction: Action<in JavaExec>
    ) = register(name, JavaExec::class) {
        group = "skanalyzer"
        doNotTrackState(name)

        standardInput = System.`in`

        if (System.getProperty("idea.active")?.toBoolean() == true) {
            jvmArgs("-Djansi.passthrough=true")
        }

        doFirst {
            workingDir(project.rootDir.resolve("run").apply {
                mkdirs()
            })
        }

        val java: JavaPluginExtension by project.extensions
        val javaToolchains: JavaToolchainService by project.extensions

        javaLauncher.set(javaToolchains.launcherFor {
            vendor.set(JvmVendorSpec.JETBRAINS)
            languageVersion.set(java.toolchain.languageVersion)
        })

        configurationAction.execute(this)
    }
}
