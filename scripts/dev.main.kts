#!/usr/bin/env kotlin
@file:Import("util.main.kts")
@file:Import("generator.main.kts")
@file:OptIn(kotlin.io.path.ExperimentalPathApi::class)

import com.sun.net.httpserver.SimpleFileServer
import java.net.InetSocketAddress
import kotlin.io.path.*

info("Parsing arguments")

var highlight: String? = null
var targetUrl: String? = null

var i = 0
while (i < args.size) when (args[i++]) {
    "--highlight" -> highlight = args[i++]
    "--targetUrl" -> targetUrl = args[i++]
    else -> fail("Unrecognized argument: ${args[i - 1]}")
}

if (highlight == null) fail("Highlight is not set")
if (targetUrl == null) fail("Highlight is not set")
if (!targetUrl!!.endsWith("/")) targetUrl += "/"

val dir = createTempDirectory()
JTransport.pretty()
generate(Path("."), highlight!!, dir, targetUrl!!)
Runtime.getRuntime().addShutdownHook(Thread {
    dir.deleteRecursively()
})

info("Launching server at http://127.0.0.1:8080")

SimpleFileServer.createFileServer(InetSocketAddress(8080), dir, SimpleFileServer.OutputLevel.INFO).start()