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

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.event.NodeChangedEvent;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.projecttree.generic.StorableNode;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.rest.AsyncRequestCallback;
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
 *
 * @author Artem Zatsarynnyy
 */
public class DefaultNewResourceAction extends Action {
    protected String               title;
    protected AppContext           appContext;
    protected SelectionAgent       selectionAgent;
    protected EditorAgent          editorAgent;
    protected ProjectServiceClient projectServiceClient;
    protected EventBus             eventBus;
    protected AnalyticsEventLogger eventLogger;

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
     * @param eventBus
     */
    public DefaultNewResourceAction(String title,
                                    String description,
                                    @Nullable ImageResource icon,
                                    @Nullable SVGResource svgIcon,
                                    AppContext appContext,
                                    SelectionAgent selectionAgent,
                                    @Nullable EditorAgent editorAgent,
                                    ProjectServiceClient projectServiceClient,
                                    EventBus eventBus) {
        super(title, description, icon, svgIcon);
        this.title = title;
        this.appContext = appContext;
        this.selectionAgent = selectionAgent;
        this.editorAgent = editorAgent;
        this.projectServiceClient = projectServiceClient;
        this.eventBus = eventBus;
    }

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
     * @param eventBus
     * @param eventLogger
     *         {@link com.codenvy.api.analytics.logger.AnalyticsEventLogger} instance
     */
    public DefaultNewResourceAction(String title,
                                    String description,
                                    @Nullable ImageResource icon,
                                    @Nullable SVGResource svgIcon,
                                    AppContext appContext,
                                    SelectionAgent selectionAgent,
                                    @Nullable EditorAgent editorAgent,
                                    ProjectServiceClient projectServiceClient,
                                    EventBus eventBus,
                                    AnalyticsEventLogger eventLogger) {
        super(title, description, icon, svgIcon);
        this.title = title;
        this.appContext = appContext;
        this.selectionAgent = selectionAgent;
        this.editorAgent = editorAgent;
        this.projectServiceClient = projectServiceClient;
        this.eventBus = eventBus;
        this.eventLogger = eventLogger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (eventLogger != null) {
            eventLogger.log(this);
        }

        new AskValueDialog("New " + title, "Name:", new AskValueCallback() {
            @Override
            public void onOk(String value) {
                final String name = getExtension().isEmpty() ? value : value + '.' + getExtension();
                final StorableNode parent = getParent();
                projectServiceClient.createFile(parent.getPath(), name, getDefaultContent(), getMimeType(),
                                                new AsyncRequestCallback<ItemReference>() {
                                                    @Override
                                                    protected void onSuccess(ItemReference result) {
                                                        eventBus.fireEvent(NodeChangedEvent.createNodeChildrenChangedEvent(
                                                                (AbstractTreeNode<?>)parent));
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
        e.getPresentation().setVisible(appContext.getCurrentProject() != null);
        e.getPresentation().setEnabled(getParent() != null);
    }

    /**
     * Returns extension for a new resource, e.g. html.
     * By default, returns an empty string.
     */
    protected String getExtension() {
        return "";
    }

    /**
     * Returns default content for a new resource.
     * By default, returns an empty string.
     */
    protected String getDefaultContent() {
        return "";
    }

    /**
     * Returns MIME-type for a new resource.
     * By default, returns <code>null</code>.
     */
    protected String getMimeType() {
        return null;
    }

    /** Returns parent for creating new item. */
    @Nullable
    protected StorableNode getParent() {
        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null && selection.getFirstElement() != null) {
            if (selection.getFirstElement() instanceof StorableNode) {
                final StorableNode selectedNode = (StorableNode)selection.getFirstElement();
                if (selectedNode instanceof FileNode) {
                    return (StorableNode)selectedNode.getParent();
                }
                return selectedNode;
            }
        }
        return null;
    }
}
