package com.github.winteryoung.yanwte.internals.utils

import java.io.File
import java.net.JarURLConnection
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.util.*
import java.util.jar.JarFile

internal object ReflectionUtils {
    /**
     * URL protocol for an entry from a jar file: "jar"
     */
    private val URL_PROTOCOL_JAR = "jar"

    /**
     * URL protocol for an entry from a zip file: "zip"
     */
    private val URL_PROTOCOL_ZIP = "zip"

    /**
     * URL protocol for an entry from a WebSphere jar file: "wsjar"
     */
    private val URL_PROTOCOL_WSJAR = "wsjar"

    /**
     * URL protocol for an entry from a JBoss jar file: "vfszip"
     */
    private val URL_PROTOCOL_VFSZIP = "vfszip"

    /**
     * URL protocol for an entry from an OC4J jar file: "code-source"
     */
    private val URL_PROTOCOL_CODE_SOURCE = "code-source"

    /**
     * Separator between JAR URL and file path within the JAR
     */
    private val JAR_URL_SEPARATOR = "!/"

    /**
     * URL prefix for loading from the file system: "file:"
     */
    private val FILE_URL_PREFIX = "file:"

    private val FOLDER_SEPARATOR = "/"

    private fun getClassPathResources(dirPath: String, cl: ClassLoader): Array<ClassPathResource> {
        val roots = getRoots(dirPath, cl)
        val result = LinkedHashSet<ClassPathResource>(16)
        for (root in roots) {
            if (isJarResource(root)) {
                result.addAll(doFindPathMatchingJarResources(root))
            } else {
                result.addAll(doFindPathMatchingFileResources(root, dirPath))
            }
        }
        return result.toArray<ClassPathResource>(arrayOfNulls<ClassPathResource>(result.size))
    }

    fun getClasses(packageName: String, cl: ClassLoader?): Array<Class<*>> {
        @Suppress("NAME_SHADOWING")
        var cl = cl ?: Thread.currentThread().contextClassLoader
        val resources = getClassPathResources(packageName.replace(".", "/"), cl)
        val result = ArrayList<Class<*>>()
        for (resource in resources) {
            val urlPath = resource.url.path
            if (!urlPath.endsWith(".class") || urlPath.contains("$")) {
                continue
            }
            val cls = resolveClass(cl, resource)
            if (cls != null) {
                result.add(cls)
            }
        }
        return result.toArray(arrayOfNulls<Class<Any>>(result.size))
    }

    private fun resolveClass(cl: ClassLoader, resource: ClassPathResource): Class<*>? {
        val className = resolveClassName(resource)
        try {
            return cl.loadClass(className)
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        }

    }

    private fun resolveClassName(resource: ClassPathResource): String {
        val path = resource.classPathPath
        var className = path.substring(0, path.length - ".class".length)
        className = className.replace("/", ".")
        return className
    }

    private fun getRoots(dirPath: String, cl: ClassLoader): Array<URL> {
        val resources = cl.getResources(dirPath)
        return resources.toList().toTypedArray()
    }

    private fun doFindPathMatchingFileResources(rootUrl: URL, dirPath: String): Collection<ClassPathResource> {
        val filePath = rootUrl.file
        val file = File(filePath)
        val rootDir = file.absoluteFile
        return doFindMatchingFileSystemResources(rootDir, dirPath)
    }

    private fun doFindMatchingFileSystemResources(rootDir: File, dirPath: String): Collection<ClassPathResource> {
        val allFiles = HashSet<File>()
        retrieveAllFiles(rootDir, allFiles)
        val classPathRoot = parseClassPathRoot(rootDir, dirPath)
        val result = LinkedHashSet<ClassPathResource>(allFiles.size)
        for (file in allFiles) {
            val absolutePath = file.getAbsolutePath()
            val url = URL("file:///" + absolutePath)
            var classPathPath = absolutePath.substring(classPathRoot.length)
            classPathPath = classPathPath.replace("\\", "/")
            result.add(ClassPathResource(url, classPathPath))
        }
        return result
    }

    private fun parseClassPathRoot(rootDir: File, dirPath: String): String {
        var absolutePath = rootDir.absolutePath
        absolutePath = absolutePath.replace("\\", "/")
        val lastIndex = absolutePath.lastIndexOf(dirPath)
        var result = absolutePath.substring(0, lastIndex)
        if (!result.endsWith("/")) {
            result = result + "/"
        }
        return result
    }

    private fun retrieveAllFiles(dir: File, allFiles: MutableSet<File>) {
        val subFiles = dir.listFiles()
        assert(subFiles != null)
        allFiles.addAll(Arrays.asList(*subFiles))

        for (subFile in subFiles!!) {
            if (subFile.isDirectory) {
                retrieveAllFiles(subFile, allFiles)
            }
        }
    }

