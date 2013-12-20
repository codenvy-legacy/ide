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

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.resources.marshal.JSONDeserializer;
import com.google.gwt.json.client.JSONObject;


/**
 * This is a derivative of {@link Resource}, that adds File-specific properties and methods to provide
 * an access to files stored on VFS.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class File extends Resource {
    public static final String TYPE = "file";

    /** Id of version of file. */
    protected String versionId;

    /** Content length. */
    protected long length = -1;

    /** Date of last modification in long format. */
    protected long lastModificationDate;

    /** Locking flag. */
    protected boolean locked;

    /** content if retrieved */
    private String content = null;

    private boolean contentChanged = false;

    private Array<File> versionHistory = Collections.<File>createArray();

    private Lock lock = null;

    /** Empty instance of file. */
    protected File() {
        super(TYPE);
    }

    /** For extending classes */
    protected File(String itemType) {
        super(itemType);
    }

    public File(JSONObject itemObject) {
        this();
        init(itemObject);
    }

    /** @return version id */
    public String getVersionId() {
        return versionId;
    }

    /**
     * @param versionId
     *         the version id
     */
    public void setVersionId(String versionId) {
        this.versionId = versionId;
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

    /** @return date of last modification */
    public long getLastModificationDate() {
        return lastModificationDate;
    }

    /**
     * @param lastModificationDate
     *         the date of last modification
     */
    public void setLastModificationDate(long lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    /** @return <code>true</code> if object locked and <code>false</code> otherwise */
    public boolean isLocked() {
        return locked;
    }

    /**
     * @param locked
     *         locking flag. Must be <code>true</code> if object locked and <code>false</code> otherwise
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
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
     * Init from JSONObject
     *
     * @param itemObject
     */
    public void init(JSONObject itemObject) {
        id = itemObject.get("id").isString().stringValue();
        name = itemObject.get("name").isString().stringValue();
        mimeType = itemObject.get("mimeType").isString().stringValue();
        //path = itemObject.get("path").isString().stringValue();
        //parentId = itemObject.get("parentId").isString().stringValue();
        creationDate = (long)itemObject.get("creationDate").isNumber().doubleValue();
        links = JSONDeserializer.LINK_DESERIALIZER.toMap(itemObject.get("links"));
        versionId = itemObject.get("versionId").isString().stringValue();
        length = (long)itemObject.get("length").isNumber().doubleValue();
        lastModificationDate = (long)itemObject.get("lastModificationDate").isNumber().doubleValue();
        locked = itemObject.get("locked").isBoolean().booleanValue();
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

    /** @return the history */
    public Array<File> getVersionHistory() {
        return versionHistory;
    }

    /**
     * @param versionHistory
     *         set history
     */
    public void setVersionHistory(Array<File> versionHistory) {
        this.versionHistory = versionHistory;
    }

    /** Clear history */
    public void clearVersionHistory() {
        this.versionHistory = Collections.createArray();
    }

    /** @return lock object */
    public Lock getLock() {
        return lock;
    }

    /**
     * @param set
     *         lock object
     */
    public void setLock(Lock lock) {
        this.lock = lock;
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

    /** @return true if phantom file representing the version */
    public boolean isVersion() {
        return versionId == null ? false : !versionId.equals("0");
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
