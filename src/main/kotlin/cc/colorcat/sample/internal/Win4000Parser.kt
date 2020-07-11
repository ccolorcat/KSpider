package cc.colorcat.sample.internal

import cc.colorcat.kspider.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * Author: cxx
 * Date: 2020-05-27
 * GitHub: https://github.com/ccolorcat
 */

class Win4000Parser : Parser {
    // http://www.win4000.com/meinv202118_1.html
    companion object {
        private val FILTER = "^(http)(s)?://www.win4000.com/(.)*".toRegex()
        val nextGroupStartPage = "^(http)(s)?://(.)*((\\d{4,}.(htm|html))|(_1.(htm|html)))$".toRegex()
    }

    override fun parse(seed: Seed, snapshot: WebSnapshot): List<Scrap> {
        val doc = Jsoup.parse(snapshot.contentToString(), seed.baseUrl())
        val imageAndNext = doc.selectFirst("div.pic-meinv a[href~=^(http)(s)?://(.)*\\.(htm|html)$]")
        val image = imageAndNext.selectFirst("img.pic-large[src~=^(http)(s)?://(.)*\\.(jpg|png|jpeg)$]")

        val title = parseTitle(imageAndNext, doc)

        val imageUrl = image.attr("src")
        val imageScrap = seed.newScrapWithFill(mapOf("url" to imageUrl, "dir" to title), DepthPattern.PARALLEL)

        val nextUrl = imageAndNext.attr("href")
        val pattern = if (nextUrl.matches(nextGroupStartPage)) DepthPattern.RAISE else DepthPattern.PARALLEL
        val nextScrap = seed.newScrapWithJoin(nextUrl, pattern)
            .fill("dir", title)
            .fillIfAbsent("Host", seed.uri.host)
            .fill("Referer", seed.uri.toString())

        return listOf(imageScrap, nextScrap)
    }

    override fun canParse(url: String): Boolean {
        return url.matches(FILTER)
    }

    private fun parseTitle(imageAndNext: Element, doc: Document): String {
        var title = imageAndNext.attr("title")
        if (title.isNullOrEmpty()) {
            title = doc.selectFirst("dir.ptitle > h1")?.text()
        }
        if (title.isNullOrEmpty()) {
            title = doc.selectFirst("meta[name=keywords]")?.attr("content")
        }
        if (title.isNullOrEmpty()) {
            title = "win4000"
        }
        return title
    }
}