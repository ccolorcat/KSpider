package cc.colorcat.kspider.internal

import cc.colorcat.kspider.*
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.util.Collections.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
internal fun <T> List<T>.toImmutableList(): List<T> = unmodifiableList(ArrayList(this))

internal fun <T> MutableList<T>.addIfAbsent(t: T): Boolean {
    if (!this.contains(t)) {
        return this.add(t)
    }
    return false
}

internal fun <T> mutableListWith(initCapacity: Int): MutableList<T> = ArrayList(initCapacity)

internal fun <K, T> Map<K, List<T>>.toImmutableMap(): Map<K, List<T>> {
    val result = mutableMapOf<K, List<T>>()
    this.forEach { result[it.key] = it.value.toImmutableList() }
    return unmodifiableMap(result)
}

internal fun <K, T> Map<K, List<T>>.toMutableMap(): MutableMap<K, MutableList<T>> {
    val result = mutableMapOf<K, MutableList<T>>()
    this.forEach { result[it.key] = it.value.toMutableList() }
    return result
}

internal fun defaultService(): ExecutorService {
    val executor = ThreadPoolExecutor(
        8,
        10,
        60L,
        TimeUnit.SECONDS,
        LinkedBlockingDeque(),
        ThreadPoolExecutor.DiscardOldestPolicy()
    )
    executor.allowCoreThreadTimeOut(true)
    return executor
}

internal val emptyEventListener: EventListener by lazy {
    object : EventListener {
        override fun onSuccess(seed: Seed) {
        }

        override fun onFailure(seed: Seed, reason: Throwable) {
        }

        override fun onReachedMaxDepth(seed: Seed) {
        }

        override fun onHandled(scrap: Scrap) {
        }
    }
}

internal val emptySeedJar: SeedJar by lazy {
    object : SeedJar {
        override fun save(success: List<Seed>, failed: List<Seed>, reachedMaxDepth: List<Seed>) {}

        override fun load(): List<Seed> = emptyList()
    }
}

internal val emptyWebJar: WebJar by lazy {
    object : WebJar {
        override fun save(seed: Seed, snapshot: WebSnapshot) {
        }

        override fun load(seed: Seed): WebSnapshot? = null
    }
}

internal fun parseCharset(contentType: String?): Charset? {
    return contentType
        ?.split(";")
        ?.map { it.trim().split("=") }
        ?.filter { it.size == 2 && "charset".equals(it[0], true) }
        ?.let { if (it.isEmpty()) null else Charset.forName(it[0][1]) }
}

internal fun InputStream.readByteArray(): ByteArray {
    val output = ByteArrayOutputStream()
    val buffer = ByteArray(4096)
    var length = this.read(buffer)
    while (length != -1) {
        output.write(buffer, 0, length)
        length = this.read(buffer)
    }
    output.flush()
    return output.toByteArray()
}
