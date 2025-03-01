package me.glicz.skanalyzer.plugin.bootstrap

import me.glicz.skanalyzer.plugin.util.asPath
import me.glicz.skanalyzer.plugin.util.runtimeClasspath
import me.glicz.skanalyzer.plugin.util.sha256Digest
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedArtifact

class Library(artifact: ResolvedArtifact) : Asset(
    sha256Digest.digest(artifact.file.readBytes()),
    artifact.moduleVersion.id.toString(),
    "${artifact.moduleVersion.id.asPath()}/${artifact.file.name}",
    artifact.file
)

class Libraries(list: List<Library>) : Assets<Library>("libraries", list)

fun runtimeLibraries(project: Project): Libraries {
    return Libraries(project.configurations.runtimeClasspath.resolvedConfiguration.resolvedArtifacts.map { Library(it) })
}
