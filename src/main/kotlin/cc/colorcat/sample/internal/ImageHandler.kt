package cc.colorcat.sample.internal

import cc.colorcat.kspider.*
import cc.colorcat.netbird.Headers
import java.net.URI
import java.nio.file.Paths

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
class ImageHandler(private val saveDir: String) : Handler {
    private val TAG = "ImageHandler"

    override fun handle(scrap: Scrap): Boolean {
        val url = scrap.data["url"]
        if (!isImageUrl(url)) return false

        val siteName = scrap.data["site_name"]
        val dir = scrap.data["dir"] ?: "other"
        val fileName = parseFileName(url)
        val subPath = if (siteName == null) arrayOf(dir, fileName) else arrayOf(siteName, dir, fileName)

        val savePath = Paths.get(saveDir, *subPath).toFile()
//        if (savePath.exists()) {
//            Log.w(TAG, "$savePath exists, url=$url")
//            return true
//        }
        val host = scrap.data["Host"] ?: URI.create(url!!).host
        val referer = scrap.data["Referer"] ?: scrap.uri.toString()
        val headers = Headers.ofWithIgnoreNull(
            listOf("Host", "Referer", UserAgent.NAME),
            listOf(host, referer, UserAgent.CHROME_MAC)
        )
        DownloadManager.dispatch(url!!, savePath, headers)
        return true
    }
}
