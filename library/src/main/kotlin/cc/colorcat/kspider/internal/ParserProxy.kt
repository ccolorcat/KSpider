package cc.colorcat.kspider.internal

import cc.colorcat.kspider.Parser
import cc.colorcat.kspider.Scrap
import cc.colorcat.kspider.Seed
import cc.colorcat.kspider.WebSnapshot
import java.util.*

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
internal class ParserProxy(private val parsers: Map<String, List<Parser>>) : Parser {

    override fun parse(seed: Seed, snapshot: WebSnapshot): List<Scrap> {
        val parsers = this.parsers[seed.tag]
        if (parsers == null || parsers.isEmpty()) return emptyList()
        val scraps = LinkedList<Scrap>()
        parsers.forEach { scraps.addAll(it.parse(seed, snapshot)) }
        return scraps
    }
}
