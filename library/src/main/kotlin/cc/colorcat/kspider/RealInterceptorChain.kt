package cc.colorcat.kspider

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
internal class RealInterceptorChain(
        override val seed: Seed,
        override val connection: Connection,
        override val parser: Parser,
        private val interceptors: List<Interceptor>,
        private val index: Int
) : Interceptor.Chain {

    override fun proceed(seed: Seed): List<Scrap> {
        val next = RealInterceptorChain(seed, connection, parser, interceptors, index + 1)
        val interceptor = interceptors[index]
        return interceptor.intercept(next)
    }
}
