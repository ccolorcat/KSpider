package cc.colorcat.kspider

import java.io.Closeable
import java.io.IOException
import java.net.URI
import java.security.MessageDigest
import java.util.*

/**
 * Author: cxx
 * Date: 2020-05-28
 * GitHub: https://github.com/ccolorcat
 */
private const val IMAGE = "\\.(jpg|png|jpeg|bmp|gif|tif|psd|webp|pcx|tga|exif|fpx|svg|cdr|pcd|dxf|ufo|eps|ai|raw|wmf)"
const val IMAGE_URL_REGEX = "^(http)(s)?://(.)*$IMAGE$"
const val IMAGE_PATH_REGEX = "(.)*$IMAGE$"
val imageUrlRegex = IMAGE_URL_REGEX.toRegex()
val imagePathRegex = IMAGE_PATH_REGEX.toRegex()

private const val HTML = "\\.(htm|html)"
const val HTML_URL_REGEX = "^(http)(s)?://(.)*$HTML$"
const val HTML_PATH_REGEX = "(.)*$HTML$"
val htmlUrlRegex = HTML_URL_REGEX.toRegex()
val htmlPathRegex = HTML_PATH_REGEX.toRegex()

private val httpSchemeRegex = "(http)(s)?".toRegex()
private val httpRegex = "^(http)(s)?://(.)*".toRegex()

fun parseFileName(url: String?, defaultName: String = System.currentTimeMillis().toString()): String {
    if (url == null) return defaultName
    var index = url.lastIndexOf('/')
    if (index != -1) index++
    return if (index < url.length) url.substring(index) else defaultName
}

fun isImageUrl(url: String?): Boolean {
    return url?.toLowerCase()?.matches(imageUrlRegex) ?: false
}

fun isImagePath(path: String?): Boolean {
    return path?.toLowerCase()?.matches(imagePathRegex) ?: false
}

fun isHtmOrHtmlUrl(url: String?): Boolean {
    return url?.toLowerCase()?.matches(htmlUrlRegex) ?: false
}

fun isHtmOrHtmlPath(path: String?): Boolean {
    return path?.toLowerCase()?.matches(htmlPathRegex) ?: false
}

fun isHttpUri(uri: URI): Boolean {
    return uri.scheme?.toLowerCase()?.matches(httpSchemeRegex) ?: false
}

fun isHttpUrl(url: String?): Boolean {
    return url?.toLowerCase()?.matches(httpRegex) ?: false
}

fun <T> linkedListOf(): MutableList<T> = LinkedList()

fun close(closeable: Closeable?) {
    if (closeable != null) {
        try {
            closeable.close()
        } catch (ignore: IOException) {
        }
    }
}

private val HEX_CHARS = "0123456789abcdef".toCharArray()

fun md5(input: String): String {
    return MessageDigest.getInstance("MD5").digest(input.toByteArray()).let { hexBinary(it) }
}

private fun hexBinary(data: ByteArray): String {
    val builder = StringBuilder(data.size shl 1)
    data.forEach {
        val i = it.toInt()
        builder.append(HEX_CHARS[i shr 4 and 0xF]).append(HEX_CHARS[i and 0xF])
    }
    return builder.toString()
}