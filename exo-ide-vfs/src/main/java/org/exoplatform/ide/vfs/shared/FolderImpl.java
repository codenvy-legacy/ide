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
 * Representation of Folder object used to interaction with client via JSON.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: Folder.java 79657 2012-02-21 07:25:22Z andrew00x $
 */
public class FolderImpl extends ItemImpl implements Folder {

    /**
     * Instance of Folder with specified attributes.
     *
     * @param id
     *         id of object
     * @param name
     *         name of object
     * @param mimeType
     *         media type
     * @param path
     *         path of object
     * @param parentId
     *         id of parent folder. May be <code>null</code> if current folder is root folder
     * @param creationDate
     *         creation date in long format
     * @param properties
     *         other properties of folder
     * @param links
     *         hyperlinks for retrieved or(and) manage item
     */
    @SuppressWarnings("rawtypes")
    public FolderImpl(String vfsId, String id, String name, String mimeType, String path, String parentId, long creationDate,
                      List<Property> properties, Map<String, Link> links) {
        this(vfsId, id, name, ItemType.FOLDER, mimeType, path, parentId, creationDate, properties, links);
    }

    /** Empty instance of Folder. */
    public FolderImpl() {
        this(ItemType.FOLDER);
        mimeType = FOLDER_MIME_TYPE;
    }

    // === For Project ===

    protected FolderImpl(String vfsId, String id, String name, ItemType itemType, String mimeType, String path, String parentId,
                         long creationDate, List<Property> properties, Map<String, Link> links) {
        super(vfsId, id, name, itemType, mimeType, path, parentId, creationDate, properties, links);
    }

    protected FolderImpl(ItemType itemType) {
        super(itemType);
    }

    // ===================

    @Override
    public String createPath(String childName) {
        return this.path + '/' + childName;
    }
}
