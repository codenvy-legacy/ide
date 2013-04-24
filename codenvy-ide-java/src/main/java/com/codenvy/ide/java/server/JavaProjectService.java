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
package com.codenvy.ide.java.server;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.Property;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@Path("ide")
public class JavaProjectService {
    @Inject
    VirtualFileSystemRegistry registry;

    @Inject
    EventListenerList eventListenerList;

    @Path("create/project/java")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Project createJavaProject(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name,
                                     @QueryParam("source") String source, List<Property> properties)
            throws VirtualFileSystemException {

        VirtualFileSystem vfs = registry.getProvider(vfsId).newInstance(null, eventListenerList);
        Project project =
                vfs.createProject(vfs.getInfo().getRoot().getId(), name, "deprecated.project.type", properties);
        String projectId = project.getId();

        // create ReadMe.txt file
        String readMeContent = "This file was auto created when you created this project.";
        InputStream readMeIS = new ByteArrayInputStream(readMeContent.getBytes());
        vfs.createFile(projectId, "Readme.txt", MediaType.TEXT_PLAIN_TYPE, readMeIS);

        Folder sourceFolder = vfs.createFolder(projectId, source);

        String javaFileContent = "\n" +
                                 "public class HelloWorld{\n" +
                                 "   public static void main(String args[]){\n" +
                                 "      System.out.println(\"Hello World!\");\n" +
                                 "   }\n" +
                                 "}";
        InputStream javaFileIS = new ByteArrayInputStream(javaFileContent.getBytes());
        vfs.createFile(sourceFolder.getId(), "HelloWorld.java", MediaType.TEXT_PLAIN_TYPE, javaFileIS);

        return project;
    }
}