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
package org.eclipse.che.ide.part.editor;

import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.editor.EditorWithErrors;
import org.eclipse.che.ide.api.event.ProjectActionEvent;
import org.eclipse.che.ide.api.event.ProjectActionHandler;
import org.eclipse.che.ide.api.parts.EditorPartStack;
import org.eclipse.che.ide.api.parts.PartPresenter;
import org.eclipse.che.ide.api.parts.PartStackView;
import org.eclipse.che.ide.api.parts.PropertyListener;
import org.eclipse.che.ide.api.project.tree.VirtualFile;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.collections.Collections;

import org.eclipse.che.ide.texteditor.openedfiles.ListOpenedFilesPresenter;
import org.eclipse.che.ide.part.PartStackPresenter;

import org.eclipse.che.ide.util.loging.Log;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
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

    private interface CloseTabCallback {
        void onTabsClosed();
    }

    @Inject
    public EditorPartStackPresenter(final EditorPartStackView view, EventBus eventBus,
                                    PartStackEventHandler partStackEventHandler, ListOpenedFilesPresenter listOpenedFilesPresenter) {
        super(eventBus, partStackEventHandler, view, null);
        partsClosable = true;
        this.listOpenedFilesPresenter = listOpenedFilesPresenter;

        view.setShowListButtonHandler(this);

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                //do nothing
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                if (!parts.isEmpty()) {
                    close(activePart, new CloseTabCallback() {
                        @Override
                        public void onTabsClosed() {
                            closeActivePart(this);
                        }
                    });
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

        PartStackView.TabItem tabItem = view.addTab(titleSVGImage, part.getTitle(),
                part.getTitleToolTip(), null, partsClosable);

        if (part instanceof EditorWithErrors) {
            final EditorWithErrors presenter = ((EditorWithErrors)part);
            final TabItemWithMarks tab = (TabItemWithMarks)tabItem;

            part.addPropertyListener(new PropertyListener() {
                @Override
                public void propertyChanged(PartPresenter source, int propId) {
                    if (presenter.getErrorState() != null) {
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
    }

    /*close active part and do action from callBack*/
    protected void closeActivePart(final CloseTabCallback closeTabCallback) {
        if (activePart != null) {
            close(activePart, closeTabCallback);
        } else {
            Log.warn(getClass(), "No active part");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void close(final PartPresenter part) {
        close(part, null);
    }

    /*close tab and do action from callBack*/
    protected void close(final PartPresenter part, final CloseTabCallback closeTabCallback) {
        part.onClose(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable throwable) {
            }

            @Override
            public void onSuccess(Void aVoid) {
                view.removeTab(parts.indexOf(part));
                parts.remove(part);
                part.removePropertyListener(propertyListener);
                if (activePart == part) {
                    //select another part
                    setActivePart(parts.isEmpty() ? null : parts.get(parts.size() - 1));

                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            if (closeTabCallback != null) {
                                closeTabCallback.onTabsClosed();
                            }
                        }
                    });



                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onShowListClicked(int x, int y, AsyncCallback<Void> callback) {
        Array<VirtualFile> openedFiles = Collections.createArray();
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
