package me.glicz.skanalyzer.plugin.bootstrap

import me.glicz.skanalyzer.plugin.util.plugin
import me.glicz.skanalyzer.plugin.util.sha256Digest
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedArtifact

class Plugin(artifact: ResolvedArtifact) : Asset(
    sha256Digest.digest(artifact.file.readBytes()),
    artifact.name,
    "${artifact.name}.jar",
    artifact.file
)

class Plugins(list: List<Plugin>) : Assets<Plugin>("plugins", list)

fun plugins(project: Project): Plugins {
    return Plugins(project.configurations.plugin.resolvedConfiguration.resolvedArtifacts.map { Plugin(it) })
}
