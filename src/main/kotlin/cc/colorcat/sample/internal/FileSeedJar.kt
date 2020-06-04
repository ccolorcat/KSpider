package cc.colorcat.sample.internal

import cc.colorcat.kspider.Seed
import cc.colorcat.kspider.SeedJar
import java.io.File
import java.io.IOException

/**
 * Author: cxx
 * Date: 2020-05-28
 * GitHub: https://github.com/ccolorcat
 */
class FileSeedJar(private val saveDir: File) : SeedJar {
    init {
        saveDir.parentFile.mkdirs()
    }

    override fun save(success: List<Seed>, failed: List<Seed>, reachedMaxDepth: List<Seed>) {
        val seeds = (failed + reachedMaxDepth).map { it.newSeedWithResetDepth() }
        saveDir.writeText(toJson(seeds))
    }

    override fun load(): List<Seed> {
        return try {
            fromJson(saveDir.readText())
        } catch (e: IOException) {
            emptyList()
        }
    }
}