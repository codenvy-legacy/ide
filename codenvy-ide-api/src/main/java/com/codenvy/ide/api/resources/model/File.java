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
package com.codenvy.ide.api.resources.model;

import com.codenvy.api.project.shared.dto.ItemReference;

/**
 * This is a derivative of {@link Resource}, that adds File-specific
 * properties and methods to provide an access to files stored on VFS.
 *
 * @author Nikolay Zamosenchuk
 */
public class File extends Resource {
    public static final String  TYPE           = "file";
    /** Content length. */
    protected           long    length         = -1;
    /** content if retrieved */
    private             String  content        = null;
    private             boolean contentChanged = false;

    /** Empty instance of file. */
    protected File() {
        super(TYPE);
    }

    /** For extending classes */
    protected File(String itemType) {
        super(itemType);
    }

    public File(ItemReference itemReference) {
        this();
        init(itemReference);
    }

    /** @return content length */
    public long getLength() {
        return length;
    }

    /**
     * @param length
     *         the content length
     */
    public void setLength(long length) {
        this.length = length;
    }


    private void fixMimeType() {
        // Firefox adds ";charset=utf-8" to mime-type. Lets clear it.
        if (mimeType != null) {
            int index = mimeType.indexOf(';');
            if (index > 0) {
                mimeType = mimeType.substring(0, index);
            }
        }
    }

    /**
     * Initialize from {@link ItemReference}.
     *
     * @param itemReference
     */
    public void init(ItemReference itemReference) {
        name = itemReference.getName();
        mimeType = itemReference.getMediaType();
        setLinks(itemReference.getLinks());
        this.contentChanged = false;
        fixMimeType();
    }

    /** @return the content if already retrieved */
    public String getContent() {
        return content;
    }

    /**
     * @param content
     *         the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /** @return the contentChanged */
    public boolean isContentChanged() {
        return contentChanged;
    }

    /**
     * @param contentChanged
     *         the contentChanged to set
     */
    public void setContentChanged(boolean contentChanged) {
        this.contentChanged = contentChanged;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFile() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFolder() {
        return false;
    }
}
