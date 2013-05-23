/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
