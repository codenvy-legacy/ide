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
package com.codenvy.ide.ext.tutorials.server;

import com.codenvy.ide.annotations.NotNull;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.*;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.exoplatform.ide.vfs.shared.PropertyFilter.ALL_FILTER;

/**
 * RESTful service for creating 'Tutorial' projects.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: TutorialsService.java Sep 13, 2013 3:21:23 PM azatsarynnyy $
 */
@Path("{ws-name}/tutorials")
public class TutorialsService {
    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    /**
     * Create 'DTO tutorial' project.
     *
     * @param vfsId
     *         identifier of virtual file system
     * @param name
     *         name of the newly created project
     * @param rootId
     *         identifier of parent folder for the new project
     * @param properties
     *         properties to set to project
     * @throws VirtualFileSystemException
     *         if any error occurred in VFS
     * @throws IOException
     *         if any error occurred while input-output operations
     */
    @Path("dto")
    @POST
    public void createDTOTutorialProject(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name,
                                         @QueryParam("rootid") String rootId, List<Property> properties)
            throws VirtualFileSystemException, IOException {
        createProject(vfsId, "templates/DtoTutorial.zip", name, rootId, properties);
    }

    private void createProject(@NotNull String vfsId, @NotNull String templatePath, @NotNull String name, @NotNull String rootId,
                               @NotNull List<Property> properties) throws VirtualFileSystemException, IOException {
        InputStream templateStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(templatePath);
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        if (templateStream == null) {
            throw new InvalidArgumentException("Can't find project template.");
        }

        Folder projectFolder = vfs.createFolder(rootId, name);

        vfs.importZip(projectFolder.getId(), templateStream, true);
        updateProperties(name, properties, vfs, projectFolder);
    }

    private void updateProperties(@NotNull String name, @NotNull List<Property> properties, @NotNull VirtualFileSystem vfs,
                                  @NotNull Folder projectFolder)
            throws VirtualFileSystemException {
        Item projectItem = vfs.getItem(projectFolder.getId(), false, ALL_FILTER);
        if (projectItem instanceof ProjectImpl) {
            Project project = (Project)projectItem;
            vfs.updateItem(project.getId(), properties, null);
        } else {
            throw new IllegalStateException("Something other than project was created on " + name);
        }
    }

    /**
     * Create 'Notification tutorial' project.
     *
     * @param vfsId
     *         identifier of virtual file system
     * @param name
     *         name of the newly created project
     * @param rootId
     *         identifier of parent folder for the new project
     * @param properties
     *         properties to set to project
     * @throws VirtualFileSystemException
     *         if any error occurred in VFS
     * @throws IOException
     *         if any error occurred while input-output operations
     */
    @Path("notification")
    @POST
    public void createNotificationTutorialProject(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name,
                                                  @QueryParam("rootid") String rootId, List<Property> properties)
            throws VirtualFileSystemException, IOException {
        createProject(vfsId, "templates/NotificationTutorial.zip", name, rootId, properties);
    }

    /**
     * Create 'Action tutorial' project.
     *
     * @param vfsId
     *         identifier of virtual file system
     * @param name
     *         name of the newly created project
     * @param rootId
     *         identifier of parent folder for the new project
     * @param properties
     *         properties to set to project
     * @throws VirtualFileSystemException
     *         if any error occurred in VFS
     * @throws IOException
     *         if any error occurred while input-output operations
     */
    @Path("action")
    @POST
    public void createActionTutorialProject(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name,
                                            @QueryParam("rootid") String rootId, List<Property> properties)
            throws VirtualFileSystemException, IOException {
        createProject(vfsId, "templates/ActionTutorial.zip", name, rootId, properties);
    }

    /**
     * Create 'Wizard tutorial' project.
     *
     * @param vfsId
     *         identifier of virtual file system
     * @param name
     *         name of the newly created project
     * @param rootId
     *         identifier of parent folder for the new project
     * @param properties
     *         properties to set to project
     * @throws VirtualFileSystemException
     *         if any error occurred in VFS
     * @throws IOException
     *         if any error occurred while input-output operations
     */
    @Path("wizard")
    @POST
    public void createWizardTutorialProject(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name,
                                            @QueryParam("rootid") String rootId, List<Property> properties)
            throws VirtualFileSystemException, IOException {
        createProject(vfsId, "templates/WizardTutorial.zip", name, rootId, properties);
    }

    /**
     * Create 'New project wizard tutorial' project.
     *
     * @param vfsId
     *         identifier of virtual file system
     * @param name
     *         name of the newly created project
     * @param rootId
     *         identifier of parent folder for the new project
     * @param properties
     *         properties to set to project
     * @throws VirtualFileSystemException
     *         if any error occurred in VFS
     * @throws IOException
     *         if any error occurred while input-output operations
     */
    @Path("newproject")
    @POST
    public void createNewProjectWizardTutorialProject(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name,
                                                      @QueryParam("rootid") String rootId, List<Property> properties)
            throws VirtualFileSystemException, IOException {
        createProject(vfsId, "templates/NewProjectWizardTutorial.zip", name, rootId, properties);
    }

    /**
     * Create 'New resource wizard tutorial' project.
     *
     * @param vfsId
     *         identifier of virtual file system
     * @param name
     *         name of the newly created project
     * @param rootId
     *         identifier of parent folder for the new project
     * @param properties
     *         properties to set to project
     * @throws VirtualFileSystemException
     *         if any error occurred in VFS
     * @throws IOException
     *         if any error occurred while input-output operations
     */
    @Path("newresource")
    @POST
    public void createNewResourceWizardTutorialProject(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name,
                                                       @QueryParam("rootid") String rootId, List<Property> properties)
            throws VirtualFileSystemException, IOException {
        createProject(vfsId, "templates/NewResourceWizardTutorial.zip", name, rootId, properties);
    }

    /**
     * Create 'Parts tutorial' project.
     *
     * @param vfsId
     *         identifier of virtual file system
     * @param name
     *         name of the newly created project
     * @param rootId
     *         identifier of parent folder for the new project
     * @param properties
     *         properties to set to project
     * @throws VirtualFileSystemException
     *         if any error occurred in VFS
     * @throws IOException
     *         if any error occurred while input-output operations
     */
    @Path("parts")
    @POST
    public void createTutorialProject(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name,
                                      @QueryParam("rootid") String rootId, List<Property> properties)
            throws VirtualFileSystemException, IOException {
        createProject(vfsId, "templates/PartsTutorial.zip", name, rootId, properties);
    }
}