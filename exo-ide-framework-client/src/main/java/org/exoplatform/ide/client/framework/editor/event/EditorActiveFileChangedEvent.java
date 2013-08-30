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

import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * @version $Id: $
 *          <p/>
 *          Fired when changed active file in editor
 */
public class EditorActiveFileChangedEvent extends GwtEvent<EditorActiveFileChangedHandler> {

    public static final GwtEvent.Type<EditorActiveFileChangedHandler> TYPE =
            new GwtEvent.Type<EditorActiveFileChangedHandler>();

    private FileModel file;

    private Editor editor;

    public EditorActiveFileChangedEvent(FileModel file, Editor editor) {
        this.file = file;
        this.editor = editor;
    }

    @Override
    protected void dispatch(EditorActiveFileChangedHandler handler) {
        handler.onEditorActiveFileChanged(this);
    }

    @Override
    public GwtEvent.Type<EditorActiveFileChangedHandler> getAssociatedType() {
        return TYPE;
    }

    public FileModel getFile() {
        return file;
    }

    /** @return the editor */
    public Editor getEditor() {
        return editor;
    }

}
