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
package com.codenvy.ide.api.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event that notifies of file content changes.
 */
public class FileContentUpdateEvent extends GwtEvent<FileContentUpdateHandler> {
    /** The event type. */
    public static Type<FileContentUpdateHandler> TYPE = new Type<>();

    /**
     * The path to the file that is updated.
     */
    private final String filePath;

    /**
     * Constructor.
     * 
     * @param filePath the path of the file that changed
     */
    public FileContentUpdateEvent(final String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Type<FileContentUpdateHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FileContentUpdateHandler handler) {
        handler.onFileContentUpdate(this);
    }

    /**
     * Returns the path to the file that had changes.
     * 
     * @return the path
     */
    public String getFilePath() {
        return filePath;
    }
}
