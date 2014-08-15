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

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ui.dialogs.askValue.AskValueCallback;
import com.codenvy.ide.ui.dialogs.askValue.AskValueDialog;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nullable;

/**
 * Implementation of an {@link Action} that provides ability to create new resource.
 * After performing this action, it asks user for the new resource's name with {@link AskValueDialog}
 * and then creates new file in the selected folder.
 * By default, this action enabled and visible when any project is opened.
 *
 * @author Artem Zatsarynnyy
 */
public class DefaultNewResourceAction extends Action {
    protected String           title;
    protected ResourceProvider resourceProvider;
    protected SelectionAgent   selectionAgent;
    protected EditorAgent      editorAgent;

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
     * @param resourceProvider
     *         {@link com.codenvy.ide.api.resources.ResourceProvider}
     * @param selectionAgent
     *         {@link com.codenvy.ide.api.selection.SelectionAgent}
     * @param editorAgent
     *         {@link com.codenvy.ide.api.editor.EditorAgent}
     */
    public DefaultNewResourceAction(String title,
                                    String description,
                                    @Nullable ImageResource icon,
                                    @Nullable SVGResource svgIcon,
                                    ResourceProvider resourceProvider,
                                    SelectionAgent selectionAgent,
                                    EditorAgent editorAgent) {
        super(title, description, icon, svgIcon);
        this.title = title;
        this.resourceProvider = resourceProvider;
        this.selectionAgent = selectionAgent;
        this.editorAgent = editorAgent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new AskValueDialog("New " + title, "Name:", new AskValueCallback() {
            @Override
            public void onOk(String value) {
                Project activeProject = resourceProvider.getActiveProject();
                final String name = getExtension().isEmpty() ? value : value + '.' + getExtension();
                activeProject.createFile(getParent(), name, getDefaultContent(), getMimeType(), new AsyncCallback<File>() {
                    @Override
                    public void onSuccess(File result) {
                        if (result.isFile()) {
                            editorAgent.openEditor(result);
                        }
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                    }
                });
            }
        }).show();
    }

    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setEnabledAndVisible(resourceProvider.getActiveProject() != null);
    }

    /**
     * Returns extension for a new resource, e.g. html.
     * By default, this method returns an empty string.
     */
    protected String getExtension() {
        return "";
    }

    /**
     * Returns default content for a new resource.
     * By default, this method returns an empty string.
     */
    protected String getDefaultContent() {
        return "";
    }

    /**
     * Returns MIME-type for a new resource.
     * By default, this method returns null.
     */
    protected String getMimeType() {
        return null;
    }

    /** Returns parent folder for creating new resource. */
    protected Folder getParent() {
        Project activeProject = resourceProvider.getActiveProject();
        Folder parent = null;
        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null && selection.getFirstElement() != null) {
            if (selection.getFirstElement() instanceof Resource) {
                Resource resource = (Resource)selection.getFirstElement();
                if (resource.isFile()) {
                    parent = resource.getParent();
                } else {
                    parent = (Folder)resource;
                }
            }
        } else {
            parent = activeProject;
        }
        return parent;
    }
}
