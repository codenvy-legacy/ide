/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.sdk.tools;

import com.codenvy.commons.lang.IoUtil;
import com.codenvy.ide.commons.GwtXmlUtils;
import com.codenvy.ide.maven.tools.MavenUtils;

import org.apache.maven.model.Model;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Tool to integrate 3rd-party extensions to existing Codenvy IDE.
 * <p/>
 * This tool looks up all 3rd-party extensions in a special folder.
 * For every found extension:
 * <p/>
 * - get GWT-module name from gwt.xml descriptor;
 * <p/>
 * - get Maven artifact coordinates;
 * <p/>
 * - add collected info in IDE.gwt.xml and pom.xml.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
public class InstallExtension {
    public static final String IDE_GWT_XML_FILE_NAME       = "IDE.gwt.xml";
    /** CLI argument specifies the location of the directory that contains extensions to add. */
    public static final String EXT_DIR_PARAMETER           = "--extDir=";
    /** CLI argument specifies the location of the directory that contains resource files to re-build Codenvy IDE. */
    public static final String EXT_RESOURCES_DIR_PARAMETER = "--extResourcesDir=";
    /** Location of the directory that contains 3rd-party extensions. */
    public static       Path   extDirPath                  = null;
    /** Location of the directory that contains resource files to re-build Codenvy IDE. */
    public static       Path   extResourcesWorkDirPath     = null;

    public static void main(String[] args) throws IOException {
        for (String arg : args) {
            if (arg.startsWith(EXT_DIR_PARAMETER)) {
                extDirPath = Paths.get(arg.substring(EXT_DIR_PARAMETER.length()));
            } else if (arg.startsWith(EXT_RESOURCES_DIR_PARAMETER)) {
                final Path extResourcesDirPath = Paths.get(arg.substring(EXT_RESOURCES_DIR_PARAMETER.length()));
                final String tempDirName = "temp";
                extResourcesWorkDirPath = extResourcesDirPath.resolve(tempDirName);
                // delete working directory from previous build if it exist
                IoUtil.deleteRecursive(extResourcesWorkDirPath.toFile());
                Files.createDirectory(extResourcesWorkDirPath);
                IoUtil.copy(extResourcesDirPath.toFile(), extResourcesWorkDirPath.toFile(), new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return !(tempDirName.equals(name));
                    }
                });
            } else {
                System.err.println("Unknown flag: " + arg);
                System.exit(1);
            }
        }

        List<Extension> extensions = findExtensionsByPath(extDirPath);
        for (Extension extension : extensions) {
            MavenUtils.addDependency(extResourcesWorkDirPath.resolve("pom.xml").toFile(),
                                     extension.groupId,
                                     extension.artifactId,
                                     extension.artifactVersion,
                                     null);
            final Path ideGwtXmlPath = IoUtil.findFile(IDE_GWT_XML_FILE_NAME, extResourcesWorkDirPath.toFile()).toPath();
            GwtXmlUtils.inheritGwtModule(ideGwtXmlPath, extension.gwtModuleName);
        }
    }

    private static List<Extension> findExtensionsByPath(Path extDirPath) throws IOException {
        File[] files = extDirPath.toFile().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });

        List<Extension> extensions = new ArrayList<>();
        for (File file : files) {
            Extension extension = getExtensionFromFile(file.toPath());
            extensions.add(extension);
            System.out.println(String.format("Extension %s found", extension.gwtModuleName));
        }
        System.out.println(String.format("Found: %d extension(s)", extensions.size()));
        return extensions;
    }

    private static Extension getExtensionFromFile(Path zipPath) throws IOException {
        try (ZipFile zipFile = new ZipFile(zipPath.toString())) {
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
                // have both entries
                if (pomEntry != null && gwtXmlEntry != null) {
                    break;
                }
            }

            // TODO consider Codenvy extension validator
            if (gwtXmlEntry == null || pomEntry == null) {
                throw new IllegalArgumentException(String.format("%s is not a valid Codenvy extension", zipPath.toString()));
            }

            String gwtModuleName = gwtXmlEntry.getName().replace(File.separatorChar, '.');
            gwtModuleName = gwtModuleName.substring(0, gwtModuleName.length() - GwtXmlUtils.GWT_MODULE_XML_SUFFIX.length());
            Model pom = MavenUtils.readModel(zipFile.getInputStream(pomEntry));
            return new Extension(gwtModuleName, MavenUtils.getGroupId(pom), pom.getArtifactId(), MavenUtils.getVersion(pom));
        }
    }

    private static class Extension {
        String gwtModuleName;
        String groupId;
        String artifactId;
        String artifactVersion;

        Extension(String gwtModuleName, String groupId, String artifactId, String artifactVersion) {
            this.gwtModuleName = gwtModuleName;
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.artifactVersion = artifactVersion;
        }

        @Override
        public String toString() {
            return "Extension{" +
                   "gwtModuleName='" + gwtModuleName + '\'' +
                   ", groupId='" + groupId + '\'' +
                   ", artifactId='" + artifactId + '\'' +
                   ", artifactVersion='" + artifactVersion + '\'' +
                   '}';
        }
    }
}
