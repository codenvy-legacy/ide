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
}
