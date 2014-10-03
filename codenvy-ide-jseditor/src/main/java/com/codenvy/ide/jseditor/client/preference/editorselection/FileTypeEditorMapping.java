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

import com.codenvy.ide.api.filetypes.FileType;
import com.codenvy.ide.jseditor.client.editortype.EditorType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A store for editor to filetype to editor mappings.
 *
 * @author "Mickaël Leduque"
 */
public class FileTypeEditorMapping implements Iterable<Entry<FileType, EditorType>> {

    /** The actual mapping. */
    private final Map<FileType, EditorType> values = new HashMap<>();

    /**
     * Sets-up an editor association for the file type.
     *
     * @param fileType
     *         the file type
     * @param editorType
     *         the editor type
     */
    public void setEditor(final FileType filetype, final EditorType editorType) {
        this.values.put(filetype, editorType);
    }

    /**
     * Returns the editor association for the file type.
     *
     * @param fileType
     *         the file type
     * @return the associated editor or null
     */
    public EditorType getEditor(final FileType fileType) {
        return this.values.get(fileType);
    }

    @Override
    public Iterator<Entry<FileType, EditorType>> iterator() {
        return this.values.entrySet().iterator();
    }
}
