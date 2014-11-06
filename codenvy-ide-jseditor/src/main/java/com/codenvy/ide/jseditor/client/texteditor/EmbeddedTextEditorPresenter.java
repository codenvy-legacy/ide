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
package com.codenvy.ide.jseditor.client.texteditor;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.editor.AbstractEditorPresenter;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.api.event.FileEventHandler;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.WorkspaceAgent;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.texteditor.HandlesUndoRedo;
import com.codenvy.ide.api.texteditor.UndoableEditor;
import com.codenvy.ide.api.texteditor.outline.OutlineModel;
import com.codenvy.ide.api.texteditor.outline.OutlinePresenter;
import com.codenvy.ide.debug.BreakpointManager;
import com.codenvy.ide.debug.BreakpointRenderer;
import com.codenvy.ide.debug.HasBreakpointRenderer;
import com.codenvy.ide.jseditor.client.codeassist.CodeAssistantFactory;
import com.codenvy.ide.jseditor.client.document.DocumentStorage;
import com.codenvy.ide.jseditor.client.document.DocumentStorage.EmbeddedDocumentCallback;
import com.codenvy.ide.jseditor.client.document.EmbeddedDocument;
import com.codenvy.ide.jseditor.client.editorconfig.EditorUpdateAction;
import com.codenvy.ide.jseditor.client.editorconfig.TextEditorConfiguration;
import com.codenvy.ide.jseditor.client.events.GutterClickEvent;
import com.codenvy.ide.jseditor.client.events.GutterClickHandler;
import com.codenvy.ide.jseditor.client.gutter.Gutters;
import com.codenvy.ide.jseditor.client.preference.EditorPrefLocalizationConstant;
import com.codenvy.ide.jseditor.client.quickfix.QuickAssistantFactory;
import com.codenvy.ide.jseditor.client.text.LinearRange;
import com.codenvy.ide.jseditor.client.text.TextPosition;
import com.codenvy.ide.jseditor.client.text.TextRange;
import com.codenvy.ide.jseditor.client.texteditor.EmbeddedTextEditorPartView.Delegate;
import com.codenvy.ide.ui.dialogs.CancelCallback;
import com.codenvy.ide.ui.dialogs.ConfirmCallback;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.web.bindery.event.shared.EventBus;


/**
 * Presenter part for the embedded variety of editor implementations.
 */
