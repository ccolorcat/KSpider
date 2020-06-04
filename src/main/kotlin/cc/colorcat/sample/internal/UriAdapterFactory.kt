package cc.colorcat.sample.internal

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.net.URI

/**
 * Author: cxx
 * Date: 2020-05-28
 * GitHub: https://github.com/ccolorcat
 */
class UriAdapterFactory : TypeAdapterFactory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        return if (!URI::class.java.isAssignableFrom(type.rawType)) {
            null
        } else {
            (UriAdapter() as TypeAdapter<T>)
        }
    }
}

private class UriAdapter : TypeAdapter<URI>() {
    override fun write(out: JsonWriter, value: URI) {
        out.value(value.toString())
    }

    override fun read(`in`: JsonReader): URI {
        return URI.create(`in`.nextString())
    }

}