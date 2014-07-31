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
package com.codenvy.ide.ext.java.server.projecttypes;

import com.codenvy.api.core.ForbiddenException;
import com.codenvy.api.core.ServerException;
import com.codenvy.api.project.server.FileEntry;
import com.codenvy.api.project.server.Project;
import com.codenvy.api.project.server.ValueProviderFactory;
import com.codenvy.api.project.shared.ValueProvider;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link com.codenvy.api.project.server.ValueProviderFactory} implementation for 'builder.ant.source_folders' attribute.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class AntSourceFoldersValueProviderFactory implements ValueProviderFactory {

    @Override
    public String getName() {
        return "builder.ant.source_folders";
    }

    @Override
    public ValueProvider newInstance(final Project project) {
        return new ValueProvider() {
            @Override
            public List<String> getValues() {
                final List<String> list = new LinkedList<>();
                FileEntry buildDescriptor = null;
                try {
                    buildDescriptor = (FileEntry)project.getBaseFolder().getChild("build.xml");
                } catch (ForbiddenException | ServerException ignored) {
                }
                if (buildDescriptor != null) {
                    list.addAll(getAntSourceFolders(buildDescriptor));
                }
                return list;
            }

            @Override
            public void setValues(List<String> strings) {
                // nothing to do
            }
        };
    }

    private List<String> getAntSourceFolders(FileEntry buildXml) {
        final String defaultSourceDirectoryPath = "src";
        List<String> list = new ArrayList<>(1);
        list.add(defaultSourceDirectoryPath);
        return list;
    }

}
