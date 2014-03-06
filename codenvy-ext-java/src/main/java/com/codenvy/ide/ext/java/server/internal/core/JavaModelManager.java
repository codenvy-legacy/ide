/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.java.server.internal.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.util.Messages;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * @author Evgen Vidolob
 */
public class JavaModelManager {

    public static  boolean          ZIP_ACCESS_VERBOSE             = false;
    /**
     * A set of java.io.Files used as a cache of external jars that
     * are known to be existing.
     * Note this cache is kept for the whole session.
     */
    public static  HashSet<File>    existingExternalFiles          = new HashSet<>();
    /**
     * A set of external files ({@link #existingExternalFiles}) which have
     * been confirmed as file (i.e. which returns true to {@link java.io.File#isFile()}.
     * Note this cache is kept for the whole session.
     */
    public static  HashSet<File>    existingExternalConfirmedFiles = new HashSet<>();
    /**
     * The singleton manager
     */
    private static JavaModelManager MANAGER                        = new JavaModelManager();
    /**
     * List of IPath of jars that are known to be invalid - such as not being a valid/known format
     */
    private Set<IPath> invalidArchives;

    /**
     * A cache of opened zip files per thread.
     * (for a given thread, the object value is a HashMap from IPath to java.io.ZipFile)
     */
    private ThreadLocal<ZipCache> zipFiles = new ThreadLocal<>();

    public static JavaModelManager getJavaModelManager() {
        return MANAGER;
    }

    /**
     * Helper method - returns the targeted item (IResource if internal or java.io.File if external),
     * or null if unbound
     * Internal items must be referred to using container relative paths.
     */
    public static Object getTarget(IPath path, boolean checkResourceExistence) {
        File externalFile = new File(path.toOSString());
        if (!checkResourceExistence) {
            return externalFile;
        } else if (existingExternalFilesContains(externalFile)) {
            return externalFile;
        } else {
            if (JavaModelManager.ZIP_ACCESS_VERBOSE) {
                System.out.println("(" + Thread.currentThread() + ") [JavaModel.getTarget...)] Checking existence of " +
                                   path.toString()); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (externalFile.isFile()) { // isFile() checks for existence (it returns false if a directory)
                // cache external file
                existingExternalFilesAdd(externalFile);
                return externalFile;
            } else {
                if (externalFile.exists()) {
                    existingExternalFilesAdd(externalFile);
                    return externalFile;
                }
            }
        }
        return null;
    }

    private synchronized static void existingExternalFilesAdd(File externalFile) {
        existingExternalFiles.add(externalFile);
    }

    private synchronized static boolean existingExternalFilesContains(File externalFile) {
        return existingExternalFiles.contains(externalFile);
    }

    /**
     * Flushes the cache of external files known to be existing.
     */
    public static void flushExternalFileCache() {
        existingExternalFiles = new HashSet<>();
        existingExternalConfirmedFiles = new HashSet<>();
    }

    /**
     * Helper method - returns whether an object is afile (i.e. which returns true to {@link java.io.File#isFile()}.
     */
    public static boolean isFile(Object target) {
        return getFile(target) != null;
    }

    /**
     * Helper method - returns the file item (i.e. which returns true to {@link java.io.File#isFile()},
     * or null if unbound
     */
    public static synchronized File getFile(Object target) {
        if (existingExternalConfirmedFiles.contains(target))
            return (File)target;
        if (target instanceof File) {
            File f = (File)target;
            if (f.isFile()) {
                existingExternalConfirmedFiles.add(f);
                return f;
            }
        }

        return null;
    }

    /**
     * Returns the open ZipFile at the given path. If the ZipFile
     * does not yet exist, it is created, opened, and added to the cache
     * of open ZipFiles.
     * <p/>
     * The path must be a file system path if representing an external
     * zip/jar, or it must be an absolute workspace relative path if
     * representing a zip/jar inside the workspace.
     *
     * @throws org.eclipse.core.runtime.CoreException
     *         If unable to create/open the ZipFile
     */
    public ZipFile getZipFile(IPath path) throws CoreException {

        if (isInvalidArchive(path))
            throw new CoreException(new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.status_IOException, new ZipException()));

        ZipCache zipCache;
        ZipFile zipFile;
        if ((zipCache = this.zipFiles.get()) != null
            && (zipFile = zipCache.getCache(path)) != null) {
            return zipFile;
        }
        File localFile = null;
//        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//        IResource file = root.findMember(path);
//        if (file != null) {
//            // internal resource
//            URI location;
//            if (file.getType() != IResource.FILE || (location = file.getLocationURI()) == null) {
//                throw new CoreException(
//                        new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.bind(Messages.file_notFound, path.toString()), null));
//            }
//            localFile = Util.toLocalFile(location, null*//*no progress availaible*//*);
//            if (localFile == null)
//                throw new CoreException(new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.bind(Messages.file_notFound,
// path.toString()), null));
//        } else {
//            // external resource -> it is ok to use toFile()
        localFile = path.toFile();
//        }
        if (!localFile.exists()) {
            throw new CoreException(
                    new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.bind(Messages.file_notFound, path.toString()), null));
        }

        try {
            if (ZIP_ACCESS_VERBOSE) {
                System.out.println("(" + Thread.currentThread() + ") [JavaModelManager.getZipFile(IPath)] Creating ZipFile on " +
                                   localFile); //$NON-NLS-1$ //$NON-NLS-2$
            }
            zipFile = new ZipFile(localFile);
            if (zipCache != null) {
                zipCache.setCache(path, zipFile);
            }
            return zipFile;
        } catch (IOException e) {
            addInvalidArchive(path);
            throw new CoreException(new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.status_IOException, e));
        }
    }

    public boolean isInvalidArchive(IPath path) {
        return this.invalidArchives != null && this.invalidArchives.contains(path);
    }

    public void removeFromInvalidArchiveCache(IPath path) {
        if (this.invalidArchives != null) {
            this.invalidArchives.remove(path);
        }
    }

    public void addInvalidArchive(IPath path) {
        // unlikely to be null
        if (this.invalidArchives == null) {
            this.invalidArchives = Collections.synchronizedSet(new HashSet<IPath>());
        }
        if (this.invalidArchives != null) {
            this.invalidArchives.add(path);
        }
    }

    /**
     * Define a zip cache object.
     */
    static class ZipCache {
        Object owner;
        private Map<IPath, ZipFile> map;

        ZipCache(Object owner) {
            this.map = new HashMap<>();
            this.owner = owner;
        }

        public void flush() {
            Thread currentThread = Thread.currentThread();
            for (ZipFile zipFile : this.map.values()) {
                try {
                    if (JavaModelManager.ZIP_ACCESS_VERBOSE) {
                        System.out.println("(" + currentThread + ") [JavaModelManager.flushZipFiles()] Closing ZipFile on " +
                                           zipFile.getName()); //$NON-NLS-1$//$NON-NLS-2$
                    }
                    zipFile.close();
                } catch (IOException e) {
                    // problem occured closing zip file: cannot do much more
                }
            }
        }

        public ZipFile getCache(IPath path) {
            return this.map.get(path);
        }

        public void setCache(IPath path, ZipFile zipFile) {
            this.map.put(path, zipFile);
        }
    }
}
