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
package com.codenvy.ide.part;

import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.event.ResourceChangedEvent;
import com.codenvy.ide.api.event.ResourceChangedHandler;
import com.codenvy.ide.api.ui.workspace.EditorPartStack;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PartStackView;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.texteditor.TextEditorPresenter;
import com.codenvy.ide.texteditor.openedfiles.ListOpenedFilesPresenter;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

/**
 * EditorPartStackPresenter is a special PartStackPresenter that is shared among all
 * Perspectives and used to display Editors.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
public class EditorPartStackPresenter extends PartStackPresenter implements EditorPartStack, ShowListButtonClickHandler {
    
    private ListOpenedFilesPresenter listOpenedFilesPresenter;
    
    /**
     * @param view
     * @param eventBus
     */
    @Inject
    public EditorPartStackPresenter(@Named("editorPartStack") PartStackView view,
                                    EventBus eventBus,
                                    PartStackEventHandler partStackEventHandler, ListOpenedFilesPresenter listOpenedFilesPresenter) {
        super(eventBus, partStackEventHandler, view, null);
        partsClosable = true;
        this.listOpenedFilesPresenter = listOpenedFilesPresenter;
        
        if (view instanceof EditorPartStackView){
            ((EditorPartStackView)view).setShowListButtonHandler(this);
        }

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                //do nothing
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                for (int i = parts.size() - 1; i >= 0; i--) {
                    PartPresenter part = parts.get(i);
                    if (part instanceof TextEditorPresenter) {
                        removePart(part);
                    }
                }
            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {
                //do nothing
            }
        });
        
        eventBus.addHandler(ResourceChangedEvent.TYPE, new ResourceChangedHandler() {
            @Override
            public void onResourceRenamed(ResourceChangedEvent event) {
            }

            @Override
            public void onResourceMoved(ResourceChangedEvent event) {
            }

            @Override
            public void onResourceDeleted(ResourceChangedEvent event) {
                if (event.getResource() instanceof Project) {
                    for (int i = parts.size() - 1; i >= 0; i--) {
                        PartPresenter part = parts.get(i);
                        if (part instanceof TextEditorPresenter
                            && ((TextEditorPresenter)part).getEditorInput().getFile().getProject().equals((Project)event.getResource())) {
                            //Set file's project to null for not to refer to non existing project:
                            ((TextEditorPresenter)part).getEditorInput().getFile().setProject(null);
                        }
                    }
                }
            }

            @Override
            public void onResourceCreated(ResourceChangedEvent event) {
            }

            @Override
            public void onResourceTreeRefreshed(ResourceChangedEvent event) {
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void addPart(PartPresenter part) {
        if (!(part instanceof EditorPartPresenter)) {
            Log.warn(getClass(), "EditorPartStack is not intended to be used to open non-Editor Parts.");
        }

        if (parts.contains(part)) {
            // part already exists
            // activate it
            setActivePart(part);
            // and return
            return;
        }
        parts.add(part);
        part.addPropertyListener(propertyListener);
        // include close button
        ImageResource titleImage = part.getTitleImage();
        PartStackView.TabItem tabItem =
                view.addTabButton(titleImage == null ? null : new Image(titleImage), part.getTitle(), part.getTitleToolTip(), null,
                                  partsClosable);
        bindEvents(tabItem, part);
        setActivePart(part);
        // request focus
        onRequestFocus();
    }

    /** {@inheritDoc} */
    @Override
    public void setActivePart(PartPresenter part) {
        if (!(part instanceof EditorPartPresenter)) {
            Log.warn(getClass(), "EditorPartStack is not intended to be used to open non-Editor Parts.");
        }
        if (activePart == part) {
            return;
        }
        activePart = part;


        AcceptsOneWidget contentPanel = view.getContentPanel();

        if (part == null) {
            view.setActiveTabButton(-1);
        } else {
            view.setActiveTabButton(parts.indexOf(activePart));
            activePart.go(contentPanel);
        }
        // request part stack to get the focus
        onRequestFocus();
        // notify handler, that part changed
        partStackHandler.onActivePartChanged(activePart);

    }

    /** {@inheritDoc} */
    @Override
    protected void close(PartPresenter part) {
        // may cancel close
        if (part.onClose()) {
            int partIndex = parts.indexOf(part);
            view.removeTabButton(partIndex);
            parts.remove(part);
            part.removePropertyListener(propertyListener);
            if (activePart == part) {
                view.clearContentPanel();
                //select another part
                setActivePart(parts.isEmpty() ? null : parts.get(parts.size() - 1));
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onShowListClicked(int x, int y, AsyncCallback<Void> callback) {
        Array<File> openedFiles = Collections.createArray();
        for (PartPresenter part : getParts().asIterable()) {
            if (part instanceof EditorPartPresenter) {
                openedFiles.add(((EditorPartPresenter)part).getEditorInput().getFile());
            }
        }

        listOpenedFilesPresenter.showDialog(openedFiles, x, y, callback);
    }
}