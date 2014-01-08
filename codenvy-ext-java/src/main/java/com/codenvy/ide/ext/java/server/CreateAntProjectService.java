/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package com.codenvy.ide.ext.java.server;

import com.codenvy.api.vfs.server.MountPoint;
import com.codenvy.api.vfs.server.VirtualFile;
import com.codenvy.api.vfs.server.VirtualFileSystemProvider;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.exceptions.InvalidArgumentException;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.shared.PropertyFilter;
import com.codenvy.api.vfs.shared.dto.Property;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Server service for creating projects.
 *
 * @author Andrey Plotnikov
 */
@Path("create-ant/{ws-name}")
public class CreateAntProjectService {
    @Inject
    VirtualFileSystemRegistry registry;

    @Path("project/java")
    @POST
    @Produces(APPLICATION_JSON)
    public void createJavaProject(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name,
                                  List<Property> properties) throws VirtualFileSystemException, IOException {
        createProject(vfsId, name, properties, "conf/Simple_Ant_jar.zip");
    }

    @Path("project/spring")
    @POST
    @Produces(APPLICATION_JSON)
    public void createSpringProject(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name,
                                    List<Property> properties) throws VirtualFileSystemException, IOException {
        createProject(vfsId, name, properties, "conf/Simple_Ant_Spring.zip");
    }

    private void createProject(String vfsId, String name, List<Property> properties, String templatePath)
            throws VirtualFileSystemException, IOException {
        VirtualFileSystemProvider provider = registry.getProvider(vfsId);
        MountPoint mountPoint = provider.getMountPoint(false);
        VirtualFile root = mountPoint.getRoot();
        VirtualFile projectFolder = root.createFolder(name);
        InputStream templateStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(templatePath);
        if (templateStream == null) {
            throw new InvalidArgumentException("Can't find " + templatePath);
        }
        projectFolder.unzip(templateStream, true);
        updateProperties(properties, projectFolder);
    }

    private void updateProperties(List<Property> properties, VirtualFile projectFolder)
            throws VirtualFileSystemException {
        List<Property> propertyList = projectFolder.getProperties(PropertyFilter.ALL_FILTER);
        propertyList.addAll(properties);
        projectFolder.updateProperties(propertyList, null);
    }


}