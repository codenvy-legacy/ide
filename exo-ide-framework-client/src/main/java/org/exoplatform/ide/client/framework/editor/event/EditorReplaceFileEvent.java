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
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class EditorReplaceFileEvent extends GwtEvent<EditorReplaceFileHandler> {

    public static final GwtEvent.Type<EditorReplaceFileHandler> TYPE = new GwtEvent.Type<EditorReplaceFileHandler>();

    private FileModel file;

    private FileModel newFile;

    boolean updateContent = true;

    public EditorReplaceFileEvent(FileModel file, FileModel newFile) {
        this.file = file;
        this.newFile = newFile;
    }

    public EditorReplaceFileEvent(FileModel file, FileModel newFile, boolean updateContent) {
        this.file = file;
        this.newFile = newFile;
        this.updateContent = updateContent;
    }

    public FileModel getFile() {
        return file;
    }

    public FileModel getNewFile() {
        return newFile;
    }

    public boolean isUpdateContent() {
        return updateContent;
    }

    @Override
    protected void dispatch(EditorReplaceFileHandler handler) {
        handler.onEditorReplaceFile(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditorReplaceFileHandler> getAssociatedType() {
        return TYPE;
    }

}
