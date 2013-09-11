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
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class OpenFileEvent extends GwtEvent<OpenFileHandler> {

    public static final GwtEvent.Type<OpenFileHandler> TYPE = new GwtEvent.Type<OpenFileHandler>();

    private FileModel file;

    private String fileId;

    private CursorPosition cursorPosition;

    private int ignoreErrorsCount = 0;

    /** Check is lock file. */
    private boolean lockFile = true;

    public OpenFileEvent(FileModel file) {
        this(file, new CursorPosition(1, 1));
    }

    public OpenFileEvent(FileModel file, CursorPosition cursorPosition) {
        this.file = file;
        this.cursorPosition = cursorPosition;

    }

    public OpenFileEvent(FileModel file, boolean lockFile) {
        this.file = file;
        this.lockFile = lockFile;
    }

    public OpenFileEvent(FileModel file, boolean lockFile, int ignoreErrorsCount) {
        this.file = file;
        this.lockFile = lockFile;
        this.ignoreErrorsCount = ignoreErrorsCount;
    }

    public OpenFileEvent(String fileId) {
        this.fileId = fileId;
    }

    public FileModel getFile() {
        return file;
    }

    public String getFileId() {
        return fileId;
    }

    public boolean isLockFile() {
        return lockFile;
    }

    @Override
    protected void dispatch(OpenFileHandler handler) {
        handler.onOpenFile(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<OpenFileHandler> getAssociatedType() {
        return TYPE;
    }

    public int getIgnoreErrorsCount() {
        return ignoreErrorsCount;
    }

    public CursorPosition getCursorPosition() {
        return cursorPosition;
    }

}
