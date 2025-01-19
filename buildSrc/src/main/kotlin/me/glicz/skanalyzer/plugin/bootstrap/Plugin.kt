package me.glicz.skanalyzer.plugin.bootstrap

import org.gradle.api.artifacts.ResolvedArtifact
import me.glicz.skanalyzer.plugin.utils.*
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import java.io.File
import kotlin.io.path.writeLines

class Plugin(private val artifact: ResolvedArtifact) : Asset(
    sha256Digest.digest(artifact.file.readBytes()),
    artifact.name,
    "${artifact.name}.jar",
    artifact.file
)

class Plugins(list: List<Plugin>) : Assets<Plugin>("plugins", list)

fun plugins(project: Project): Plugins {
    return Plugins(project.configurations.plugin.resolvedConfiguration.resolvedArtifacts.map { Plugin(it) })
}
