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


import static com.codenvy.ide.jseditor.client.texteditor.EmbeddedTextEditorPresenter.DEFAULT_CONTENT_TYPE;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.codenvy.ide.api.editor.EditorWithErrors;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.text.Region;
import com.codenvy.ide.api.texteditor.HandlesUndoRedo;
import com.codenvy.ide.api.texteditor.TextEditorOperations;
import com.codenvy.ide.jseditor.client.codeassist.CompletionsSource;
import com.codenvy.ide.jseditor.client.document.DocumentHandle;
import com.codenvy.ide.jseditor.client.document.EmbeddedDocument;
import com.codenvy.ide.jseditor.client.editorconfig.TextEditorConfiguration;
import com.codenvy.ide.jseditor.client.events.CompletionRequestEvent;
import com.codenvy.ide.jseditor.client.events.DocumentChangeEvent;
import com.codenvy.ide.jseditor.client.events.DocumentReadyEvent;
import com.codenvy.ide.jseditor.client.filetype.FileTypeIdentifier;
import com.codenvy.ide.jseditor.client.infopanel.InfoPanel;
import com.codenvy.ide.jseditor.client.infopanel.InfoPanelFactory;
import com.codenvy.ide.texteditor.selection.CursorModelWithHandler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Implementation of the View part of the editors of the embedded kind.
 * 
 * @author "Mickaël Leduque"
 */
