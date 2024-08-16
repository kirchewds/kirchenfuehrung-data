@file:Import("util.main.kts")
@file:OptIn(kotlin.io.path.ExperimentalPathApi::class)

import io.gitlab.jfronny.commons.serialize.dsl.*
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import kotlin.io.path.*

fun generate(sourceDir: Path, highlight: String, targetDir: Path, targetUrl: String) {
    info("Reading tours dir")

    val ignored = arrayOf("scripts", ".idea", ".git", ".gitignore", ".gitattributes", ".gitlab-ci.yml", "public")

    val tourDirs = sourceDir.absolute()
        .listDirectoryEntries()
        .filterNot { it.fileName.toString() in ignored }

    if (!tourDirs.any { it.fileName.toString() == highlight }) fail("Highlight set but doesn't exist")

    info("Setting up target dir")
    if (targetDir.exists()) targetDir.deleteRecursively()
    targetDir.createDirectories()

    info("Processing tours")
    val jsonPath: Path = targetDir.resolve("tours.json")

    val trackPattern = Regex("([0-9]+) .+\\.(?:mp3|jpg)", RegexOption.DOT_MATCHES_ALL)

    val coverName = "cover.jpg"
    val descriptionName = "description.txt"

    jsonPath.jObject(JTransport) {
        "version"(1)
        "highlight"(highlight)
        jArray("tours") { tourDirs.forEach { tourDir ->
            info("Processing tour: $tourDir")
            val targetTourDir = targetDir.resolve(tourDir.name.pathEscaped).createDirectory()
            jObject {
                "name"(tourDir.name)
                if (tourDir.resolve(coverName).exists()) {
                    "cover"("$targetUrl${tourDir.name.pathEscaped}/$coverName")
                    tourDir.resolve(coverName).copyTo(targetTourDir.resolve(coverName))
                } else jNull("cover")
                if (tourDir.resolve(descriptionName).exists()) {
                    "description"(tourDir.resolve(descriptionName).readText(StandardCharsets.UTF_8))
                } else "description"("A tour of ${tourDir.name}")
                jArray("tracks") {
                    tourDir.listDirectoryEntries()
                        .filter { it.name != coverName && it.name != descriptionName }
                        .groupBy { it.nameWithoutExtension }
                        .toList()
                        .sortedBy {
                            it.second.map {
                                trackPattern.matchEntire(it.name) ?: fail<MatchResult>("Track name does not match pattern: ${it.name}")
                            }[0].groupValues[1].toInt()
                        }
                        .forEach {
                            info("Processing track: ${it.first}")
                            if (it.second.size != 2) fail("Unexpected number of components in pair: ${it.second.size} for ${it.first}")
                            it.second.forEach { it.copyTo(targetTourDir.resolve(it.name.pathEscaped)) }
                            fun byExtension(ext: String) =
                                "$targetUrl${tourDir.name.pathEscaped}/${it.second.first { it.extension == ext }.name.pathEscaped}"
                            jObject {
                                "name"(it.first)
                                "image"(byExtension("jpg"))
                                "audio"(byExtension("mp3"))
                            }
                        }
                }
            }
        } }
    }
    info("Created metadata at $jsonPath, view online at ${targetUrl}tours.json")
}