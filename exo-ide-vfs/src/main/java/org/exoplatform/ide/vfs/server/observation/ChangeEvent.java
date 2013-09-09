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
package org.exoplatform.ide.vfs.server.observation;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemUser;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ChangeEvent {
    public static enum ChangeType {
        ACL_UPDATED("acl_updated"),
        CONTENT_UPDATED("content_updated"),
        CREATED("created"),
        DELETED("deleted"),
        MOVED("moved"),
        PROPERTIES_UPDATED("properties_updated"),
        RENAMED("renamed");

        private final String value;

        private ChangeType(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private final VirtualFileSystem     vfs;
    private final String                itemId;
    private final String                itemPath;
    private final String                oldItemPath;
    private final String                mimeType;
    private final ChangeType            type;
    private final VirtualFileSystemUser user;

    public ChangeEvent(VirtualFileSystem vfs,
                       String itemId,
                       String itemPath,
                       String mimeType,
                       ChangeType type,
                       VirtualFileSystemUser user) {
        this(vfs, itemId, itemPath, null, mimeType, type, user);
    }

    public ChangeEvent(VirtualFileSystem vfs,
                       String itemId,
                       String itemPath,
                       String oldItemPath,
                       String mimeType,
                       ChangeType type,
                       VirtualFileSystemUser user) {
        if (vfs == null) {
            throw new IllegalArgumentException("Virtual File System may not be null. ");
        }
        if (itemId == null) {
            throw new IllegalArgumentException("Item ID may not be null. ");
        }
        if (itemPath == null) {
            throw new IllegalArgumentException("Item path may not be null. ");
        }
        if (type == null) {
            throw new IllegalArgumentException("Change type may not be null. ");
        }
        if (user == null) {
            throw new IllegalArgumentException("User may not be null. ");
        }
        this.vfs = vfs;
        this.itemId = itemId;
        this.itemPath = itemPath;
        this.oldItemPath = oldItemPath;
        this.mimeType = mimeType;
        this.type = type;
        this.user = user;
    }

    public VirtualFileSystem getVirtualFileSystem() {
        return vfs;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemPath() {
        return itemPath;
    }

    public String getOldItemPath() {
        return oldItemPath;
    }

    public String getMimeType() {
        return mimeType;
    }

    public ChangeType getType() {
        return type;
    }

    public VirtualFileSystemUser getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "ChangeEvent{" +
               "vfs=" + vfs +
               ", itemId='" + itemId + '\'' +
               ", itemPath='" + itemPath + '\'' +
               ", oldItemPath='" + oldItemPath + '\'' +
               ", mimeType='" + mimeType + '\'' +
               ", type=" + type +
               ", user='" + user + '\'' +
               '}';
    }
}
