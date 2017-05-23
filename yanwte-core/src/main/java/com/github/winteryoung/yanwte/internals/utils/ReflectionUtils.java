package com.github.winteryoung.yanwte.internals.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 反射工具。实现是基于 Spring 3.1.11.RELEASE 版的 PathMatchingResourcePatternResolver 修改的。
 *
 * @author Winter Young
 * @since 2015/7/7
 */
public class ReflectionUtils {
    private ReflectionUtils() {
    }

    /**
     * URL protocol for an entry from a jar file: "jar"
     */
    private static final String URL_PROTOCOL_JAR = "jar";

    /**
     * URL protocol for an entry from a zip file: "zip"
     */
    private static final String URL_PROTOCOL_ZIP = "zip";

    /**
     * URL protocol for an entry from a WebSphere jar file: "wsjar"
     */
    private static final String URL_PROTOCOL_WSJAR = "wsjar";

    /**
     * URL protocol for an entry from a JBoss jar file: "vfszip"
     */
    private static final String URL_PROTOCOL_VFSZIP = "vfszip";

    /**
     * URL protocol for an entry from an OC4J jar file: "code-source"
     */
    private static final String URL_PROTOCOL_CODE_SOURCE = "code-source";

    /**
     * Separator between JAR URL and file path within the JAR
     */
    private static final String JAR_URL_SEPARATOR = "!/";

    /**
     * URL prefix for loading from the file system: "file:"
     */
    private static final String FILE_URL_PREFIX = "file:";

    /**
     * 文件夹隔离符。
     */
    private static final String FOLDER_SEPARATOR = "/";

    /**
     * 递归地在类路径中以指定的类加载器获取 dirPath 指定的目录下面所有的资源。cl 可为空，为空时取系统类加载器。
     * 返回值一定不为 null。
     */
    private static ClassPathResource[] getClassPathResources(String dirPath, ClassLoader cl) throws IOException {
        if (cl == null) {
            cl = Thread.currentThread().getContextClassLoader();
        }
        URL[] roots = getRoots(dirPath, cl);
        Set<ClassPathResource> result = new LinkedHashSet<>(16);
        for (URL root : roots) {
            if (isJarResource(root)) {
                result.addAll(doFindPathMatchingJarResources(root));
            } else {
                result.addAll(doFindPathMatchingFileResources(root, dirPath));
            }
        }
        return result.toArray(new ClassPathResource[result.size()]);
    }

    /**
     * 使用 cl 指定的类加载器递归加载 packageName 指定的包名下面的所有的类。不会返回 null。
     * cl 为空时使用系统类加载器。返回值一定不为 null。返回值中不包含类路径中的内部类。
     */
    public static Class<?>[] getClasses(String packageName, ClassLoader cl) throws IOException {
        if (cl == null) {
            cl = Thread.currentThread().getContextClassLoader();
        }
        ClassPathResource[] resources = getClassPathResources(StringUtils.replace(packageName, ".", "/"), cl);
        List<Class<?>> result = Lists.newArrayList();
        for (ClassPathResource resource : resources) {
            String urlPath = resource.getUrl().getPath();
            if (!urlPath.endsWith(".class") || urlPath.contains("$")) {
                continue;
            }
            Class<?> cls = resolveClass(cl, resource);
            if (cls != null) {
                result.add(cls);
            }
        }
        return result.toArray(new Class<?>[result.size()]);
    }

