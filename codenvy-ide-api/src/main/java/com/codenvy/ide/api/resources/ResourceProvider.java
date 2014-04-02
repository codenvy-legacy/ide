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
package com.codenvy.ide.api.resources;

import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.extension.SDK;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Public interface of Resources API is presented by {@link ResourceProvider}. This class is designed to provide
 * an access to IDE resources, such as Files, Folders, Projects and derivatives entities. Project is the root
 * entry point that provides an access to actual resources. <br/>
 * <p/>
 * Project encapsulates the model of the resources used in it. It can be classic Files and Folders, as well as
 * custom Package instances, the derivatives of the Folder, custom RubyScript instance, the derivatives of the
 * File. Concrete model of the Project is called Project Model and it is presented by Project class. <br/>
 * <p/>
 * Project Models are created in relation to Project Primary nature. When project is created from scratch,
 * primary nature is retrieved from properties provided, then corresponding ModelProvider invoked to create
 * empty Project that is initialized with JSon data retrieved from REST VFS service.
 *
 * @author Nikolay Zamosenchuk
 */
@SDK(title = "ide.api.resource")
public interface ResourceProvider {
    /**
     * Returns active project or null if none opened
     *
     * @return active project or null if none.
     */
    public Project getActiveProject();

    /**
     * Sets the active project.
     *
     * @param project
     *         active project
     */
    public void setActiveProject(Project project);

    /**
     * Reads already stored project. Model Provider will be invoked to deserialized
     * Project Model corresponding to the value of the project's attribute 'language'.
     *
     * @param name
     * @param callback
     */
    public void getProject(String name, AsyncCallback<Project> callback);

    /**
     * Creates new project based on provided {@link ProjectDescriptor}.
     *
     * @param name
     * @param projectDescriptor
     * @param callback
     */
    public void createProject(String name, ProjectDescriptor projectDescriptor, AsyncCallback<Project> callback);

    /** Reads already stored projects and shows them in project explorer. */
    public void showListProjects();

    /**
     * Registers {@link ModelProvider} instance for the given language.
     *
     * @param language
     * @param modelProvider
     */
    public void registerModelProvider(String language, ModelProvider modelProvider);

    /**
     * Returns {@link ModelProvider} instance for the given language or Generic Model Provider if none was found.
     *
     * @param language
     * @return
     */
    public ModelProvider getModelProvider(String language);

    /**
     * Register file type.
     *
     * @param fileType
     */
    public void registerFileType(FileType fileType);

    /**
     * Get file type matched for file
     *
     * @param file
     * @return
     */
    public FileType getFileType(File file);


    /**
     * Returns root folder's id.
     *
     * @return
     */
    public Folder getRoot();

    /**
     * Returns root folder's id.
     *
     * @return
     */
    public String getRootId();

    /**
     * Delete resource item.
     *
     * @param item
     * @param callback
     */
    public void delete(Resource item, AsyncCallback<String> callback);
}