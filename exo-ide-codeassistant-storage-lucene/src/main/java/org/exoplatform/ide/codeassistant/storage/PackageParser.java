/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
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
package org.exoplatform.ide.codeassistant.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 2:34:18 PM Mar 5, 2012 evgen $
 */
public class PackageParser {
    private PackageParser() {
    }

    public static Set<String> parse(InputStream jar) throws IOException {
        Set<String> packages = new HashSet<String>();
        ZipInputStream zip = new ZipInputStream(jar);
        try {
            ZipEntry entry = zip.getNextEntry();
            while (entry != null) {

                String name = entry.getName();
                if (name.endsWith(".class")) {

                    packages.addAll(parsePath(name));
                }
                entry = zip.getNextEntry();
            }
        } finally {
            zip.close();
        }

        return packages;
    }

    /**
     * @param name
     * @return
     */
    private static Set<String> parsePath(String name) {

        String[] segments = name.split("/");
        Set<String> packageSegment = new HashSet<String>();
        StringBuilder first = new StringBuilder(segments[0]);
        packageSegment.add(first.toString());
        for (int i = 1; i < segments.length - 1; i++) {
            first.append('.').append(segments[i]);
            packageSegment.add(first.toString());
        }
        return packageSegment;
    }

    /**
     * @param jarFile
     * @return
     * @throws IOException
     */
    public static Set<String> parse(File jarFile) throws IOException {
        FileInputStream jarStream = new FileInputStream(jarFile);
        try {
            return parse(jarStream);
        } finally {
            jarStream.close();
        }
    }

    /**
     * @param jarFile
     * @param ignoredPackages
     * @return
     */
    public static Set<String> parse(File jarFile, Set<String> ignoredPackages) throws IOException {
        FileInputStream jarStream = new FileInputStream(jarFile);
        try {
            Set<String> packages = new HashSet<String>();
            ZipInputStream zip = new ZipInputStream(jarStream);
            try {
                ZipEntry entry = zip.getNextEntry();
                boolean ignore = false;
                while (entry != null) {

                    String name = entry.getName();
                    if (name.endsWith(".class")) {
                        ignore = false;
                        for (String s : ignoredPackages) {
                            if (entry.getName().startsWith(s)) {
                                ignore = true;
                                break;
                            }
                        }
                        if (!ignore) {
                            packages.addAll(parsePath(name));
                        }
                    }
                    entry = zip.getNextEntry();
                }
            } finally {
                zip.close();
            }
            return packages;
        } finally {
            jarStream.close();
        }
    }
}
