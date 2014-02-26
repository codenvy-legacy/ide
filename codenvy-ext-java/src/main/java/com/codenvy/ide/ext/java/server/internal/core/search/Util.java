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
package com.codenvy.ide.ext.java.server.internal.core.search;

import com.codenvy.ide.ext.java.server.internal.core.search.indexing.IndexManager;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaModelStatusConstants;
import org.eclipse.jdt.core.JavaModelException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;

/**
 * @author Evgen Vidolob
 */
public class Util {

    /**
     * Returns true if the given name ends with one of the known java like extension.
     * (implementation is not creating extra strings)
     */
    public final static boolean isJavaLikeFileName(String name) {
        if (name == null) return false;
        return indexOfJavaLikeExtension(name) != -1;
    }

    /*
 * Returns the index of the Java like extension of the given file name
 * or -1 if it doesn't end with a known Java like extension.
 * Note this is the index of the '.' even if it is not considered part of the extension.
 */
    public static int indexOfJavaLikeExtension(String fileName) {
        int fileNameLength = fileName.length();
        char[][] javaLikeExtensions = getJavaLikeExtensions();
        extensions: for (int i = 0, length = javaLikeExtensions.length; i < length; i++) {
            char[] extension = javaLikeExtensions[i];
            int extensionLength = extension.length;
            int extensionStart = fileNameLength - extensionLength;
            int dotIndex = extensionStart - 1;
            if (dotIndex < 0) continue;
            if (fileName.charAt(dotIndex) != '.') continue;
            for (int j = 0; j < extensionLength; j++) {
                if (fileName.charAt(extensionStart + j) != extension[j])
                    continue extensions;
            }
            return dotIndex;
        }
        return -1;
    }

    public static char[][] getJavaLikeExtensions() {
        return new char[][]{{'j','a','v','a'}};
    }

    /*
 * Returns whether the given resource path matches one of the inclusion/exclusion
 * patterns.
 * NOTE: should not be asked directly using pkg root pathes
 * @see IClasspathEntry#getInclusionPatterns
 * @see IClasspathEntry#getExclusionPatterns
 */
    public final static boolean isExcluded(IPath resourcePath, char[][] inclusionPatterns, char[][] exclusionPatterns, boolean isFolderPath) {
        if (inclusionPatterns == null && exclusionPatterns == null) return false;
        return org.eclipse.jdt.internal.compiler.util.Util.isExcluded(resourcePath.toString().toCharArray(), inclusionPatterns, exclusionPatterns, isFolderPath);
    }

    /*
     * Returns whether the given resource matches one of the exclusion patterns.
     * NOTE: should not be asked directly using pkg root pathes
     * @see IClasspathEntry#getExclusionPatterns
     */
    public final static boolean isExcluded(IResource resource, char[][] inclusionPatterns, char[][] exclusionPatterns) {
        IPath path = resource.getFullPath();
        // ensure that folders are only excluded if all of their children are excluded
        int resourceType = resource.getType();
        return isExcluded(path, inclusionPatterns, exclusionPatterns, resourceType == IResource.FOLDER || resourceType == IResource.PROJECT);
    }

    /**
     * Returns the toString() of the given full path minus the first given number of segments.
     * The returned string is always a relative path (it has no leading slash)
     */
    public static String relativePath(IPath fullPath, int skipSegmentCount) {
        boolean hasTrailingSeparator = fullPath.hasTrailingSeparator();
        String[] segments = fullPath.segments();

        // compute length
        int length = 0;
        int max = segments.length;
        if (max > skipSegmentCount) {
            for (int i1 = skipSegmentCount; i1 < max; i1++) {
                length += segments[i1].length();
            }
            //add the separator lengths
            length += max - skipSegmentCount - 1;
        }
        if (hasTrailingSeparator)
            length++;

        char[] result = new char[length];
        int offset = 0;
        int len = segments.length - 1;
        if (len >= skipSegmentCount) {
            //append all but the last segment, with separators
            for (int i = skipSegmentCount; i < len; i++) {
                int size = segments[i].length();
                segments[i].getChars(0, size, result, offset);
                offset += size;
                result[offset++] = '/';
            }
            //append the last segment
            int size = segments[len].length();
            segments[len].getChars(0, size, result, offset);
            offset += size;
        }
        if (hasTrailingSeparator)
            result[offset++] = '/';
        return new String(result);
    }

    public static void verbose(String log) {
        verbose(log, System.out);
    }
    public static synchronized void verbose(String log, PrintStream printStream) {
        int start = 0;
        do {
            int end = log.indexOf('\n', start);
            printStream.print(Thread.currentThread());
            printStream.print(" "); //$NON-NLS-1$
            printStream.print(log.substring(start, end == -1 ? log.length() : end+1));
            start = end+1;
        } while (start != 0);
        printStream.println();
    }

    public static byte[] getResourceContentsAsByteArray(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            if(IndexManager.DEBUG){
                e.printStackTrace();
            }
            return new byte[0];
        }
    }

    public static char[] getResourceContentsAsCharArray(File file) throws JavaModelException {
        InputStream stream = null;
        try {
            stream = new FileInputStream(file);
            return org.eclipse.jdt.internal.compiler.util.Util.getInputStreamAsCharArray(stream, (int)file.length(), "UTF-8");
        } catch (IOException e) {
            throw new JavaModelException(e, IJavaModelStatusConstants.IO_EXCEPTION);
        }
        finally {
            if(stream != null){
                try {
                    stream.close();
                } catch (IOException ignore) {
                }
            }
        }

    }
}
