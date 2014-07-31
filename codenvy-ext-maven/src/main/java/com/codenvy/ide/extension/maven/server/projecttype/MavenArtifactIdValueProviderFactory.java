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
import com.codenvy.api.project.shared.ValueProvider;
import com.codenvy.ide.extension.maven.shared.MavenAttributes;
import com.codenvy.ide.maven.tools.MavenUtils;

import org.apache.maven.model.Model;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Evgen Vidolob
 */
public class MavenArtifactIdValueProviderFactory implements ValueProviderFactory {
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
                    final FileEntry pomFile = (FileEntry)project.getBaseFolder().getChild("pom.xml");
                    final Model model = MavenUtils.readModel(pomFile.getInputStream());
                    list.add(model.getArtifactId());
                } catch (ForbiddenException | ServerException | IOException ignored) {
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
                    FileEntry pomFile = (FileEntry)project.getBaseFolder().getChild("pom.xml");
                    Model model;
                    if (pomFile != null) {
                        model = MavenUtils.readModel(pomFile.getInputStream());
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
