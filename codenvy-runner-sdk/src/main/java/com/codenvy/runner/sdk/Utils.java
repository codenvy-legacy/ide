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
package com.codenvy.runner.sdk;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.core.util.DownloadPlugin;
import com.codenvy.api.core.util.HttpDownloadPlugin;
import com.codenvy.api.core.util.LineConsumer;
import com.codenvy.api.core.util.ProcessUtil;
import com.codenvy.api.core.util.ValueHolder;
import com.codenvy.api.project.server.Constants;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.commons.lang.IoUtil;
import com.codenvy.ide.commons.GwtXmlUtils;
import com.codenvy.ide.maven.tools.MavenUtils;

import org.apache.maven.model.Model;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * A smattering of useful methods.
 *
 * @author Artem Zatsarynnyy
 */
class Utils {
    private static DownloadPlugin downloadPlugin = new HttpDownloadPlugin();

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
        URL codenvyPlatformDistributionUrl = Thread.currentThread().getContextClassLoader().getResource("CodenvyPlatform.zip");
        if (codenvyPlatformDistributionUrl == null) {
            throw new IOException("Unable to get Codenvy Platform binary distribution.");
        }
        return codenvyPlatformDistributionUrl;
    }

    /** Download project to the specified destination folder. */
    static java.io.File exportProject(ProjectDescriptor projectDescriptor, java.io.File destinationFolder) throws IOException {
        List<Link> projectLinks = projectDescriptor.getLinks();
        final Link exportZipLink = getLinkByRel(Constants.LINK_REL_EXPORT_ZIP, projectLinks);

        final ValueHolder<IOException> errorHolder = new ValueHolder<>();
        final ValueHolder<java.io.File> resultHolder = new ValueHolder<>();
        downloadPlugin.download(exportZipLink.getHref(), destinationFolder, new DownloadPlugin.Callback() {
            @Override
            public void done(java.io.File downloaded) {
                resultHolder.set(downloaded);
            }

            @Override
            public void error(IOException e) {
                errorHolder.set(e);
            }
        });
        final IOException ioError = errorHolder.get();
        if (ioError != null) {
            throw ioError;
        }
        return resultHolder.get();
    }

    private static Link getLinkByRel(String rel, List<Link> links) {
        for (Link link : links) {
            if (rel.equals(link.getRel())) {
                return link;
            }
        }
        return null;
    }

    /**
     * Builds project with Maven from the specified sources.
     *
     * @param sourcesPath
     *         path to the folder that contains project sources to build
     * @param artifactNamePattern
     *         name pattern of the artifact to return
     * @return {@link java.util.zip.ZipFile} that represents a built artifact
     */
    static ZipFile buildProjectFromSources(Path sourcesPath, String artifactNamePattern) throws Exception {
        final String[] command = new String[]{MavenUtils.getMavenExecCommand(), "clean", "package"};
        ProcessBuilder processBuilder = new ProcessBuilder(command).directory(sourcesPath.toFile());
        Process process = processBuilder.start();
        ProcessLineConsumer consumer = new ProcessLineConsumer();
        ProcessUtil.process(process, consumer, consumer);
        process.waitFor();
        if (process.exitValue() != 0) {
            throw new Exception(consumer.getOutput().toString());
        }
        return new ZipFile(IoUtil.findFile(artifactNamePattern, sourcesPath.resolve("target").toFile()));
    }

    /**
     * Read extension descriptor from the specified JAR.
     *
     * @param zipFile
     *         JAR file with Codenvy Extension
     * @return {@link ExtensionDescriptor}
     * @throws IOException
     *         if can not read specified JAR file
     * @throws IllegalArgumentException
     *         if specified JAR does not contains a valid Codenvy Extension
     */
    static ExtensionDescriptor getExtensionFromJarFile(ZipFile zipFile) throws IOException {
        try {
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

            // TODO: consider Codenvy extensions validator
            if (gwtXmlEntry == null || pomEntry == null) {
                throw new IllegalArgumentException(String.format("%s is not a valid Codenvy Extension", zipFile.getName()));
            }

            String gwtModuleName = gwtXmlEntry.getName().replace(java.io.File.separatorChar, '.');
            gwtModuleName = gwtModuleName.substring(0, gwtModuleName.length() - GwtXmlUtils.GWT_MODULE_XML_SUFFIX.length());
            Model pom = MavenUtils.readModel(zipFile.getInputStream(pomEntry));
            return new ExtensionDescriptor(gwtModuleName, MavenUtils.getGroupId(pom), pom.getArtifactId(), MavenUtils.getVersion(pom));
        } finally {
            zipFile.close();
        }
    }

    private static class ProcessLineConsumer implements LineConsumer {
        final StringBuilder output = new StringBuilder();

        @Override
        public void writeLine(String line) throws IOException {
            output.append('\n').append(line);
        }

        @Override
        public void close() throws IOException {
            //nothing to close
        }

        StringBuilder getOutput() {
            return output;
        }
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
