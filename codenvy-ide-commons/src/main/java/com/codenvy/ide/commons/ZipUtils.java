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
package com.codenvy.ide.commons;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static com.codenvy.ide.commons.FileUtils.ANY_FILTER;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ZipUtils {
    public static void zipDir(String zipRootPath, File dir, File zip, FilenameFilter filter) throws IOException {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Not a directory. ");
        }
        if (filter == null) {
            filter = ANY_FILTER;
        }
        FileOutputStream fos = null;
        ZipOutputStream zipOut = null;
        try {
            byte[] b = new byte[8192];
            fos = new FileOutputStream(zip);
            zipOut = new ZipOutputStream(fos);
            LinkedList<File> q = new LinkedList<File>();
            q.add(dir);
            while (!q.isEmpty()) {
                File current = q.pop();
                File[] list = current.listFiles();
                if (list != null) {
                    for (File f : list) {
                        if (!filter.accept(current, f.getName())) {
                            continue;
                        }
                        final String zipEntryName =
                                f.getAbsolutePath().substring(zipRootPath.length() + 1).replace('\\', '/');
                        if (f.isDirectory()) {
                            q.push(f);
                            zipOut
                                    .putNextEntry(new ZipEntry(zipEntryName.endsWith("/") ? zipEntryName : (zipEntryName + '/')));
                        } else {
                            zipOut.putNextEntry(new ZipEntry(zipEntryName));
                            FileInputStream in = null;
                            try {
                                in = new FileInputStream(f);
                                int r;
                                while ((r = in.read(b)) != -1) {
                                    zipOut.write(b, 0, r);
                                }
                            } finally {
                                if (in != null) {
                                    in.close();
                                }
                                zipOut.closeEntry();
                            }
                        }
                    }
                }
            }
        } finally {
            if (zipOut != null) {
                zipOut.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    public static Collection<String> listEntries(File zip) throws IOException {
        return listEntries(new FileInputStream(zip));
    }

    public static Collection<String> listEntries(InputStream in) throws IOException {
        List<String> list = new ArrayList<String>();
        ZipInputStream zipIn = null;
        try {
            zipIn = new ZipInputStream(in);
            ZipEntry zipEntry;
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    list.add(zipEntry.getName());
                }
                zipIn.closeEntry();
            }
        } finally {
            if (zipIn != null) {
                zipIn.close();
            }
            in.close();
        }
        return list;
    }

    public static void unzip(File zip, File targetDir) throws IOException {
        unzip(new FileInputStream(zip), targetDir);
    }

    public static void unzip(InputStream in, File targetDir) throws IOException {
        ZipInputStream zipIn = null;
        try {
            zipIn = new ZipInputStream(in);
            byte[] b = new byte[8192];
            ZipEntry zipEntry;
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                File file = new File(targetDir, zipEntry.getName());
                if (!zipEntry.isDirectory()) {
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    try {
                        int r;
                        while ((r = zipIn.read(b)) != -1) {
                            fos.write(b, 0, r);
                        }
                    } finally {
                        fos.close();
                    }
                } else {
                    file.mkdirs();
                }
                zipIn.closeEntry();
            }
        } finally {
            if (zipIn != null) {
                zipIn.close();
            }
            in.close();
        }
    }

    private ZipUtils() {
    }
}