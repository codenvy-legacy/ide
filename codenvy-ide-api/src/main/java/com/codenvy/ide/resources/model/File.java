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
package com.codenvy.ide.resources.model;

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

    // ===

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
        id = itemReference.getId();
        name = itemReference.getName();
        mimeType = itemReference.getMediaType();
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
