package cc.colorcat.sample.internal

import cc.colorcat.kspider.*
import org.jsoup.Jsoup

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
class HDWallpaperParser : Parser {
    private companion object {
        private const val HOST = "www.hdwallpapers.in"
    }

    override fun parse(seed: Seed, snapshot: WebSnapshot): List<Scrap> {
        if (HOST != seed.uri.host) return emptyList()
        val scraps = linkedListOf<Scrap>()
        val doc = Jsoup.parse(snapshot.contentToString(), seed.baseUrl())

        // find image's detail page
        doc.select("ul.wallpapers a[href$=.html]")
            .map { it.attr("href") }
            .mapTo(scraps) { seed.newScrapWithJoin(it) }

        // find next page
        doc.select("div.pagination > span.selected + a[href^=/]")
            .map { it.attr("href") }
            .mapTo(scraps) { seed.newScrapWithJoin(it) }

        // find image url
        doc.select("div.thumbbg1 a[href~=^(/)(.)*\\.(jpg|png|jpeg)][target=_blank]")
            .map { it.attr("href") }
            .mapTo(scraps) { seed.newScrapWithFill("url", seed.newUriWithJoin(it)) }

        return scraps
    }
}