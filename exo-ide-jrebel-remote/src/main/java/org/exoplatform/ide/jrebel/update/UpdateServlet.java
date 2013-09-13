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
package org.exoplatform.ide.jrebel.update;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This servlet is created for updating remote application. It assumes application that should be updated deployed in
 * ROOT context and JRebel is configured to listen updates of application folder. At the moment usage of JRebel remote
 * plugin is difficult for us. It is required to have configuration files (rebel.xml and rebel-remote.xml) for each jar
 * we want to update without redeploy.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class UpdateServlet extends HttpServlet {
    protected File jrebelDir;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (jrebelDir != null) {
            File tmpDir = new File(System.getProperty("java.io.tmpdir"), req.getServerName() + System.currentTimeMillis());
            if (!tmpDir.mkdirs()) {
                throw new IOException("Unable create temporary folder. ");
            }
            try {
                unzip(req.getInputStream(), tmpDir);
                File classes = new File(jrebelDir, "/classpath/classes");
                copy(new File(tmpDir, "WEB-INF/classes"), classes, ANY_FILTER);
                deleteFiles(classes, req.getHeaders("x-exo-ide-classes-delete"));
                File lib = new File(jrebelDir, "/classpath/lib");
                copy(new File(tmpDir, "WEB-INF/lib"), lib, ANY_FILTER);
                deleteFiles(lib, req.getHeaders("x-exo-ide-lib-delete"));
                File web = new File(jrebelDir, "web");
                copy(tmpDir, web, new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return !(dir.getAbsolutePath().endsWith("WEB-INF/classes")
                                 || dir.getAbsolutePath().endsWith("WEB-INF/lib")
                                 || dir.getAbsolutePath().endsWith("META-INF/maven"));
                    }
                });
                deleteFiles(web, req.getHeaders("x-exo-ide-web-delete"));
            } finally {
                deleteRecursive(tmpDir);
            }
        }
    }

    private void deleteFiles(File baseDir, Enumeration relPaths) {
        if (relPaths != null) {
            while (relPaths.hasMoreElements()) {
                String relPath = (String)relPaths.nextElement();
                new File(baseDir, relPath).delete();
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (jrebelDir != null) {
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            PrintWriter w = resp.getWriter();
            File resourceDir = new File(jrebelDir, "/classpath/classes");
            int relFilePathOffset = resourceDir.getAbsolutePath().length() + 1;
            for (File f : list(resourceDir, ANY_FILTER)) {
                digest.reset();
                w.printf("%s %s\n", countFileHash(f, digest), f.getAbsolutePath().substring(relFilePathOffset));
            }
            w.println();
            resourceDir = new File(jrebelDir, "/classpath/lib");
            relFilePathOffset = resourceDir.getAbsolutePath().length() + 1;
            for (File f : list(resourceDir, ANY_FILTER)) {
                w.printf("%s %s\n", countFileHash(f, digest), f.getAbsolutePath().substring(relFilePathOffset));
            }
            w.println();
            resourceDir = new File(jrebelDir, "/web");
            relFilePathOffset = resourceDir.getAbsolutePath().length() + 1;
            for (File f : list(resourceDir, ANY_FILTER)) {
                w.printf("%s %s\n", countFileHash(f, digest), f.getAbsolutePath().substring(relFilePathOffset));
            }
            w.flush();
        }
    }

    public static String countFileHash(java.io.File file, MessageDigest digest) throws IOException {
        FileInputStream fis = null;
        DigestInputStream dis = null;
        byte[] b = new byte[8192];
        try {
            fis = new FileInputStream(file);
            dis = new DigestInputStream(fis, digest);
            while (dis.read(b) != -1) {
            }
            return toHex(digest.digest());
        } finally {
            if (dis != null) {
                dis.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }

    private static final char[] hex = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                                                 'e', 'f'};

    public static String toHex(byte[] hash) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
            b.append(hex[(hash[i] >> 4) & 0x0f]);
            b.append(hex[hash[i] & 0x0f]);
        }
        return b.toString();
    }

    public static boolean deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] list = fileOrDirectory.listFiles();
            if (list == null) {
                return false;
            }
            for (File f : list) {
                if (!deleteRecursive(f)) {
                    return false;
                }
            }
        }
        return !fileOrDirectory.exists() || fileOrDirectory.delete();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        doInit();
    }

    /**
     * Try to find directory with name 'jrebel'. Directory should exist in some level higher  than context root of this
     * servlet, e.g.
     * <p/>
     * <b>servlet context</b>&nbsp;&nbsp;-&nbsp;<i>some_path</i>/tomcat/webapps/my_app<br/>
     * <b>jrebel directory</b>&nbsp;-&nbsp;<i>some_path</i>/tomcat/jrebel
     */
    protected void doInit() {
        File app = new File(getServletConfig().getServletContext().getRealPath("/"));
        for (File parent = app.getParentFile(); parent != null && this.jrebelDir == null; parent = parent.getParentFile()) {
            File tmp = new File(parent, "jrebel");
            if (tmp.exists()) {
                this.jrebelDir = tmp;
            }
        }
    }

    public static List<File> list(File dir, FilenameFilter filter) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Not a directory. ");
        }
        List<File> files = new ArrayList<File>();
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
                    if (f.isDirectory()) {
                        q.push(f);
                    } else {
                        files.add(f);
                    }
                }
            }
        }
        return files;
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

    public static final FilenameFilter ANY_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return true;
        }
    };

    public static void copy(File source, File target, FilenameFilter filter) throws IOException {
        if (!source.exists()) {
            return;
        }
        if (source.isDirectory()) {
            String sourceRoot = source.getAbsolutePath();
            LinkedList<File> q = new LinkedList<File>();
            q.add(source);
            while (!q.isEmpty()) {
                File current = q.pop();
                File[] list = current.listFiles();
                if (list != null) {
                    for (File f : list) {
                        if (!filter.accept(current, f.getName())) {
                            continue;
                        }
                        File newFile = new File(target, f.getAbsolutePath().substring(sourceRoot.length() + 1));
                        if (f.isDirectory()) {
                            if (!(newFile.exists() || newFile.mkdirs())) {
                                throw new IOException("Unable create directory: " + newFile.getAbsolutePath());
                            }
                            q.push(f);
                        } else {
                            copyFile(f, newFile);
                        }
                    }
                }
            }
        } else {
            File parent = target.getParentFile();
            if (!(parent.exists() || parent.mkdirs())) {
                throw new IOException("Unable create directory: " + parent.getAbsolutePath());
            }
            copyFile(source, target);
        }
    }

    private static void copyFile(File source, File target) throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;
        byte[] b = new byte[8192];
        try {
            in = new FileInputStream(source);
            out = new FileOutputStream(target);
            int r;
            while ((r = in.read(b)) != -1) {
                out.write(b, 0, r);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
