#!/usr/bin/env kotlin
@file:Import("util.main.kts")
@file:Import("generator.main.kts")

import java.nio.file.Path
import kotlin.io.path.*

info("Parsing arguments")

var highlight: String? = null
var targetDir: Path? = null
var targetUrl: String? = null

if (args.size % 2 != 0) fail("Argument lacks value")
var i = 0
while (i < args.size) when (args[i++]) {
    "--highlight" -> highlight = args[i++]
    "--targetDir" -> targetDir = Path(args[i++]).absolute()
    "--targetUrl" -> targetUrl = args[i++]
    else -> fail("Unrecognized argument: ${args[i - 1]}")
}

if (highlight == null) fail("Highlight is not set")
if (targetDir == null) fail("Highlight is not set")
if (targetUrl == null) fail("Highlight is not set")
if (!targetUrl!!.endsWith("/")) targetUrl += "/"

generate(Path("."), highlight!!, targetDir!!, targetUrl!!)