    private static Class<?> resolveClass(ClassLoader cl, ClassPathResource resource) {
        String className = resolveClassName(resource);
        try {
            return cl.loadClass(className);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static String resolveClassName(ClassPathResource resource) {
        String path = resource.getClassPathPath();
        String className = path.substring(0, path.length() - ".class".length());
        className = StringUtils.replace(className, "/", ".");
        return className;
    }

    private static URL[] getRoots(String dirPath, ClassLoader cl) throws IOException {
        Enumeration<URL> resources = cl.getResources(dirPath);
        ArrayList<URL> resourceUrls = Collections.list(resources);
        return resourceUrls.toArray(new URL[resourceUrls.size()]);
    }

    private static Collection<ClassPathResource> doFindPathMatchingFileResources(URL rootUrl, String dirPath)
        throws IOException {
        String filePath = rootUrl.getFile();
        File file = new File(filePath);
        File rootDir = file.getAbsoluteFile();
        return doFindMatchingFileSystemResources(rootDir, dirPath);
    }

    private static Collection<ClassPathResource> doFindMatchingFileSystemResources(File rootDir, String dirPath)
        throws IOException {
        Set<File> allFiles = Sets.newLinkedHashSet();
        retrieveAllFiles(rootDir, allFiles);
        String classPathRoot = parseClassPathRoot(rootDir, dirPath);
        Set<ClassPathResource> result = new LinkedHashSet<>(allFiles.size());
        for (File file : allFiles) {
            String absolutePath = file.getAbsolutePath();
            URL url = new URL("file:///" + absolutePath);
            String classPathPath = absolutePath.substring(classPathRoot.length());
            classPathPath = StringUtils.replace(classPathPath, "\\", "/");
            result.add(new ClassPathResource(url, classPathPath));
        }
        return result;
    }

    private static String parseClassPathRoot(File rootDir, String dirPath) {
        String absolutePath = rootDir.getAbsolutePath();
        absolutePath = StringUtils.replace(absolutePath, "\\", "/");
        int lastIndex = absolutePath.lastIndexOf(dirPath);
        String result = absolutePath.substring(0, lastIndex);
        if (!result.endsWith("/")) {
            result = result + "/";
        }
        return result;
    }

    private static void retrieveAllFiles(File dir, Set<File> allFiles) {
        File[] subFiles = dir.listFiles();
        assert subFiles != null;
        allFiles.addAll(Arrays.asList(subFiles));

        for (File subFile : subFiles) {
            if (subFile.isDirectory()) {
                retrieveAllFiles(subFile, allFiles);
            }
        }
    }

    private static Collection<ClassPathResource> doFindPathMatchingJarResources(URL rootUrl) throws IOException {
        URLConnection con = rootUrl.openConnection();
        JarFile jarFile;
        String rootEntryPath;
        boolean newJarFile = false;

        if (con instanceof JarURLConnection) {
            // Should usually be the case for traditional JAR files.
            JarURLConnection jarCon = (JarURLConnection) con;
            jarCon.setUseCaches(true);
            jarFile = jarCon.getJarFile();
            JarEntry jarEntry = jarCon.getJarEntry();
            rootEntryPath = (jarEntry != null ? jarEntry.getName() : "");
        } else {
            // No JarURLConnection -> need to resort to URL file parsing.
            // We'll assume URLs of the format "jar:path!/entry", with the protocol
            // being arbitrary as long as following the entry format.
            // We'll also handle paths with and without leading "file:" prefix.
            String urlFile = rootUrl.getFile();
            int separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
            if (separatorIndex != -1) {
                String jarFileUrl = urlFile.substring(0, separatorIndex);
                rootEntryPath = urlFile.substring(separatorIndex + JAR_URL_SEPARATOR.length());
                jarFile = getJarFile(jarFileUrl);
            } else {
                jarFile = new JarFile(urlFile);
                rootEntryPath = "";
            }
            newJarFile = true;
        }

        try {
            if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
                // Root entry path must end with slash to allow for proper matching.
                // The Sun JRE does not return a slash here, but BEA JRockit does.
                rootEntryPath = rootEntryPath + "/";
            }
            Set<ClassPathResource> result = new LinkedHashSet<>(8);
            for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
                JarEntry entry = entries.nextElement();
                String entryPath = entry.getName();
                if (entryPath.startsWith(rootEntryPath)) {
                    String relativePath = entryPath.substring(rootEntryPath.length());
                    String rootPath = rootUrl.getPath();
                    rootPath = rootPath.endsWith("/") ? rootPath : rootPath + "/";
                    String newPath = applyRelativePath(rootPath, relativePath);
                    String classPathPath = applyRelativePath(rootEntryPath, relativePath);
                    result.add(new ClassPathResource(new URL(newPath), classPathPath));
                }
            }
            return result;
        } finally {
            // Close jar file, but only if freshly obtained -
            // not from JarURLConnection, which might cache the file reference.
            if (newJarFile) {
                jarFile.close();
            }
        }
    }

    /**
     * Apply the given relative path to the given path,
     * assuming standard Java folder separation (i.e. "/" separators).
     *
     * @param path         the path to start from (usually a full file path)
     * @param relativePath the relative path to apply
     *                     (relative to the full file path above)
     * @return the full file path that results from applying the relative path
     */
    private static String applyRelativePath(String path, String relativePath) {
        int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        if (separatorIndex != -1) {
            String newPath = path.substring(0, separatorIndex);
            if (!relativePath.startsWith(FOLDER_SEPARATOR)) {
                newPath += FOLDER_SEPARATOR;
            }
            return newPath + relativePath;
        } else {
            return relativePath;
        }
    }

    /**
     * Resolve the given jar file URL into a JarFile object.
     */
    private static JarFile getJarFile(String jarFileUrl) throws IOException {
        if (jarFileUrl.startsWith(FILE_URL_PREFIX)) {
            try {
                return new JarFile(toURI(jarFileUrl).getSchemeSpecificPart());
            } catch (URISyntaxException ex) {
                // Fallback for URLs that are not valid URIs (should hardly ever happen).
                return new JarFile(jarFileUrl.substring(FILE_URL_PREFIX.length()));
            }
        } else {
            return new JarFile(jarFileUrl);
        }
    }

    /**
     * Create a URI instance for the given location String,
     * replacing spaces with "%20" URI encoding first.
     *
     * @param location the location String to convert into a URI instance
     * @return the URI instance
     * @throws URISyntaxException if the location wasn't a valid URI
     */
    private static URI toURI(String location) throws URISyntaxException {
        return new URI(StringUtils.replace(location, " ", "%20"));
    }

    private static boolean isJarResource(URL url) {
        String protocol = url.getProtocol();
        return (URL_PROTOCOL_JAR.equals(protocol) || URL_PROTOCOL_ZIP.equals(protocol) ||
                URL_PROTOCOL_VFSZIP.equals(protocol) || URL_PROTOCOL_WSJAR.equals(protocol) ||
                (URL_PROTOCOL_CODE_SOURCE.equals(protocol) && url.getPath().contains(JAR_URL_SEPARATOR)));
    }

    /**
     * 类路径资源。
     */
    private static class ClassPathResource {
        /**
         * 此资源对应的 URL 对象。
         */
        private URL url;

        /**
         * 类路径下的路径。特点是这个路径字符串去掉了类路径的“根”部分。
         */
        private String classPathPath;

        /**
         * ctor.
         */
        ClassPathResource(URL url, String classPathPath) {
            this.url = url;
            this.classPathPath = classPathPath;
        }

        URL getUrl() {
            return url;
        }

        String getClassPathPath() {
            return classPathPath;
        }
    }
}
