@file:Repository("https://maven.frohnmeyer-wds.de/artifacts", options = [])
@file:DependsOn("io.gitlab.jfronny:commons-serialize-dsl:2.0.0-SNAPSHOT")
@file:DependsOn("io.gitlab.jfronny:commons-serialize-json:2.0.0-SNAPSHOT")
@file:DependsOn("io.gitlab.jfronny:commons-logger:2.0.0-SNAPSHOT")

import io.gitlab.jfronny.commons.logger.SystemLoggerPlus
import io.gitlab.jfronny.commons.serialize.Transport
import io.gitlab.jfronny.commons.serialize.json.JsonReader
import io.gitlab.jfronny.commons.serialize.json.JsonWriter
import java.io.IOException
import java.io.Reader
import java.io.Writer
import kotlin.system.exitProcess

object JTransport: Transport<IOException, JsonReader, JsonWriter> {
    override fun createReader(p0: Reader): JsonReader = JsonReader(p0).setLenient(true)
    override fun createWriter(p0: Writer): JsonWriter = JsonWriter(p0).setSerializeNulls(true)
    override fun getFormatMime(): String = "application/json"
}
val log: SystemLoggerPlus = SystemLoggerPlus.forName("Kirchenführer/Data")

fun fail(msg: String) {
    log.error(msg)
    exitProcess(1)
}

fun <T> fail(msg: String): T {
    fail(msg)
    throw IllegalStateException(msg)
}

fun info(msg: String) = log.info(msg)

fun encodePath(input: String): String {
    fun isUnsafe(ch: Char): Boolean {
        return if (ch.code > 128 || ch.code < 0) true else " %$&+,/:;=?@<>#%".indexOf(ch) >= 0
    }

    fun toHex(ch: Int): Char {
        return (if (ch < 10) '0'.code + ch else 'A'.code + ch - 10).toChar()
    }

    val resultStr = StringBuilder()
    for (ch in input.toCharArray()) {
        if (isUnsafe(ch)) {
            resultStr.append('%')
            resultStr.append(toHex(ch.code / 16))
            resultStr.append(toHex(ch.code % 16))
        } else {
            resultStr.append(ch)
        }
    }
    return resultStr.toString()
}