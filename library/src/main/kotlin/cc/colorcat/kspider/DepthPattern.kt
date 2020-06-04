package cc.colorcat.kspider

/**
 * Author: cxx
 * Date: 2020-05-27
 * GitHub: https://github.com/ccolorcat
 */
enum class DepthPattern {
    RESET {
        override fun depth(oldDepth: Int): Int = 0
    },
    PARALLEL {
        override fun depth(oldDepth: Int): Int = oldDepth
    },
    RAISE {
        override fun depth(oldDepth: Int): Int = oldDepth + 1
    };

    abstract fun depth(oldDepth: Int): Int
}