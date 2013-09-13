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
package org.exoplatform.ide.vfs.server;

import org.everrest.core.impl.MultivaluedMapImpl;
import org.exoplatform.ide.vfs.server.exceptions.NotSupportedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.ItemNode;
import org.exoplatform.ide.vfs.shared.LockToken;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * VirtualFileSystem is designed for usage over REST. Some of operations are not uncomfortable for local usage. Use this class as wrapper
 * for VirtualFileSystem.
 */
public class VirtualFileSystemAdapter {
    private final VirtualFileSystem virtualFileSystem;

    public VirtualFileSystemAdapter(VirtualFileSystem virtualFileSystem) {
        this.virtualFileSystem = virtualFileSystem;
    }

    public Item copy(String id, String parentId) throws VirtualFileSystemException {
        return virtualFileSystem.copy(id, parentId);
    }

    public File createFile(String parentId, String name, String mediaType, InputStream content) throws VirtualFileSystemException {
        return virtualFileSystem.createFile(
                parentId,
                name,
                mediaType == null ? MediaType.APPLICATION_OCTET_STREAM_TYPE : MediaType.valueOf(mediaType),
                content);
    }

    public Folder createFolder(String parentId, String name) throws VirtualFileSystemException {
        return virtualFileSystem.createFolder(parentId, name);
    }

    public Project createProject(String parentId, String name, String type, List<Property> properties) throws VirtualFileSystemException {
        return virtualFileSystem.createProject(parentId, name, type, properties);
    }

    public void delete(String id) throws VirtualFileSystemException {
        delete(id, null);
    }

    public void delete(String id, String lockToken) throws VirtualFileSystemException {
        virtualFileSystem.delete(id, lockToken);
    }

    public List<AccessControlEntry> getACL(String id) throws NotSupportedException, VirtualFileSystemException {
        return virtualFileSystem.getACL(id);
    }

    public ItemList<Item> getChildren(String folderId, boolean includePermissions) throws VirtualFileSystemException {
        return getChildren(folderId, -1, 0, null, includePermissions);
    }

    public ItemList<Item> getChildren(String folderId, String itemType, boolean includePermissions) throws VirtualFileSystemException {
        return getChildren(folderId, -1, 0, itemType, includePermissions);
    }

    public ItemList<Item> getChildren(String folderId, int maxItems, int skipCount, String itemType, boolean includePermissions)
            throws VirtualFileSystemException {
        return virtualFileSystem.getChildren(folderId, maxItems, skipCount, itemType, includePermissions, PropertyFilter.ALL_FILTER);
    }

    public ItemNode getTree(String folderId, int depth, boolean includePermissions) throws VirtualFileSystemException {
        return virtualFileSystem.getTree(folderId, depth, includePermissions, PropertyFilter.ALL_FILTER);
    }

    public ContentStream getContent(String id) throws VirtualFileSystemException {
        return virtualFileSystem.getContent(id);
    }

    public ContentStream getContentByPath(String path) throws VirtualFileSystemException {
        return getContentByPath(path, null);
    }

    public ContentStream getContentByPath(String path, String versionId) throws VirtualFileSystemException {
        return virtualFileSystem.getContent(path, versionId);
    }

    public VirtualFileSystemInfo getInfo() throws VirtualFileSystemException {
        return virtualFileSystem.getInfo();
    }

    public Item getItem(String id, boolean includePermissions) throws VirtualFileSystemException {
        return virtualFileSystem.getItem(id, includePermissions, PropertyFilter.ALL_FILTER);
    }

    public Item getItemByPath(String path, boolean includePermissions) throws VirtualFileSystemException {
        return getItemByPath(path, null, includePermissions);
    }

    public Item getItemByPath(String path, String versionId, boolean includePermissions) throws VirtualFileSystemException {
        return virtualFileSystem.getItemByPath(path, versionId, includePermissions, PropertyFilter.ALL_FILTER);
    }

    public ContentStream getVersion(String id, String versionId) throws VirtualFileSystemException {
        return virtualFileSystem.getVersion(id, versionId);
    }

    public ItemList<File> getVersions(String id) throws VirtualFileSystemException {
        return getVersions(id, -1, 0);
    }

