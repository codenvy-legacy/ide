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
package com.codenvy.ide.texteditor.embeddedimpl.common;

import javax.validation.constraints.NotNull;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.editor.AbstractTextEditorPresenter;
import com.codenvy.ide.api.editor.DocumentProvider;
import com.codenvy.ide.api.editor.DocumentProvider.DocumentCallback;
import com.codenvy.ide.api.editor.SelectionProvider;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.FileEventHandler;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.debug.BreakpointGutterManager;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.outline.OutlineImpl;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.outline.OutlineModel;
import com.codenvy.ide.texteditor.api.outline.OutlinePresenter;
import com.codenvy.ide.util.executor.UserActivityManager;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter part for the embedded variety of editor implementations.
 * 
 * @author "Mickaël Leduque"
 */
public class EmbeddedTextEditorPresenter extends AbstractTextEditorPresenter implements FileEventHandler {

    private EmbeddedTextEditorPartView          editor;
    private OutlineImpl                         outline;
    private final Resources                     resources;
    private final UserActivityManager           userActivityManager;
    private final BreakpointGutterManager       breakpointGutterManager;
    private final DtoFactory                    dtoFactory;
    private final WorkspaceAgent                workspaceAgent;
    private final EmbeddedTextEditorViewFactory textEditorViewFactory;

    @Inject
    public EmbeddedTextEditorPresenter(final Resources resources,
                                       final UserActivityManager userActivityManager,
                                       final BreakpointGutterManager breakpointGutterManager,
                                       final DtoFactory dtoFactory,
                                       final WorkspaceAgent workspaceAgent,
                                       final EventBus eventBus,
                                       final EmbeddedTextEditorViewFactory textEditorViewFactory) {
        this.resources = resources;
        this.userActivityManager = userActivityManager;
        this.breakpointGutterManager = breakpointGutterManager;
        this.dtoFactory = dtoFactory;
        this.workspaceAgent = workspaceAgent;
        this.textEditorViewFactory = textEditorViewFactory;

        eventBus.addHandler(FileEvent.TYPE, this);
    }

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
                    public void onDocument(final Document document) {
                        EmbeddedTextEditorPresenter.this.document = document;
                        AnnotationModel annotationModel = documentProvider.getAnnotationModel(input);
                        editor.setDocument(document, annotationModel);
                        firePropertyChange(PROP_INPUT);
                        editor.addChangeHandler(new ChangeHandler() {

                            @Override
                            public void onChange(ChangeEvent event) {
                                document.set(editor.getContents());
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
                updateDirtyState(editor.isDirty());
            }
        });
    }

    @Override
    public Document getDocument() {
        return document;
    }

    @Override
    public void close(boolean save) {
        documentProvider.documentClosed(document);
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void doRevertToSaved() {
        // do nothing
    }

    @Override
    public SelectionProvider getSelectionProvider() {
        return null;
    }

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
        return editor.asWidget();
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(getWidget());
    }

    @Override
    public String getTitleToolTip() {
        return null;
    }

    @Override
    public void initialize(@NotNull TextEditorConfiguration configuration,
                           @NotNull DocumentProvider documentProvider,
                           @NotNull NotificationManager notificationManager) {
        super.initialize(configuration, documentProvider, notificationManager);
        editor = this.textEditorViewFactory.createTextEditorPartView(resources,
                                                                     userActivityManager,
                                                                     breakpointGutterManager,
                                                                     dtoFactory);
    }

    @Override
    public TextEditorPartView getView() {
        return editor;
    }

    @Override
    public void activate() {
        // TODO
    }

    @Override
    public void onFileOperation(FileEvent event) {
        if (event.getOperationType() != FileEvent.FileOperation.CLOSE) {
            return;
        }

        File eventFile = event.getFile();
        File file = input.getFile();
        if (file.equals(eventFile)) {
            workspaceAgent.removePart(this);
        }
    }

    @Override
    protected void afterSave() {
        // TODO
    }
}
