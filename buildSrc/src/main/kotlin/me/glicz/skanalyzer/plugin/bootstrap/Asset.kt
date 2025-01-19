package me.glicz.skanalyzer.plugin.bootstrap

import me.glicz.skanalyzer.plugin.utils.stringHash
import java.io.File

abstract class Asset(val hash: ByteArray, val id: String, val path: String, protected val file: File) {
    fun write(dir: File) {
        val out = dir.resolve(path)
        out.parentFile.mkdirs()

        file.copyTo(out)
    }

    override fun toString(): String {
        return "${hash.stringHash}\t$id\t$path"
    }
}

abstract class Assets<T : Asset>(val name: String, val list: List<T>) {
    fun write(metaInf: File) {
        val assetsDir = metaInf.resolve(name);

        metaInf.resolve("$name.list")
            .apply { createNewFile() }
            .writeText(list.joinToString("\n") { it.write(assetsDir); it.toString() })
    }
}
