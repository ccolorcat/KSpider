package cc.colorcat.kspider

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
internal class ConnectionInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): List<Scrap> {
        val seed = chain.seed
        val snapshot = chain.connection.get(seed)
        return if (snapshot != null) chain.parser.parse(seed, snapshot) else emptyList()
    }
}