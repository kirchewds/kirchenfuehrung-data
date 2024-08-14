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

private val Int.hexChar get() = (if (this < 10) '0'.code + this else 'A'.code + this - 10).toChar()
private val Char.hex get() = (code / 16).hexChar.toString() + (code % 16).hexChar
private val legalChars = listOf('a'..'z', 'A'..'Z', '0'..'9', setOf('.')).flatten()
val String.pathEscaped get() = let { str -> buildString {
    for (ch in str) {
        if (ch in legalChars) append(ch)
        else append(ch.hex)
    }
} }
