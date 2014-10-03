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
package com.codenvy.ide.jseditor.client.preference.keymaps;


import java.util.Map.Entry;

import javax.inject.Inject;

import com.codenvy.ide.jseditor.client.editortype.EditorType;
import com.codenvy.ide.jseditor.client.editortype.EditorTypeRegistry;
import com.codenvy.ide.jseditor.client.keymap.Keymap;
import com.codenvy.ide.jseditor.client.keymap.KeymapValuesHolder;
import com.codenvy.ide.jseditor.client.preference.EditorPrefLocalizationConstant;
import com.codenvy.ide.jseditor.client.preference.EditorPreferenceResource;
import com.codenvy.ide.jseditor.client.preference.EditorPreferenceResource.CellStyle;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Implementation of the {@link KeymapsPreferenceView}.
 */
public class KeymapsPreferenceViewImpl extends Composite implements KeymapsPreferenceView {

    /** The UI binder instance. */
    private static final KeymapsPreferenceViewImplUiBinder UIBINDER = GWT.create(KeymapsPreferenceViewImplUiBinder.class);

    private final EditorTypeRegistry editorTypeRegistry;
    private final CellStyle cellStyle;

    private KeymapSelectionColumn keymapSelectionColumn;

    private ActionDelegate delegate;
    private KeymapValuesHolder valuesHolder;

    @UiField(provided = true)
    EditorPrefLocalizationConstant constants;

    @UiField(provided = true)
    CellTable<EditorType> keyBindingSelection;


    @Inject
    public KeymapsPreferenceViewImpl(final EditorTypeRegistry editorTypeRegistry,
                                     final EditorPreferenceResource resources,
                                     final EditorPrefLocalizationConstant constants) {
        this.keyBindingSelection = new CellTable<EditorType>(5, resources);

        this.constants = constants;

        initWidget(UIBINDER.createAndBindUi(this));

        this.editorTypeRegistry = editorTypeRegistry;
        this.cellStyle = resources.cellStyle();

        // build keybinding selection table
        final TextColumn<EditorType> editorColumn = new TextColumn<EditorType>() {
            @Override
            public String getValue(final EditorType type) {
                return editorTypeRegistry.getName(type);
            }

            @Override
            public String getCellStyleNames(final Context context, final EditorType object) {
                return resources.cellStyle().prefCell() + " " + resources.cellStyle().firstColumn();
            }
        };

        this.keyBindingSelection.addColumn(editorColumn);

        // disable row selection
        this.keyBindingSelection.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
    }

    @Override
    public void setDelegate(final ActionDelegate delegate) {
        this.delegate = delegate;
    }

    private void handleEditorKeymapChanged(final EditorType editorType, final Keymap keymap) {
        this.delegate.editorKeymapChanged(editorType, keymap);
    }

    @Override
    protected void onLoad() {
        // delayed until the view is displayed

        this.keyBindingSelection.setRowData(this.editorTypeRegistry.getEditorTypes());
        for (final Entry<EditorType, Keymap> entry : this.valuesHolder) {
            Log.debug(KeymapsPreferenceViewImpl.class,
                      "Select configured keymap for editor type '" + entry.getKey() + "': " + entry.getValue());
            this.keymapSelectionColumn.setSelection(entry.getKey(), entry.getValue());
        }
        this.keyBindingSelection.redraw();
    }

    public void setKeymapValuesHolder(final KeymapValuesHolder newValue) {
        this.valuesHolder = newValue;

        final FieldUpdater<EditorType, Keymap> fieldUpdater = new FieldUpdater<EditorType, Keymap>() {

            @Override
            public void update(final int index, final EditorType object, final Keymap value) {
                handleEditorKeymapChanged(object, value);
            }
        };
        keymapSelectionColumn = new KeymapSelectionColumn(this.valuesHolder, fieldUpdater, this.cellStyle.selectWidth());
        this.keyBindingSelection.addColumn(keymapSelectionColumn);
    }

    /** UI binder interface for the {@link KeymapsPreferenceViewImpl} component. */
    interface KeymapsPreferenceViewImplUiBinder extends UiBinder<HTMLPanel, KeymapsPreferenceViewImpl> {
    }
}
