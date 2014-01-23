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

import com.codenvy.api.project.server.ValueProviderFactory;
import com.codenvy.api.project.shared.ValueProvider;
import com.codenvy.api.vfs.server.MountPoint;
import com.codenvy.api.vfs.server.VirtualFile;
import com.codenvy.api.vfs.server.VirtualFileSystemProvider;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.shared.dto.Project;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * //
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class BuilderValueProviderFactory implements ValueProviderFactory {

    @Inject
    private VirtualFileSystemRegistry registry;

    @Inject
    @Named("vfs.local.id")
    private String vfsId;

    @Override
    public String getName() {
        return "builder.name";
    }

    @Override
    public ValueProvider newInstance(final Project project) {
        return new ValueProvider() {
            @Override
            public List<String> getValues() {
                final List<String> list = new ArrayList<>(1);
                VirtualFileSystemProvider provider;
                try {
                    // TODO: get VFS ID
                    // final String vfsId = (String)EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_ID);
                    provider = registry.getProvider(vfsId);
                    MountPoint mountPoint = provider.getMountPoint(false);
                    VirtualFile root = mountPoint.getRoot();
                    VirtualFile projectFolder = root.getChild(project.getName());
                    VirtualFile pomFile = projectFolder.getChild("pom.xml");
                    if (pomFile != null) {
                        list.add("maven");
                    }
                } catch (VirtualFileSystemException e) {

                }

                return list;
            }

            @Override
            public void setValues(List<String> strings) {
                // Nothing to do
            }
        };
    }
}
