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
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the

class BundlerPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        val library = configurations.register("library")
        val plugin = configurations.register("plugin") {
            isTransitive = false
        }

        val bundleLibraries = tasks.register<BundleAssets>("bundleLibraries") {
            group = "skanalyzer internal"

            bundleName.set("libraries")
            configuration.set(library)
        }

        val bundlePlugins = tasks.register<BundleAssets>("bundlePlugins") {
            group = "skanalyzer internal"

            bundleName.set("plugins")
            assetIdResolver.set { artifact -> artifact.name }
            assetPathResolver.set { artifact -> "${artifact.name}.jar" }
            configuration.set(plugin)
        }

        val bundlerJar = tasks.register<Zip>("bundlerJar") {
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
        configurationAction: Action<in JavaExec>,
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

        val java = project.the<JavaPluginExtension>()
        val javaToolchains = project.the<JavaToolchainService>()

        javaLauncher.set(javaToolchains.launcherFor {
            vendor.set(JvmVendorSpec.JETBRAINS)
            languageVersion.set(java.toolchain.languageVersion)
        })

        configurationAction.execute(this)
    }
}
