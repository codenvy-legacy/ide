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
package org.exoplatform.ide.client.framework.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class FileOpenedEvent extends GwtEvent<FileOpenedHandler> {

    public static final GwtEvent.Type<FileOpenedHandler> TYPE = new GwtEvent.Type<FileOpenedHandler>();

    private FileModel file;

    private String editor;

    public FileOpenedEvent(FileModel file) {
        this.file = file;
    }

    public FileOpenedEvent(FileModel file, String editor) {
        this(file);
        this.editor = editor;
    }

    public FileModel getFile() {
        return file;
    }

    public String getEditor() {
        return editor;
    }

    @Override
    protected void dispatch(FileOpenedHandler handler) {
        handler.onFileOpened(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<FileOpenedHandler> getAssociatedType() {
        return TYPE;
    }

}
