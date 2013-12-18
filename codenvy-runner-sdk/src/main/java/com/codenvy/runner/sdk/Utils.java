/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.runner.sdk;

import com.codenvy.ide.commons.GwtXmlUtils;
import com.codenvy.ide.commons.MavenUtils;

import org.apache.maven.model.Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * A smattering of useful methods.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: Utils.java Dec 16, 2013 11:30:14 AM azatsarynnyy $
 */
class Utils {
    /** Not instantiable. */
    private Utils() {
    }

    /** Returns URL to get Tomcat binary distribution. */
    static URL getTomcatBinaryDistribution() throws IOException {
        URL tomcatDistributionUrl = Thread.currentThread().getContextClassLoader().getResource("tomcat.zip");
        if (tomcatDistributionUrl == null) {
            throw new IOException("Unable to get Tomcat binary distribution.");
        }
        return tomcatDistributionUrl;
    }

    /** Returns URL to get Codenvy Platform binary distribution. */
    static URL getCodenvyPlatformBinaryDistribution() throws IOException {
        URL codenvyPlatformDistributionUrl =
                Thread.currentThread().getContextClassLoader().getResource("CodenvyPlatform.zip");
        if (codenvyPlatformDistributionUrl == null) {
            throw new IOException("Unable to get Codenvy Platform binary distribution.");
        }
        return codenvyPlatformDistributionUrl;
    }

    static String getMavenExecCommand() {
        final File mvnHome = getMavenHome();
        if (mvnHome != null) {
            final String mvn = "bin" + File.separatorChar + "mvn";
            return new File(mvnHome, mvn).getAbsolutePath(); // use Maven home directory if it's set
        } else {
            return "mvn"; // otherwise 'mvn' should be in PATH variable
        }
    }

    static File getMavenHome() {
        final String m2HomeEnv = System.getenv("M2_HOME");
        if (m2HomeEnv == null) {
            return null;
        }
        final File m2Home = new File(m2HomeEnv);
        return m2Home.exists() ? m2Home : null;
    }

    static void unzip(File zip, Path dirRelPathToUnzip, File targetDir) throws IOException {
        ZipInputStream zipIn = null;
        FileInputStream in = new FileInputStream(zip);
        try {
            zipIn = new ZipInputStream(in);
            byte[] b = new byte[8192];
            ZipEntry zipEntry;
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                if (Paths.get(zipEntry.getName()).toString().equals(dirRelPathToUnzip.toString()) ||
                    !zipEntry.getName().startsWith(dirRelPathToUnzip.toString())) {
                    continue;
                }
                final String relName = zipEntry.getName().substring(dirRelPathToUnzip.toString().length() + 1);
                File file = new File(targetDir, relName);
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

    static ExtensionDescriptor getExtensionFromJarFile(ZipFile zipFile) throws IOException {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        ZipEntry gwtXmlEntry = null;
        ZipEntry pomEntry = null;
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (!entry.isDirectory()) {
                if (entry.getName().endsWith(GwtXmlUtils.GWT_MODULE_XML_SUFFIX)) {
                    gwtXmlEntry = entry;
                } else if (entry.getName().endsWith("pom.xml")) {
                    pomEntry = entry;
                }
            }
        }

        // TODO: consider Codenvy extensions validator
        if (gwtXmlEntry == null || pomEntry == null) {
            throw new IllegalArgumentException(zipFile.getName() + " is not a valid Codenvy extension");
        }

        String gwtModuleName = gwtXmlEntry.getName().replace(File.separatorChar, '.');
        gwtModuleName = gwtModuleName.substring(0, gwtModuleName.length() - GwtXmlUtils.GWT_MODULE_XML_SUFFIX.length());
        final Model pom = MavenUtils.readPom(zipFile.getInputStream(pomEntry));
        zipFile.close();
        final String groupId = pom.getGroupId() == null ? pom.getParent().getGroupId() : pom.getGroupId();
        final String version = pom.getVersion() == null ? pom.getParent().getVersion() : pom.getVersion();
        return new ExtensionDescriptor(gwtModuleName, groupId, pom.getArtifactId(), version);
    }

    static class ExtensionDescriptor {
        String gwtModuleName;
        String groupId;
        String artifactId;
        String version;

        ExtensionDescriptor(String gwtModuleName, String groupId, String artifactId, String version) {
            this.gwtModuleName = gwtModuleName;
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
        }
    }
}
