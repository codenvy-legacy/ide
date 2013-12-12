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

import com.codenvy.ide.api.extension.SDK;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.resources.model.*;
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
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
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
     * Reads already stored project. Model Provider will be invoked to deserialized Project Model corresponding to
     * Primary Nature stored in Project's Properties.
     *
     * @param name
     * @param callback
     */
    public void getProject(String name, AsyncCallback<Project> callback);

    /**
     * Creates new empty project based on provided properties (Project Description).
     *
     * @param name
     * @param properties
     * @param callback
     */
    public void createProject(String name, Array<Property> properties, AsyncCallback<Project> callback);


    /**
     * Experimental and to be changed: List all projects stored on vfs
     *
     * @param callback
     */
    public void listProjects(AsyncCallback<String> callback);

    /**
     * Reads already stored projects and shows them in project explorer.
     *
     */
    public void showListProjects();

    /**
     * Registers ModelProvider instance for given Primary Project Nature
     *
     * @param primaryNature
     * @param modelProvider
     */
    public void registerModelProvider(String primaryNature, ModelProvider modelProvider);

    /**
     * Retrieves Model Provider instance for given Primary Nature, of Generic Model Provider is none found.
     *
     * @param primaryNature
     * @return
     */
    public ModelProvider getModelProvider(String primaryNature);

    /**
     * Register Nature
     *
     * @param nature
     */
    public void registerNature(ProjectNature nature);

    /**
     * Get nature by ID
     *
     * @param natureId
     * @return
     */
    public ProjectNature getNature(String natureId);

    /**
     * Apply nature to the given Project. Project Description will flushed to VFS immediately.
     *
     * @param project
     * @param natureId
     * @param callback
     */
    public void applyNature(Project project, String natureId, AsyncCallback<Project> callback);

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
     * Returns vfs id.
     *
     * @return
     */
    public String getVfsId();

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