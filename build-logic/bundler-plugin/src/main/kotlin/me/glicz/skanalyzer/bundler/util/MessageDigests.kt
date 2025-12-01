package me.glicz.skanalyzer.bundler.util

import java.io.File
import java.security.MessageDigest

internal object MessageDigests {
    fun sha256() = MessageDigest.getInstance("SHA-256")!!
}

internal fun MessageDigest.digest(file: File) = digest(file.readBytes())!!
