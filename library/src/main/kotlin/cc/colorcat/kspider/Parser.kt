package cc.colorcat.kspider

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
interface Parser {
    fun parse(seed: Seed, snapshot: WebSnapshot): List<Scrap>

    fun canParse(url: String): Boolean = true
}