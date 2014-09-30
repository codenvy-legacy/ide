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
package com.codenvy.ide.texteditor;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.editor.AbstractTextEditorPresenter;
import com.codenvy.ide.api.editor.DocumentProvider;
import com.codenvy.ide.api.editor.DocumentProvider.DocumentCallback;
import com.codenvy.ide.api.editor.EditorWithErrors;
import com.codenvy.ide.api.editor.SelectionProvider;
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.api.event.FileEventHandler;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.WorkspaceAgent;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.text.Document;
import com.codenvy.ide.api.text.DocumentEvent;
import com.codenvy.ide.api.text.DocumentListener;
import com.codenvy.ide.api.text.annotation.AnnotationModel;
import com.codenvy.ide.api.texteditor.HasHandlesOperationsView;
import com.codenvy.ide.api.texteditor.TextEditorConfiguration;
import com.codenvy.ide.api.texteditor.TextEditorPartView;
import com.codenvy.ide.api.texteditor.UndoManager;
import com.codenvy.ide.api.texteditor.outline.OutlineModel;
import com.codenvy.ide.api.texteditor.outline.OutlinePresenter;
import com.codenvy.ide.debug.BreakpointGutterManager;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.outline.OutlineImpl;
import com.codenvy.ide.util.executor.UserActivityManager;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;


/** @author Evgen Vidolob */
public class TextEditorPresenter extends AbstractTextEditorPresenter implements FileEventHandler, EditorWithErrors, HasHandlesOperationsView {

    protected TextEditorViewImpl      editor;
    private   Resources               resources;
    private   UserActivityManager     userActivityManager;
    private   OutlineImpl             outline;
    private   BreakpointGutterManager breakpointGutterManager;
    private   DtoFactory              dtoFactory;
    private   WorkspaceAgent          workspaceAgent;
    private   EditorState             errorState;

    @Inject
    public TextEditorPresenter(Resources resources,
                               UserActivityManager userActivityManager,
                               BreakpointGutterManager breakpointGutterManager,
                               DtoFactory dtoFactory,
                               WorkspaceAgent workspaceAgent,
                               EventBus eventBus) {
        this.resources = resources;
        this.userActivityManager = userActivityManager;
        this.breakpointGutterManager = breakpointGutterManager;
        this.dtoFactory = dtoFactory;
        this.workspaceAgent = workspaceAgent;

        eventBus.addHandler(FileEvent.TYPE, this);
    }

    /** {@inheritDoc} */
    @Override
    protected void initializeEditor() {
        editor.configure(configuration);

        // Postpone setting a document to give the time for editor (TextEditorViewImpl) to fully construct itself.
        // Otherwise, the editor may not be ready to render the document.
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                documentProvider.getDocument(input, new DocumentCallback() {
                    @Override
                    public void onDocument(Document document) {
                        TextEditorPresenter.this.document = document;
                        AnnotationModel annotationModel = documentProvider.getAnnotationModel(input);
                        editor.setDocument(document, annotationModel);
                        editor.getView().setInfoPanelExist(true);
                        editor.getInfoPanel().createDefaultState(input.getContentDescription(), document.getNumberOfLines());
                        firePropertyChange(PROP_INPUT);
                        document.addDocumentListener(new DocumentListener() {
                            @Override
                            public void documentAboutToBeChanged(DocumentEvent event) {
                            }

                            @Override
                            public void documentChanged(DocumentEvent event) {
                                handleDocumentChanged();
                            }
                        });
                    }
                });
            }
        });
    }

    private void handleDocumentChanged() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                updateDirtyState(editor.getUndoManager().undoable());
            }
        });
    }

    @Override
    public Document getDocument() {
        return document;
    }

    /** {@inheritDoc} */
    @Override
    public void close(boolean save) {
        documentProvider.documentClosed(document);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEditable() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void doRevertToSaved() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public SelectionProvider getSelectionProvider() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public OutlinePresenter getOutline() {
        if (outline != null) {
            return outline;
        }
        OutlineModel outlineModel = configuration.getOutline(editor);
        if (outlineModel != null) {
            outline = new OutlineImpl(resources, outlineModel, editor, this);
            return outline;
        } else {
            return null;
        }
    }

    @NotNull
    protected Widget getWidget() {
        return new TextEditorViewResizable(editor);
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(getWidget());
    }

    /** {@inheritDoc} */
    @Override
    public String getTitleToolTip() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void initialize(@NotNull TextEditorConfiguration configuration,
                           @NotNull DocumentProvider documentProvider,
                           @NotNull NotificationManager notificationManager) {
        super.initialize(configuration, documentProvider, notificationManager);
        editor = new TextEditorViewImpl(resources, userActivityManager, breakpointGutterManager, dtoFactory);
//        editor.getTextListenerRegistrar().add(textListener);
    }

    @Override
    public TextEditorPartView getView() {
        return editor;
    }

    /** {@inheritDoc} */
    @Override
    public void activate() {
        editor.getBuffer().synchronizeScrollTop();
        editor.getFocusManager().focus();
    }

    /** {@inheritDoc} */
    @Override
    public void onFileOperation(FileEvent event) {
        if (event.getOperationType() != FileEvent.FileOperation.CLOSE) {
            return;
        }

        FileNode eventFile = event.getFile();
        FileNode file = input.getFile();
        if (file.equals(eventFile)) {
            workspaceAgent.removePart(this);
        }
    }

    @Override
    protected void afterSave() {
        editor.resetHistory();
    }

    @Override
    public EditorState getErrorState() {
        return errorState;
    }

    @Override
    public void setErrorState(EditorState errorState) {
        this.errorState = errorState;
        firePropertyChange(ERROR_STATE);
    }

    public UndoManager getUndoManager() {
        return this.getView().getUndoManager();
    }

    @Override
    public UndoManager getUndoRedo() {
        return this.getView().getUndoManager();
    }
}
