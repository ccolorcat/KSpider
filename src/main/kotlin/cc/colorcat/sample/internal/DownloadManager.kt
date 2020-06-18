package cc.colorcat.sample.internal

import cc.colorcat.netbird.*
import java.io.File
import java.nio.file.Files
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

import cc.colorcat.kspider.Log as Logger

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
object DownloadManager {
    private const val TAG = "Download"

    private val netBird by lazy {
        NetBird.Builder("https://www.google.com")
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
    private val taskRetryCount = ConcurrentHashMap<String, Int>()
    private val emptyHeaders = Headers.ofWithIgnoreNull(emptyMap())

    var maxLoad = 1000
    private val successCount = AtomicInteger(0)
    private val failureCount = AtomicInteger(0)

    fun dispatch(url: String, savePath: File, headers: Headers = emptyHeaders): Boolean {
        if (savePath.exists()) {
            Logger.w(TAG, "$savePath exists, url=$url")
            return false
        }
        if ((taskRetryCount.size < maxLoad || maxLoad < 0) && !taskRetryCount.containsKey(url)) {
            taskRetryCount[url] = 0
            download(url, savePath, headers)
            return true
        }
        return false
    }

    private fun download(url: String, savePath: File, headers: Headers) {
        val request = Request.Builder().url(url).addHeaders(headers).build()
        netBird.newCall(request).enqueue(FileParser.create(savePath), object : SimpleListener<File> {
            override fun onSuccess(result: File) {
                Logger.i(TAG, "success(${formatStatsInfo(true)}) $url -> $result ${formatFileSize(result)}")
            }

            override fun onFailure(cause: StateIOException) {
                val count = taskRetryCount[url]!!
                if (count < maxRetry) {
                    taskRetryCount[url] = count + 1
                    download(url, savePath, headers)
                } else {
                    Files.deleteIfExists(savePath.toPath())
                    Logger.e(TAG, "failed(${formatStatsInfo(false)}) $url ${formatErrorInfo(cause)}")
                }
            }
        })
    }

    private fun formatStatsInfo(success: Boolean): String {
        val sc: Int
        val fc: Int
        if (success) {
            sc = successCount.incrementAndGet()
            fc = failureCount.get()
        } else {
            sc = successCount.get()
            fc = failureCount.incrementAndGet()
        }
        return "$sc/$fc/${taskRetryCount.size}"
    }

    private const val K = 1024
    private const val M = 1024 * 1024

    fun formatFileSize(file: File): String {
        val size = file.length()
        if (size > M) return String.format("%.2fMB", size.toDouble() / M)
        if (size > K) return String.format("%dKB", size / K)
        return "${size}Byte"
    }

    private fun formatErrorInfo(cause: StateIOException): String {
        return "state=${cause.state()}, message=${cause.message}, cause=${cause.cause}"
    }
}
