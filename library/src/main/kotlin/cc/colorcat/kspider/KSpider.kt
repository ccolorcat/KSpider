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
    internal val connections: Map<String, Connection> = builder.connections
    internal val defaultConnection: Connection = builder.defaultConnection
    val executor: ExecutorService = builder.executor
    val depthFirst: Boolean = builder.depthFirst
    val maxRetry: Int = builder.maxRetry
    val maxSeedOnRunning: Int = builder.maxSeedOnRunning
    val maxDepth: Int = builder.maxDepth
    val eventListener: EventListener = builder.eventListener
    val seedJar: SeedJar = builder.seedJar
    val webJar: WebJar = builder.webJar
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

    internal fun newConnection(host: String): Connection {
        return connections.getOrDefault(host, defaultConnection).clone()
    }

    override fun newCall(seed: Seed): Call = RealCall(seed, this)

    fun newBuilder(): Builder = Builder(this)


    class Builder {
        private val _parsers: MutableMap<String, MutableList<Parser>>
        private val _handlers: MutableMap<String, MutableList<Handler>>
        private val _interceptors: MutableList<Interceptor>
        private val _connections: MutableMap<String, Connection>
        val parsers: Map<String, List<Parser>>
            get() = _parsers.toImmutableMap()
        val handlers: Map<String, List<Handler>>
            get() = _handlers.toImmutableMap()
        val interceptors: List<Interceptor>
            get() = _interceptors.toImmutableList()
        val connections: Map<String, Connection>
            get() = _connections.toMap()
        var defaultConnection: Connection
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
        var webJar: WebJar
            private set

        constructor() {
            _parsers = mutableMapOf()
            _handlers = mutableMapOf()
            _interceptors = mutableListOf()
            _connections = mutableMapOf()
            defaultConnection = HttpConnection()
            executor = defaultService()
            depthFirst = false
            maxRetry = 3
            maxSeedOnRunning = 20
            maxDepth = 100
            eventListener = emptyEventListener
            seedJar = emptySeedJar
            webJar = emptyWebJar
        }

        internal constructor(spider: KSpider) {
            _parsers = spider.parsers.toMutableMap()
            _handlers = spider.handlers.toMutableMap()
            _interceptors = spider.interceptors.toMutableList()
            _connections = spider.connections.toMutableMap()
            defaultConnection = spider.defaultConnection
            executor = spider.executor
            depthFirst = spider.depthFirst
            maxRetry = spider.maxRetry
            maxSeedOnRunning = spider.maxSeedOnRunning
            maxDepth = spider.maxDepth
            eventListener = spider.eventListener
            seedJar = spider.seedJar
            webJar = spider.webJar
        }

        fun registerParser(tag: String, parser: Parser): Builder {
            _parsers.computeIfAbsent(tag) { mutableListOf() }.addIfAbsent(parser)
            return this
        }

        fun unregisterParser(tag: String, parser: Parser): Builder {
            _parsers[tag]?.remove(parser)
            return this
        }

        fun registerHandler(tag: String, handler: Handler): Builder {
            _handlers.computeIfAbsent(tag) { mutableListOf() }.addIfAbsent(handler)
            return this
        }

        fun unregisterHandler(tag: String, handler: Handler): Builder {
            _handlers[tag]?.remove(handler)
            return this
        }

        fun registerConnection(host: String, connection: Connection): Builder {
            _connections[host] = connection
            return this
        }

        fun registerConnection(hosts: Array<String>, connection: Connection): Builder {
            hosts.forEach { _connections[it] = connection }
            return this
        }

        fun unregisterConnection(host: String): Builder {
            _connections.remove(host)
            return this
        }

        fun defaultConnection(connection: Connection): Builder {
            defaultConnection = connection
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

        fun webJar(webJar: WebJar): Builder {
            this.webJar = webJar
            return this
        }

        fun build(): KSpider = KSpider(this)
    }
}