/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api.resources;

import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.extension.SDK;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
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

    /**
     * Refreshes root folder
     */
    public void refreshRoot();

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
     * Returns root folder.
     *
     * @return
     */
    public Folder getRoot();

    /**
     * Returns ID of root folder.
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