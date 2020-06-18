package cc.colorcat.kspider

import java.net.URI
import java.nio.charset.Charset

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
@Suppress("UNUSED")
data class WebSnapshot(
    val uri: URI,
    internal val content: ByteArray,
    private val charset: Charset = Charsets.UTF_8
) {
    private val resources = mutableMapOf<Charset, String>()

    fun contentToString(): String {
        return resources.computeIfAbsent(charset) { String(content, it) }
    }

    fun contentToString(charset: String): String = contentToString(Charset.forName(charset))

    fun contentToString(charset: Charset): String {
        return resources.computeIfAbsent(charset) { String(content, charset) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WebSnapshot

        if (uri != other.uri) return false
        if (!content.contentEquals(other.content)) return false
        if (charset != other.charset) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uri.hashCode()
        result = 31 * result + content.contentHashCode()
        result = 31 * result + charset.hashCode()
        return result
    }
}