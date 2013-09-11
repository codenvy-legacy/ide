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

import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class EditorFileOpenedEvent extends GwtEvent<EditorFileOpenedHandler> {

    public static final GwtEvent.Type<EditorFileOpenedHandler> TYPE = new GwtEvent.Type<EditorFileOpenedHandler>();

    private FileModel file;

    private Editor editor;

    private Map<String, FileModel> openedFiles;

    public EditorFileOpenedEvent(FileModel file, Editor editor, Map<String, FileModel> openedFiles) {
        this.file = file;
        this.editor = editor;
        this.openedFiles = openedFiles;
    }

    public FileModel getFile() {
        return file;
    }

    public Map<String, FileModel> getOpenedFiles() {
        return openedFiles;
    }

    public Editor getEditor() {
        return editor;
    }

    @Override
    protected void dispatch(EditorFileOpenedHandler handler) {
        handler.onEditorFileOpened(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditorFileOpenedHandler> getAssociatedType() {
        return TYPE;
    }

}
