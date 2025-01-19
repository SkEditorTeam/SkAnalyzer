package me.glicz.skanalyzer.plugin.utils

import org.gradle.api.artifacts.ModuleVersionIdentifier
import java.security.MessageDigest

val sha256Digest: MessageDigest = MessageDigest.getInstance("SHA-256")

fun ModuleVersionIdentifier.asPath(): String {
    return "${group.replace('.', '/')}/${name.replace('.', '/')}"
}

val ByteArray.stringHash
    get() = joinToString("") { byte ->
        "%02x".format(byte)
    }
