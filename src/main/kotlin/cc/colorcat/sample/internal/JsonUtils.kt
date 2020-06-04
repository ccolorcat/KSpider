package cc.colorcat.sample.internal

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * Author: cxx
 * Date: 2020-05-28
 * GitHub: https://github.com/ccolorcat
 */
val gson = GsonBuilder().registerTypeAdapterFactory(UriAdapterFactory()).create()


inline fun toJson(any: Any): String = gson.toJson(any)

inline fun <reified  T : Any> fromJson(json: String): T {
    return gson.fromJson(json, object: TypeToken<T>(){}.type)
//    return gson.fromJson(json, ParameterizedType)
}
