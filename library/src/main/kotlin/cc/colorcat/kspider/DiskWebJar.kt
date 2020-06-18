package cc.colorcat.kspider

import java.io.*
import java.nio.file.Files
import java.nio.file.Path

/**
 * Author: cxx
 * Date:  2020-06-17
 * Github: https://github.com/ccolorcat
 */
class DiskWebJar(private val saveDir: File) : WebJar {
    init {
        if (!saveDir.exists() && !saveDir.mkdirs()) {
            throw IOException("create $saveDir failed")
        }
    }

    override fun save(seed: Seed, snapshot: WebSnapshot) {
        var output: OutputStream? = null
        try {
            output = Files.newOutputStream(getPath(seed)).buffered().apply { write(snapshot.content);flush() }
        } finally {
            close(output)
        }
    }

    override fun load(seed: Seed): WebSnapshot? {
        val file = getPath(seed).toFile()
        if (!file.exists()) return null
        var input: InputStream? = null
        try {
            input = file.inputStream().buffered()
            return WebSnapshot(seed.uri, input.readBytes())
        } finally {
            close(input)
        }
    }

    private fun getPath(seed: Seed): Path {
        return saveDir.toPath().resolve(md5(seed.uri.toString()))
    }
}