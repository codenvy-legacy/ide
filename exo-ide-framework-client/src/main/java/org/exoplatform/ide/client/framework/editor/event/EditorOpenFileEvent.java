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

import org.exoplatform.ide.client.framework.event.CursorPosition;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class EditorOpenFileEvent extends GwtEvent<EditorOpenFileHandler> {

    public static final GwtEvent.Type<EditorOpenFileHandler> TYPE = new GwtEvent.Type<EditorOpenFileHandler>();

    private FileModel file;

    private CursorPosition cursorPosition;

    public EditorOpenFileEvent(FileModel file) {
        this(file, new CursorPosition(1, 1));
    }

    public EditorOpenFileEvent(FileModel file, CursorPosition cursorPosition) {
        this.file = file;
        this.cursorPosition = cursorPosition;
    }

    public FileModel getFile() {
        return file;
    }

    public CursorPosition getCursorPosition() {
        return cursorPosition;
    }

    @Override
    protected void dispatch(EditorOpenFileHandler handler) {
        handler.onEditorOpenFile(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditorOpenFileHandler> getAssociatedType() {
        return TYPE;
    }

}
