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


import javax.inject.Inject;

import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.text.RegionImpl;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.texteditor.api.ContentFormatter;
import com.codenvy.ide.texteditor.api.FocusManager;
import com.codenvy.ide.texteditor.api.KeyListener;
import com.codenvy.ide.texteditor.api.SelectionModel;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.codenvy.ide.texteditor.api.TextEditorOperations;
import com.codenvy.ide.texteditor.api.TextInputListener;
import com.codenvy.ide.texteditor.api.UndoManager;
import com.codenvy.ide.texteditor.api.parser.Parser;
import com.codenvy.ide.texteditor.api.reconciler.Reconciler;
import com.codenvy.ide.texteditor.selection.CursorModelWithHandler;
import com.codenvy.ide.util.ListenerManager;
import com.codenvy.ide.util.ListenerManager.Dispatcher;
import com.codenvy.ide.util.ListenerRegistrar;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Implementation of the View part of the editors of the embedded kind.
 * 
 * @author "Mickaël Leduque"
 */
public class EmbeddedTextEditorPartViewImpl extends Composite implements EmbeddedTextEditorPartView {

    private final SimplePanel                        panel;
    private final EditorWidgetFactory                editorWidgetFactory;
    private final ListenerManager<TextInputListener> textInputListenerManager = ListenerManager.create();

    private EditorWidget                             editor;
    private CursorModelWithHandler                   cursorModel;
    private EmbeddedDocument                         embeddedDocument;
    private Document                                 document;
    private ContentFormatter                         contentFormatter;

    private String                                   editorMode               = null;

    @Inject
    public EmbeddedTextEditorPartViewImpl(final EditorWidgetFactory editorWidgetFactory) {
        this.panel = new SimplePanel();
        this.panel.setSize("100%", "100%");
        initWidget(this.panel);

        this.editorWidgetFactory = editorWidgetFactory;
    }

    // Undo/redo is handled by editor implementation
    @Override
    public void setUndoManager(final UndoManager undoManager) {
        throw new RuntimeException("This editor doesn't have an undo manager"); // deliberately fail here
    }

    @Override
    public void configure(final TextEditorConfiguration configuration) {
        final Parser parser = configuration.getParser(this);
        if (parser != null) {
            this.editorMode = parser.getName(parser.defaultState());
        }


        // configure the content formatter
        contentFormatter = configuration.getContentFormatter(this);

        // configure a reconciler
        Reconciler reconciler = configuration.getReconciler(this);
        if (reconciler != null) {
            reconciler.install(this);
        }

    }

    @Override
    public void setDocument(final Document document) {
        this.document = document;

        this.editor = editorWidgetFactory.createEditorWidget(this.editorMode, this.document);
        this.panel.add(this.editor);

        this.embeddedDocument = this.editor.getDocument();
        this.cursorModel = new EmbeddedEditorCursorModel(this.embeddedDocument);

        this.editor.setValue(document.get());

        textInputListenerManager.dispatch(new Dispatcher<TextInputListener>() {
            @Override
            public void dispatch(TextInputListener listener) {
                listener.inputDocumentChanged(null, document);
            }
        });
    }

    @Override
    public void setDocument(final Document document, final AnnotationModel annotationModel) {
        setDocument(document);
    }

    @Override
    public Document getDocument() {
        return this.document;
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
    public boolean canDoOperation(int operation) {

        if (TextEditorOperations.FORMAT == operation && this.contentFormatter != null) {
            return true;
        }
        return false;
    }

    @Override
    public void doOperation(int operation) {
        switch (operation) {
            case TextEditorOperations.FORMAT:
                if (contentFormatter != null) {
                    Region selectedRegion = this.getSelectedRegion();

                    if (selectedRegion == null || selectedRegion.getLength() <= 0 || selectedRegion.getOffset() < 0) {
                        Log.debug(EmbeddedTextEditorPartViewImpl.class, "No proper region selected, formatting whole document.");
                        selectedRegion = new RegionImpl(0, getDocument().getLength());
                    }
                    contentFormatter.format(getDocument(), selectedRegion);
                }
                break;
            default:
                throw new UnsupportedOperationException("Operation code: " + operation + " is not supported!");
        }

    }

    @Override
    public void addTextInputListener(final TextInputListener listener) {
        this.textInputListenerManager.add(listener);
    }

    @Override
    public void removeTextInputListener(final TextInputListener listener) {
        this.textInputListenerManager.remove(listener);
    }

    @Override
    public FocusManager getFocusManager() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenerRegistrar<KeyListener> getKeyListenerRegistrar() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SelectionModel getSelection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CursorModelWithHandler getCursorModel() {
        return this.cursorModel;
    }

    @Override
    public void onResize() {
        // TODO
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
}
