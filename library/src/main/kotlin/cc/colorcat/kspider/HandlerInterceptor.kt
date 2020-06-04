package cc.colorcat.kspider

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
internal class HandlerInterceptor(private val spider: KSpider) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): List<Scrap> {
        val scraps = chain.proceed(chain.seed).let { it as? MutableList ?: it.toMutableList() }
        scraps.removeIf {
            it.data.isNotEmpty() && tryHandle(it)
        }
        return scraps
    }

    private fun tryHandle(scrap: Scrap): Boolean {
        var handled = false
        spider.handlers[scrap.tag]?.forEach { if (it.handle(scrap)) handled = true }
        if (handled) spider.dispatcher.handled(scrap)
        return handled
    }
}