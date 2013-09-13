/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.client.framework.editor.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.FileModel;

/** @version $Id: $ */

public class EditorFileContentChangedEvent extends GwtEvent<EditorFileContentChangedHandler> {

    public static final GwtEvent.Type<EditorFileContentChangedHandler> TYPE =
            new GwtEvent.Type<EditorFileContentChangedHandler>();

    private FileModel file;

    private boolean hasUndoChanges;

    private boolean hasRedoChanges;

    public EditorFileContentChangedEvent(FileModel file, boolean hasUndoChanges, boolean hasRedoChanges) {
        this.file = file;
        this.hasUndoChanges = hasUndoChanges;
        this.hasRedoChanges = hasRedoChanges;
    }

    @Override
    protected void dispatch(EditorFileContentChangedHandler handler) {
        handler.onEditorFileContentChanged(this);
    }

    @Override
    public GwtEvent.Type<EditorFileContentChangedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @return changed item */
    public FileModel getFile() {
        return file;
    }

    public boolean hasUndoChanges() {
        return hasUndoChanges;
    }

    public boolean hasRedoChanges() {
        return hasRedoChanges;
    }

}
