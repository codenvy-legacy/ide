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
package com.codenvy.ide.part.editor;

import com.codenvy.ide.api.constraints.Constraints;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorWithErrors;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.parts.EditorPartStack;
import com.codenvy.ide.api.parts.PartPresenter;
import com.codenvy.ide.api.parts.PartStackView;
import com.codenvy.ide.api.parts.PropertyListener;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.part.PartStackPresenter;
import com.codenvy.ide.texteditor.TextEditorPresenter;
import com.codenvy.ide.texteditor.openedfiles.ListOpenedFilesPresenter;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * EditorPartStackPresenter is a special PartStackPresenter that is shared among all
 * Perspectives and used to display Editors.
 *
 * @author Nikolay Zamosenchuk
 * @author Stéphane Daviet
 */
@Singleton
public class EditorPartStackPresenter extends PartStackPresenter implements EditorPartStack, ShowListButtonClickHandler {

    private ListOpenedFilesPresenter listOpenedFilesPresenter;

    @Inject
    public EditorPartStackPresenter(@Named("editorPartStack") final PartStackView view,
                                    EventBus eventBus,
                                    PartStackEventHandler partStackEventHandler, ListOpenedFilesPresenter listOpenedFilesPresenter) {
        super(eventBus, partStackEventHandler, view, null);
        partsClosable = true;
        this.listOpenedFilesPresenter = listOpenedFilesPresenter;

        if (view instanceof EditorPartStackView) {
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
                    if (part instanceof EditorPartPresenter) {
                        removePart(part);
                    }
                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void addPart(PartPresenter part, Constraints constraint) {
        addPart(part);
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
        SVGResource titleSVGResource = part.getTitleSVGImage();
        SVGImage titleSVGImage = null;
        if (titleSVGResource != null) {
            titleSVGImage = part.decorateIcon(new SVGImage(titleSVGResource));
        }
        PartStackView.TabItem tabItem =
                view.addTabButton(titleSVGImage,
                                  part.getTitle(),
                                  part.getTitleToolTip(),
                                  null,
                                  partsClosable);

        if (part instanceof TextEditorPresenter) {
            final TextEditorPresenter presenter = ((TextEditorPresenter)part);
            final TabItemWithMarks tab = (TabItemWithMarks)tabItem;
            part.addPropertyListener(new PropertyListener() {
                @Override
                public void propertyChanged(PartPresenter source, int propId) {
                    if (view instanceof EditorPartStackView) {
                        if (presenter.getErrorState().equals(EditorWithErrors.EditorState.ERROR)) {
                            tab.setErrorMark(true);
                        } else {
                            tab.setErrorMark(false);
                        }
                        if (presenter.getErrorState().equals(EditorWithErrors.EditorState.WARNING)) {
                            tab.setWarningMark(true);
                        } else {
                            tab.setWarningMark(false);
                        }
                    }
                }
            });
        }

        bindEvents(tabItem, part);
        part.go(partViewContainer);

        setActivePart(part);

//        // request focus
//        onRequestFocus();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                partStackHandler.onActivePartChanged(activePart);
            }
        });

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

        if (part == null) {
            view.setActiveTab(-1);
        } else {
            view.setActiveTab(parts.indexOf(activePart));
        }
        // request part stack to get the focus
        onRequestFocus();

//        // notify handler, that part changed
//        partStackHandler.onActivePartChanged(activePart);
    }

    /** {@inheritDoc} */
    @Override
    protected void close(PartPresenter part) {
        // may cancel close
        if (part.onClose()) {
            view.removeTab(parts.indexOf(part));
            parts.remove(part);
            part.removePropertyListener(propertyListener);
            if (activePart == part) {
                //select another part
                setActivePart(parts.isEmpty() ? null : parts.get(parts.size() - 1));
                partStackHandler.onActivePartChanged(activePart);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onShowListClicked(int x, int y, AsyncCallback<Void> callback) {
        Array<FileNode> openedFiles = Collections.createArray();
        for (PartPresenter part : getParts().asIterable()) {
            if (part instanceof EditorPartPresenter) {
                openedFiles.add(((EditorPartPresenter)part).getEditorInput().getFile());
            }
        }

        listOpenedFilesPresenter.showDialog(openedFiles, x, y, callback);
    }

    @Override
    protected void sortPartsOnView(Constraints constraint) {
    }

}
