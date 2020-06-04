package cc.colorcat.sample.internal

import cc.colorcat.kspider.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * Author: cxx
 * Date: 2020-05-28
 * GitHub: https://github.com/ccolorcat
 */
class TpzjParser : Parser {
    // https://www.tupianzj.com/meinv/20200330/207762.html
    private companion object {
        private const val HOST = "www.tupianzj.com"
    }

    override fun parse(seed: Seed, snapshot: WebSnapshot): List<Scrap> {
        if (HOST != seed.uri.host.toLowerCase()) return emptyList()
        val doc = Jsoup.parse(snapshot.contentToString())
        val imageAndNext = doc.selectFirst("div#bigpic") ?: return emptyList()
        val title = parseTitle(imageAndNext, doc)

        val imageUrl = imageAndNext.selectFirst("img#bigpicimg[src~=$IMAGE_URL_REGEX]").attr("src")
        val imageScrap = seed.newScrapWithFill("url", imageUrl, DepthPattern.PARALLEL).fillIfAbsent("dir", title)

        val nextPagePath = imageAndNext.selectFirst("a[href~=$HTML_PATH_REGEX][target=_self]").attr("href")
        val pattern = if (nextPagePath.startsWith("/")) DepthPattern.RAISE else DepthPattern.PARALLEL
        val nextPageScrap = seed.newScrapWithJoin(nextPagePath, pattern)
            .fillIfAbsent("Host", HOST)
            .fill("Referer", seed.uri.toString())
        if (pattern == DepthPattern.RAISE) nextPageScrap.remove("dir") else nextPageScrap.fillIfAbsent("dir", title)
        return listOf(imageScrap, nextPageScrap)
    }

    private fun parseTitle(imageAndNext: Element, doc: Document): String {
        var title = doc.selectFirst("div[class=list_con bgff] > h1").text()
        if (title.isNullOrEmpty()) {
            title = "tupianzj"
        }
        return title
    }
}