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
package com.codenvy.ide.ext.extruntime.server;

import com.codenvy.ide.ext.extruntime.shared.ApplicationInstance;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.exoplatform.ide.vfs.impl.fs.LocalFSMountStrategy;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.ProjectImpl;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.exoplatform.ide.vfs.shared.PropertyFilter.ALL_FILTER;

/**
 * RESTful front-end for {@link ExtensionLauncher}.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtensionRuntimeService.java Jul 3, 2013 3:21:23 PM azatsarynnyy $
 */
@Path("{ws-name}/extruntime")
public class ExtensionRuntimeService {
    private static final Log LOG = ExoLogger.getLogger(ExtensionRuntimeService.class);

    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    @Inject
    private LocalFSMountStrategy fsMountStrategy;

    @Inject
    private ExtensionLauncher launcher;

    /**
     * Create empty Codenvy extension project.
     *
     * @param vfsId      identifier of virtual file system
     * @param name       name of the newly created project
     * @param rootId     identifier of parent folder for new project
     * @param properties properties to set to project
     * @throws VirtualFileSystemException if any error occurred in VFS
     * @throws IOException                if any error occurred while input-output operations
     */
    @Path("createempty")
    @POST
    public void createEmptyCodenvyExtensionProject(@QueryParam("vfsid") String vfsId,
                                                   @QueryParam("name") String name,
                                                   @QueryParam("rootid") String rootId,
                                                   List<Property> properties) throws VirtualFileSystemException,
            IOException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        InputStream templateStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("EmptyExtension.zip");
        createProject(vfs, name, rootId, templateStream, properties);
    }

    /**
     * Create sample Codenvy extension project.
     *
     * @param vfsId      identifier of virtual file system
     * @param name       name of the newly created project
     * @param rootId     identifier of parent folder for new project
     * @param properties properties to set to project
     * @param groupId    project's groupID
     * @param artifactId project's artifactId
     * @param version    project's version
     * @throws VirtualFileSystemException if any error occurred in VFS
     * @throws IOException                if any error occurred while input-output operations
     */
    @Path("createsample")
    @POST
    public void createSampleCodenvyExtensionProject(@QueryParam("vfsid") String vfsId,
                                                    @QueryParam("name") String name,
                                                    @QueryParam("rootid") String rootId,
                                                    List<Property> properties,
                                                    @QueryParam("groupid") String groupId,
                                                    @QueryParam("artifactid") String artifactId,
                                                    @QueryParam("version") String version) throws VirtualFileSystemException,
            IOException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        InputStream templateStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("GistExtensionSample.zip");
        createProject(vfs, name, rootId, templateStream, properties);

        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        MavenXpp3Writer pomWriter = new MavenXpp3Writer();

        File pomFile = (File) vfs.getItemByPath(name + "/pom.xml", null, false, PropertyFilter.NONE_FILTER);
        InputStream pomContent = vfs.getContent(pomFile.getId()).getStream();
        try {
            Model pom = pomReader.read(pomContent, false);
            pom.setGroupId(groupId);
            pom.setArtifactId(artifactId);
            pom.setVersion(version);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            pomWriter.write(stream, pom);
            vfs.updateContent(pomFile.getId(), MediaType.valueOf(pomFile.getMimeType()), new ByteArrayInputStream(stream.toByteArray()),
                    null);
        } catch (XmlPullParserException e) {
            LOG.warn("Error occurred while setting maven project coordinates.", e);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Launch Codenvy application with a custom extension.
     *
     * @param vfsId     identifier of virtual file system
     * @param projectId identifier of project we want to launch
     * @return launched application description
     * @throws VirtualFileSystemException if any error occurred in VFS
     * @throws ExtensionLauncherException if any error occurred while launching an extension
     */
    @Path("launch")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationInstance launch(@QueryParam("vfsid") String vfsId,
                                      @QueryParam("projectid") String projectId) throws VirtualFileSystemException,
            ExtensionLauncherException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        return launcher.launch(vfs, projectId, fsMountStrategy.getMountPath().getPath());
    }

    /**
     * Get logs of launched Codenvy application.
     *
     * @param appId id of Codenvy application to get its logs
     * @return retrieved logs
     * @throws ExtensionLauncherException if any error occurred while getting logs
     */
    @Path("logs/{appid}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String logs(@PathParam("appid") String appId) throws ExtensionLauncherException {
        return launcher.getLogs(appId);
    }

    /**
     * Stop previously launched Codenvy application.
     *
     * @param appId identifier of Codenvy application to stop
     * @throws ExtensionLauncherException if error occurred while stopping an application
     */
    @Path("stop/{appid}")
    @GET
    public void stop(@PathParam("appid") String appId) throws ExtensionLauncherException {
        launcher.stopApp(appId);
    }

    private void createProject(VirtualFileSystem vfs, String name, String rootId, InputStream template, List<Property> properties) throws VirtualFileSystemException, IOException {
        if (template == null) {
            throw new InvalidArgumentException("Can't find project template.");
        }

        Folder projectFolder = vfs.createFolder(rootId, name);

        vfs.importZip(projectFolder.getId(), template, true);
        updateProperties(name, properties, vfs, projectFolder);
    }

    private void updateProperties(String name, List<Property> properties, VirtualFileSystem vfs, Folder projectFolder) throws VirtualFileSystemException {
        Item projectItem = vfs.getItem(projectFolder.getId(), false, ALL_FILTER);
        if (projectItem instanceof ProjectImpl) {
            Project project = (Project) projectItem;
            vfs.updateItem(project.getId(), properties, null);
        } else {
            throw new IllegalStateException("Something other than project was created on " + name);
        }
    }

}
