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

package com.codenvy.ide.api.resources;

import com.codenvy.ide.resources.model.File;
import com.google.gwt.event.shared.GwtEvent;


/**
 * Event that describes the fact that file is going to be opened
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 * @version $Id: OpenFileEvent.java 34360 2009-07-22 23:58:59Z nzamosenchuk $
 */
public class FileEvent extends GwtEvent<FileEventHandler> {

    public static Type<FileEventHandler> TYPE = new Type<FileEventHandler>();

    public static enum FileOperation {
        OPEN, SAVE, CLOSE;
    }

    private File file;

    private FileOperation fileOperation;

    /**
     * @param fileName
     *         name of the file
     */
    public FileEvent(File file, FileOperation fileOperation) {
        this.fileOperation = fileOperation;
        this.file = file;
    }

    /** {@inheritDoc} */
    @Override
    public Type<FileEventHandler> getAssociatedType() {
        return TYPE;
    }

    /** @return the name of the file // should return the model object */
    public File getFile() {
        return file;
    }

    /** @return the type of operation performed with file */
    public FileOperation getOperationType() {
        return fileOperation;
    }

    @Override
    protected void dispatch(FileEventHandler handler) {
        handler.onFileOperation(this);
    }
}
