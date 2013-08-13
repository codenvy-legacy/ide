/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.api.resources;

import com.codenvy.ide.api.extension.SDK;
import com.codenvy.ide.json.JsonArray;
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
    public void createProject(String name, JsonArray<Property> properties, AsyncCallback<Project> callback);


    /**
     * Experimental and to be changed: List all projects stored on vfs
     *
     * @param callback
     */
    public void listProjects(AsyncCallback<JsonArray<String>> callback);

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