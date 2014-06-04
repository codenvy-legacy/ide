/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api.resources;

import com.codenvy.ide.api.resources.model.File;
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
