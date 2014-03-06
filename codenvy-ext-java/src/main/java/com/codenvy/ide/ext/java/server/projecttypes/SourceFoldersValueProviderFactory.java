/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.ext.java.server.projecttypes;

import com.codenvy.api.project.server.AbstractVirtualFileEntry;
import com.codenvy.api.project.server.Project;
import com.codenvy.api.project.server.ValueProviderFactory;
import com.codenvy.api.project.shared.ValueProvider;
import com.codenvy.api.vfs.server.VirtualFile;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.ide.maven.tools.MavenUtils;

import org.apache.maven.model.Model;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link ValueProviderFactory} implementation for 'folders.source' attribute.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class SourceFoldersValueProviderFactory implements ValueProviderFactory {

    @Inject
    private VirtualFileSystemRegistry registry;

    @Override
    public String getName() {
        return "folders.source";
    }

    @Override
    public ValueProvider newInstance(final Project project) {
        return new ValueProvider() {
            @Override
            public List<String> getValues() {
                final List<String> list = new ArrayList<>();
                AbstractVirtualFileEntry mavenBuildDescriptor = project.getBaseFolder().getChild("pom.xml");
                AbstractVirtualFileEntry antBuildDescriptor = project.getBaseFolder().getChild("build.xml");
                try {
                    if (mavenBuildDescriptor != null) {
                        list.addAll(getMavenSourceFolders(mavenBuildDescriptor.getVirtualFile()));
                    } else if (antBuildDescriptor != null) {
                        list.addAll(getAntSourceFolders(antBuildDescriptor.getVirtualFile()));
                    }
                } catch (VirtualFileSystemException | IOException e) {
                    throw new IllegalStateException(e);
                }
                return list;
            }

            @Override
            public void setValues(List<String> strings) {
                // Nothing to do
            }
        };
    }

    private List<String> getAntSourceFolders(VirtualFile buildXml) throws VirtualFileSystemException {
        final String defaultSourceDirectoryPath = "src";
        List<String> list = new ArrayList<>(1);
        list.add(defaultSourceDirectoryPath);
        return list;
    }

    private List<String> getMavenSourceFolders(VirtualFile pomXml) throws VirtualFileSystemException, IOException {
        final String defaultSourceDirectoryPath = "src/main/java";
        final String defaultTestSourceDirectoryPath = "src/main/java";

        Model model = MavenUtils.readModel(pomXml.getContent().getStream());
        List<String> list = MavenUtils.getSourceDirectories(model);
        if (list.isEmpty()) {
            list.add(defaultSourceDirectoryPath);
            list.add(defaultTestSourceDirectoryPath);
        }
        return list;
    }

}
