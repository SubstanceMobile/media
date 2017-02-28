package mobile.substance.sdk.music.playback.cast

import java.util.*

class CaseInsensitiveMap<V : Any> : HashMap<String, V>() {

    override fun put(key: String, value: V): V? {
        return super.put(key.toLowerCase(), value)
    }

    override fun get(key: String): V? {
        return super.get(key.toLowerCase())
    }

}