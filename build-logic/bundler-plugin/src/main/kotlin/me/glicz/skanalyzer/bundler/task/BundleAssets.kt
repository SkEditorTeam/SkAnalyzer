package me.glicz.skanalyzer.bundler.task

import me.glicz.skanalyzer.bundler.util.Hash
import me.glicz.skanalyzer.bundler.util.MessageDigests.sha256
import me.glicz.skanalyzer.bundler.util.asPath
import me.glicz.skanalyzer.bundler.util.digest
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.nio.file.Path
import kotlin.io.path.*

@UntrackedTask(because = "dynamic assets")
abstract class BundleAssets : DefaultTask() {
    @get:Input
    abstract val bundleName: Property<String>

    @get:Input
    abstract val assetIdResolver: Property<(ResolvedArtifact) -> String>

    @get:Input
    abstract val assetPathResolver: Property<(ResolvedArtifact) -> String>

    @get:Classpath
    abstract val configuration: Property<Configuration>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    init {
        assetIdResolver.convention { artifact ->
            artifact.moduleVersion.id.toString()
        }

        assetPathResolver.convention { artifact ->
            "${artifact.moduleVersion.id.asPath}/${artifact.file.name}"
        }

        outputDir.convention(bundleName.flatMap { bundleName ->
            project.layout.buildDirectory.dir("generated/${bundleName}")
        })
    }

    @TaskAction
    fun run() {
        val bundleName = bundleName.get()

        val outputDir = outputDir.get().asPath.apply {
            @OptIn(ExperimentalPathApi::class)
            deleteRecursively()
            createDirectories()
        }

        val libraries = configuration.get().resolvedConfiguration.resolvedArtifacts.map { artifact ->
            Asset(
                hash = Hash(sha256().digest(artifact.file)),
                id = assetIdResolver.get().invoke(artifact),
                path = assetPathResolver.get().invoke(artifact),
                artifact = artifact.file.toPath()
            )
        }

        outputDir.resolve("$bundleName.list").apply {
            createFile()

            writeText(libraries.joinToString("\n") { library ->
                val artifactPath = outputDir.resolve(bundleName).resolve(library.path).apply {
                    parent.createDirectories()
                }
                library.artifact.copyTo(artifactPath)

                library.toString()
            })
        }
    }

    private class Asset(val hash: Hash, val id: String, val path: String, val artifact: Path) {
        override fun toString() = "$hash\t$id\t$path"
    }
}