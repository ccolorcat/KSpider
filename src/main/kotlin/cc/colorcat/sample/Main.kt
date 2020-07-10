package cc.colorcat.sample

import cc.colorcat.kspider.*
import cc.colorcat.kspider.EventListener
import cc.colorcat.sample.internal.*
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import java.io.File
import java.nio.file.Paths
import java.time.LocalDate
import java.util.*

/**
 * Author: cxx
 * Date: 2020-05-28
 * GitHub: https://github.com/ccolorcat
 */
private const val TAG = "Event"
private const val DRIVER_PATH = "/Users/cxx/Workspace/library/chromedriver"

private val saveDir = Paths.get(System.getProperty("user.home"), "spider").toString()

//private val saveDir = "/Volumes/Untitled/spider"
private val spider = KSpider.Builder()
    .defaultConnection(WebDriverConnection(DRIVER_PATH, false))
//    .registerConnection(
//        arrayOf(BILI_SPACE_HOST, BILI_SPACE_DETAIL_HOST),
//        BiliWebDriverConnection(DRIVER_PATH, false, ::reachEndBili)
//    )
    .registerConnection(BILI_SPACE_HOST, BiliAlbumWebDriverConnection(DRIVER_PATH, false))
    .seedJar(FileSeedJar(File(saveDir, "seed.txt")))
    .webJar(DiskWebJar(Paths.get(saveDir, "cache").toFile()))
    .registerParser("bing", BingParser())
    .registerHandler("bing", BingHandler(saveDir))
    .registerParser("image", Win4000Parser())
    .registerParser("image", HDWallpaperParser())
    .registerParser("image", TpzjParser())
//    .registerParser("image", BiliDynamicParser())
//    .registerParser("image", BiliDynamicDetailParser())
//    .registerParser("image", BiliDynamicParser2())
    .registerParser("image", BiliAlbumParser())
    .registerHandler("image", ImageHandler(saveDir))
    .eventListener(object : EventListener {
        override fun onSuccess(seed: Seed) {
        }

        override fun onFailure(seed: Seed, reason: Throwable) {
            Log.e(TAG, "onFailure: $seed reason:$reason")
        }

        override fun onReachedMaxDepth(seed: Seed) {
            Log.w(TAG, "reachedMaxDepth: $seed")
        }

        override fun onHandled(scrap: Scrap) {
//            Log.d(TAG, "handled: $scrap")
        }
    })
    .maxDepth(10)
    .depthFirst(true)
    .build()

private val reachData = LocalDate.of(2019, 1, 1) // 爬 B 站动态时，截止的动态时间

private fun reachEndBili(driver: WebDriver): Boolean {
    return driver.findElements(By.cssSelector("a[class='detail-link tc-slate']"))
        .findLast { before(it.text, reachData) } != null
}

private fun before(text: String, date: LocalDate = LocalDate.now()): Boolean {
    return parseDate(text).isBefore(date)
}

private fun parseDate(text: String): LocalDate {
    val result = text.split("-")
    val date = if (result.size > 1) result.map { it.toInt() } else emptyList()
    return when (date.size) {
        2 -> LocalDate.of(LocalDate.now().year, date[0], date[1])
        3 -> LocalDate.of(date[0], date[1], date[2])
        else -> LocalDate.now()
    }
}

private val sqss = Pair("十千三岁", "https://space.bilibili.com/424263116/")  // sexy
private val qgqsdle = Pair("且攻且受的念儿", "https://space.bilibili.com/305276429/")
private val lzx = Pair("绫斩仙", "https://space.bilibili.com/499720112/")
private val ecyppj = Pair("二次元の泡泡酱", "https://space.bilibili.com/24715356/")
private val yyyy = Pair("ラプラス", "https://space.bilibili.com/4739847/")
private val csj = Pair("纯水酱", "https://space.bilibili.com/21686859/") // the size of image too big
private val idshwm = Pair("ID是坏文明", "https://space.bilibili.com/39457507/")
private val dthk = Pair("动态好康の搬运美图牙", "https://space.bilibili.com/306069337/")
private val wfzry = Pair("晚风知人意シ", "https://space.bilibili.com/386462683/")
private val gzwz = Pair("篝之雾枝Official", "https://space.bilibili.com/52600877/")
private val qldftj = Pair("勤劳の发图姬", "https://space.bilibili.com/431059983/")
private val fsmrhx = Pair("浮生梦若回响", "https://space.bilibili.com/544270456/")
private val fcrqz = Pair("枫赤然秋至", "https://space.bilibili.com/475348820/")
private val xmyzj = Pair("西木野真姬-Official", "https://space.bilibili.com/100690295/")
private val btzjdmtj = Pair("不太正经の喵太酱", "https://space.bilibili.com/113429394/")
private val uchihaItai = Pair("UchihaItai", "https://space.bilibili.com/399094478/")
private val srx = Pair("散人兄", "https://space.bilibili.com/35599050/")
private val xcyz = Pair("星尘YZ", "https://space.bilibili.com/489348703/")
private val wsllndhzx = Pair("污神丶零落泪滴化作雪", "https://space.bilibili.com/19504033/")
private val wygzj = Pair("五月古筝酱", "https://space.bilibili.com/317477087/")
private val lgmlldd = Pair("老干妈榴莲蛋挞", "https://space.bilibili.com/102740397/")
private val wdmzyc = Pair("我的名字亦宸", "https://space.bilibili.com/200347229/")
private val halmadswj = Pair("画埃罗芒阿的纱雾酱", "https://space.bilibili.com/424427706/")
private val wnsdhqp = Pair("无奈输的好奇葩", "https://space.bilibili.com/366151712/")
private val agytdm = Pair("阿狗与他的猫", "https://space.bilibili.com/12313178/") // todo
private val qmdzzmt = Pair("勤勉の転載美图", "https://space.bilibili.com/623607452/")
private val yyy = Pair("依缘y", "https://space.bilibili.com/16021397/")
private val lldmtby = Pair("林落的美图搬运", "https://space.bilibili.com/546240754/")
private val blg = Pair("板栗宫丶", "https://space.bilibili.com/375247/")

private const val DYNAMIC = "dynamic"
private const val ALBUM = "album"

fun main() {
    bySelect(wdmzyc, ALBUM)
}

private fun bySelect(selected: Pair<String, String>, path: String = ALBUM) {
    val url = selected.second
    val seeds = if (url.startsWith("http")) {
        listOf(Seed.newSeed("image", "$url$path", 0, mapOf("site_name" to "BiliBili", "dir" to selected.first)))
    } else {
        emptyList()
    }
    spider.startWithSeedJar(seeds)
}

private fun byInput() {
    print("Input Url: ")
    val scanner = Scanner(System.`in`)
    val url = scanner.nextLine()
    val seeds = if (url.toLowerCase().startsWith("http")) {
        listOf(Seed.newSeed("image", url, 0))
    } else {
        emptyList()
    }
    spider.startWithSeedJar(seeds)
}