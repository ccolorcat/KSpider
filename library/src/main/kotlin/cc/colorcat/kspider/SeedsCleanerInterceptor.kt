package cc.colorcat.kspider

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
internal class SeedsCleanerInterceptor(private val spider: KSpider) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): List<Scrap> {
        val scraps = chain.proceed(chain.seed).let { it as? MutableList ?: it.toMutableList() }
        scraps.removeIf {
            if (it.depth >= spider.maxDepth) {
                spider.dispatcher.onReachedMaxDepth(it)
                true
            } else {
                false
            }
        }
        return scraps
    }
}
