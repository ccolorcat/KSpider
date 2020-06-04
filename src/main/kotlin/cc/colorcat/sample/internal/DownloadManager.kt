package cc.colorcat.sample.internal

import cc.colorcat.netbird.*
import java.io.File

import cc.colorcat.kspider.Log as Logger

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
object DownloadManager {
    private const val TAG = "Download"

    private val netBird by lazy {
        NetBird.Builder("https://www.google.com.hk")
            .connectTimeOut(30000)
            .readTimeOut(30000)
            .maxRunning(10)
            .logLevel(Level.NOTHING)
            .build()
    }
    var maxRetry: Int = 3
        set(value) {
            if (value > 0) {
                field = value
            }
        }
    private val tasks = mutableMapOf<String, Int>()
    private val emptyHeaders = Headers.ofWithIgnoreNull(emptyMap())

    fun dispatch(url: String, savePath: File, headers: Headers = emptyHeaders) {
        if (!tasks.containsKey(url)) {
            tasks[url] = 0
            download(url, savePath, headers)
        }
    }

    private fun download(url: String, savePath: File, headers: Headers) {
        val request = Request.Builder().url(url).addHeaders(headers).build()
        netBird.newCall(request).enqueue(FileParser.create(savePath), object : SimpleListener<File> {
            override fun onSuccess(result: File) {
                Logger.i(TAG, "success $url -> $result")
            }

            override fun onFailure(cause: StateIOException) {
                val count = tasks[url] as Int
                if (count < maxRetry) {
                    tasks[url] = count + 1
                    download(url, savePath, headers)
                } else {
                    Logger.e(TAG, "failed $url cause:$cause")
                }
            }
        })
    }
}
