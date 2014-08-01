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
import com.codenvy.api.project.server.Project;
import com.codenvy.api.project.server.ValueProviderFactory;
import com.codenvy.api.project.server.VirtualFileEntry;
import com.codenvy.api.project.shared.ValueProvider;
import com.codenvy.ide.maven.tools.MavenUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOG = LoggerFactory.getLogger(MavenSourceFoldersValueProviderFactory.class);

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
                    final VirtualFileEntry pomFile = project.getBaseFolder().getChild("pom.xml");
                    if (pomFile != null && pomFile.isFile()) {
                        list.addAll(MavenUtils.getSourceDirectories(pomFile.getVirtualFile()));
                    }
                } catch (ForbiddenException | ServerException | IOException e) {
                    LOG.error(e.getMessage(), e);
                }
                return list;
            }

            @Override
            public void setValues(List<String> strings) {
                // nothing to do
            }
        };
    }
}
