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

import com.codenvy.ide.jseditor.client.editortype.EditorType;
import com.codenvy.ide.jseditor.client.keymap.Keymap;
import com.codenvy.ide.jseditor.client.keymap.KeymapValuesHolder;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;

public class KeymapSelectionColumn extends Column<EditorType, Keymap> {
    private final KeymapValuesHolder valuesHolder;

    public KeymapSelectionColumn(final KeymapValuesHolder valuesHolder,
                                 final FieldUpdater<EditorType, Keymap> fieldUpdater) {
        super(new KeymapSelectionCell("gwt-ListBox", 12d, Unit.EM));
        this.valuesHolder = valuesHolder;

        setFieldUpdater(new FieldUpdater<EditorType, Keymap>() {

            @Override
            public void update(final int index, final EditorType editorType, final Keymap keymap) {
                Log.debug(KeymapSelectionColumn.class, "Value update for editor " + editorType + " keymap=" + keymap);
                KeymapSelectionColumn.this.valuesHolder.setKeymap(editorType, keymap);
                fieldUpdater.update(index, editorType, keymap);
            }
        });
    }

    @Override
    public Keymap getValue(final EditorType editorType) {
        if (this.valuesHolder == null) {
            return null;
        } else {
            return this.valuesHolder.getKeymap(editorType);
        }
    }

    public void setSelection(final EditorType key, final Keymap value) {
        final KeymapSelectionCell cell = (KeymapSelectionCell)getCell();
        cell.setViewData(key, value.getKey());
    }
}
