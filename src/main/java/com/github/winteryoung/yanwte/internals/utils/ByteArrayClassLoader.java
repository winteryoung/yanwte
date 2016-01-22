/**
 * 从 byte-buddy 的源码中拷贝而来。
 */
package com.github.winteryoung.yanwte.internals.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link java.lang.ClassLoader} that is capable of loading explicitly defined classes. The class loader will free
 * any binary resources once a class that is defined by its binary data is loaded. This class loader is thread safe
 * since the class loading mechanics are only called from synchronized context.
 */
public class ByteArrayClassLoader extends ClassLoader {
    /**
     * A mutable map of type names mapped to their binary representation.
     */
    protected final Map<String, byte[]> typeDefinitions;

    /**
     * The persistence handler of this class loader.
     */
    protected final PersistenceHandler persistenceHandler;

    /**
     * The protection domain to apply. Might be {@code null} when referencing the default protection domain.
     */
    protected final ProtectionDomain protectionDomain;

    /**
     * The access control context of this class loader's instantiation.
     */
    protected final AccessControlContext accessControlContext;

    public ByteArrayClassLoader(ClassLoader parent,
                                Map<String, byte[]> typeDefinitions) {
        this(parent, typeDefinitions, null, PersistenceHandler.LATENT);
    }

    /**
     * Creates a new class loader for a given definition of classes.
     *
     * @param parent             The {@link java.lang.ClassLoader} that is the parent of this class loader.
     * @param typeDefinitions    A map of fully qualified class names pointing to their binary representations.
     * @param protectionDomain   The protection domain to apply where {@code null} references an implicit
     *                           protection domain.
     * @param persistenceHandler The persistence handler of this class loader.
     */
    public ByteArrayClassLoader(ClassLoader parent,
                                Map<String, byte[]> typeDefinitions,
                                ProtectionDomain protectionDomain,
                                PersistenceHandler persistenceHandler) {
        super(parent);
        this.typeDefinitions = new HashMap<>(typeDefinitions);
        this.protectionDomain = protectionDomain;
        this.persistenceHandler = persistenceHandler;
        accessControlContext = AccessController.getContext();
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            // This does not need synchronization because this method is only called from within
            // ClassLoader in a synchronized context.
            return AccessController.doPrivileged(new ClassLoadingAction(name), accessControlContext);
        } catch (PrivilegedActionException e) {
            throw (ClassNotFoundException) e.getCause();
        }
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream inputStream = super.getResourceAsStream(name);
        if (inputStream != null) {
            return inputStream;
        } else {
            return persistenceHandler.inputStream(name, typeDefinitions);
        }
    }

    @Override
    public String toString() {
        return "ByteArrayClassLoader{" +
                "parent=" + getParent() +
                ", typeDefinitions=" + typeDefinitions +
                ", persistenceHandler=" + persistenceHandler +
                ", protectionDomain=" + protectionDomain +
                ", accessControlContext=" + accessControlContext +
                '}';
    }

    /**
     * A persistence handler decides on weather the byte array that represents a loaded class is exposed by
     * the {@link java.lang.ClassLoader#getResourceAsStream(String)} method.
     */
    public enum PersistenceHandler {

        /**
         * The manifest persistence handler retains all class file representations and makes them accessible.
         */
        MANIFEST(true) {
            @Override
            protected byte[] lookup(String name, Map<String, byte[]> typeDefinitions) {
                return typeDefinitions.get(name);
            }

            @Override
            protected InputStream inputStream(String resourceName, Map<String, byte[]> typeDefinitions) {
                if (!resourceName.endsWith(CLASS_FILE_SUFFIX)) {
                    return null;
                }
                byte[] binaryRepresentation = typeDefinitions.get(resourceName.replace('/', '.')
                        .substring(0, resourceName.length() - CLASS_FILE_SUFFIX.length()));
                return binaryRepresentation == null
                        ? null
                        : new ByteArrayInputStream(binaryRepresentation);
            }
        },

        /**
         * The latent persistence handler hides all class file representations and does not make them accessible
         * even before they are loaded.
         */
        LATENT(false) {
            @Override
            protected byte[] lookup(String name, Map<String, byte[]> typeDefinitions) {
                return typeDefinitions.remove(name);
            }

            @Override
            protected InputStream inputStream(String resourceName, Map<String, byte[]> typeDefinitions) {
                return null;
            }
        };

        /**
         * The suffix of files in the Java class file format.
         */
        private static final String CLASS_FILE_SUFFIX = ".class";

        /**
         * {@code true} if this persistence handler represents manifest class file storage.
         */
        private final boolean manifest;

        /**
         * Creates a new persistence handler.
         *
         * @param manifest {@code true} if this persistence handler represents manifest class file storage.
         */
        PersistenceHandler(boolean manifest) {
            this.manifest = manifest;
        }

        /**
         * Checks if this persistence handler represents manifest class file storage.
         *
         * @return {@code true} if this persistence handler represents manifest class file storage.
         */
        public boolean isManifest() {
            return manifest;
        }

        /**
         * Performs a lookup of a class file by its name.
         *
         * @param name            The name of the class to be loaded.
         * @param typeDefinitions A map of fully qualified class names pointing to their binary representations.
         * @return The byte array representing the requested class or {@code null} if no such class is known.
         */
        protected abstract byte[] lookup(String name, Map<String, byte[]> typeDefinitions);

        /**
         * Performs a lookup of an input stream for exposing a class file as a resource.
         *
         * @param resourceName    The resource name of the class to be exposed as its class file.
         * @param typeDefinitions A map of fully qualified class names pointing to their binary representations.
         * @return An input stream representing the requested resource or {@code null} if no such resource is known.
         */
        protected abstract InputStream inputStream(String resourceName, Map<String, byte[]> typeDefinitions);

        @Override
        public String toString() {
            return "ByteArrayClassLoader.PersistenceHandler." + name();
        }
    }

    /**
     * A class loading action is responsible to perform the loading of a class in a privileged security context.
     */
    private class ClassLoadingAction implements PrivilegedExceptionAction<Class<?>> {

        /**
         * A convenience index referencing the beginning of an array to improve code readability.
         */
        private static final int FROM_BEGINNING = 0;

        /**
         * The name of the type to be loaded.
         */
        private final String name;

        /**
         * Creates a new class loading action.
         *
         * @param name The name of the type to be loaded.
         */
        private ClassLoadingAction(String name) {
            this.name = name;
        }

        @Override
        public Class<?> run() throws ClassNotFoundException {
            byte[] javaType = persistenceHandler.lookup(name, typeDefinitions);
            if (javaType != null) {
                return defineClass(name, javaType, FROM_BEGINNING, javaType.length, protectionDomain);
            }
            throw new ClassNotFoundException(name);
        }

        @Override
        public String toString() {
            return "ByteArrayClassLoader.ClassLoadingAction{classLoader=" + ByteArrayClassLoader.this + ", name='" + name + "'}";
        }
    }
}
