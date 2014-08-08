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


import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.text.Region;
import com.codenvy.ide.jseditor.client.JsEditorConstants;
import com.codenvy.ide.jseditor.client.document.EmbeddedDocument;
import com.codenvy.ide.jseditor.client.editorconfig.EmbeddedTextEditorConfiguration;
import com.codenvy.ide.jseditor.client.filetype.FileTypeIdentifier;
import com.codenvy.ide.jseditor.client.infopanel.InfoPanel;
import com.codenvy.ide.texteditor.selection.CursorModelWithHandler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the View part of the editors of the embedded kind.
 * 
 * @author "Mickaël Leduque"
 */
public class EmbeddedTextEditorPartViewImpl<T extends EditorWidget> extends Composite implements EmbeddedTextEditorPartView {

    private final static EditorViewUiBinder uibinder    = GWT.create(EditorViewUiBinder.class);

    private final EditorWidgetFactory<T>    editorWidgetFactory;
    private final FileTypeIdentifier        fileTypeIdentifier;

    @UiField(provided = true)
    InfoPanel                               infoPanel;

    @UiField
    SimplePanel                             editorPanel;

    private T                               editor;
    private CursorModelWithHandler          cursorModel;
    private EmbeddedDocument                embeddedDocument;

    private List<String>                    editorModes = null;

    private int                             tabSize     = 3;

    @Inject
    public EmbeddedTextEditorPartViewImpl(final EditorWidgetFactory<T> editorWidgetFactory,
                                          final FileTypeIdentifier fileTypeIdentifier,
                                          final JsEditorConstants constants) {
        infoPanel = new InfoPanel(this, constants);

        HTMLPanel panel = uibinder.createAndBindUi(this);
        initWidget(panel);

        this.editorWidgetFactory = editorWidgetFactory;
        this.fileTypeIdentifier = fileTypeIdentifier;

    }


    @Override
    public void configure(final EmbeddedTextEditorConfiguration configuration) {
        configure(configuration, null);
    }

    @Override
    public void configure(final EmbeddedTextEditorConfiguration configuration, final ItemReference file) {
        if (file != null) {
            List<String> types = this.fileTypeIdentifier.identifyType(file);
            if (types != null && !types.isEmpty()) {
                this.editorModes = types;
            }
        }

        // ultimate fallback - can't make more generic for text
        if (this.editorModes == null) {
            this.editorModes = Collections.singletonList("text/plain");
        }

        this.tabSize = configuration.getTabWidth();
    }

    @Override
    public void setContents(final String contents) {

        this.editor = editorWidgetFactory.createEditorWidget(this.editorModes.get(0));
        this.editorPanel.add(this.editor);

        this.embeddedDocument = this.editor.getDocument();
        this.cursorModel = new EmbeddedEditorCursorModel(this.embeddedDocument);

        this.editor.setValue(contents);
        this.editor.setTabSize(this.tabSize);

        // set up infopanel
        this.infoPanel.createDefaultState(this.editorModes.get(0), this.embeddedDocument.getLineCount(), this.editor.getTabSize());
        this.editor.addCursorActivityHandler(this.infoPanel);
        this.editor.addBlurHandler(this.infoPanel);
        this.editor.addFocusHandler(this.infoPanel);
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
    public boolean canDoOperation(int operation) {
        return false;
    }

    @Override
    public void doOperation(int operation) {
        switch (operation) {
            default:
                throw new UnsupportedOperationException("Operation code: " + operation + " is not supported!");
        }

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

    /**
     * UI binder interface for this component.
     * 
     * @author "Mickaël Leduque"
     */
    interface EditorViewUiBinder extends UiBinder<HTMLPanel, EmbeddedTextEditorPartViewImpl< ? >> {
    }

    @Override
    public void markClean() {
        this.editor.markClean();
    }
}
