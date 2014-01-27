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
package com.codenvy.ide.ext.java.server.projecttypes;

import com.codenvy.api.vfs.server.MountPoint;
import com.codenvy.api.vfs.server.VirtualFile;
import com.codenvy.api.vfs.server.VirtualFileSystemProvider;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.exceptions.InvalidArgumentException;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.io.InputStream;


/**
 * REST-service to unzip project templates.
 *
 * @author Artem Zatsarynnyy
 */
@Path("create-java/{ws-name}")
public class CreateProjectFromTemplateService {
    @Inject
    VirtualFileSystemRegistry registry;

    @Path("template/maven-jar")
    @POST
    public void unzipMavenJarTemplate(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name)
            throws VirtualFileSystemException, IOException {
        unzipTemplate(vfsId, name, "conf/Simple_jar.zip");
    }

    @Path("template/maven-war")
    @POST
    public void unzipMavenWarTemplate(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name)
            throws VirtualFileSystemException, IOException {
        unzipTemplate(vfsId, name, "conf/Simple_war.zip");
    }

    @Path("template/maven-spring")
    @POST
    public void unzipMavenSpringTemplate(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name)
            throws VirtualFileSystemException, IOException {
        unzipTemplate(vfsId, name, "conf/Simple_spring.zip");
    }

    @Path("template/ant-jar")
    @POST
    public void unzipAntJarTemplate(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name)
            throws VirtualFileSystemException, IOException {
        unzipTemplate(vfsId, name, "conf/Simple_Ant_jar.zip");
    }

    @Path("template/ant-spring")
    @POST
    public void unzipAntSpringTemplate(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name)
            throws VirtualFileSystemException, IOException {
        unzipTemplate(vfsId, name, "conf/Simple_Ant_Spring.zip");
    }

    private void unzipTemplate(String vfsId, String projectName, String templatePath) throws VirtualFileSystemException, IOException {
        VirtualFileSystemProvider provider = registry.getProvider(vfsId);
        MountPoint mountPoint = provider.getMountPoint(false);
        VirtualFile root = mountPoint.getRoot();
        VirtualFile projectFolder = root.getChild(projectName);
        InputStream templateStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(templatePath);
        if (templateStream == null) {
            throw new InvalidArgumentException("Can't find " + templatePath);
        }
        projectFolder.unzip(templateStream, true);
    }
}