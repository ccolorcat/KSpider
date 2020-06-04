package cc.colorcat.kspider

import java.net.URI

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
class Scrap internal constructor(tag: String, uri: URI, depth: Int, data: Map<String, String>) : Seed(tag, uri, depth, data) {
    override fun remove(key: String): Scrap {
        super.remove(key)
        return this
    }

    override fun fill(key: String, value: String): Scrap {
        super.fill(key, value)
        return this
    }

    override fun fill(data: Map<String, String>): Scrap {
        super.fill(data)
        return this
    }

    override fun fillIfAbsent(key: String, value: String): Scrap {
        super.fillIfAbsent(key, value)
        return this
    }

    override fun fillIfAbsent(data: Map<String, String>): Scrap {
        super.fillIfAbsent(data)
        return this
    }
}