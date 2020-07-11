package cc.colorcat.kspider

import java.io.IOException

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
interface Connection : Cloneable {
    @Throws(IOException::class)
    fun get(seed: Seed): WebSnapshot?

    public override fun clone(): Connection

    fun onSeedFinish(seed: Seed) {}

    fun onAllFinish() {}

    fun canConnect(seed: Seed): Boolean = true
}
