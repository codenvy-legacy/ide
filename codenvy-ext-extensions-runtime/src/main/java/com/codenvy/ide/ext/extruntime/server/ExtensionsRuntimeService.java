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

import com.codenvy.api.vfs.server.MountPoint;
import com.codenvy.api.vfs.server.VirtualFile;
import com.codenvy.api.vfs.server.VirtualFileSystemProvider;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.exceptions.InvalidArgumentException;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.shared.PropertyFilter;
import com.codenvy.api.vfs.shared.dto.Property;
import com.codenvy.ide.ext.extruntime.server.builder.BuilderException;
import com.codenvy.ide.ext.extruntime.server.builder.ExtensionsBuilder;
import com.codenvy.ide.ext.extruntime.server.runner.ExtensionsRunner;
import com.codenvy.ide.ext.extruntime.server.runner.RunnerException;
import com.codenvy.ide.ext.extruntime.shared.ApplicationInstance;
import com.codenvy.vfs.impl.fs.LocalFSMountStrategy;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * RESTful front-end for {@link com.codenvy.ide.ext.extruntime.server.builder.ExtensionsBuilder} and {@link
 * com.codenvy.ide.ext.extruntime.server.runner.ExtensionsRunner}.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtensionsRuntimeService.java Jul 3, 2013 3:21:23 PM azatsarynnyy $
 */
@Path("{ws-name}/extruntime")
public class ExtensionsRuntimeService {
    private static final Log LOG = ExoLogger.getLogger(ExtensionsRuntimeService.class);
    @Inject
    private VirtualFileSystemRegistry vfsRegistry;
    @Inject
    private ExtensionsRunner          runner;
    @Inject
    private ExtensionsBuilder         builder;
    @Inject
    private LocalFSMountStrategy      fsMountStrategy;

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
    @Path("createempty")
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
    @Path("createsample")
    @POST
    public void createSampleCodenvyExtensionProject(@QueryParam("vfsid") String vfsId,
                                                    @QueryParam("name") String name,
                                                    List<Property> properties,
                                                    @QueryParam("groupid") String groupId,
                                                    @QueryParam("artifactid") String artifactId,
                                                    @QueryParam("version") String version)
            throws VirtualFileSystemException, IOException {
        createProject(vfsId, name, properties, "templates/GistExtensionSample.zip");

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

    /**
     * Build Codenvy extension project inside Codenvy Platform.
     *
     * @param vfsId
     *         identifier of virtual file system
     * @param projectId
     *         identifier of project we want to build
     * @param tomcatBundle
     *         whether to create Tomcat bundle or not
     * @return WAR download URL
     * @throws VirtualFileSystemException
     *         if an error occurs in VFS
     * @throws BuilderException
     *         if an error occurs while building an extension
     */
    @Path("build")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String build(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                        @QueryParam("bundle") boolean tomcatBundle)
            throws VirtualFileSystemException, BuilderException {
        VirtualFileSystemProvider vfsProvider = vfsRegistry.getProvider(vfsId);
        return builder.build(vfsProvider.getMountPoint(false), projectId, tomcatBundle);
    }

    /**
     * Run a specified WAR, that contains Codenvy Platform with (or without) any extension.
     * <p/>
     * Hot update ability is supported.
     *
     * @param warUrl
     *         URL to Codenvy Platform WAR
     * @param enableHotUpdate
     *         whether to enable the ability hot update or not
     * @param vfsId
     *         identifier of virtual file system (makes sense only when hot update is enabled)
     * @param projectId
     *         identifier of project we want to launch (makes sense only when hot update is enabled)
     * @return launched application description
     * @throws VirtualFileSystemException
     *         if an error occurs in VFS
     * @throws RunnerException
     *         if an error occurs while launching an extension
     */
    @Path("run")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationInstance launch(@QueryParam("warUrl") String warUrl,
                                      @QueryParam("hotupdate") boolean enableHotUpdate,
                                      @QueryParam("vfsid") String vfsId,
                                      @QueryParam("projectid") String projectId) throws VirtualFileSystemException,
                                                                                        RunnerException {
        VirtualFileSystemProvider provider = vfsRegistry.getProvider(vfsId);
        return runner.run(warUrl, enableHotUpdate, provider.getMountPoint(false), projectId,
                          fsMountStrategy.getMountPath().getPath());
    }

    /**
     * Get logs of launched Codenvy application.
     *
     * @param appId
     *         id of Codenvy application to get its logs
     * @return retrieved logs
     * @throws RunnerException
     *         if an error occurs while getting application's logs
     */
    @Path("logs/{appid}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String logs(@PathParam("appid") String appId) throws RunnerException {
        return runner.getLogs(appId);
    }

    /**
     * Stop previously launched Codenvy application.
     *
     * @param appId
     *         identifier of Codenvy application to stop
     * @throws RunnerException
     *         if an error occurs while stopping an application
     */
    @Path("stop/{appid}")
    @GET
    public void stop(@PathParam("appid") String appId) throws RunnerException {
        runner.stopApp(appId);
    }

    private void createProject(String vfsId, String name,
                               List<Property> properties, String templatePath) throws VirtualFileSystemException,
                                                                                      IOException {
        if (templatePath == null || templatePath.isEmpty()) {
            throw new InvalidArgumentException("Can't find project template.");
        }

        VirtualFileSystemProvider provider = vfsRegistry.getProvider(vfsId);
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
