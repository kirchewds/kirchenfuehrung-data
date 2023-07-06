#!/usr/bin/env kotlin
@file:Import("util.main.kts")
@file:Import("generator.main.kts")
@file:OptIn(kotlin.io.path.ExperimentalPathApi::class)

import com.sun.net.httpserver.SimpleFileServer
import java.net.InetSocketAddress
import kotlin.io.path.*

info("Parsing arguments")

var highlight: String? = null

if (args.size % 2 != 0) fail("Argument lacks value")
var i = 0
while (i < args.size) when (args[i++]) {
    "--highlight" -> highlight = args[i++]
    else -> fail("Unrecognized argument: ${args[i - 1]}")
}

if (highlight == null) fail("Highlight is not set")

val dir = createTempDirectory()
generate(Path("."), highlight!!, dir, "http://127.0.0.1:8080/")
Runtime.getRuntime().addShutdownHook(Thread {
    dir.deleteRecursively()
})

info("Launching server")

SimpleFileServer.createFileServer(InetSocketAddress(8080), dir, SimpleFileServer.OutputLevel.INFO).start()