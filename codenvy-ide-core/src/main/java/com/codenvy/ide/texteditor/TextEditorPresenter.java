/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.texteditor;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.editor.AbstractTextEditorPresenter;
import com.codenvy.ide.api.editor.DocumentProvider;
import com.codenvy.ide.api.editor.DocumentProvider.DocumentCallback;
import com.codenvy.ide.api.editor.SelectionProvider;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.FileEventHandler;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.debug.BreakpointGutterManager;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.outline.OutlineImpl;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.text.store.TextChange;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.codenvy.ide.texteditor.api.TextListener;
import com.codenvy.ide.texteditor.api.outline.OutlineModel;
import com.codenvy.ide.texteditor.api.outline.OutlinePresenter;
import com.codenvy.ide.util.executor.UserActivityManager;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class TextEditorPresenter extends AbstractTextEditorPresenter implements FileEventHandler {

    private final TextListener textListener = new TextListener() {

        @Override
        public void onTextChange(TextChange textChange) {
            if (!isDirty()) {
                updateDirtyState(true);
            }
        }
    };
    protected TextEditorViewImpl      editor;
    private   Resources               resources;
    private   UserActivityManager     userActivityManager;
    private   OutlineImpl             outline;
    private   BreakpointGutterManager breakpointGutterManager;
    private   DtoFactory              dtoFactory;
    private   WorkspaceAgent          workspaceAgent;

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
                        firePropertyChange(PROP_INPUT);
                    }
                });
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
        HTML h = new HTML();
        h.getElement().appendChild(editor.getElement());
        return h;
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
        editor.getTextListenerRegistrar().add(textListener);
    }

    /** {@inheritDoc} */
    @Override
    public void activate() {
        editor.getBuffer().synchronizeScrollTop();
    }

    /** {@inheritDoc} */
    @Override
    public void onFileOperation(FileEvent event) {
        if (event.getOperationType() != FileEvent.FileOperation.CLOSE) {
            return;
        }

        File eventFile = event.getFile();
        File file = input.getFile();
        if (file.equals(eventFile))
            workspaceAgent.removePart(this);
    }

    @Override
    protected void afterSave() {
        editor.resetHistory();
    }
}
