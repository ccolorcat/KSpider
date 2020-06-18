package cc.colorcat.kspider

import java.util.*

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
internal class Dispatcher(private val spider: KSpider) {
    private val reachedMaxDepth = LinkedList<Seed>()
    private val finished = LinkedList<Call>()
    private val waiting = LinkedList<Call>()
    private val running = LinkedList<Call>()

    @Synchronized
    internal fun enqueue(calls: List<Call>, depthFirst: Boolean) {
        val filters = calls.filter { call ->
            !existUri(call)
        }
        if (depthFirst) {
            waiting.addAll(0, filters)
        } else {
            waiting.addAll(filters)
        }
        promoteCalls()
    }

    private fun existUri(call: Call): Boolean {
        val uri = call.seed.uri
        return running.any { it.seed.uri == uri }
                || waiting.any { it.seed.uri == uri }
                || finished.any { it.seed.uri == uri }
    }

    private fun promoteCalls() {
        if (running.size >= spider.maxSeedOnRunning) return
        if (!waiting.isEmpty()) {
            val iterator = waiting.iterator()
            while (iterator.hasNext()) {
                val call = iterator.next()
                running.add(call)
                spider.executor.submit(call)
                iterator.remove()
                if (running.size >= spider.maxSeedOnRunning) return
            }
        } else if (running.isEmpty()) {
            onAllFinished()
        }
    }

    private fun onAllFinished() {
        val success = mutableListOf<Seed>()
        val failed = mutableListOf<Seed>()
        finished.forEach {
            if (it.retryCount > spider.maxRetry) {
                failed.add(it.seed)
            } else {
                success.add(it.seed)
            }
        }
        spider.seedJar.save(success, failed, reachedMaxDepth)
        spider.defaultConnection.onAllFinish()
        spider.connections.forEach { (_, conn) -> conn.onAllFinish() }
    }

    @Synchronized
    internal fun finished(call: Call, reason: Throwable?) {
        val seed = call.seed
        running.removeIf { seed.uri == it.seed.uri }
        if (reason == null) {
            // successful
            enqueueFinished(call)
            spider.eventListener.onSuccess(seed)
        } else {
            call.incrementRetryCount()
            if (call.retryCount <= spider.maxRetry) {
                enqueue(listOf(call), spider.depthFirst) // retry
            } else {
                // failed
                enqueueFinished(call)
                spider.eventListener.onFailure(seed, reason)
            }
        }
    }

    private fun enqueueFinished(call: Call) {
        finished.add(call)
        call as RealCall
        call.connection.onSeedFinish(call.seed)
        promoteCalls()
    }

    internal fun handled(scrap: Scrap) {
        spider.eventListener.onHandled(scrap)
    }

    internal fun onReachedMaxDepth(seed: Seed) {
        synchronized(reachedMaxDepth) {
            reachedMaxDepth.add(seed)
        }
        spider.eventListener.onReachedMaxDepth(seed)
    }
}