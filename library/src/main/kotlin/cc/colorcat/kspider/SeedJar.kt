package cc.colorcat.kspider

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
interface SeedJar {
    fun save(success: List<Seed>, failed: List<Seed>, reachedMaxDepth: List<Seed>)

    fun load(): List<Seed>
}