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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class UpdateUtil {

    private static final String UPDATE_FILE_PREFIX = "update-";

    private static final SecureRandom gen = new SecureRandom();

    /**
     * Create new directory with random name.
     *
     * @param parent
     *         parent for creation directory
     * @return newly created directory
     */
    public static File makeProjectDirectory(File parent) {
        File dir = new File(parent, UPDATE_FILE_PREFIX + Long.toString(Math.abs(gen.nextLong())));
        if (!dir.mkdirs()) {
            throw new RuntimeException("Unable create project directory. ");
        }
        return dir;
    }

    /**
     * Remove specified file or directory.
     *
     * @param fileOrDirectory
     *         the file or directory to cancel
     * @return <code>true</code> if specified File was deleted and <code>false</code> otherwise
     */
    public static boolean delete(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File f : fileOrDirectory.listFiles()) {
                if (!delete(f)) {
                    return false;
                }
            }
        }
        return !fileOrDirectory.exists() || fileOrDirectory.delete();
    }

    /**
     * Unzip content of input stream in directory.
     *
     * @param in
     *         zipped content
     * @param targetDir
     *         target directory
     * @throws IOException
     *         if any i/o error occurs
     */
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

}
