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
package com.codenvy.ide.newresource;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.gwt.client.QueryExpression;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.MimeType;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.event.RefreshProjectTreeEvent;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.projecttree.generic.ItemNode;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.askValue.AskValueCallback;
import com.codenvy.ide.ui.dialogs.askValue.AskValueDialog;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.resources.client.ImageResource;
import com.google.web.bindery.event.shared.EventBus;

import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nullable;

/**
 * Implementation of an {@link Action} that provides ability to create new file.
 * After performing this action, it asks user for the new file's name with {@link AskValueDialog}
 * and then creates new file in the user selected folder.
 * By default, this action enabled and visible when any project is opened.
 *
 * @author Artem Zatsarynnyy
 */
public class DefaultNewResourceAction extends Action {
    protected String                 title;
    protected AppContext             appContext;
    protected SelectionAgent         selectionAgent;
    protected EditorAgent            editorAgent;
    protected ProjectServiceClient   projectServiceClient;
    protected EventBus               eventBus;
    private   DtoUnmarshallerFactory dtoUnmarshallerFactory;

    /**
     * Creates new action.
     *
     * @param title
     *         action's title
     * @param description
     *         action's description
     * @param icon
     *         action's icon
     * @param svgIcon
     *         action's SVG icon
     * @param appContext
     *         {@link com.codenvy.ide.api.app.AppContext} instance
     * @param selectionAgent
     *         {@link com.codenvy.ide.api.selection.SelectionAgent} instance
     * @param editorAgent
     *         {@link com.codenvy.ide.api.editor.EditorAgent} instance. Need for opening created file in editor
     * @param projectServiceClient
     *         {@link com.codenvy.api.project.gwt.client.ProjectServiceClient} instance
     */
    public DefaultNewResourceAction(String title,
                                    String description,
                                    @Nullable ImageResource icon,
                                    @Nullable SVGResource svgIcon,
                                    AppContext appContext,
                                    SelectionAgent selectionAgent,
                                    @Nullable EditorAgent editorAgent,
                                    ProjectServiceClient projectServiceClient,
                                    DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                    EventBus eventBus) {
        super(title, description, icon, svgIcon);
        this.title = title;
        this.appContext = appContext;
        this.selectionAgent = selectionAgent;
        this.editorAgent = editorAgent;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.eventBus = eventBus;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new AskValueDialog("New " + title, "Name:", new AskValueCallback() {
            @Override
            public void onOk(String value) {
                final String name = getExtension().isEmpty() ? value : value + '.' + getExtension();
                projectServiceClient
                        .createFile(getParentPath(), name, getDefaultContent(), getMimeType(), new AsyncRequestCallback<Void>() {
                            @Override
                            protected void onSuccess(Void result) {
                                openFileInEditor(getParentPath(), name);
                                eventBus.fireEvent(new RefreshProjectTreeEvent());
                            }

                            @Override
                            protected void onFailure(Throwable exception) {
                                Log.error(DefaultNewResourceAction.class, exception);
                            }
                        });
            }
        }).show();
    }

    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setEnabledAndVisible(appContext.getCurrentProject() != null);
    }

    /** Returns extension for a new resource, e.g. html. Default implementation returns an empty string. */
    protected String getExtension() {
        return "";
    }

    /** Returns default content for a new resource. Default implementation returns an empty string. */
    protected String getDefaultContent() {
        return "";
    }

    /** Returns MIME-type for a new resource. Default implementation returns <code>text/plain</code>. */
    protected String getMimeType() {
        return MimeType.TEXT_PLAIN;
    }

    /** Returns path to the parent folder for creating new resource. */
    protected String getParentPath() {
        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null) {
            if (selection.getFirstElement() instanceof ItemNode) {
                final ItemNode selectedNode = (ItemNode)selection.getFirstElement();
                final String nodePath = selectedNode.getPath();

                if (selectedNode instanceof FileNode) {
                    return nodePath.substring(0, nodePath.length() - selectedNode.getName().length());
                } else {
                    return nodePath;
                }
            }
        }
        return appContext.getCurrentProject().getProjectDescription().getPath();
    }

    private void openFileInEditor(String itemParentPath, String itemName) {
        Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ItemReference.class);
        projectServiceClient.search(new QueryExpression().setPath(itemParentPath).setName(itemName),
                                    new AsyncRequestCallback<Array<ItemReference>>(unmarshaller) {
                                        @Override
                                        protected void onSuccess(Array<ItemReference> itemReferenceArray) {
                                            if (!itemReferenceArray.isEmpty()) {
                                                editorAgent.openEditor(itemReferenceArray.get(0));
                                            }
                                        }

                                        @Override
                                        protected void onFailure(Throwable throwable) {

                                        }
                                    });
    }
}
