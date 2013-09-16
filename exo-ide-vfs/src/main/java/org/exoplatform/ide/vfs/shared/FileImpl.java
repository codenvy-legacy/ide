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
package org.exoplatform.ide.vfs.shared;

import java.util.List;
import java.util.Map;

/**
 * Representation of File object used to interaction with client via JSON.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: FileImpl.java 79657 2012-02-21 07:25:22Z andrew00x $
 */
public class FileImpl extends ItemImpl implements File {
    /** Id of version of file. */
    protected String versionId;

    /** Content length. */
    protected long length = -1;

    /** Date of last modification in long format. */
    protected long lastModificationDate;

    /** Locking flag. */
    protected boolean locked;

    /**
     * Instance of file with specified attributes.
     *
     * @param id
     *         id of object
     * @param name
     *         the name of object
     * @param path
     *         path of object
     * @param parentId
     *         id of parent folder
     * @param creationDate
     *         creation date in long format
     * @param lastModificationDate
     *         date of last modification in long format
     * @param versionId
     *         id of versions of file
     * @param mimeType
     *         media type of content
     * @param length
     *         content length
     * @param locked
     *         is file locked or not
     * @param properties
     *         other properties of file
     * @param links
     *         hyperlinks for retrieved or(and) manage item
     */
    @SuppressWarnings("rawtypes")
    public FileImpl(String vfsId, String id, String name, String path, String parentId, long creationDate, long lastModificationDate,
                    String versionId, String mimeType, long length, boolean locked, List<Property> properties,
                    Map<String, Link> links) {
        super(vfsId, id, name, ItemType.FILE, mimeType, path, parentId, creationDate, properties, links);
        this.lastModificationDate = lastModificationDate;
        this.locked = locked;
        this.versionId = versionId;
        this.length = length;
    }

    /** Empty instance of file. */
    public FileImpl() {
        super(ItemType.FILE);
    }

    @Override
    public String getVersionId() {
        return versionId;
    }

    @Override
    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public void setLength(long length) {
        this.length = length;
    }

    @Override
    public long getLastModificationDate() {
        return lastModificationDate;
    }

    @Override
    public void setLastModificationDate(long lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