    public ItemList<File> getVersions(String id, int maxItems, int skipCount) throws VirtualFileSystemException {
        return virtualFileSystem.getVersions(id, maxItems, skipCount, PropertyFilter.ALL_FILTER);
    }

    public LockToken lock(String id) throws NotSupportedException, VirtualFileSystemException {
        return virtualFileSystem.lock(id);
    }

    public Item move(String id, String parentId) throws VirtualFileSystemException {
        return move(id, parentId, null);
    }

    public Item move(String id, String parentId, String lockToken) throws VirtualFileSystemException {
        return virtualFileSystem.move(id, parentId, lockToken);
    }

    public Item rename(String id, String mediaType, String newname) throws VirtualFileSystemException {
        return rename(id, mediaType, newname, null);
    }

    public Item rename(String id, String mediaType, String newname, String lockToken) throws VirtualFileSystemException {
        return virtualFileSystem.rename(id, mediaType == null ? null : MediaType.valueOf(mediaType), newname, lockToken);
    }

    public ItemList<Item> search(Map<String, List<String>> query) throws NotSupportedException, VirtualFileSystemException {
        return search(query, -1, 0);
    }

    public ItemList<Item> search(Map<String, List<String>> query, int maxItems, int skipCount)
            throws NotSupportedException, VirtualFileSystemException {
        MultivaluedMap<String, String> myQuery = new MultivaluedMapImpl();
        myQuery.putAll(query);
        return virtualFileSystem.search(myQuery, maxItems, skipCount, PropertyFilter.ALL_FILTER);
    }

    public ItemList<Item> search(String statement) throws NotSupportedException, VirtualFileSystemException {
        return search(statement, -1, 0);
    }

    public ItemList<Item> search(String statement, int maxItems, int skipCount) throws NotSupportedException, VirtualFileSystemException {
        return virtualFileSystem.search(statement, maxItems, skipCount);
    }

    public void unlock(String id, String lockToken) throws NotSupportedException, VirtualFileSystemException {
        virtualFileSystem.unlock(id, lockToken);
    }

    public void updateACL(String id, List<AccessControlEntry> acl, boolean override)
            throws NotSupportedException, VirtualFileSystemException {
        updateACL(id, acl, override, null);
    }

    public void updateACL(String id, List<AccessControlEntry> acl, boolean override, String lockToken)
            throws NotSupportedException, VirtualFileSystemException {
        virtualFileSystem.updateACL(id, acl, override, lockToken);
    }

    public void updateContent(String id, String mediaType, InputStream newcontent) throws VirtualFileSystemException {
        updateContent(id, mediaType, newcontent, null);
    }

    public void updateContent(String id, String mediaType, InputStream newcontent, String lockToken) throws VirtualFileSystemException {
        virtualFileSystem.updateContent(id, mediaType == null ? null : MediaType.valueOf(mediaType), newcontent, lockToken);
    }

    public void updateContentByPath(String path, String mediaType, InputStream newcontent) throws VirtualFileSystemException {
        updateContentByPath(path, mediaType, newcontent, null);
    }

    public void updateContentByPath(String path, String mediaType, InputStream newcontent, String lockToken)
            throws VirtualFileSystemException {
        final Item item = getItemByPath(path, false);
        updateContent(item.getId(), mediaType, newcontent, lockToken);
    }

    public Item updateItem(String id, List<Property> properties) throws VirtualFileSystemException {
        return updateItem(id, properties, null);
    }

    public Item updateItem(String id, List<Property> properties, String lockToken) throws VirtualFileSystemException {
        return virtualFileSystem.updateItem(id, properties, lockToken);
    }

    public ContentStream exportZip(String folderId) throws IOException, VirtualFileSystemException {
        return virtualFileSystem.exportZip(folderId);
    }

    public void importZip(String parentId, InputStream in) throws IOException, VirtualFileSystemException {
        importZip(parentId, in, false);
    }

    public void importZip(String parentId, InputStream in, boolean overwrite) throws IOException, VirtualFileSystemException {
        virtualFileSystem.importZip(parentId, in, overwrite);
    }
}