public class EmbeddedTextEditorPresenter extends AbstractEditorPresenter implements EmbeddedTextEditor, FileEventHandler,
                                                                                    UndoableEditor,
                                                                                    HasBreakpointRenderer,
                                                                                    Delegate {

    /** File type used when we have no idea of the actual content type. */
    public final static String DEFAULT_CONTENT_TYPE = "text/plain";

    private final Resources                      resources;
    private final WorkspaceAgent                 workspaceAgent;
    private final EmbeddedTextEditorViewFactory  textEditorViewFactory;
    private       EditorPrefLocalizationConstant constant;

    private final DocumentStorage      documentStorage;
    private final EventBus             generalEventBus;
    private final DialogFactory        dialogFactory;
    private final CodeAssistantFactory codeAssistantFactory;
    private final QuickAssistantFactory quickAssistantFactory;
    private final BreakpointManager    breakpointManager;

    private List<EditorUpdateAction> updateActions;

    private TextEditorConfiguration    configuration;
    private NotificationManager        notificationManager;
    private EmbeddedTextEditorPartView editor;
    private OutlineImpl                outline;

    /** The editor's error state. */
    private EditorState errorState;

    @AssistedInject
    public EmbeddedTextEditorPresenter(final BreakpointManager breakpointManager,
                                       final Resources resources,
                                       final WorkspaceAgent workspaceAgent,
                                       final EventBus eventBus,
                                       final EditorPrefLocalizationConstant constant,
                                       final DocumentStorage documentStorage,
                                       final CodeAssistantFactory codeAssistantFactory,
                                       final QuickAssistantFactory quickAssistantFactory,
                                       @Assisted final EmbeddedTextEditorViewFactory textEditorViewFactory,
                                       DialogFactory dialogFactory) {
        this.breakpointManager = breakpointManager;
        this.constant = constant;
        this.resources = resources;
        this.workspaceAgent = workspaceAgent;
        this.textEditorViewFactory = textEditorViewFactory;
        this.documentStorage = documentStorage;
        this.codeAssistantFactory = codeAssistantFactory;
        this.quickAssistantFactory = quickAssistantFactory;

        this.generalEventBus = eventBus;
        this.dialogFactory = dialogFactory;
        eventBus.addHandler(FileEvent.TYPE, this);
    }

    @Override
    protected void initializeEditor() {
        editor.configure(getConfiguration(), getEditorInput().getFile());
        new TextEditorInit(configuration, 
                           generalEventBus,
                           this.editor.getEditorHandle(),
                           this.codeAssistantFactory,
                           this.quickAssistantFactory,
                           this).init();

        // Postpone setting a document to give the time for editor (TextEditorViewImpl) to fully construct itself.
        // Otherwise, the editor may not be ready to render the document.
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                documentStorage.getDocument(input.getFile(), new EmbeddedDocumentCallback() {
                    @Override
                    public void onDocumentReceived(final String contents) {
                        editor.setContents(contents, input.getFile());
                        firePropertyChange(PROP_INPUT);

                        setupEventHandlers();
                    }
                });
            }
        });
    }

    private void setupEventHandlers() {
        this.editor.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(final ChangeEvent event) {
                handleDocumentChanged();
            }
        });
        this.editor.addGutterClickHandler(new GutterClickHandler() {
            @Override
            public void onGutterClick(final GutterClickEvent event) {
                if (Gutters.BREAKPOINTS_GUTTER.equals(event.getGutterId())
                    ||Gutters.LINE_NUMBERS_GUTTER.equals(event.getGutterId())) {
                    breakpointManager.changeBreakPointState(event.getLineNumber());
                }
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
    public void close(final boolean save) {
        this.documentStorage.documentClosed(this.editor.getEmbeddedDocument());
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
    public OutlinePresenter getOutline() {
        if (outline != null) {
            return outline;
        }
        final OutlineModel outlineModel = getConfiguration().getOutline();
        if (outlineModel != null) {
            outline = new OutlineImpl(resources, outlineModel, editor, this);
            return outline;
        } else {
            return null;
        }
    }

    @Nonnull
    protected Widget getWidget() {
        return editor.asWidget();
    }

    @Override
    public void go(final AcceptsOneWidget container) {
        container.setWidget(getWidget());
    }

    @Override
    public String getTitleToolTip() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void onClose(@Nonnull final AsyncCallback<Void> callback) {
        if (isDirty()) {
            dialogFactory.createConfirmDialog(
                    constant.askWindowCloseTitle(),
                    constant.askWindowSaveChangesMessage(getEditorInput().getName()),
                    new ConfirmCallback() {
                        @Override
                        public void accepted() {
                            doSave();
                            handleClose();
                            callback.onSuccess(null);
                        }
                    },
                    new CancelCallback() {
                        @Override
                        public void cancelled() {
                            handleClose();
                            callback.onSuccess(null);
                        }
                    }).show();
        } else {
            handleClose();
            callback.onSuccess(null);
        }
    }

    @Override
    public EmbeddedTextEditorPartView getView() {
        return this.editor;
    }

    @Override
    public void activate() {
        this.editor.setFocus();
    }

    @Override
    public void onFileOperation(final FileEvent event) {
        if (event.getOperationType() != FileEvent.FileOperation.CLOSE) {
            return;
        }

        final FileNode eventFile = event.getFile();
        final FileNode file = input.getFile();
        if (file.equals(eventFile)) {
            workspaceAgent.removePart(this);
        }
    }

    @Override
    public void initialize(@Nonnull final TextEditorConfiguration configuration,
                           @Nonnull final NotificationManager notificationManager) {
        this.configuration = configuration;
        this.notificationManager = notificationManager;
        this.editor = this.textEditorViewFactory.createTextEditorPartView();
        this.editor.setDelegate(this);
    }

    @Override
    public TextEditorConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public ImageResource getTitleImage() {
        return input.getImageResource();
    }

    @Override
    public SVGResource getTitleSVGImage() {
        return input.getSVGResource();
    }

    @Nonnull
    @Override
    public String getTitle() {
        if (isDirty()) {
            return "*" + input.getName();
        } else {
            return input.getName();
        }
    }

    @Override
    public void doSave() {
        doSave(new AsyncCallback<EditorInput>() {
            @Override
            public void onSuccess(final EditorInput result) {
                // do nothing
            }

            @Override
            public void onFailure(final Throwable caught) {
                // do nothing
            }
        });
    }

    @Override
    public void doSave(final AsyncCallback<EditorInput> callback) {
        final EmbeddedDocument doc = editor.getEmbeddedDocument();

        this.documentStorage.saveDocument(getEditorInput(), doc, false, new AsyncCallback<EditorInput>() {
            @Override
            public void onSuccess(EditorInput editorInput) {
                updateDirtyState(false);
                afterSave();
                if (callback != null) {
                    callback.onSuccess(editorInput);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                final Notification notification = new Notification(caught.getMessage(), ERROR);
                notificationManager.showNotification(notification);
                if (callback != null) {
                    callback.onFailure(caught);
                }
            }
        });
    }

    /** Override this method for handling after save actions. */
    protected void afterSave() {
        this.editor.markClean();
    }

    @Override
    public void doSaveAs() {
        // TODO not implemented
    }

    @Override
    public HandlesUndoRedo getUndoRedo() {
        if (this.editor != null) {
            return this.editor.getUndoRedo();
        } else {
            return null;
        }
    }

    @Override
    public EditorState getErrorState() {
        return this.errorState;
    }

    @Override
    public void setErrorState(final EditorState errorState) {
        this.errorState = errorState;
        firePropertyChange(ERROR_STATE);
    }

    @Override
    public BreakpointRenderer getBreakpointRenderer() {
        return this.editor.getBreakpointRenderer();
    }

    @Override
    public EmbeddedDocument getDocument() {
        return this.getView().getEmbeddedDocument();
    }

    @Override
    public String getContentType() {
        return this.getView().getContentType();
    }

    @Override
    public TextRange getSelectedTextRange() {
        return getDocument().getSelectedTextRange();
    }

    @Override
    public LinearRange getSelectedLinearRange() {
        return getDocument().getSelectedLinearRange();
    }

    @Override
    public void showMessage(final String message) {
        getView().showMessage(message);
    }

    @Override
    public TextPosition getCursorPosition() {
        return getDocument().getCursorPosition();
    }

    @Override
    public int getCursorOffset() {
        final TextPosition textPosition = getDocument().getCursorPosition();
        return getDocument().getIndexFromPosition(textPosition);
    }

    @Override
    public void refreshEditor() {
        if (this.updateActions != null) {
            for (final EditorUpdateAction action : this.updateActions) {
                action.doRefresh();
            }
        }
    }

    @Override
    public void addEditorUpdateAction(final EditorUpdateAction action) {
        if (action == null) {
            return;
        }
        if (this.updateActions == null) {
            this.updateActions = new ArrayList<>();
        }
        this.updateActions.add(action);
    }
}
