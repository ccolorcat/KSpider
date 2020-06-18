package cc.colorcat.kspider

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
internal class ConnectionInterceptor(private val webJar: WebJar) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): List<Scrap> {
        val seed = chain.seed
        var snapshot = webJar.load(seed)
        if (snapshot == null) {
            snapshot = chain.connection.get(seed)
            if (snapshot != null) {
                webJar.save(seed, snapshot)
            }
        }
        return if (snapshot != null) chain.parser.parse(seed, snapshot) else emptyList()
    }
}