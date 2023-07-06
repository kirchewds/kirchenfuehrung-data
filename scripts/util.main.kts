@file:Repository("https://maven.frohnmeyer-wds.de/artifacts", options = [])
@file:DependsOn("io.gitlab.jfronny:commons-gson-dsl:1.3-SNAPSHOT")

import kotlin.system.exitProcess
import io.gitlab.jfronny.gson.Gson
import io.gitlab.jfronny.commons.serialize.gson.api.v1.GsonHolders
import io.gitlab.jfronny.commons.log.Logger

val gson: Gson = GsonHolders.API.modifyBuilder { it.serializeNulls() }.gson
val log: Logger = Logger.forName("Kirchenführer/Data")

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