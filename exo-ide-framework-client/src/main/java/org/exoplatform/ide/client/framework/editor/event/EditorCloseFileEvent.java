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

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class EditorCloseFileEvent extends GwtEvent<EditorCloseFileHandler> {

    public static final GwtEvent.Type<EditorCloseFileHandler> TYPE = new GwtEvent.Type<EditorCloseFileHandler>();

    private FileModel file;

    private boolean ignoreChanges = false;

    public EditorCloseFileEvent(FileModel file) {
        this.file = file;
    }

    public EditorCloseFileEvent(FileModel file, boolean ignoreChanges) {
        this.file = file;
        this.ignoreChanges = ignoreChanges;
    }

    public boolean isIgnoreChanges() {
        return ignoreChanges;
    }

    /**
     * Return file will be closed
     *
     * @return
     */
    public FileModel getFile() {
        return file;
    }

    @Override
    protected void dispatch(EditorCloseFileHandler handler) {
        handler.onEditorCloseFile(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditorCloseFileHandler> getAssociatedType() {
        return TYPE;
    }

}