public class EmbeddedTextEditorPartViewImpl extends Composite implements EmbeddedTextEditorPartView,
                                                                         EditorWithErrors {

    private final static EditorViewUiBinder uibinder = GWT.create(EditorViewUiBinder.class);
    private final EventBus generalEventBus;

    private final FileTypeIdentifier fileTypeIdentifier;

    @UiField(provided = true)
    InfoPanel infoPanel;

    @UiField
    SimplePanel editorPanel;

    private EditorWidgetFactory< ? > editorWidgetFactory;
    private EditorWidget editor;
    private CursorModelWithHandler cursorModel;
    private EmbeddedDocument embeddedDocument;

    /** The view delegate. */
    private Delegate delegate;

    private List<String> editorModes = null;

    private int tabSize = 3;
    private boolean delayedFocus = false;
    private boolean codeAssistEnabled = false;

    /** The editor handle for this editor view. */
    private final EditorHandle handle = new EditorHandle() {
        @Override
        public EmbeddedTextEditorPartView getEditor() {
            return EmbeddedTextEditorPartViewImpl.this;
        }
    };

    @Inject
    public EmbeddedTextEditorPartViewImpl(final FileTypeIdentifier fileTypeIdentifier,
                                          final InfoPanelFactory infoPanelFactory,
                                          final EventBus generalEventBus) {
        this.infoPanel = infoPanelFactory.create(this);

        final HTMLPanel panel = uibinder.createAndBindUi(this);
        initWidget(panel);

        this.fileTypeIdentifier = fileTypeIdentifier;
        this.generalEventBus = generalEventBus;
    }

    @Override
    public void configure(final TextEditorConfiguration configuration) {
        configure(configuration, null);
    }

    @Override
    public void configure(final TextEditorConfiguration configuration, final FileNode file) {
        this.editorModes = detectFileType(file);

        this.tabSize = configuration.getTabWidth();
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

    @Override
    public void setContents(final String contents) {

        this.editor = editorWidgetFactory.createEditorWidget(this.editorModes.get(0));
        this.editorPanel.add(this.editor);

        this.embeddedDocument = this.editor.getDocument();
        this.cursorModel = new EmbeddedEditorCursorModel(this.embeddedDocument);

        this.editor.setValue(contents);
        this.generalEventBus.fireEvent(new DocumentReadyEvent(this.getEditorHandle(), this.embeddedDocument));
        final DocumentHandle docHandle = this.embeddedDocument.getDocumentHandle();
        docHandle.getDocEventBus().fireEvent(new DocumentChangeEvent(docHandle, 0, contents.length(), contents));

        this.editor.setTabSize(this.tabSize);

        // set up infopanel
        this.infoPanel.createDefaultState(this.editorModes.get(0),
                                          editor.getEditorType(),
                                          editor.getKeymap(),
                                          this.embeddedDocument.getLineCount(),
                                          this.editor.getTabSize());
        this.editor.addCursorActivityHandler(this.infoPanel);
        this.editor.addBlurHandler(this.infoPanel);
        this.editor.addFocusHandler(this.infoPanel);

        // handle delayed focus
        // should also check if I am visible, but how ?
        if (delayedFocus) {
            this.editor.setFocus();
            this.delayedFocus = false;
        }
    }

    @Override
    public EmbeddedDocument getEmbeddedDocument() {
        return this.embeddedDocument;
    }

    @Override
    public void setReadOnly(final boolean isReadOnly) {
        this.editor.setReadOnly(isReadOnly);
    }

    @Override
    public boolean isReadOnly() {
        return this.editor.isReadOnly();
    }

    @Override
    public boolean canDoOperation(final int operation) {
        if (TextEditorOperations.CODEASSIST_PROPOSALS == operation && this.codeAssistEnabled) {
            return true;
        }
        return false;
    }

    @Override
    public void doOperation(final int operation) {
        switch (operation) {
            case TextEditorOperations.CODEASSIST_PROPOSALS:
                if (this.embeddedDocument != null) {
                    this.embeddedDocument.getDocumentHandle().getDocEventBus().fireEvent(new CompletionRequestEvent());
                }
                break;
            default:
                throw new UnsupportedOperationException("Operation code: " + operation + " is not supported!");
        }
    }

    @Override
    public CursorModelWithHandler getCursorModel() {
        return this.cursorModel;
    }

    public HandlerRegistration addChangeHandler(final ChangeHandler handler) {
        return this.editor.addChangeHandler(handler);
    }

    @Override
    public boolean isDirty() {
        return this.editor.isDirty();
    }

    @Override
    public String getContents() {
        return this.editor.getValue();
    }

    @Override
    public Region getSelectedRegion() {
        return this.editor.getSelectedRange();
    }

    @Override
    public void setFocus() {
        if (this.editor != null) {
            this.editor.setFocus();
        } else {
            this.delayedFocus = true;
        }
    }

    @Override
    public EditorHandle getEditorHandle() {
        return handle;
    }

    @Override
    public void setSelectedRegion(final Region region) {
        this.setSelectedRegion(region, true);
    }

    @Override
    public void setSelectedRegion(final Region region, final boolean show) {
        this.editor.setSelectedRange(region, show);
    }

    public void showMessage(final String message) {
        this.editor.showMessage(message);
    }

    @Override
    public String getContentType() {
        // Before the editor content is ready (configure), the editorModes is not defined
        if (this.editorModes == null || this.editorModes.isEmpty()) {
            return null;
        } else {
            return this.editorModes.get(0);
        }
    }

    @Override
    public void onResize() {
        if (this.editor != null) {
            this.editor.onResize();
        }
    }

    @Override
    public HandlesUndoRedo getUndoRedo() {
        return this.editor.getUndoRedo();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void setEditorWidgetFactory(final EditorWidgetFactory editorWidgetFactory) {
        this.editorWidgetFactory = editorWidgetFactory;
    }

    @Override
    public HasGutter getHasGutter() {
        return this.editor;
    }

    @Override
    public HasTextMarkers getHasTextMarkers() {
        return this.editor;
    }

    @Override
    public HasKeybindings getHasKeybindings() {
        return this.editor;
    }

    @Override
    public void showCompletionProposals(final CompletionsSource source) {
        this.editor.showCompletionProposals(source);
    }


    @Override
    public void setDelegate(final Delegate delegate) {
        this.delegate = delegate;
    }

    protected Delegate getDelegate() {
        return this.delegate;
    }

    @Override
    public EditorState getErrorState() {
        return this.delegate.getErrorState();
    }

    @Override
    public void setErrorState(final EditorState errorState) {
        this.delegate.setErrorState(errorState);
    }

    @Override
    public void setCodeAssistEnabled(boolean codeAssistEnabled) {
        this.codeAssistEnabled = true;
    }

    /**
     * UI binder interface for this component.
     * 
     * @author "Mickaël Leduque"
     */
    interface EditorViewUiBinder extends UiBinder<HTMLPanel, EmbeddedTextEditorPartViewImpl> {
    }

    @Override
    public void markClean() {
        this.editor.markClean();
    }
}
