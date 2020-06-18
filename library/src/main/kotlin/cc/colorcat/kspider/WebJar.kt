package cc.colorcat.kspider

/**
 * Author: cxx
 * Date:  2020-06-17
 * Github: https://github.com/ccolorcat
 */
interface WebJar {
    fun save(seed: Seed, snapshot: WebSnapshot)

    fun load(seed: Seed): WebSnapshot?
}