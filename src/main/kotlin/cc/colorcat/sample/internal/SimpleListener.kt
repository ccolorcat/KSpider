package cc.colorcat.sample.internal

import cc.colorcat.netbird.Listener
import cc.colorcat.netbird.StateIOException

/**
 * Author: cxx
 * Date: 2020-05-28
 * GitHub: https://github.com/ccolorcat
 */
interface SimpleListener<T> : Listener<T> {
    override fun onSuccess(result: T) {
    }

    override fun onFinish() {
    }

    override fun onFailure(cause: StateIOException) {
    }

    override fun onStart() {
    }
}