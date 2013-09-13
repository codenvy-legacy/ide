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

import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class EditorFileClosedEvent extends GwtEvent<EditorFileClosedHandler> {

    public static final GwtEvent.Type<EditorFileClosedHandler> TYPE = new GwtEvent.Type<EditorFileClosedHandler>();

    private FileModel file;

    private Map<String, FileModel> openedFiles;

    public EditorFileClosedEvent(FileModel file, Map<String, FileModel> openedFiles) {
        this.file = file;
        this.openedFiles = openedFiles;
    }

    public FileModel getFile() {
        return file;
    }

    public Map<String, FileModel> getOpenedFiles() {
        return openedFiles;
    }

    @Override
    protected void dispatch(EditorFileClosedHandler handler) {
        handler.onEditorFileClosed(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditorFileClosedHandler> getAssociatedType() {
        return TYPE;
    }

}
