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
import javax.inject.Inject;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.editor.AbstractEditorPresenter;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.editor.EditorWithErrors;
import com.codenvy.ide.api.event.FileContentUpdateEvent;
import com.codenvy.ide.api.event.FileContentUpdateHandler;
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.api.event.FileEventHandler;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.WorkspaceAgent;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.texteditor.HandlesTextOperations;
import com.codenvy.ide.api.texteditor.HandlesUndoRedo;
import com.codenvy.ide.api.texteditor.HasReadOnlyProperty;
import com.codenvy.ide.api.texteditor.TextEditorOperations;
import com.codenvy.ide.api.texteditor.UndoableEditor;
import com.codenvy.ide.api.texteditor.outline.OutlineModel;
import com.codenvy.ide.debug.BreakpointManager;
import com.codenvy.ide.debug.BreakpointRenderer;
import com.codenvy.ide.debug.HasBreakpointRenderer;
import com.codenvy.ide.jseditor.client.JsEditorConstants;
import com.codenvy.ide.jseditor.client.codeassist.CodeAssistantFactory;
import com.codenvy.ide.jseditor.client.codeassist.CompletionsSource;
import com.codenvy.ide.jseditor.client.debug.BreakpointRendererFactory;
import com.codenvy.ide.jseditor.client.document.DocumentHandle;
import com.codenvy.ide.jseditor.client.document.DocumentStorage;
import com.codenvy.ide.jseditor.client.document.DocumentStorage.EmbeddedDocumentCallback;
import com.codenvy.ide.jseditor.client.document.EmbeddedDocument;
import com.codenvy.ide.jseditor.client.editorconfig.EditorUpdateAction;
import com.codenvy.ide.jseditor.client.editorconfig.TextEditorConfiguration;
import com.codenvy.ide.jseditor.client.events.CompletionRequestEvent;
import com.codenvy.ide.jseditor.client.events.DocumentChangeEvent;
import com.codenvy.ide.jseditor.client.events.DocumentReadyEvent;
import com.codenvy.ide.jseditor.client.events.GutterClickEvent;
import com.codenvy.ide.jseditor.client.events.GutterClickHandler;
import com.codenvy.ide.jseditor.client.filetype.FileTypeIdentifier;
import com.codenvy.ide.jseditor.client.gutter.Gutters;
import com.codenvy.ide.jseditor.client.keymap.Keybinding;
import com.codenvy.ide.jseditor.client.position.PositionConverter;
import com.codenvy.ide.jseditor.client.quickfix.QuickAssistantFactory;
import com.codenvy.ide.jseditor.client.text.LinearRange;
import com.codenvy.ide.jseditor.client.text.TextPosition;
import com.codenvy.ide.jseditor.client.text.TextRange;
import com.codenvy.ide.jseditor.client.texteditor.EmbeddedTextEditorPartView.Delegate;
import com.codenvy.ide.rest.AsyncRequestLoader;
import com.codenvy.ide.texteditor.selection.CursorModelWithHandler;
import com.codenvy.ide.ui.dialogs.CancelCallback;
import com.codenvy.ide.ui.dialogs.ConfirmCallback;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.codenvy.ide.ui.dialogs.choice.ChoiceDialog;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.web.bindery.event.shared.EventBus;


/**
 * Presenter part for the embedded variety of editor implementations.
 */
