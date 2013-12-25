/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
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
package com.codenvy.ide.ext.extensions.server;

import com.codenvy.api.vfs.server.MountPoint;
import com.codenvy.api.vfs.server.VirtualFile;
import com.codenvy.api.vfs.server.VirtualFileSystemProvider;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.exceptions.InvalidArgumentException;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.shared.PropertyFilter;
import com.codenvy.api.vfs.shared.dto.Property;
import com.codenvy.ide.maven.tools.MavenUtils;

import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Service for creating Codenvy extension projects.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: CreateProjectService.java Jul 3, 2013 3:21:23 PM azatsarynnyy $
 */
@Path("{ws-name}/extension/create")
public class CreateProjectService {
    private static final Logger LOG = LoggerFactory.getLogger(CreateProjectService.class);
    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    @Inject
    @Named("extension-url") // TODO(GUICE): better name ??
    private String baseUrl;

    /**
     * Create sample Codenvy extension project.
     *
     * @param vfsId
     *         identifier of virtual file system
     * @param name
     *         name of the newly created project
     * @param properties
     *         properties to set to project
     * @param groupId
     *         project's groupID
     * @param artifactId
     *         project's artifactId
     * @param version
     *         project's version
     * @throws VirtualFileSystemException
     *         if any error occurred in VFS
     * @throws IOException
     *         if any error occurred while input-output operations
     */
    @Path("sample")
    @POST
    public void createSampleCodenvyExtensionProject(@QueryParam("vfsid") String vfsId,
                                                    @QueryParam("name") String name,
                                                    List<Property> properties,
                                                    @QueryParam("groupid") String groupId,
                                                    @QueryParam("artifactid") String artifactId,
                                                    @QueryParam("version") String version)
            throws VirtualFileSystemException, IOException {
        createProject(vfsId, name, properties, baseUrl + "/gist-extension.zip");
        VirtualFileSystemProvider vfsProvider = vfsRegistry.getProvider(vfsId);
        MountPoint mountPoint = vfsProvider.getMountPoint(false);
        VirtualFile pomFile = mountPoint.getVirtualFile(name + "/pom.xml");
        Model pom;
        try (InputStream pomContent = pomFile.getContent().getStream()) {
            pom = MavenUtils.readModel(pomContent);
        }
        pom.setGroupId(groupId);
        pom.setArtifactId(artifactId);
        pom.setVersion(version);
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        MavenUtils.writeModel(pom, bOut);
        pomFile.updateContent(pomFile.getMediaType(), new ByteArrayInputStream(bOut.toByteArray()), null);
    }

    private void createProject(@NotNull String vfsId,
                               @NotNull String name,
                               @NotNull List<Property> properties,
                               @NotNull String templatePath) throws VirtualFileSystemException, IOException {
        if (templatePath == null || templatePath.isEmpty()) {
            throw new InvalidArgumentException("Can't find project template.");
        }

        VirtualFileSystemProvider provider = vfsRegistry.getProvider(vfsId);
        MountPoint mountPoint = provider.getMountPoint(false);
        VirtualFile root = mountPoint.getRoot();
        VirtualFile projectFolder = root.createFolder(name);
        InputStream templateStream = new FileInputStream(new File(templatePath));
        projectFolder.unzip(templateStream, true);
        updateProperties(properties, projectFolder);
    }

    private void updateProperties(@NotNull List<Property> properties, @NotNull VirtualFile projectFolder)
            throws VirtualFileSystemException {
        List<Property> propertyList = projectFolder.getProperties(PropertyFilter.ALL_FILTER);
        propertyList.addAll(properties);
        projectFolder.updateProperties(propertyList, null);
    }
}