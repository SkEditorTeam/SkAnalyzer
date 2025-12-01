package me.glicz.skanalyzer.bundler.util

internal class Hash(private val bytes: ByteArray) {
    override fun toString() = bytes.toHexString()
}
