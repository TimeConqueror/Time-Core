package ru.timeconqueror.timecore.api.util.json

import net.minecraft.util.ResourceLocation
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation

/*
* Code from CoffeeCore. Credits to Socolio and DreenDex
*/
open class JsonElement(val builder: StringBuilder) {

    private var first = true

    fun checkFirst() {
        if (first) {
            first = false
        } else {
            builder.append(",")
        }
    }
}

class JSONObject(builder: StringBuilder) : JsonElement(builder) {

    inner class Setter(val obj: JSONObject)

    val set = Setter(this)

    inline fun obj(name: String, block: JSONObject.() -> Unit) {
        checkFirst()
        builder.append("\"").append(name).append("\":{")
        block(JSONObject(builder))
        builder.append("}")
    }

    inline fun array(name: String, block: JSONArray.() -> Unit) {
        checkFirst()
        builder.append("\"").append(name).append("\":[")
        block(JSONArray(builder))
        builder.append("]")
    }

    infix fun String.set(value: String) {
        checkFirst()
        builder.append("\"").append(this).append("\":\"").append(value).append("\"")
    }

    infix fun String.set(value: ResourceLocation) {
        checkFirst()
        builder.append("\"").append(this).append("\":\"").append(value).append("\"")
    }

    infix fun String.set(value: BlockModelLocation) {
        set(value.toString())
    }

    infix fun String.set(value: Int) {
        checkFirst()
        builder.append("\"").append(this).append("\":").append(value)
    }

    infix fun String.set(value: Float) {
        checkFirst()
        builder.append("\"").append(this).append("\":").append(value)
    }

    inline operator fun String.unaryMinus() = obj(this) {}

    inline operator fun String.invoke(block: JSONObject.() -> Unit) {
        obj(this, block)
    }

    inline operator fun Int.invoke(block: JSONObject.() -> Unit) {
        obj(this.toString(), block)
    }

    inline operator fun String.get(vararg array: JSONObject.() -> Unit) {
        array(this) {
            array.forEach(::obj)
        }
    }

    inline operator fun String.get(emptyMarker: Unit) {
        array(this) {}
    }

    /**
     * Sets raw [rawJson] to provided value.
     * It should be a valid json value with all quotes: property value, object or array.
     * Raw object should be valid JSON String!
     *
     * Example for object:
     * ```
     * fun makeJson() {
     *  val rawJsonObject = "{"property":"value"}"
     *
     *   return json {
     *      "emissive" setRaw rawJsonObject
     *   }
     * }
     * ```
     * Results in:
     * ```
     *  {
     *      "emissive" : {
     *          "property":"value"
     *      }
     *  }
     * ```
     *
     * Example for property value:
     * ```
     * fun makeJson() {
     *  val rawPropVal = "\"value\""
     *
     *   return json {
     *      "emissive" setRaw rawPropVal
     *   }
     * }
     * ```
     * Results in:
     * ```
     *  {
     *      "emissive" : "value"
     *  }
     * ```
     */
    infix fun String.setRaw(rawJson: String) {
        checkFirst()
        builder.append("\"").append(this).append("\":").append(rawJson)
    }
}

class JSONArray(builder: StringBuilder) : JsonElement(builder) {

    fun obj(block: JSONObject.() -> Unit) {
        checkFirst()
        builder.append("{")
        block(JSONObject(builder))
        builder.append("}")
    }

    fun value(value: String) {
        checkFirst()
        builder.append("\"").append(value).append("\"")
    }

    fun value(vararg values: String) {
        values.forEach { value(it) }
    }

}

inline fun json(block: JSONObject.() -> Unit): String {
    val builder = StringBuilder()

    builder.append("{")
    block(JSONObject(builder))
    builder.append("}")

    return builder.toString()
}

infix fun JSONObject.Setter.model(value: String) = obj.apply { "model" set value }
infix fun JSONObject.Setter.model(value: ResourceLocation) = obj.apply { "model" set value }
infix fun JSONObject.Setter.model(value: BlockModelLocation) = obj.apply { "model" set value.toString() }
infix fun JSONObject.Setter.parent(value: ResourceLocation) = obj.apply { "parent" set value }

inline var JSONObject.x: Int
    get() = throw UnsupportedOperationException("You shouldn't try to get this")
    set(value) = "x" set value
inline var JSONObject.y: Int
    get() = throw UnsupportedOperationException("You shouldn't try to get this")
    set(value) = "y" set value
inline var JSONObject.z: Int
    get() = throw UnsupportedOperationException("You shouldn't try to get this")
    set(value) = "z" set value

val JSONObject.uvlock: Unit
    get() = "uvlock" set "true"