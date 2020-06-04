package cc.colorcat.kspider

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
interface EventListener {
    fun onSuccess(seed: Seed)

    fun onFailure(seed: Seed, reason: Throwable)

    fun onReachedMaxDepth(seed: Seed)

    fun onHandled(scrap: Scrap)
}