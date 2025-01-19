package me.glicz.skanalyzer.plugin.bootstrap

import org.gradle.api.artifacts.ResolvedArtifact
import me.glicz.skanalyzer.plugin.utils.*
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import java.io.File
import kotlin.io.path.writeLines

class Library(private val artifact: ResolvedArtifact) : Asset(
    sha256Digest.digest(artifact.file.readBytes()),
    artifact.moduleVersion.id.toString(),
    "${artifact.moduleVersion.id.asPath()}/${artifact.file.name}",
    artifact.file
)

class Libraries(list: List<Library>) : Assets<Library>("libraries", list)

fun runtimeLibraries(project: Project): Libraries {
    return Libraries(project.configurations.runtimeClasspath.resolvedConfiguration.resolvedArtifacts.map { Library(it) })
}