    private fun doFindPathMatchingJarResources(rootUrl: URL): Collection<ClassPathResource> {
        val con = rootUrl.openConnection()
        val jarFile: JarFile
        var rootEntryPath: String
        var newJarFile = false

        if (con is JarURLConnection) {
            // Should usually be the case for traditional JAR files.
            con.useCaches = true
            jarFile = con.jarFile
            val jarEntry = con.jarEntry
            rootEntryPath = if (jarEntry != null) jarEntry.name else ""
        } else {
            // No JarURLConnection -> need to resort to URL file parsing.
            // We'll assume URLs of the format "jar:path!/entry", with the protocol
            // being arbitrary as long as following the entry format.
            // We'll also handle paths with and without leading "file:" prefix.
            val urlFile = rootUrl.file
            val separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR)
            if (separatorIndex != -1) {
                val jarFileUrl = urlFile.substring(0, separatorIndex)
                rootEntryPath = urlFile.substring(separatorIndex + JAR_URL_SEPARATOR.length)
                jarFile = getJarFile(jarFileUrl)
            } else {
                jarFile = JarFile(urlFile)
                rootEntryPath = ""
            }
            newJarFile = true
        }

        try {
            if ("" != rootEntryPath && !rootEntryPath.endsWith("/")) {
                // Root entry path must end with slash to allow for proper matching.
                // The Sun JRE does not return a slash here, but BEA JRockit does.
                rootEntryPath = rootEntryPath + "/"
            }
            val result = LinkedHashSet<ClassPathResource>(8)
            val entries = jarFile.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                val entryPath = entry.name
                if (entryPath.startsWith(rootEntryPath)) {
                    val relativePath = entryPath.substring(rootEntryPath.length)
                    var rootPath = rootUrl.path
                    rootPath = if (rootPath.endsWith("/")) rootPath else rootPath + "/"
                    val newPath = applyRelativePath(rootPath, relativePath)
                    val classPathPath = applyRelativePath(rootEntryPath, relativePath)
                    result.add(ClassPathResource(URL(newPath), classPathPath))
                }
            }
            return result
        } finally {
            // Close jar file, but only if freshly obtained -
            // not from JarURLConnection, which might cache the file reference.
            if (newJarFile) {
                jarFile.close()
            }
        }
    }

    /**
     * Apply the given relative path to the given path,
     * assuming standard Java folder separation (i.e. "/" separators).

     * @param path         the path to start from (usually a full file path)
     * *
     * @param relativePath the relative path to apply
     * *                     (relative to the full file path above)
     * *
     * @return the full file path that results from applying the relative path
     */
    private fun applyRelativePath(path: String, relativePath: String): String {
        val separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR)
        if (separatorIndex != -1) {
            var newPath = path.substring(0, separatorIndex)
            if (!relativePath.startsWith(FOLDER_SEPARATOR)) {
                newPath += FOLDER_SEPARATOR
            }
            return newPath + relativePath
        } else {
            return relativePath
        }
    }

    /**
     * Resolve the given jar file URL into a JarFile object.
     */
    private fun getJarFile(jarFileUrl: String): JarFile {
        if (jarFileUrl.startsWith(FILE_URL_PREFIX)) {
            try {
                return JarFile(toURI(jarFileUrl).schemeSpecificPart)
            } catch (ex: URISyntaxException) {
                // Fallback for URLs that are not valid URIs (should hardly ever happen).
                return JarFile(jarFileUrl.substring(FILE_URL_PREFIX.length))
            }

        } else {
            return JarFile(jarFileUrl)
        }
    }

    /**
     * Create a URI instance for the given location String,
     * replacing spaces with "%20" URI encoding first.

     * @param location the location String to convert into a URI instance
     * *
     * @return the URI instance
     * *
     * @throws URISyntaxException if the location wasn't a valid URI
     */
    private fun toURI(location: String): URI {
        return URI(location.replace(" ", "%20"))
    }

    private fun isJarResource(url: URL): Boolean {
        val protocol = url.protocol
        return URL_PROTOCOL_JAR == protocol || URL_PROTOCOL_ZIP == protocol ||
                URL_PROTOCOL_VFSZIP == protocol || URL_PROTOCOL_WSJAR == protocol ||
                URL_PROTOCOL_CODE_SOURCE == protocol && url.path.contains(JAR_URL_SEPARATOR)
    }

    class ClassPathResource(val url: URL, val classPathPath: String)
}
