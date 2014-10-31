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
import com.codenvy.ide.CoreLocalizationConstant;
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
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.codenvy.ide.ui.dialogs.InputCallback;
import com.codenvy.ide.ui.dialogs.input.InputValidator;
import com.codenvy.ide.util.NameUtils;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nullable;

/**
 * Implementation of an {@link Action} that provides an ability to create new file/folder.
 * After performing this action, it asks user for the resource's name
 * and then creates resource in the selected folder.
 *
 * @author Artem Zatsarynnyy
 */
public abstract class AbstractNewResourceAction extends Action {
    protected String                   title;
    @Inject
    protected AppContext               appContext;
    @Inject
    protected SelectionAgent           selectionAgent;
    @Inject
    protected EditorAgent              editorAgent;
    @Inject
    protected ProjectServiceClient     projectServiceClient;
    @Inject
    protected EventBus                 eventBus;
    @Inject
    protected AnalyticsEventLogger     eventLogger;
    @Inject
    protected DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    @Inject
    protected DialogFactory            dialogFactory;
    protected InputValidator           fileNameValidator;
    protected InputValidator           folderNameValidator;
    @Inject
    private   CoreLocalizationConstant coreLocalizationConstant;

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
     */
    public AbstractNewResourceAction(String title, String description, @Nullable ImageResource icon, @Nullable SVGResource svgIcon) {
        super(title, description, icon, svgIcon);
        fileNameValidator = new FileNameValidator();
        folderNameValidator = new FolderNameValidator();
        this.title = title;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (eventLogger != null) {
            eventLogger.log(this);
        }

        dialogFactory.createInputDialog(coreLocalizationConstant.newResourceTitle(title),
                                        coreLocalizationConstant.newResourceLabel(title.toLowerCase()), new InputCallback() {
                    @Override
                    public void accepted(String value) {
                        final String name = getExtension().isEmpty() ? value : value + '.' + getExtension();
                        final StorableNode parent = getParent();
                        projectServiceClient.createFile(
                                parent.getPath(), name, getDefaultContent(), getMimeType(),
                                new AsyncRequestCallback<ItemReference>(dtoUnmarshallerFactory.newUnmarshaller(ItemReference.class)) {
                                    @Override
                                    protected void onSuccess(ItemReference result) {
                                        eventBus.fireEvent(NodeChangedEvent.createNodeChildrenChangedEvent((AbstractTreeNode<?>)parent));
                                        if ("file".equals(result.getType())) {
                                            FileNode file = new FileNode(null, result, eventBus, projectServiceClient, null);
                                            editorAgent.openEditor(file);
                                        }
                                    }

                                    @Override
                                    protected void onFailure(Throwable exception) {
                                        Log.error(AbstractNewResourceAction.class, exception);
                                    }
                                });
                    }
                }, null).withValidator(fileNameValidator).show();
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

    private class FileNameValidator implements InputValidator {
        @Nullable
        @Override
        public Violation validate(String value) {
            if (!NameUtils.checkFileName(value)) {
                return new Violation() {
                    @Override
                    public String getMessage() {
                        return coreLocalizationConstant.invalidName();
                    }
                };
            }
            return null;
        }
    }

    private class FolderNameValidator implements InputValidator {
        @Nullable
        @Override
        public Violation validate(String value) {
            if (!NameUtils.checkFolderName(value)) {
                return new Violation() {
                    @Override
                    public String getMessage() {
                        return coreLocalizationConstant.invalidName();
                    }
                };
            }
            return null;
        }
    }
}
