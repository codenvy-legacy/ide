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
package com.codenvy.ide.extension.maven.server.projecttype;

import com.codenvy.api.core.ForbiddenException;
import com.codenvy.api.core.ServerException;
import com.codenvy.api.project.server.FileEntry;
import com.codenvy.api.project.server.Project;
import com.codenvy.api.project.server.ValueProviderFactory;
import com.codenvy.api.project.shared.ValueProvider;
import com.codenvy.ide.maven.tools.MavenUtils;

import org.apache.maven.model.Model;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link ValueProviderFactory} implementation for 'builder.maven.source_folders' attribute.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class MavenSourceFoldersValueProviderFactory implements ValueProviderFactory {

    @Override
    public String getName() {
        return "builder.maven.source_folders";
    }

    @Override
    public ValueProvider newInstance(final Project project) {
        return new ValueProvider() {
            @Override
            public List<String> getValues() {
                final List<String> list = new LinkedList<>();
                try {
                    final FileEntry pomFile = (FileEntry)project.getBaseFolder().getChild("pom.xml");
                    if (pomFile != null) {
                        list.addAll(getSourceFolders(pomFile));
                    }
                } catch (ForbiddenException | ServerException | IOException ignored) {
                }
                return list;
            }

            @Override
            public void setValues(List<String> strings) {
                // nothing to do
            }
        };
    }

    private List<String> getSourceFolders(FileEntry pomXml) throws IOException, ServerException {
        final String defaultSourceDirectoryPath = "src/main/java";
        final String defaultTestSourceDirectoryPath = "src/test/java";

        final Model model = MavenUtils.readModel(pomXml.getInputStream());
        final List<String> list = MavenUtils.getSourceDirectories(model);
        if (list.isEmpty()) {
            list.add(defaultSourceDirectoryPath);
            list.add(defaultTestSourceDirectoryPath);
        }
        return list;
    }
}
