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
package com.codenvy.ide.jseditor.client.preference;


import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;

import com.codenvy.ide.api.filetypes.FileType;
import com.codenvy.ide.jseditor.client.JsEditorExtension;
import com.codenvy.ide.jseditor.client.editortype.EditorType;
import com.codenvy.ide.jseditor.client.editortype.EditorTypeRegistry;
import com.codenvy.ide.jseditor.client.keymap.Keymap;
import com.codenvy.ide.jseditor.client.keymap.KeymapValuesHolder;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.view.client.AbstractDataProvider;

/**
 * Implementation of the {@link EditorTypePreferenceView}.
 * 
 * @author "Mickaël Leduque"
 */
public class EditorTypePreferenceViewImpl extends Composite implements EditorTypePreferenceView {

    /** The UI binder instance. */
    private static final EditorTypePreferenceViewImplUiBinder UIBINDER    = GWT.create(EditorTypePreferenceViewImplUiBinder.class);

    private final EditorTypeRegistry                          editorTypeRegistry;
    private final List<EditorType>                            editorsList = new ArrayList<>();
    private final EditorType                                  defaultEditorType;

    private KeymapSelectionColumn                             keymapSelectionColumn;
    private EditorTypeSelectionColumn                         editorSelectionColumn;

    private ActionDelegate                                    delegate;
    private KeymapValuesHolder                                valuesHolder;
    private FileTypeEditorMapping                             filetypeEditorMapping;

    @UiField(provided = true)
    CellTable<FileType>                                       editorsSelection;

    @UiField(provided = true)
    Pager                                                     editorsPager;

    @UiField(provided = true)
    EditorPrefLocalizationConstant                            constants;

    @UiField(provided = true)
    CellTable<EditorType>                                     keyBindingSelection;

    @UiField
    CellStyle                                                 cellStyle;

    @Inject
    public EditorTypePreferenceViewImpl(final EditorTypeRegistry editorTypeRegistry,
                                        final EditorPreferenceResource resources,
                                        final @Named(JsEditorExtension.DEFAULT_EDITOR_TYPE_INSTANCE) EditorType defaultEditorType,
                                        final EditorPrefLocalizationConstant constants) {
        this.keyBindingSelection = new CellTable<EditorType>(5, resources);
        this.editorsSelection = new CellTable<FileType>(10, resources);

        final Pager.PagerResources pagerResource = GWT.create(Pager.PagerResources.class);
        this.editorsPager = new Pager(TextLocation.CENTER, pagerResource, true, 5, true);

        this.constants = constants;

        initWidget(UIBINDER.createAndBindUi(this));

        this.editorTypeRegistry = editorTypeRegistry;
        this.defaultEditorType = defaultEditorType;
        Log.debug(EditorTypePreferenceViewImpl.class, "Default editor type is " + this.defaultEditorType);

        // build keybinding selection table
        final TextColumn<EditorType> editorColumn = new TextColumn<EditorType>() {
            @Override
            public String getValue(final EditorType type) {
                return editorTypeRegistry.getName(type);
            }

            @Override
            public String getCellStyleNames(final Context context, final EditorType object) {
                return cellStyle.prefCell();
            }
        };

        this.keyBindingSelection.addColumn(editorColumn);

        // build editor selection table
        final TextColumn<FileType> filetypeNameColumn = new TextColumn<FileType>() {
            @Override
            public String getValue(final FileType type) {
                return type.getContentDescription();
            }

            @Override
            public String getCellStyleNames(final Context context, final FileType object) {
                return cellStyle.prefCell();
            }
        };

        this.editorsSelection.addColumn(filetypeNameColumn);
        this.editorsPager.setDisplay(editorsSelection);

        // disable row selection in both tables
        this.editorsSelection.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
        this.keyBindingSelection.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

        // set the first column width to the same size for both tables
        this.editorsSelection.setColumnWidth(filetypeNameColumn, "11em");
        this.keyBindingSelection.setColumnWidth(editorColumn, "11em");
    }

    @Override
    public void buildEditorTypesList() {

    }

    @Override
    public void setDelegate(final ActionDelegate delegate) {
        this.delegate = delegate;
    }

    void handleFileTypeEditorChanged(final FileType fileType, final EditorType editor) {
        this.delegate.filetypeEditorChanged(fileType, editor);
    }

    private void handleEditorKeymapChanged(final EditorType editorType, final Keymap keymap) {
        this.delegate.editorKeymapChanged(editorType, keymap);
    }

    @Override
    protected void onLoad() {
        // delayed until the view is displayed

        this.keyBindingSelection.setRowData(this.editorTypeRegistry.getEditorTypes());
        for (final Entry<EditorType, Keymap> entry : this.valuesHolder) {
            Log.debug(EditorTypePreferenceViewImpl.class,
                      "Select configured keymap for editor type '" + entry.getKey() + "': " + entry.getValue());
            this.keymapSelectionColumn.setSelection(entry.getKey(), entry.getValue());
        }
        this.keyBindingSelection.redraw();

        this.editorsList.clear();
        this.editorsList.addAll(this.editorTypeRegistry.getEditorTypes());
        this.editorsSelection.redraw();

        // hide the pager if only one page
        if (this.editorsList.size() <= this.editorsSelection.getPageSize()) {
            this.editorsPager.getElement().getStyle().setVisibility(Visibility.HIDDEN);
        } else {
            this.editorsPager.getElement().getStyle().setVisibility(Visibility.VISIBLE);
        }
    }

    public void setKeymapValuesHolder(final KeymapValuesHolder newValue) {
        this.valuesHolder = newValue;

        final FieldUpdater<EditorType, Keymap> fieldUpdater = new FieldUpdater<EditorType, Keymap>() {

            @Override
            public void update(final int index, final EditorType object, final Keymap value) {
                handleEditorKeymapChanged(object, value);
            }
        };
        keymapSelectionColumn = new KeymapSelectionColumn(this.valuesHolder, fieldUpdater);
        this.keyBindingSelection.addColumn(keymapSelectionColumn);
    }

    public void setFileTypeDataProvider(final AbstractDataProvider<FileType> dataProvider) {
        dataProvider.addDataDisplay(this.editorsSelection);
    }

    @Override
    public void setFileTypeEditorMapping(final FileTypeEditorMapping fileTypeEditorMapping) {
        this.filetypeEditorMapping = fileTypeEditorMapping;
        final FieldUpdater<FileType, EditorType> fieldUpdater = new FieldUpdater<FileType, EditorType>() {

            @Override
            public void update(final int index, final FileType fileType, final EditorType editor) {
                handleFileTypeEditorChanged(fileType, editor);
            }
        };
        this.editorSelectionColumn = new EditorTypeSelectionColumn(this.editorsList, this.filetypeEditorMapping,
                                                                   fieldUpdater, this.editorTypeRegistry, this.defaultEditorType);
        this.editorsSelection.addColumn(this.editorSelectionColumn);
    }

    /** UI binder interface for the {@link EditorTypePreferenceViewImpl} component. */
    interface EditorTypePreferenceViewImplUiBinder extends UiBinder<HTMLPanel, EditorTypePreferenceViewImpl> {
    }

    interface CellStyle extends CssResource {
        String prefCell();
    }
}
