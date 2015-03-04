/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.newresource;

import com.codenvy.api.analytics.client.logger.AnalyticsEventLogger;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ProjectAction;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.event.ItemEvent;
import com.codenvy.ide.api.projecttree.TreeNode;
import com.codenvy.ide.api.projecttree.VirtualFile;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.projecttree.generic.ItemNode;
import com.codenvy.ide.api.projecttree.generic.StorableNode;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.json.JsonHelper;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.codenvy.ide.ui.dialogs.InputCallback;
import com.codenvy.ide.ui.dialogs.input.InputDialog;
import com.codenvy.ide.ui.dialogs.input.InputValidator;
import com.codenvy.ide.util.NameUtils;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nullable;

import static com.codenvy.ide.api.event.ItemEvent.ItemOperation.CREATED;

/**
 * Implementation of an {@link Action} that provides an ability to create new resource (e.g. file, folder).
 * After performing this action, it asks user for the resource's name
 * and then creates resource in the selected folder.
 *
 * @author Artem Zatsarynnyy
 */
public abstract class AbstractNewResourceAction extends ProjectAction {
    protected final InputValidator           fileNameValidator;
    protected final InputValidator           folderNameValidator;
    protected final String                   title;
    protected       SelectionAgent           selectionAgent;
    protected       EditorAgent              editorAgent;
    protected       ProjectServiceClient     projectServiceClient;
    protected       EventBus                 eventBus;
    protected       AppContext               appContext;
    protected       AnalyticsEventLogger     eventLogger;
    protected       DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    protected       DialogFactory            dialogFactory;
    protected       CoreLocalizationConstant coreLocalizationConstant;

    /**
     * Creates new action.
     *
     * @param title
     *         action's title
     * @param description
     *         action's description
     * @param svgIcon
     *         action's SVG icon
     */
    public AbstractNewResourceAction(String title, String description, @Nullable SVGResource svgIcon) {
        super(title, description, svgIcon);
        fileNameValidator = new FileNameValidator();
        folderNameValidator = new FolderNameValidator();
        this.title = title;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (eventLogger != null) {
            eventLogger.log(this);
        }

        InputDialog inputDialog = dialogFactory.createInputDialog(
                coreLocalizationConstant.newResourceTitle(title),
                coreLocalizationConstant.newResourceLabel(title.toLowerCase()),
                new InputCallback() {
                    @Override
                    public void accepted(String value) {
                        onAccepted(value);
                    }
                }, null).withValidator(fileNameValidator);
        inputDialog.show();
    }

    private void onAccepted(String value) {
        final String name = getExtension().isEmpty() ? value : value + '.' + getExtension();
        final StorableNode parent = getNewResourceParent();
        if (parent == null) {
            throw new IllegalStateException("No selected parent.");
        }

        projectServiceClient.createFile(
                parent.getPath(), name, getDefaultContent(), getMimeType(),
                new AsyncRequestCallback<ItemReference>(dtoUnmarshallerFactory.newUnmarshaller(ItemReference.class)) {
                    @Override
                    protected void onSuccess(final ItemReference result) {
                        onFileCreated(result);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        dialogFactory.createMessageDialog("", JsonHelper.parseJsonMessage(exception.getMessage()), null).show();
                    }
                });
    }

    private void onFileCreated(final ItemReference result) {
        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            throw new IllegalStateException("No opened project.");
        }

        currentProject.getCurrentTree().getNodeByPath(result.getPath(), new AsyncCallback<TreeNode<?>>() {
            @Override
            public void onSuccess(TreeNode<?> treeNode) {
                eventBus.fireEvent(new ItemEvent((ItemNode)treeNode, CREATED));
                if ("file".equals(result.getType())) {
                    editorAgent.openEditor((VirtualFile)treeNode);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.error(AbstractNewResourceAction.class, throwable);
            }
        });
    }

    @Override
    public void updateProjectAction(ActionEvent e) {
        e.getPresentation().setEnabled(getNewResourceParent() != null);
    }

    /**
     * Returns extension (without dot) for a new resource.
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
     * By default, returns {@code null}.
     */
    protected String getMimeType() {
        return null;
    }

    /** Returns parent for creating new item or {@code null} if resource can not be created. */
    @Nullable
    protected StorableNode getNewResourceParent() {
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

    @Inject
    private void init(SelectionAgent selectionAgent,
                      EditorAgent editorAgent,
                      ProjectServiceClient projectServiceClient,
                      EventBus eventBus,
                      AppContext appContext,
                      AnalyticsEventLogger eventLogger,
                      DtoUnmarshallerFactory dtoUnmarshallerFactory,
                      DialogFactory dialogFactory,
                      CoreLocalizationConstant coreLocalizationConstant) {
        this.selectionAgent = selectionAgent;
        this.editorAgent = editorAgent;
        this.projectServiceClient = projectServiceClient;
        this.eventBus = eventBus;
        this.appContext = appContext;
        this.eventLogger = eventLogger;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dialogFactory = dialogFactory;
        this.coreLocalizationConstant = coreLocalizationConstant;
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
