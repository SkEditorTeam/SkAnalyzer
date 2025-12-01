package me.glicz.skanalyzer.bundler.util

import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.file.FileSystemLocation
import java.nio.file.Path

internal val FileSystemLocation.asPath: Path
    get() = asFile.toPath()

internal val ModuleVersionIdentifier.asPath
    get() = "$group/$name".replace('.', '/')

internal fun ByteArray.toHexString() =
    joinToString("") { "%02x".format(it) }
