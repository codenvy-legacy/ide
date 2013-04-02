/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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