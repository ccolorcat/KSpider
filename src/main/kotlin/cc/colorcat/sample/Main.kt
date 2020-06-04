package cc.colorcat.sample

import cc.colorcat.kspider.*
import cc.colorcat.kspider.EventListener
import cc.colorcat.sample.internal.*
import java.io.File
import java.nio.file.Paths
import java.util.*

/**
 * Author: cxx
 * Date: 2020-05-28
 * GitHub: https://github.com/ccolorcat
 */
private const val TAG = "Event"
private const val DRIVER_PATH = "/Users/cxx/Workspace/library/chromedriver"

private val saveDir = Paths.get(System.getProperty("user.home"), "spider").toString()
private val spider = KSpider.Builder()
    .connection(WebDriverConnection(DRIVER_PATH))
    .seedJar(FileSeedJar(File(saveDir, "seed.txt")))
    .registerParser("bing", BingParser())
    .registerHandler("bing", BingHandler(saveDir))
    .registerParser("image", Win4000Parser())
    .registerParser("image", HDWallpaperParser())
    .registerParser("image", TpzjParser())
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


fun main() {
    print("Input Url: ")
    val scanner = Scanner(System.`in`)
    val url = scanner.nextLine()
//    spider.start("image", url, mapOf("name" to "tupianzj"))
    val seeds = if (isHtmOrHtmlUrl(url)) {
        listOf(Seed.newSeed("image", url, 0, mapOf("name" to "tupianzj")))
    } else {
        emptyList()
    }
    spider.startWithSeedJar(seeds)
}