package dextop

import net.sourceforge.argparse4j.ArgumentParsers
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.net.URL
import java.nio.charset.Charset

fun main(args: Array<String>) {
    val parser = ArgumentParsers.newFor("dextop")
        .build()
        .defaultHelp(true)
        .description("Generates desktop files for executables")
    parser.addArgument("-e", "--exec")
        .help("The executable")
        .dest("exec")
        .required(true)
    parser.addArgument("-i", "--icon")
        .help("The icon of the file")
        .dest("icon")
        .required(true)
    parser.addArgument("-n", "--name")
        .help("The name of the executable")
        .dest("name")
        .required(true)
    parser.addArgument("-c", "--category")
        .help("The category of the executable")
        .dest("category")
        .required(true)
    val namespace = parser.parseArgs(args)
    val desktopDir = File(SystemUtils.USER_HOME, ".local", "share", "applications")
    val execDir = File(SystemUtils.USER_HOME, ".dextop", "exec")
    val iconDir = File(SystemUtils.USER_HOME, ".dextop", "icons")
    createDirs(desktopDir, execDir, iconDir)
    val desktopTemplate = getTemplate()
    val execSrc = File(namespace.getString("exec"))
    val iconSrc = File(namespace.getString("icon"))
    if (!execSrc.exists()) throw IllegalArgumentException("Invalid Path ${execSrc.absolutePath} The file doesn't exist")
    if (!iconSrc.exists()) throw IllegalArgumentException("Invalid Path ${iconSrc.absolutePath} The file doesn't exist")
    val execDst = File(execDir, FilenameUtils.getName(namespace.getString("exec")))
    val iconDst = File(iconDir, FilenameUtils.getName(namespace.getString("icon")))
    println("Moving ${FilenameUtils.getName(namespace.getString("exec"))} from $execSrc to $execDir")
    println("Moving ${FilenameUtils.getName(namespace.getString("icon"))} from $iconSrc to $iconDst")
    FileUtils.moveFile(execSrc, execDst)
    FileUtils.moveFile(iconSrc, iconDst)
    println("Generating .desktop file")
    FileUtils.write(
        File(desktopDir, "${namespace.getString("name")}.desktop"), String.format(
            desktopTemplate,
            namespace.getString("name"),
            execDst.absolutePath,
            iconDst.absolutePath,
            namespace.getString("category")
        ), Charset.defaultCharset()
    )
    println("Done!")
}


fun getTemplate(): String {
    URL("https://gist.githubusercontent.com/NastyGamer/52e1f486a36f799927af88716a4c8e5e/raw/4df07143746c7645fca22c0d61c4bbf0b20fa06f/Parsable%2520Desktop%2520File")
        .openStream().use {
            return IOUtils.toString(it, Charset.defaultCharset())
        }

}

fun createDirs(desktopDir: File, execDir: File, iconDir: File) {
    if (!desktopDir.exists()) desktopDir.mkdirs()
    if (!execDir.exists()) execDir.mkdirs()
    if (!iconDir.exists()) iconDir.mkdirs()
}

fun File(vararg files: String): File {
    var p = ""
    files.forEach { s -> p = p.plus(s).plus(File.separator) }
    return File(p)
}