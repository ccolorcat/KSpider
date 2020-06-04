package cc.colorcat.kspider

import java.io.IOException

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
interface Interceptor {
    @Throws(IOException::class)
    fun intercept(chain: Chain): List<Scrap>

    interface Chain {
        val seed: Seed

        val connection: Connection

        val parser: Parser

        @Throws(IOException::class)
        fun proceed(seed: Seed): List<Scrap>
    }
}