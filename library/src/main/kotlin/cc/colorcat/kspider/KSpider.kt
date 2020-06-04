package cc.colorcat.kspider

import cc.colorcat.kspider.internal.*
import java.util.concurrent.ExecutorService

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
class KSpider private constructor(builder: Builder) : Call.Factory {
    val parsers: Map<String, List<Parser>> = builder.parsers
    internal val parser: Parser = ParserProxy(parsers)
    val handlers: Map<String, List<Handler>> = builder.handlers
    val interceptors: List<Interceptor> = builder.interceptors
    internal val _connection: Connection = builder.connection
    val connection: Connection
        get() = _connection.clone()
    val executor: ExecutorService = builder.executor
    val depthFirst: Boolean = builder.depthFirst
    val maxRetry: Int = builder.maxRetry
    val maxSeedOnRunning: Int = builder.maxSeedOnRunning
    val maxDepth: Int = builder.maxDepth
    val eventListener: EventListener = builder.eventListener
    val seedJar: SeedJar = builder.seedJar
    internal val dispatcher: Dispatcher = Dispatcher(this)

    fun start(tag: String, uri: String) {
        mapAndEnqueue(listOf(Seed.newSeed(tag, uri)))
    }

    fun start(tag: String, uri: String, defaultData: Map<String, String> = emptyMap()) {
        start(tag, listOf(uri), defaultData)
    }

    fun start(tag: String, uris: List<String>, defaultData: Map<String, String> = emptyMap()) {
        val seeds = Seed.newSeeds(tag, uris, 0, defaultData)
        mapAndEnqueue(seeds)
    }

    fun start(tagAndUris: Map<String, List<String>>, defaultData: Map<String, String> = emptyMap()) {
        val seeds = mutableListOf<Seed>()
        tagAndUris.forEach { (tag, uris) -> seeds.addAll(Seed.newSeeds(tag, uris, 0, defaultData)) }
        mapAndEnqueue(seeds)
    }

    fun start(seeds: List<Seed>) {
        mapAndEnqueue(seeds)
    }

    fun startWithSeedJar(seeds: List<Seed> = emptyList()) {
        val all = seeds + seedJar.load()
        mapAndEnqueue(all)
    }

    internal fun mapAndEnqueue(seeds: List<Seed>) {
        val calls = seeds.map { newCall(it) }
        dispatcher.enqueue(calls, depthFirst)
    }

    override fun newCall(seed: Seed): Call = RealCall(seed, this)

    fun newBuilder(): Builder = Builder(this)


    class Builder {
        private val _parsers: MutableMap<String, MutableList<Parser>>
        private val _handlers: MutableMap<String, MutableList<Handler>>
        private val _interceptors: MutableList<Interceptor>
        val parsers: Map<String, List<Parser>>
            get() = _parsers.toImmutableMap()
        val handlers: Map<String, List<Handler>>
            get() = _handlers.toImmutableMap()
        val interceptors: List<Interceptor>
            get() = _interceptors.toImmutableList()
        var connection: Connection
            private set
        var executor: ExecutorService
            private set
        var depthFirst: Boolean
            private set
        var maxRetry: Int
            private set
        var maxSeedOnRunning: Int
            private set
        var maxDepth: Int
            private set
        var eventListener: EventListener
            private set
        var seedJar: SeedJar
            private set

        constructor() {
            _parsers = mutableMapOf()
            _handlers = mutableMapOf()
            _interceptors = mutableListOf()
            connection = HttpConnection()
            executor = defaultService()
            depthFirst = false
            maxRetry = 3
            maxSeedOnRunning = 20
            maxDepth = 100
            eventListener = emptyEventListener
            seedJar = emptySeedJar
        }

        internal constructor(spider: KSpider) {
            _parsers = spider.parsers.toMutableMap()
            _handlers = spider.handlers.toMutableMap()
            _interceptors = spider.interceptors.toMutableList()
            connection = spider._connection
            executor = spider.executor
            depthFirst = spider.depthFirst
            maxRetry = spider.maxRetry
            maxSeedOnRunning = spider.maxSeedOnRunning
            maxDepth = spider.maxDepth
            eventListener = spider.eventListener
            seedJar = spider.seedJar
        }

        fun registerParser(tag: String, parser: Parser): Builder {
            _parsers.computeIfAbsent(tag) { mutableListOf() }.addIfAbsent(parser)
            return this
        }

        fun unregisterParser(tag: String, parser: Parser): Builder {
            this._parsers[tag]?.remove(parser)
            return this
        }

        fun registerHandler(tag: String, handler: Handler): Builder {
            _handlers.computeIfAbsent(tag) { mutableListOf() }.addIfAbsent(handler)
            return this
        }

        fun unregisterHandler(tag: String, handler: Handler): Builder {
            this._handlers[tag]?.remove(handler)
            return this
        }

        fun addInterceptor(interceptor: Interceptor): Builder {
            this._interceptors.addIfAbsent(interceptor)
            return this
        }

        fun removeInterceptor(interceptor: Interceptor): Builder {
            this._interceptors.remove(interceptor)
            return this
        }

        fun connection(connection: Connection): Builder {
            this.connection = connection
            return this
        }

        fun executor(executor: ExecutorService): Builder {
            this.executor = executor
            return this
        }

        fun depthFirst(depthFirst: Boolean): Builder {
            this.depthFirst = depthFirst
            return this
        }

        fun maxRetry(maxRetry: Int): Builder {
            if (maxRetry < 0) throw IllegalArgumentException("maxRetry($maxRetry) < 0")
            this.maxRetry = maxRetry
            return this
        }

        fun maxSeedOnRunning(maxSeedOnRunning: Int): Builder {
            if (maxSeedOnRunning < 1) throw IllegalArgumentException("maxSeedOnRunning($maxSeedOnRunning) < 1")
            this.maxSeedOnRunning = maxSeedOnRunning
            return this
        }

        fun maxDepth(maxDepth: Int): Builder {
            if (maxDepth < 1) throw IllegalArgumentException("maxDepth($maxDepth) < 1")
            this.maxDepth = maxDepth
            return this
        }

        fun eventListener(listener: EventListener): Builder {
            this.eventListener = listener
            return this
        }

        fun seedJar(seedJar: SeedJar): Builder {
            this.seedJar = seedJar
            return this
        }

        fun build(): KSpider = KSpider(this)
    }
}