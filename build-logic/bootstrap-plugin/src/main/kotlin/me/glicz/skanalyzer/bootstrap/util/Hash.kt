package me.glicz.skanalyzer.bootstrap.util

internal class Hash(private val bytes: ByteArray) {
    override fun toString() = bytes.toHexString()
}
