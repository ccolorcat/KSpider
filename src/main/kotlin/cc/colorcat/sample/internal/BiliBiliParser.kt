package cc.colorcat.sample.internal

import cc.colorcat.kspider.*
import org.jsoup.Jsoup

/**
 * Author: cxx
 * Date:  2020-06-16
 * Github: https://github.com/ccolorcat
 */

private const val TAG = "Bilibili"

class BiliDynamicParser : Parser {
    private companion object {
        private val dynamic = "^(http)(s)?://space.bilibili.com/\\d+/(dynamic)$".toRegex()
    }

    /**
     * <a data-v-c6f3a60c="" href="//t.bilibili.com/400893746359688855?tab=2" target="_blank" class="detail-link tc-slate">昨天 07:52</a>
     */
    override fun parse(seed: Seed, snapshot: WebSnapshot): List<Scrap> {
        val scraps = linkedListOf<Scrap>()
        val doc = Jsoup.parse(snapshot.contentToString(), seed.baseUrl())
        doc.select("a[class=detail-link tc-slate][href~=^(//t.bilibili.com)(.)*][target=_blank]")
            .map { it.attr("href") }
            .forEach {
                val scrap = seed.newScrapWithJoin(it, pattern = DepthPattern.PARALLEL)
                Log.v(TAG, "detail url: ${scrap.uri}")
                scraps.add(scrap)
            }
        return scraps
    }

    override fun canParse(url: String): Boolean {
        return url.matches(dynamic);
    }
}

/**
 * <div data-v-2ef3df58="" class="boost-img-container"><img data-v-2ef3df58="" src="//i0.hdslb.com/bfs/album/8579d97cac5be5cb612cc850850d33b178cb518b.jpg@1036w_1e_1c.jpg" height="732.1554770318021" style="max-width: 518px;"></div>
 *
 * <div data-v-2ef3df58="" class="img-content" style="background-image: url(&quot;//i0.hdslb.com/bfs/album/d99a48e1b6383e1bdc0488608c7639e5b71de394.png@480w_640h_1e_1c.webp&quot;); width: 240px; height: 320px;"></div>
 */
class BiliDynamicDetailParser : Parser {
    private companion object {
        private val dynamicDetail = "^(http)(s)?://t.bilibili.com/(.)*".toRegex()
    }

    override fun parse(seed: Seed, snapshot: WebSnapshot): List<Scrap> {
        return Jsoup.parse(snapshot.contentToString(), seed.baseUrl())
            .select("div.img-content[style~=(.)*//(.)+@(.)*]")
            .map { parseScrap(seed, it.attr("style")) }
    }

    override fun canParse(url: String): Boolean {
        return url.matches(dynamicDetail)
    }
}


/**
 * https://space.bilibili.com/306069337/dynamic
 */


class BiliDynamicParser2 : Parser {
    private companion object {
        private val dynamic = "^(http)(s)?://space.bilibili.com/\\d+/(dynamic)$".toRegex()
    }

    override fun parse(seed: Seed, snapshot: WebSnapshot): List<Scrap> {
        val result = linkedListOf<Scrap>()
        val doc = Jsoup.parse(snapshot.contentToString(), seed.baseUrl())

        doc.select("div.img-content[style~=(.)*(//|http|https)(.)+@(.)*]")
            .mapTo(result) { parseScrap(seed, it.attr("style")) }
        doc.select("img.card-3[src~=^(http|https|//)(.)*]")
            .mapTo(result) { parseScrap(seed, it.attr("src")) }

        return result
    }

    override fun canParse(url: String): Boolean {
        return url.matches(dynamic)
    }
}

private fun parseScrap(seed: Seed, style: String, pattern: DepthPattern = DepthPattern.RAISE): Scrap {
    val start = style.indexOf("http").let { if (it == -1) style.indexOf("//") else it }
    val end = style.indexOf('@')
    val url = style.substring(start, end).let { if (it.startsWith("http")) it else seed.newUriWithJoin(it) }
    return seed.newScrapWithFill("url", url, pattern)
}

/**
 * https://space.bilibili.com/306069337/album
 */
class BiliAlbumParser : Parser {
    private companion object {
        private val album = "^(http)(s)?://space.bilibili.com/\\d+/(album)$".toRegex()
    }

    override fun parse(seed: Seed, snapshot: WebSnapshot): List<Scrap> {
        return Jsoup.parse(snapshot.contentToString(), seed.baseUrl())
            .select("a.picture[style~=(.)*(//|http|https)(.)+@(.)*]")
            .map { parseScrap(seed, it.attr("style"), DepthPattern.PARALLEL) }
    }

    override fun canParse(url: String): Boolean {
        return url.matches(album)
    }
}