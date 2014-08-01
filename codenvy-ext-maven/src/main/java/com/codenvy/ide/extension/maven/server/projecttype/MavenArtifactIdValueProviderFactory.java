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

import com.codenvy.api.core.ConflictException;
import com.codenvy.api.core.ForbiddenException;
import com.codenvy.api.core.ServerException;
import com.codenvy.api.project.server.FileEntry;
import com.codenvy.api.project.server.Project;
import com.codenvy.api.project.server.ValueProviderFactory;
import com.codenvy.api.project.server.VirtualFileEntry;
import com.codenvy.api.project.shared.ValueProvider;
import com.codenvy.ide.extension.maven.shared.MavenAttributes;
import com.codenvy.ide.maven.tools.MavenUtils;

import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Evgen Vidolob
 */
public class MavenArtifactIdValueProviderFactory implements ValueProviderFactory {
    private static final Logger LOG = LoggerFactory.getLogger(MavenArtifactIdValueProviderFactory.class);

    @Override
    public String getName() {
        return MavenAttributes.MAVEN_ARTIFACT_ID;
    }

    @Override
    public ValueProvider newInstance(final Project project) {
        return new ValueProvider() {
            @Override
            public List<String> getValues() {
                final List<String> list = new LinkedList<>();
                try {
                    final VirtualFileEntry pomFile = project.getBaseFolder().getChild("pom.xml");
                    if (pomFile != null && pomFile.isFile()) {
                        try (InputStream input = ((FileEntry)pomFile).getInputStream()) {
                            list.add(MavenUtils.readModel(input).getArtifactId());
                        }
                    }
                } catch (ForbiddenException | ServerException | IOException e) {
                    LOG.error(e.getMessage(), e);
                }
                return list;
            }

            @Override
            public void setValues(List<String> value) {
                if (value.isEmpty()) {
                    throw new IllegalStateException("Maven ArtifactId can't be empty.");
                }
                if (value.size() > 1) {
                    throw new IllegalStateException("Maven ArtifactId must be only one value.");
                }
                try {
                    VirtualFileEntry pomFile = project.getBaseFolder().getChild("pom.xml");
                    Model model;
                    if (pomFile != null) {
                        if (!pomFile.isFile()) {
                            throw new IllegalStateException(
                                    String.format("Unable to set ArtifactId. Path %s exists but is not a file.", pomFile.getPath()));
                        }
                        try (InputStream input = ((FileEntry)pomFile).getInputStream()) {
                            model = MavenUtils.readModel(input);
                        }
                    } else {
                        model = new Model();
                        model.setModelVersion("4.0.0");
                        MavenProjectGenerator.generateProjectStructure(project.getBaseFolder());
                        pomFile = project.getBaseFolder().createFile("pom.xml", new byte[0], "text/xml");
                    }
                    model.setArtifactId(value.get(0));
                    MavenUtils.writeModel(model, pomFile.getVirtualFile());
                } catch (ForbiddenException | ServerException | ConflictException | IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
    }
}
