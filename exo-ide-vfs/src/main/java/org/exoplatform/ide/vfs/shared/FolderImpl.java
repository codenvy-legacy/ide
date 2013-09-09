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
