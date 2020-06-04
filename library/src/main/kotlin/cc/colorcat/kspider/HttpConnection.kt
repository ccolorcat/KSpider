package cc.colorcat.kspider

import cc.colorcat.kspider.internal.parseCharset
import cc.colorcat.kspider.internal.readByteArray
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
open class HttpConnection : Connection {
    private var snapshot: WebSnapshot? = null

    override fun get(seed: Seed): WebSnapshot? {
        val uri = seed.uri
        if (!isHttpUri(uri)) throw UnsupportedOperationException("Unsupported uri, uri = $seed")
        if (snapshot != null && snapshot?.uri == uri) {
            return snapshot
        }
        snapshot = doGet(seed)
        return snapshot
    }


    override fun clone(): Connection = HttpConnection()

    override fun onSeedFinish(seed: Seed) {
        snapshot = null
    }

    override fun onAllFinish() {
    }

    protected companion object {
        @Throws(IOException::class)
        fun doGet(seed: Seed): WebSnapshot? {
            val conn = createConnection(seed)
            var input: InputStream? = null
            try {
                if (HttpURLConnection.HTTP_OK == conn.responseCode) {
                    input = conn.inputStream
                    if (input != null) {
                        val charset = parseCharset(conn.contentType)
                        val content = input.readByteArray()
                        return WebSnapshot(seed.uri, content, charset ?: Charsets.UTF_8)
                    }
                }
            } finally {
                conn.disconnect()
                close(input)
            }
            return null
        }

        @Throws(IOException::class)
        private fun createConnection(seed: Seed, method: String = "GET"): HttpURLConnection {
            val conn = seed.uri.toURL().openConnection() as HttpURLConnection
            conn.requestMethod = method
            conn.connectTimeout = 10000
            conn.readTimeout = 10000
            val data = seed.data
            data["Host"]?.also { conn.addRequestProperty("Host", it) }
            data["User-Agent"]?.also { conn.addRequestProperty("User-Agent", it) }
            data["Cookie"]?.also { conn.addRequestProperty("Cookie", it) }
            data["Referer"]?.also { conn.addRequestProperty("Referer", it) }
            return conn
        }
    }
}
