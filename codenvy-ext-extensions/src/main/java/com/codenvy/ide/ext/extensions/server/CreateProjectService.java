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
import com.codenvy.ide.annotations.NotNull;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.io.*;
import java.util.List;

import static com.codenvy.ide.ext.extensions.server.CreateProjectApplication.BASE_URL;

/**
 * Service for creating Codenvy extension projects.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: CreateProjectService.java Jul 3, 2013 3:21:23 PM azatsarynnyy $
 */
@Path("{ws-name}/extension/create")
public class CreateProjectService {
    private static final Log LOG = ExoLogger.getLogger(CreateProjectService.class);
    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    /**
     * Create empty Codenvy extension project.
     *
     * @param vfsId
     *         identifier of virtual file system
     * @param name
     *         name of the newly created project
     * @param properties
     *         properties to set to project
     * @throws VirtualFileSystemException
     *         if any error occurred in VFS
     * @throws IOException
     *         if any error occurred while input-output operations
     */
    @Path("empty")
    @POST
    public void createEmptyCodenvyExtensionProject(@QueryParam("vfsid") String vfsId,
                                                   @QueryParam("name") String name,
                                                   List<Property> properties) throws VirtualFileSystemException,
                                                                                     IOException {
        createProject(vfsId, name, properties, "templates/EmptyExtension.zip");
    }

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
        createProject(vfsId, name, properties, BASE_URL + "/gist-extension.zip");

        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        MavenXpp3Writer pomWriter = new MavenXpp3Writer();

        VirtualFileSystemProvider vfsProvider = vfsRegistry.getProvider(vfsId);
        MountPoint mountPoint = vfsProvider.getMountPoint(false);
        VirtualFile pomFile = mountPoint.getVirtualFile(name + "/pom.xml");
        InputStream pomContent = pomFile.getContent().getStream();

        try {
            Model pom = pomReader.read(pomContent, false);
            pom.setGroupId(groupId);
            pom.setArtifactId(artifactId);
            pom.setVersion(version);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            pomWriter.write(stream, pom);
            pomFile.updateContent(pomFile.getMediaType(), new ByteArrayInputStream(stream.toByteArray()), null);
        } catch (XmlPullParserException e) {
            LOG.warn("Error occurred while setting project coordinates.", e);
            throw new IllegalStateException(e.getMessage(), e);
        }
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