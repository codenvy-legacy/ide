/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.extension.maven.server;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.shared.*;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.exoplatform.ide.vfs.shared.PropertyFilter.ALL_FILTER;

/**
 * Server service for creating projects.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Path("{ws-name}/maven/create")
public class CreateProjectService {
    @Inject
    VirtualFileSystemRegistry registry;
    @Inject
    EventListenerList         eventListenerList;

    @Path("project/java")
    @POST
    @Produces(APPLICATION_JSON)
    public void createJavaProject(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name, @QueryParam("rootId") String rootId,
                                  List<Property> properties) throws VirtualFileSystemException {
        VirtualFileSystem vfs = registry.getProvider(vfsId).newInstance(null, eventListenerList);
        Folder projectFolder = vfs.createFolder(rootId, name);

        InputStream templateStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/Simple_jar.zip");
        if (templateStream == null) {
            throw new InvalidArgumentException("Can't find Simple_jar.zip");
        }
        try {
            vfs.importZip(projectFolder.getId(), templateStream, true);
            updateProperties(name, properties, vfs, projectFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateProperties(String name, List<Property> properties, VirtualFileSystem vfs, Folder projectFolder)
            throws VirtualFileSystemException {
        Item projectItem = vfs.getItem(projectFolder.getId(), false, ALL_FILTER);
        if (projectItem instanceof ProjectImpl) {
            Project project = (Project)projectItem;
            vfs.updateItem(project.getId(), properties, null);
        } else {
            throw new IllegalStateException("Something other than project was created on " + name);
        }
    }

    @Path("project/war")
    @POST
    @Produces(APPLICATION_JSON)
    public void createWarProject(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name, @QueryParam("rootId") String rootId,
                                 List<Property> properties) throws VirtualFileSystemException {
        VirtualFileSystem vfs = registry.getProvider(vfsId).newInstance(null, eventListenerList);
        Folder projectFolder = vfs.createFolder(rootId, name);

        InputStream templateStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/Simple_war.zip");
        if (templateStream == null) {
            throw new InvalidArgumentException("Can't find Simple_war.zip");
        }
        try {
            vfs.importZip(projectFolder.getId(), templateStream, true);
            updateProperties(name, properties, vfs, projectFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Path("project/spring")
    @POST
    @Produces(APPLICATION_JSON)
    public void createSpringProject(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name, @QueryParam("rootId") String rootId,
                                    List<Property> properties) throws VirtualFileSystemException {
        VirtualFileSystem vfs = registry.getProvider(vfsId).newInstance(null, eventListenerList);
        Folder projectFolder = vfs.createFolder(rootId, name);

        InputStream templateStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/Simple_spring.zip");
        if (templateStream == null) {
            throw new InvalidArgumentException("Can't find Simple_spring.zip");
        }
        try {
            vfs.importZip(projectFolder.getId(), templateStream, true);
            updateProperties(name, properties, vfs, projectFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}