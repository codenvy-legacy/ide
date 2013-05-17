/*
 * Copyright (C) 2012 eXo Platform SAS.
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
