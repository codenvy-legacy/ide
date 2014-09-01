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
package com.codenvy.ide.api.projecttree.generic;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.api.projecttree.AbstractTreeStructure;
import com.codenvy.ide.api.projecttree.TreeSettings;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

/**
 * {@link AbstractTreeStructure} for the hierarchical tree.
 *
 * @author Artem Zatsarynnyy
 */
public class GenericTreeStructure extends AbstractTreeStructure {
    protected ProjectDescriptor      project;
    protected EventBus               eventBus;
    protected EditorAgent            editorAgent;
    protected AppContext             appContext;
    protected ProjectServiceClient   projectServiceClient;
    protected DtoUnmarshallerFactory dtoUnmarshallerFactory;

    protected GenericTreeStructure(TreeSettings settings, ProjectDescriptor project, EventBus eventBus, EditorAgent editorAgent,
                                   AppContext appContext, ProjectServiceClient projectServiceClient,
                                   DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(settings);
        this.project = project;
        this.eventBus = eventBus;
        this.editorAgent = editorAgent;
        this.appContext = appContext;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void getRoots(AsyncCallback<Array<AbstractTreeNode<?>>> callback) {
        AbstractTreeNode projectRoot =
                new ProjectRootNode(project, this, settings, eventBus, projectServiceClient, dtoUnmarshallerFactory);
        callback.onSuccess(Collections.<AbstractTreeNode<?>>createArray(projectRoot));
    }

    public FileNode newFileNode(AbstractTreeNode parent, ItemReference data) {
        return new FileNode(parent, data, eventBus, projectServiceClient);
    }

    public FolderNode newFolderNode(AbstractTreeNode parent, ItemReference data) {
        return new FolderNode(parent, data, this, settings, eventBus, editorAgent, projectServiceClient, dtoUnmarshallerFactory);
    }
}
