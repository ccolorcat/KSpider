package cc.colorcat.kspider

import java.util.logging.*

/**
 * Author: cxx
 * Date:  2018-12-11
 * Github: https://github.com/ccolorcat
 */
object Log {
    private val logger = Logger.getLogger(KSpider::class.simpleName)

    init {
        logger.useParentHandlers = false
        val formatter = object : Formatter() {
            @Synchronized
            override fun format(record: LogRecord): String {
                return "${record.level.name} ${record.message}\n"
            }
        }
        val level = Level.ALL
        val handler = ConsoleHandler()
        handler.formatter = formatter
        handler.level = level
        logger.addHandler(handler)
        logger.level = level
    }

    fun v(tag: String, msg: String) {
        printLog(tag, msg, Level.FINE)
    }

    fun d(tag: String, msg: String) {
        printLog(tag, msg, Level.CONFIG)
    }

    fun i(tag: String, msg: String) {
        printLog(tag, msg, Level.INFO)
    }

    fun w(tag: String, msg: String) {
        printLog(tag, msg, Level.WARNING)
    }

    fun e(tag: String, msg: String) {
        printLog(tag, msg, Level.SEVERE)
    }

    fun e(cause: Throwable) {
        cause.printStackTrace()
    }

    private fun printLog(tag: String, msg: String, level: Level) {
        logger.log(level, "$tag: $msg")
    }
}