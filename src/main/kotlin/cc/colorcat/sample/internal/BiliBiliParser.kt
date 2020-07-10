package cc.colorcat.sample.internal

import cc.colorcat.kspider.*
import org.jsoup.Jsoup

/**
 * Author: cxx
 * Date:  2020-06-16
 * Github: https://github.com/ccolorcat
 */

private const val TAG = "Bilibili"
const val BILI_SPACE_HOST = "space.bilibili.com"
const val BILI_SPACE_DETAIL_HOST = "t.bilibili.com"


class BiliDynamicParser : Parser {
    /**
     * <a data-v-c6f3a60c="" href="//t.bilibili.com/400893746359688855?tab=2" target="_blank" class="detail-link tc-slate">昨天 07:52</a>
     */
    override fun parse(seed: Seed, snapshot: WebSnapshot): List<Scrap> {
        if (BILI_SPACE_HOST != seed.uri.host.toLowerCase()) return emptyList()

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
}

/**
 * <div data-v-2ef3df58="" class="boost-img-container"><img data-v-2ef3df58="" src="//i0.hdslb.com/bfs/album/8579d97cac5be5cb612cc850850d33b178cb518b.jpg@1036w_1e_1c.jpg" height="732.1554770318021" style="max-width: 518px;"></div>
 *
 * <div data-v-2ef3df58="" class="img-content" style="background-image: url(&quot;//i0.hdslb.com/bfs/album/d99a48e1b6383e1bdc0488608c7639e5b71de394.png@480w_640h_1e_1c.webp&quot;); width: 240px; height: 320px;"></div>
 */
class BiliDynamicDetailParser : Parser {
    override fun parse(seed: Seed, snapshot: WebSnapshot): List<Scrap> {
        if (BILI_SPACE_DETAIL_HOST != seed.uri.host.toLowerCase()) return emptyList()

        return Jsoup.parse(snapshot.contentToString(), seed.baseUrl())
            .select("div.img-content[style~=(.)*//(.)+@(.)*]")
            .map { parseScrap(seed, it.attr("style")) }
    }

    private fun parseScrap(seed: Seed, style: String): Scrap {
        val start = style.indexOf("//")
        val end = style.indexOf('@')
        val url = style.substring(start, end)
        return seed.newScrapWithFill("url", seed.newUriWithJoin(url), DepthPattern.RAISE)
    }
}


/**
 * https://space.bilibili.com/306069337/dynamic
 */
private val DYNAMIC_PAGE = "^(http)(s)?://space.bilibili.com/\\d+/(dynamic)$".toRegex()

class BiliDynamicParser2 : Parser {
    override fun parse(seed: Seed, snapshot: WebSnapshot): List<Scrap> {
//        if (BILI_SPACE_HOST != seed.uri.host.toLowerCase()) return emptyList()
        if (!seed.uri.toString().toLowerCase().matches(DYNAMIC_PAGE)) return emptyList()

        val result = linkedListOf<Scrap>()
        val doc = Jsoup.parse(snapshot.contentToString(), seed.baseUrl())

        doc.select("div.img-content[style~=(.)*(//|http|https)(.)+@(.)*]")
            .mapTo(result) { parseScrap(seed, it.attr("style")) }
        doc.select("img.card-3[src~=^(http|https|//)(.)*]")
            .mapTo(result) { parseScrap(seed, it.attr("src")) }

        return result
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
private val ALBUM_PAGE = "^(http)(s)?://space.bilibili.com/\\d+/(album)$".toRegex()

class BiliAlbumParser : Parser {
    override fun parse(seed: Seed, snapshot: WebSnapshot): List<Scrap> {
        if (!seed.uri.toString().toLowerCase().matches(ALBUM_PAGE)) return emptyList()
        return Jsoup.parse(snapshot.contentToString(), seed.baseUrl())
            .select("a.picture[style~=(.)*(//|http|https)(.)+@(.)*]")
            .map { parseScrap(seed, it.attr("style"), DepthPattern.PARALLEL) }
    }
}