public class EmbeddedTextEditorPresenter<T extends EditorWidget> extends AbstractEditorPresenter
                                                                         implements EmbeddedTextEditor,
                                                                                    FileEventHandler,
                                                                                    UndoableEditor,
                                                                                    HasBreakpointRenderer,
                                                                                    HasReadOnlyProperty,
                                                                                    HandlesTextOperations,
                                                                                    EditorWithErrors,
                                                                                    Delegate {

    /** File type used when we have no idea of the actual content type. */
    public final static String DEFAULT_CONTENT_TYPE = "text/plain";

    private final Resources                      resources;
    private final WorkspaceAgent                 workspaceAgent;
    private final EditorWidgetFactory<T>         editorWigetFactory;
    private final EditorModule<T>                editorModule;
    private final JsEditorConstants constant;

    private final DocumentStorage      documentStorage;
    private final EventBus             generalEventBus;
    private final FileTypeIdentifier   fileTypeIdentifier;
    private final DialogFactory        dialogFactory;
    private final CodeAssistantFactory codeAssistantFactory;
    private final QuickAssistantFactory quickAssistantFactory;
    private final BreakpointManager    breakpointManager;

    private List<EditorUpdateAction> updateActions;

    private TextEditorConfiguration    configuration;

    private EditorWidget               editorWidget;
    private EmbeddedDocument           document;
    private CursorModelWithHandler     cursorModel;
    private HasKeybindings keyBindingsManager = new TemporaryKeybindingsManager();

    private AsyncRequestLoader         loader;
    private NotificationManager        notificationManager;
    private final EmbeddedTextEditorPartView editorView;
    private OutlineImpl                outline;

    /** The editor's error state. */
    private EditorState errorState;

    private boolean delayedFocus = false;

    private BreakpointRendererFactory breakpointRendererFactory;
    private BreakpointRenderer breakpointRenderer;


    /** The editor handle for this editor. */
    private final EditorHandle handle = new EditorHandle() {};

    private List<String> fileTypes;

    @AssistedInject
    public EmbeddedTextEditorPresenter(final CodeAssistantFactory codeAssistantFactory,
                                       final BreakpointManager breakpointManager,
                                       final BreakpointRendererFactory breakpointRendererFactory,
                                       final DialogFactory dialogFactory,
                                       final DocumentStorage documentStorage,
                                       final JsEditorConstants constant,
                                       @Assisted final EditorWidgetFactory<T> editorWigetFactory,
                                       final EditorModule<T> editorModule,
                                       final EmbeddedTextEditorPartView editorView,
                                       final EventBus eventBus,
                                       final FileTypeIdentifier fileTypeIdentifier,
                                       final QuickAssistantFactory quickAssistantFactory,
                                       final Resources resources,
                                       final WorkspaceAgent workspaceAgent) {

        this.breakpointManager = breakpointManager;
        this.breakpointRendererFactory = breakpointRendererFactory;
        this.codeAssistantFactory = codeAssistantFactory;
        this.constant = constant;
        this.dialogFactory = dialogFactory;
        this.documentStorage = documentStorage;
        this.editorView = editorView;
        this.editorModule = editorModule;
        this.editorWigetFactory = editorWigetFactory;
        this.fileTypeIdentifier = fileTypeIdentifier;
        this.generalEventBus = eventBus;
        this.quickAssistantFactory = quickAssistantFactory;
        this.resources = resources;
        this.workspaceAgent = workspaceAgent;

        this.editorView.setDelegate(this);
        eventBus.addHandler(FileEvent.TYPE, this);
    }

    @Override
    protected void initializeEditor() {

        new TextEditorInit<T>(configuration, 
                           generalEventBus,
                           this.codeAssistantFactory,
                           this.quickAssistantFactory,
                           this).init();

        // Postpone setting a document to give the time for editor (TextEditorViewImpl) to fully construct itself.
        // Otherwise, the editor may not be ready to render the document.
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                if (editorModule.isError()) {
                    displayErrorPanel(constant.editorInitErrorMessage());
                    return;
                }
                final boolean moduleReady = editorModule.isReady();
                EditorInitCallback<T> dualCallback = new EditorInitCallback<T>(moduleReady, loader, constant) {
                    @Override
                    public void onReady(final String content) {
                        createEditor(content);
                    }

                    @Override
                    public void onError() {
                        displayErrorPanel(constant.editorInitErrorMessage());
                    }

                    @Override
                    public void onFileError() {
                        displayErrorPanel(constant.editorFileErrorMessage());
                    }
                };
                documentStorage.getDocument(input.getFile(), dualCallback);
                if (! moduleReady) {
                    editorModule.waitReady(dualCallback);
                }
            }
        });
    }

    private void createEditor(final String content) {
        this.fileTypes = detectFileType(getEditorInput().getFile());
        this.editorWidget =  editorWigetFactory.createEditorWidget(fileTypes);

        // finish editor initialization
        this.editorView.setEditorWidget(this.editorWidget);

        this.document = this.editorWidget.getDocument();
        this.document.setFile(input.getFile());
        this.cursorModel = new EmbeddedEditorCursorModel(this.document);

        this.editorWidget.setTabSize(this.configuration.getTabWidth());

        // initialize info panel
        this.editorView.initInfoPanel(this.editorWidget.getMode(), this.editorWidget.getEditorType(),
                                      this.editorWidget.getKeymap(), this.document.getLineCount(),
                                      this.configuration.getTabWidth());

        // handle delayed focus
        // should also check if I am visible, but how ?
        if (delayedFocus) {
            this.editorWidget.setFocus();
            this.delayedFocus = false;
        }

        // delayed keybindings creation ?
        switchHasKeybinding();

        this.editorWidget.setValue(content);
        this.generalEventBus.fireEvent(new DocumentReadyEvent(this.getEditorHandle(), this.document));
        final DocumentHandle docHandle = this.document.getDocumentHandle();
        docHandle.getDocEventBus().fireEvent(new DocumentChangeEvent(docHandle, 0, content.length(), content));

        final OutlineImpl outline = getOutline();
        if (outline != null) {
            outline.bind(this.cursorModel, this.document);
        }

        firePropertyChange(PROP_INPUT);

        setupEventHandlers();
        setupFileContentUpdateHandler();
    }

    private void setupEventHandlers() {
        this.editorWidget.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(final ChangeEvent event) {
                handleDocumentChanged();
            }
        });
        this.editorWidget.addGutterClickHandler(new GutterClickHandler() {
            @Override
            public void onGutterClick(final GutterClickEvent event) {
                if (Gutters.BREAKPOINTS_GUTTER.equals(event.getGutterId())
                    ||Gutters.LINE_NUMBERS_GUTTER.equals(event.getGutterId())) {
                    breakpointManager.changeBreakPointState(event.getLineNumber());
                }
            }
        });
    }

    private void setupFileContentUpdateHandler() {
        this.generalEventBus.addHandler(FileContentUpdateEvent.TYPE, new FileContentUpdateHandler() {
            @Override
            public void onFileContentUpdate(final FileContentUpdateEvent event) {
                if (event.getFilePath() != null && event.getFilePath().equals(document.getFile().getPath())) {
                    if (isDirty()) {
                        final ConfirmCallback callback = new ConfirmCallback() {
                            @Override
                            public void accepted() {
                                updateContent();
                            }
                        };
                        final ChoiceDialog choice = dialogFactory.createChoiceDialog(constant.fileUpdateTitle(),
                                                          constant.fileUpdateMessage(event.getFilePath()),
                                                          constant.fileUpdateOvewrite(),
                                                          constant.fileUpdateKeepUnsaved(),
                                                          callback,
                                                          null);
                        choice.show();
                    } else {
                        updateContent();
                    }
                }
            }
        });
    }

    private void updateContent() {
        /* -save current cursor and (ideally) viewport
         * -set editor content which is also expected to
         *     -reset dirty flag
         *     -clear history
         * -restore current cursor position
         */
        final TextPosition currentCursor = getCursorPosition();
        this.documentStorage.getDocument(document.getFile(), new EmbeddedDocumentCallback() {
            
            @Override
            public void onDocumentReceived(final String content) {
                editorWidget.setValue(content);
                final DocumentHandle docHandle = document.getDocumentHandle();
                docHandle.getDocEventBus().fireEvent(new DocumentChangeEvent(docHandle, 0, content.length(), content));
                document.setCursorPosition(currentCursor);
            }
            
            @Override
            public void onDocumentLoadFailure(final Throwable caught) {
                displayErrorPanel(constant.editorFileErrorMessage());
            }
        });
    }

    private void displayErrorPanel(final String message) {
        this.editorView.showPlaceHolder(new Label(message));
    }

    private void handleDocumentChanged() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                updateDirtyState(editorWidget.isDirty());
            }
        });
    }

    @Override
    public void close(final boolean save) {
        this.documentStorage.documentClosed(this.document);
    }

    @Inject
    public void injectAsyncLoader(final AsyncRequestLoader loader) {
        this.loader = loader;
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
    public OutlineImpl getOutline() {
        if (outline != null) {
            return outline;
        }
        final OutlineModel outlineModel = getConfiguration().getOutline();
        if (outlineModel != null) {
            outline = new OutlineImpl(resources, outlineModel);
            return outline;
        } else {
            return null;
        }
    }

    @Nonnull
    protected Widget getWidget() {
        return this.editorView.asWidget();
    }

    @Override
    public void go(final AcceptsOneWidget container) {
        container.setWidget(getWidget());
    }

    @Override
    public String getTitleToolTip() {
        return null;
    }

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
        return this.editorView;
    }

    @Override
    public void activate() {
        if (editorWidget != null) {
            this.editorWidget.refresh();
            this.editorWidget.setFocus();
        } else {
            this.delayedFocus = true;
        }
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

        this.documentStorage.saveDocument(getEditorInput(), this.document, false, new AsyncCallback<EditorInput>() {
            @Override
            public void onSuccess(EditorInput editorInput) {
                updateDirtyState(false);
                editorWidget.markClean();
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
    }

    @Override
    public void doSaveAs() {
        // TODO not implemented
    }

    @Override
    public HandlesUndoRedo getUndoRedo() {
        if (this.editorWidget != null) {
            return this.editorWidget.getUndoRedo();
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
        if (this.breakpointRenderer == null && this.editorWidget != null) {
            this.breakpointRenderer = this.breakpointRendererFactory.create(this.getHasGutter(),
                                                                            this.editorWidget.getLineStyler(),
                                                                            this.document);
        }
        return this.breakpointRenderer;
    }

    @Override
    public EmbeddedDocument getDocument() {
        return this.document;
    }

    @Override
    public String getContentType() {
        // Before the editor content is ready, the content type is not defined
        if (this.fileTypes == null || this.fileTypes.isEmpty()) {
            return null;
        } else {
            return this.fileTypes.get(0);
        }
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
        this.editorWidget.showMessage(message);
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

    @Override
    public void addKeybinding(final Keybinding keybinding) {
        // the actual HasKeyBindings object can change, so use indirection
        getHasKeybindings().addKeybinding(keybinding);
    }

    private List<String> detectFileType(final FileNode file) {
        final List<String> result = new ArrayList<>();
        if (file != null) {
            // use the identification patterns
            final List<String> types = this.fileTypeIdentifier.identifyType(file);
            if (types != null && !types.isEmpty()) {
                result.addAll(types);
            }
            // use the registered media type if there is one
            if (file.getData() != null) {
                final String storedContentType = file.getData().getMediaType();
                if (storedContentType != null
                    && ! storedContentType.isEmpty()
                    // give another chance at detection
                    && ! DEFAULT_CONTENT_TYPE.equals(storedContentType)) {
                    result.add(storedContentType);
                }
            }
        }

        // ultimate fallback - can't make more generic for text
        result.add(DEFAULT_CONTENT_TYPE);

        return result;
    }

    public HasGutter getHasGutter() {
        if (this.editorWidget != null) {
            return this.editorWidget;
        } else {
            return null;
        }
    }

    public HasTextMarkers getHasTextMarkers() {
        if (this.editorWidget != null) {
            return this.editorWidget;
        } else {
            return null;
        }
    }

    public HasKeybindings getHasKeybindings() {
        return this.keyBindingsManager;
    }

    @Override
    public CursorModelWithHandler getCursorModel() {
        return this.cursorModel;
    }

    @Override
    public PositionConverter getPositionConverter() {
        return this.editorWidget.getPositionConverter();
    }

    public void showCompletionProposals(final CompletionsSource source) {
        this.editorView.showCompletionProposals(this.editorWidget, source);
    }

    public void showCompletionProposals() {
        this.editorView.showCompletionProposals(this.editorWidget);
    }

    public EditorHandle getEditorHandle() {
        return this.handle;
    }

    private void switchHasKeybinding() {
        final HasKeybindings current = getHasKeybindings();
        if (! (current instanceof TemporaryKeybindingsManager)) {
            return;
        }
        // change the key binding instance and add all bindings to the new one
        this.keyBindingsManager = this.editorWidget;
        final List<Keybinding> bindings = ((TemporaryKeybindingsManager)current).getbindings();
        for (final Keybinding binding : bindings) {
            this.keyBindingsManager.addKeybinding(binding);
        }
    }

    @Override
    public void onResize() {
        if (this.editorWidget != null) {
            this.editorWidget.onResize();
        }
    }

    @Override
    public void editorLostFocus() {
        this.editorView.updateInfoPanelUnfocused(this.document.getLineCount());
    }

    @Override
    public void editorGotFocus() {
        this.editorView.updateInfoPanelPosition(this.document.getCursorPosition());
    }

    @Override
    public void editorCursorPositionChanged() {
        this.editorView.updateInfoPanelPosition(this.document.getCursorPosition());
    }

    @Override
    public boolean canDoOperation(final int operation) {
        if (TextEditorOperations.CODEASSIST_PROPOSALS == operation) {
            return true;
        }
        return false;
    }

    @Override
    public void doOperation(final int operation) {
        switch (operation) {
            case TextEditorOperations.CODEASSIST_PROPOSALS:
                if (this.document != null) {
                    this.document.getDocumentHandle().getDocEventBus().fireEvent(new CompletionRequestEvent());
                }
                break;
            default:
                throw new UnsupportedOperationException("Operation code: " + operation + " is not supported!");
        }
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        this.editorWidget.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() {
        return this.editorWidget.isReadOnly();
    }
}
