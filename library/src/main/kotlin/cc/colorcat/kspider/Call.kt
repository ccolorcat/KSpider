package cc.colorcat.kspider

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
interface Call : Runnable {
    val seed: Seed

    val retryCount: Int

    fun incrementRetryCount()

    fun execute()

    interface Factory {
        fun newCall(seed: Seed): Call
    }
}