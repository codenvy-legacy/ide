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
package com.codenvy.ide.jseditor.client.preference.editorselection;

import java.util.List;

import com.codenvy.ide.api.filetypes.FileType;
import com.codenvy.ide.jseditor.client.editortype.EditorType;
import com.codenvy.ide.jseditor.client.editortype.EditorTypeRegistry;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;

public class EditorSelectionColumn extends Column<FileType, EditorType> {
    private final FileTypeEditorMapping valuesHolder;

    public EditorSelectionColumn(final List<EditorType> editorTypes,
                                     final FileTypeEditorMapping filetypeEditorMapping,
                                     final FieldUpdater<FileType, EditorType> fieldUpdater,
                                     final EditorTypeRegistry editorTypeRegistry,
                                     final EditorType defaultEditor) {
        super(new EditorSelectionCell(editorTypes, "gwt-ListBox", 12d, Unit.EM, editorTypeRegistry, defaultEditor));
        this.valuesHolder = filetypeEditorMapping;

        setFieldUpdater(new FieldUpdater<FileType, EditorType>() {

            @Override
            public void update(final int index, final FileType fileType, final EditorType editorType) {
                Log.debug(EditorSelectionColumn.class, "Value update for filetype " + fileType + " editor=" + editorType);
                EditorSelectionColumn.this.valuesHolder.setEditor(fileType, editorType);
                fieldUpdater.update(index, fileType, editorType);
            }
        });
    }

    @Override
    public EditorType getValue(final FileType fileType) {
        if (this.valuesHolder == null) {
            return null;
        } else {
            return this.valuesHolder.getEditor(fileType);
        }
    }

    public void setSelection(final FileType key, final EditorType value) {
        final EditorSelectionCell cell = (EditorSelectionCell)getCell();
        cell.setViewData(key, value.getEditorTypeKey());
    }
}
