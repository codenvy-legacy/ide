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
package org.exoplatform.ide.vfs.server.impl.memory;

import org.apache.commons.fileupload.FileItem;
import org.everrest.core.impl.provider.json.*;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.*;
import org.exoplatform.ide.vfs.server.impl.memory.context.*;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.server.observation.ProjectUpdateListener;
import org.exoplatform.ide.vfs.server.util.LinksHelper;
import org.exoplatform.ide.vfs.server.util.MediaTypes;
import org.exoplatform.ide.vfs.server.util.NotClosableInputStream;
import org.exoplatform.ide.vfs.server.util.ZipContent;
import org.exoplatform.ide.vfs.shared.*;
import org.exoplatform.services.security.ConversationState;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.exoplatform.ide.vfs.server.observation.ChangeEvent.ChangeType;
import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MemoryFileSystem implements VirtualFileSystem {
    private final String                  vfsId;
    private final URI                     baseUri;
    private final MemoryFileSystemContext context;
    private final EventListenerList       listeners;

    private VirtualFileSystemInfo vfsInfo;

    public MemoryFileSystem(URI baseUri, EventListenerList listeners, String vfsId, MemoryFileSystemContext context) {
        this.baseUri = baseUri;
        this.listeners = listeners;
        this.vfsId = vfsId;
        this.context = context;
    }

    @Path("copy/{id}")
    @Override
    public Item copy(@PathParam("id") String id, //
                     @QueryParam("parentId") String parentId) throws VirtualFileSystemException {
        MemoryItem object = getItemById(id);
        MemoryItem parent = getItemById(parentId);
        if (id.equals(parentId)) {
            throw new InvalidArgumentException("Item cannot be copied to itself. ");
        }
        if (!parent.isFolder()) {
            throw new InvalidArgumentException("Unable copy item. Item specified as parent is not a folder. ");
        }
        if (!hasPermissions(parent, BasicPermissions.WRITE.value())) {
            throw new PermissionDeniedException("Unable copy item '" + object.getPath() + "' to " + parent.getPath()
                                                + ". Operation not permitted. ");
        }
        MemoryItem objectCopy = object.copy((MemoryFolder)parent);
        context.putItem(objectCopy);
        Item copy = fromMemoryItem(objectCopy, PropertyFilter.ALL_FILTER);
        if (listeners != null) {
            listeners.notifyListeners(
                    new ChangeEvent(this, copy.getId(), copy.getPath(), copy.getMimeType(), ChangeType.CREATED, getCurrentUserId()));
        }
        return copy;
    }

    @Path("file/{parentId}")
    @Override
    public File createFile(@PathParam("parentId") String parentId, //
                           @QueryParam("name") String name, //
                           @DefaultValue(MediaType.APPLICATION_OCTET_STREAM) @HeaderParam("Content-Type") MediaType mediaType, //
                           InputStream content) throws VirtualFileSystemException {
        checkName(name);
        MemoryItem parent = getItemById(parentId);
        if (!parent.isFolder()) {
            throw new InvalidArgumentException("Unable create new file. Item specified as parent is not a folder. ");
        }
        if (!hasPermissions(parent, BasicPermissions.WRITE.value())) {
            throw new PermissionDeniedException("Unable create file in folder '" + parent.getPath() +
                                                "'. Operation not permitted. ");
        }
        MemoryFile memoryFile;
        try {
            memoryFile = new MemoryFile(name, mediaType.toString(), content);
        } catch (IOException e) {
            throw new VirtualFileSystemException("Unable create file. " + e.getMessage(), e);
        }

        ((MemoryFolder)parent).addChild(memoryFile);
        context.putItem(memoryFile);
        File file = (File)fromMemoryItem(memoryFile, PropertyFilter.ALL_FILTER);
        if (listeners != null) {
            listeners.notifyListeners(
                    new ChangeEvent(this, file.getId(), file.getPath(), file.getMimeType(), ChangeType.CREATED, getCurrentUserId()));
        }
        return file;
    }

    @Path("folder/{parentId}")
    @Override
    public Folder createFolder(@PathParam("parentId") String parentId, //
                               @QueryParam("name") String name) throws VirtualFileSystemException {
        checkName(name);
        MemoryItem parent = getItemById(parentId);
        if (!parent.isFolder()) {
            throw new InvalidArgumentException("Unable create folder. Item specified as parent is not a folder. ");
        }
        if (!hasPermissions(parent, BasicPermissions.WRITE.value())) {
            throw new PermissionDeniedException("Unable create new folder in folder '" + parent.getPath() +
                                                "'. Operation not permitted. ");
        }
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        MemoryFolder memoryFolder = null;
        if (name.indexOf('/') > 0) {
            MemoryFolder current = (MemoryFolder)parent;
            for (String nodeName : name.split("/")) {
                MemoryItem node = current.getChild(nodeName);
                if (node == null) {
                    node = new MemoryFolder(nodeName);
                    current.addChild(node);
                    current = (MemoryFolder)node;
                    memoryFolder = current;
                } else if (node.isFolder()) {
                    current = (MemoryFolder)node;
                } else {
                    throw new ItemAlreadyExistException("Unable create new folder. Item with the name '" +
                                                        node.getPath() + "' already exists and is not a Folder. ");
                }
            }
        } else {
            memoryFolder = new MemoryFolder(name);
            ((MemoryFolder)parent).addChild(memoryFolder);
        }
        context.putItem(memoryFolder);
        Folder folder = (Folder)fromMemoryItem(memoryFolder, PropertyFilter.ALL_FILTER);
        if (listeners != null) {
            listeners.notifyListeners(
                    new ChangeEvent(this, folder.getId(), folder.getPath(), folder.getMimeType(), ChangeType.CREATED, getCurrentUserId()));
        }
        return folder;
    }

    @Path("project/{parentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Project createProject(@PathParam("parentId") String parentId, //
                                 @QueryParam("name") String name, //
                                 @QueryParam("type") String type, //
                                 List<Property> properties) throws VirtualFileSystemException {
        checkName(name);
        MemoryItem parent = getItemById(parentId);
        if (!parent.isFolder()) {
            throw new InvalidArgumentException("Unable create project. Item specified as parent is not a folder. ");
        }
        if (!hasPermissions(parent, BasicPermissions.WRITE.value())) {
            throw new PermissionDeniedException("Unable create new project in folder '" + parent.getPath() +
                                                "'. Operation not permitted. ");
        }
        if (properties == null) {
            properties = new ArrayList<Property>(2);
        }
        if (type != null) {
            properties.add(new PropertyImpl("vfs:projectType", type));
        }
        properties.add(new PropertyImpl("vfs:mimeType", Project.PROJECT_MIME_TYPE));
        MemoryFolder memoryProject = new MemoryFolder(name);
        memoryProject.updateProperties(properties);
        ((MemoryFolder)parent).addChild(memoryProject);
        context.putItem(memoryProject);
        ProjectImpl project = (ProjectImpl)fromMemoryItem(memoryProject, PropertyFilter.ALL_FILTER);
        if (listeners != null) {
            listeners.notifyListeners(
                    new ChangeEvent(this, project.getId(), project.getPath(), project.getMimeType(), ChangeType.CREATED,
                                    getCurrentUserId()));
        }
        return project;
    }


    @Path("delete/{id}")
    @Override
    public void delete(@PathParam("id") String id, //
                       @QueryParam("lockToken") String lockToken) throws VirtualFileSystemException {
        MemoryItem object = getItemById(id);
        if (context.getRoot().equals(object)) {
            throw new VirtualFileSystemException("Unable delete root folder. ");
        }
        if (!hasPermissions(object, BasicPermissions.WRITE.value())) {
            throw new PermissionDeniedException("Unable delete item '" + object.getName() + "'. Operation not permitted. ");
        }
        // For send notification to listeners. Path may not be retrieved after removing item from tree.
        final String deletePath = object.getPath();

        MemoryFolder parent = object.getParent();
        if (object.isFile()) {
            if (!((MemoryFile)object).isLockTokenMatched(lockToken)) {
                throw new LockException("Unable delete item '" + object.getName() + "'. Item is locked. ");
            }
            parent.removeChild(object.getName());
            context.deleteItem(object.getId());
        } else {
            final List<MemoryItem> toDelete = new ArrayList<MemoryItem>();
            object.accept(new MemoryItemVisitor() {
                @Override
                public void visit(MemoryItem i) throws VirtualFileSystemException {
                    if (i.isFolder()) {
                        for (MemoryItem ii : ((MemoryFolder)i).getChildren()) {
                            ii.accept(this);
                        }
                        toDelete.add(i);
                    } else {
                        toDelete.add(i);
                    }
                }
            });

            List<String> ids = new ArrayList<String>(toDelete.size());
            for (MemoryItem i : toDelete) {
                if (!hasPermissions(i, BasicPermissions.WRITE.value())) {
                    throw new PermissionDeniedException("Unable delete item '" + i.getPath() + "'. Operation not permitted. ");
                }
                if (i.isFile() && ((MemoryFile)i).isLocked()) {
                    throw new LockException("Unable delete item '" + object.getName() +
                                            "'. Child item '" + i.getPath() + "' is locked. ");
                }

                ids.add(i.getId());
            }
            // remove tree
            parent.removeChild(object.getName());
            context.deleteItems(ids);
        }
        if (listeners != null) {
            listeners.notifyListeners(
                    new ChangeEvent(this, object.getId(), deletePath, object.getMediaType(), ChangeType.DELETED, getCurrentUserId()));
        }
    }

    @Path("acl/{id}")
    @Override
    public List<AccessControlEntry> getACL(@PathParam("id") String id) throws NotSupportedException, VirtualFileSystemException {
        return getItemById(id).getACL();
    }

    @Path("children/{id}")
    @Override
    public ItemList<Item> getChildren(@PathParam("id") String folderId, //
                                      @DefaultValue("-1") @QueryParam("maxItems") int maxItems, //
                                      @QueryParam("skipCount") int skipCount, //
                                      @QueryParam("itemType") String itemType, //
                                      @DefaultValue(PropertyFilter.NONE) @QueryParam("propertyFilter") PropertyFilter propertyFilter)
            throws VirtualFileSystemException {
        if (skipCount < 0) {
            throw new InvalidArgumentException("'skipCount' parameter is negative. ");
        }

        ItemType itemTypeType;
        if (itemType != null) {
            try {
                itemTypeType = ItemType.fromValue(itemType);
            } catch (IllegalArgumentException e) {
                throw new InvalidArgumentException("Unknown type: " + itemType);
            }
        } else {
            itemTypeType = null;
        }

        MemoryItem object = getItemById(folderId);
        if (!object.isFolder()) {
            throw new InvalidArgumentException("Unable get children. Item '" + object.getName() + "' is not a folder. ");
        }

        MemoryFolder folder = (MemoryFolder)object;
        List<MemoryItem> children = folder.getChildren();
        int totalNumber = children.size();
        if (itemTypeType != null) {
            Iterator<MemoryItem> iterator = children.iterator();
            while (iterator.hasNext()) {
                MemoryItem next = iterator.next();
                if ((itemTypeType == ItemType.FILE && !next.isFile())
                    || (itemTypeType == ItemType.FOLDER && !next.isFolder())
                    || (itemTypeType == ItemType.PROJECT && !next.isProject())) {
                    iterator.remove();
                }
            }
        }
        if (skipCount > 0) {
            if (skipCount > children.size()) {
                throw new InvalidArgumentException("'skipCount' parameter is greater then total number of items. ");
            }
            children.subList(0, skipCount).clear();
        }

        List<Item> l;
        boolean hasMoreItems;
        if (maxItems > 0) {
            l = new ArrayList<Item>();
            Iterator<MemoryItem> iterator = children.iterator();
            for (int count = 0; count < maxItems && iterator.hasNext(); count++) {
                l.add(fromMemoryItem(iterator.next(), propertyFilter));
            }
            hasMoreItems = iterator.hasNext();
        } else {
            l = new ArrayList<Item>(children.size());
            for (MemoryItem aChildren : children) {
                l.add(fromMemoryItem(aChildren, propertyFilter));
            }
            hasMoreItems = false;
        }

        ItemList<Item> il = new ItemListImpl<Item>(l);
        il.setNumItems(totalNumber);
        il.setHasMoreItems(hasMoreItems);

        return il;
    }

    @Path("tree/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Override
    public ItemNode getTree(@PathParam("id") String folderId,
                            @DefaultValue("-1") @QueryParam("depth") int depth,
                            @DefaultValue(PropertyFilter.NONE) @QueryParam("propertyFilter") PropertyFilter propertyFilter)
            throws VirtualFileSystemException {
        MemoryItem object = getItemById(folderId);
        if (!object.isFolder()) {
            throw new InvalidArgumentException("Unable get tree. Item '" + object.getName() + "' is not a folder. ");
        }
        return new ItemNodeImpl(fromMemoryItem(object, propertyFilter), getTreeLevel(object, depth, propertyFilter));
    }

    private List<ItemNode> getTreeLevel(MemoryItem object, int depth, PropertyFilter propertyFilter)
            throws VirtualFileSystemException {
        if (depth == 0 || !object.isFolder()) {
            return null;
        }
        MemoryFolder folder = (MemoryFolder)object;
        List<MemoryItem> children = folder.getChildren();
        List<ItemNode> level = new ArrayList<ItemNode>();
        for (MemoryItem i : children) {
            level.add(new ItemNodeImpl(fromMemoryItem(i, propertyFilter), getTreeLevel(i, depth - 1, propertyFilter)));
        }
        return level;
    }

    @Path("content/{id}")
    @Override
    public ContentStream getContent(@PathParam("id") String id) throws VirtualFileSystemException {
        MemoryItem object = getItemById(id);
        if (!object.isFile()) {
            throw new InvalidArgumentException("Unable get content. Item '" + object.getName() + "' is not a file. ");
        }
        return ((MemoryFile)object).getContent();
    }

    @Path("contentbypath/{path:.*}")
    @Override
    public ContentStream getContent(@PathParam("path") String path, //
                                    @QueryParam("versionId") String versionId) throws VirtualFileSystemException {
        MemoryItem object = getItemByPath(path);
        if (!object.isFile()) {
            throw new InvalidArgumentException("Unable get content. Item '" + path + "' is not a file. ");
        }
        if (!(versionId == null || versionId.equals(((MemoryFile)object).getVersionId()))) {
            throw new NotSupportedException("Versioning is not supported. ");
        }
        return ((MemoryFile)object).getContent();
    }

    @Override
    public VirtualFileSystemInfo getInfo() throws VirtualFileSystemException {
        if (vfsInfo == null) {
            BasicPermissions[] basicPermissions = BasicPermissions.values();
            List<String> permissions = new ArrayList<String>(basicPermissions.length);
            for (BasicPermissions bp : basicPermissions) {
                permissions.add(bp.value());
            }
            Folder root = (Folder)fromMemoryItem(context.getRoot(), PropertyFilter.ALL_FILTER);
            vfsInfo =
                    new VirtualFileSystemInfoImpl(this.vfsId, false, true, VirtualFileSystemInfo.ANONYMOUS_PRINCIPAL,
                                                  VirtualFileSystemInfo.ANY_PRINCIPAL, permissions,
                                                  VirtualFileSystemInfo.ACLCapability.MANAGE,
                                                  VirtualFileSystemInfo.QueryCapability.NONE,
                                                  LinksHelper.createUrlTemplates(baseUri, vfsId), root);
        }
        return vfsInfo;
    }

    @Path("item/{id}")
    @Override
    public Item getItem(@PathParam("id") String id, //
                        @DefaultValue("*") @QueryParam("propertyFilter") PropertyFilter propertyFilter)
            throws VirtualFileSystemException {
        return fromMemoryItem(getItemById(id), propertyFilter);
    }

    @Path("itembypath/{path:.*}")
    @Override
    public Item getItemByPath(@PathParam("path") String path, //
                              @QueryParam("versionId") String versionId, //
                              @DefaultValue("*") @QueryParam("propertyFilter") PropertyFilter propertyFilter)
            throws VirtualFileSystemException {
        MemoryItem object = getItemByPath(path);
        if (object.isFile()) {
            if (!(versionId == null || versionId.equals(((MemoryFile)object).getVersionId()))) {
                throw new NotSupportedException("Versioning is not supported. ");
            }
        } else if (versionId != null) {
            throw new InvalidArgumentException("Object " + path + " is not a file. Version ID must not be set. ");
        }

        return fromMemoryItem(object, propertyFilter);
    }

    @Path("version/{id}/{versionId}")
    @Override
    public ContentStream getVersion(@PathParam("id") String id, //
                                    @PathParam("versionId") String versionId) throws VirtualFileSystemException {
        MemoryItem object = getItemById(id);
        if (!object.isFile()) {
            throw new InvalidArgumentException("Object '" + object.getName() + "' is not a file. ");
        }
        if (!(versionId == null || versionId.equals(((MemoryFile)object).getVersionId()))) {
            throw new NotSupportedException("Versioning is not supported. ");
        }
        return ((MemoryFile)object).getContent();
    }

    @Path("version-history/{id}")
    @Override
    public ItemList<File> getVersions(@PathParam("id") String id, //
                                      @DefaultValue("-1") @QueryParam("maxItems") int maxItems, //
                                      @QueryParam("skipCount") int skipCount, //
                                      @DefaultValue("*") @QueryParam("propertyFilter") PropertyFilter propertyFilter //
                                     ) throws VirtualFileSystemException {
        if (skipCount < 0) {
            throw new InvalidArgumentException("'skipCount' parameter is negative. ");
        }
        if (skipCount > 1) {
            // Since we don't support versioning we always have only one version.
            throw new InvalidArgumentException("'skipCount' parameter is greater then total number of items. ");
        }

        MemoryItem object = getItemById(id);
        if (!object.isFile()) {
            throw new InvalidArgumentException("Object '" + object.getName() + "' is not a file. ");
        }
        ItemList<File> l;
        if (maxItems < 0 || (maxItems - skipCount) > 0) {
            l = new ItemListImpl<File>(Collections.singletonList((File)fromMemoryItem(object, propertyFilter)));
            l.setHasMoreItems(false);
        } else {
            l = new ItemListImpl<File>(Collections.<File>emptyList());
            l.setHasMoreItems(false);
        }
        return l;
    }

    @Path("lock/{id}")
    @Override
    public LockToken lock(@PathParam("id") String id) throws NotSupportedException, VirtualFileSystemException {
        MemoryItem object = getItemById(id);
        if (!object.isFile()) {
            throw new InvalidArgumentException("Locking allowed for Files only. ");
        }
        if (!hasPermissions(object, BasicPermissions.WRITE.value())) {
            throw new PermissionDeniedException("Unable lock item '" + object.getName() + "'. Operation not permitted. ");
        }
        return new LockTokenImpl(((MemoryFile)object).lock());
    }

    @Path("move/{id}")
    @Override
    public Item move(@PathParam("id") String id, //
                     @QueryParam("parentId") String parentId, //
                     @QueryParam("lockToken") String lockToken) throws VirtualFileSystemException {
        MemoryItem object = getItemById(id);
        MemoryItem parent = getItemById(parentId);
        if (context.getRoot().equals(object)) {
            throw new InvalidArgumentException("Unable move root folder. ");
        }
        if (id.equals(parentId)) {
            throw new InvalidArgumentException("Item cannot be moved to itself. ");
        }
        if (!parent.isFolder()) {
            throw new InvalidArgumentException("Unable move item. Item specified as parent is not a folder. ");
        }
        if (!(hasPermissions(parent, BasicPermissions.WRITE.value())
              && hasPermissions(object, BasicPermissions.WRITE.value()))) {
            throw new PermissionDeniedException("Unable move item '" + object.getPath() + "' to " + parent.getPath()
                                                + ". Operation not permitted. ");
        }

        final String oldPath = object.getPath();
        if (object.isFolder()) {
            // Be sure destination folder is not child (direct or not) of moved item.
            final String destPath = parent.getPath();
            if (destPath.startsWith(oldPath)) {
                throw new InvalidArgumentException("Unable move item " + oldPath + " to " + destPath +
                                                   ". Item may not have itself as parent. ");
            }
            final List<MemoryItem> forMove = new ArrayList<MemoryItem>();
            object.accept(new MemoryItemVisitor() {
                @Override
                public void visit(MemoryItem i) throws VirtualFileSystemException {
                    if (i.isFolder()) {
                        for (MemoryItem ii : ((MemoryFolder)i).getChildren()) {
                            ii.accept(this);
                        }
                        forMove.add(i);
                    } else {
                        forMove.add(i);
                    }
                }
            });

            for (MemoryItem i : forMove) {
                if (!hasPermissions(i, BasicPermissions.WRITE.value())) {
                    throw new PermissionDeniedException("Unable move item '" + i.getPath() + "'. Operation not permitted. ");
                }
                if (i.isFile() && !((MemoryFile)i).isLockTokenMatched(lockToken)) {
                    throw new LockException("Unable move item '" + object.getName() +
                                            "'. Child item '" + i.getPath() + "' is locked. ");
                }
            }
        } else {
            if (!((MemoryFile)object).isLockTokenMatched(lockToken)) {
                throw new LockException("Unable move item " + object.getName() + ". Item is locked. ");
            }
        }

        final String name = object.getName();
        object.getParent().removeChild(name);
        ((MemoryFolder)parent).addChild(object);

        Item moved = fromMemoryItem(object, PropertyFilter.ALL_FILTER);
        if (listeners != null) {
            listeners.notifyListeners(
                    new ChangeEvent(this, moved.getId(), moved.getPath(), oldPath, moved.getMimeType(), ChangeType.MOVED,
                                    getCurrentUserId()));
        }
        return moved;
    }

    @Path("rename/{id}")
    @Override
    public Item rename(@PathParam("id") String id, //
                       @QueryParam("mediaType") MediaType newMediaType, //
                       @QueryParam("newname") String newName, //
                       @QueryParam("lockToken") String lockToken) throws VirtualFileSystemException {
        MemoryItem object = getItemById(id);
        if (context.getRoot().equals(object)) {
            throw new InvalidArgumentException("Unable rename root folder. ");
        }

        if (!hasPermissions(object, BasicPermissions.WRITE.value())) {
            throw new PermissionDeniedException("Unable rename item " + object.getName() + ". Operation not permitted. ");
        }

        if (object.isFile() && !((MemoryFile)object).isLockTokenMatched(lockToken)) {
            throw new LockException("Unable rename item " + object.getName() + ". Item is locked. ");
        }

        if ((newName == null || newName.isEmpty()) && newMediaType == null) {
            // Nothing to do. Return unchanged object.
            return fromMemoryItem(object, PropertyFilter.ALL_FILTER);
        }

        final MemoryFolder parent = object.getParent();
        final String oldPath = object.getPath();
        if (!(newName == null || newName.isEmpty())) {
            String name = object.getName();
            if (!name.equals(newName)) {
                parent.renameChild(name, newName);
            }
            if (newMediaType != null) {
                object.setMediaType(newMediaType.toString());
            }
        } else {
            if (newMediaType != null) {
                object.setMediaType(newMediaType.toString());
            }
        }

        Item renamed = fromMemoryItem(object, PropertyFilter.ALL_FILTER);
        if (listeners != null) {
            listeners.notifyListeners(new ChangeEvent(
                    this, renamed.getId(), renamed.getPath(), oldPath, renamed.getMimeType(), ChangeType.RENAMED, getCurrentUserId()));
        }
        return renamed;
    }

    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Override
    public ItemList<Item> search(MultivaluedMap<String, String> query, //
                                 @DefaultValue("-1") @QueryParam("maxItems") int maxItems, //
                                 @QueryParam("skipCount") int skipCount, //
                                 @DefaultValue("*") @QueryParam("propertyFilter") PropertyFilter propertyFilter //
                                ) throws NotSupportedException, VirtualFileSystemException {
        throw new NotSupportedException("Not supported. ");
    }

    @Override
    public ItemList<Item> search(@QueryParam("statement") String statement, //
                                 @DefaultValue("-1") @QueryParam("maxItems") int maxItems, //
                                 @QueryParam("skipCount") int skipCount //
                                ) throws NotSupportedException, VirtualFileSystemException {
        throw new NotSupportedException("Not supported. ");
    }

    @Path("unlock/{id}")
    @Override
    public void unlock(@PathParam("id") String id, //
                       @QueryParam("lockToken") String lockToken //
                      ) throws NotSupportedException, VirtualFileSystemException {
        MemoryItem object = getItemById(id);
        if (!hasPermissions(object, BasicPermissions.WRITE.value())) {
            throw new PermissionDeniedException("Unable unlock item '" + object.getName() +
                                                "'. Operation not permitted. ");
        }
        if (!object.isFile()) {
            throw new LockException("Object is not locked. "); // Folder can't be locked.
        }
        ((MemoryFile)object).unlock(lockToken);
    }

    @Path("acl/{id}")
    @Override
    public void updateACL(@PathParam("id") String id, //
                          List<AccessControlEntry> acl, //
                          @DefaultValue("false") @QueryParam("override") Boolean override, //
                          @QueryParam("lockToken") String lockToken //
                         ) throws NotSupportedException, VirtualFileSystemException {
        MemoryItem object = getItemById(id);
        if (object.isFile() && !((MemoryFile)object).isLockTokenMatched(lockToken)) {
            throw new LockException("Unable update ACL of item '" + object.getName() + "'. Item is locked. ");
        }
        if (!hasPermissions(object, BasicPermissions.WRITE.value())) {
            throw new PermissionDeniedException("Unable update ACL of item '" + object.getName() +
                                                "'. Operation not permitted. ");
        }
        object.updateACL(acl, override);
    }

    @Path("content/{id}")
    @Override
    public void updateContent(
            @PathParam("id") String id, //
            @DefaultValue(MediaType.APPLICATION_OCTET_STREAM) @HeaderParam("Content-Type") MediaType mediaType,
            InputStream newContent, //
            @QueryParam("lockToken") String lockToken) throws VirtualFileSystemException {
        MemoryItem object = getItemById(id);
        if (!object.isFile()) {
            throw new InvalidArgumentException("Object " + object.getName() + " is not file. ");
        }
        if (!((MemoryFile)object).isLockTokenMatched(lockToken)) {
            throw new LockException("Unable update content of file '" + object.getName() + "'. File is locked. ");
        }
        if (!hasPermissions(object, BasicPermissions.WRITE.value())) {
            throw new PermissionDeniedException("Unable update content of file '" + object.getName() +
                                                "'. Operation not permitted. ");
        }
        try {
            ((MemoryFile)object).setContent(newContent);
        } catch (IOException e) {
            throw new VirtualFileSystemException("Unable update content of file. " + e.getMessage(), e);
        }
        object.setMediaType(mediaType == null ? null : mediaType.toString());
        if (listeners != null) {
            listeners.notifyListeners(new ChangeEvent(
                    this, object.getId(), object.getPath(), object.getMediaType(), ChangeType.CONTENT_UPDATED, getCurrentUserId()));
        }
    }

    @Path("item/{id}")
    @Override
    public Item updateItem(@PathParam("id") String id, //
                           List<Property> properties, //
                           @QueryParam("lockToken") String lockToken) throws VirtualFileSystemException {
        MemoryItem object = getItemById(id);

        if (object.isFile() && !((MemoryFile)object).isLockTokenMatched(lockToken)) {
            throw new LockException("Unable update item '" + object.getName() + "'. Item is locked. ");
        }
        if (!hasPermissions(object, BasicPermissions.WRITE.value())) {
            throw new PermissionDeniedException("Unable update item '" + object.getName() + "'. Operation not permitted. ");
        }

        object.updateProperties(properties);
        Item updated = fromMemoryItem(object, PropertyFilter.ALL_FILTER);
        if (listeners != null) {
            listeners.notifyListeners(new ChangeEvent(
                    this, updated.getId(), updated.getPath(), updated.getMimeType(), ChangeType.PROPERTIES_UPDATED, getCurrentUserId()));
        }
        return updated;
    }

    @Path("export/{folderId}")
    @Override
    public ContentStream exportZip(@PathParam("folderId") String folderId) throws IOException, VirtualFileSystemException {
        MemoryItem object = getItemById(folderId);
        if (!object.isFolder()) {
            throw new InvalidArgumentException("Unable export to zip. Item is not a folder. ");
        }
        MemoryFolder exportFolder = (MemoryFolder)object;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ZipOutputStream zipOut = new ZipOutputStream(out);
            if (exportFolder.isProject()) {
                zipOut.putNextEntry(new ZipEntry(".project"));
                try {
                    JsonWriter jw = new JsonWriter(zipOut);
                    JsonGenerator.createJsonArray(object.getProperties(PropertyFilter.ALL_FILTER)).writeTo(jw);
                    jw.flush();
                } catch (JsonException e) {
                    throw new VirtualFileSystemException(e.getMessage(), e);
                }
            }
            LinkedList<MemoryFolder> q = new LinkedList<MemoryFolder>();
            q.add(exportFolder);
            final String rootZipPath = exportFolder.getPath();
            byte[] b = new byte[1024];
            while (!q.isEmpty()) {
                List<MemoryItem> children = q.pop().getChildren();
                for (MemoryItem current : children) {
                    final String zipEntryName = current.getPath().substring(rootZipPath.length() + 1);
                    if (current.isFile()) {
                        zipOut.putNextEntry(new ZipEntry(zipEntryName));
                        InputStream in = null;
                        try {
                            in = ((MemoryFile)current).getContent().getStream();
                            int r;
                            while ((r = in.read(b)) != -1) {
                                zipOut.write(b, 0, r);
                            }
                        } finally {
                            if (in != null) {
                                in.close();
                            }
                        }
                    } else if (current.isProject()) {
                        zipOut.putNextEntry(new ZipEntry(zipEntryName + '/'));
                        zipOut.putNextEntry(new ZipEntry(zipEntryName + "/.project"));
                        try {
                            JsonWriter jw = new JsonWriter(zipOut);
                            JsonGenerator.createJsonArray(current.getProperties(PropertyFilter.ALL_FILTER)).writeTo(jw);
                            jw.flush();
                        } catch (JsonException e) {
                            throw new VirtualFileSystemException(e.getMessage(), e);
                        }
                        q.add((MemoryFolder)current);
                    } else {
                        zipOut.putNextEntry(new ZipEntry(zipEntryName + '/'));
                        q.add((MemoryFolder)current);
                    }
                    zipOut.closeEntry();
                }
            }
            zipOut.close();
        } finally {
            out.close();
        }

        byte[] zipped = out.toByteArray();
        return new ContentStream(exportFolder.getName() + ".zip", //
                                 new ByteArrayInputStream(zipped), //
                                 "application/zip", //
                                 zipped.length, //
                                 new Date());
    }

    @Path("import/{parentId}")
    @Override
    public void importZip(@PathParam("parentId") String parentId, //
                          InputStream in, //
                          @DefaultValue("false") @QueryParam("overwrite") Boolean overwrite //
                         ) throws VirtualFileSystemException, IOException {
        ZipInputStream zip = null;
        try {
            MemoryItem object = getItemById(parentId);
            if (!object.isFolder()) {
                throw new InvalidArgumentException("Unable import from zip. Item specified as parent is not a folder. ");
            }

            final ZipContent zipContent = ZipContent.newInstance(in);

            zip = new ZipInputStream(zipContent.zippedData);
            // Wrap zip stream to prevent close it. We can pass stream to other method and it can read content of current
            // ZipEntry but not able to close original stream of ZIPed data.
            InputStream noCloseZip = new NotClosableInputStream(zip);
            MemoryFolder folder = (MemoryFolder)object;
            ZipEntry zipEntry;
            while ((zipEntry = zip.getNextEntry()) != null) {
                String zipEntryName = zipEntry.getName();
                String[] segments = zipEntryName.split("/");
                MemoryFolder current = folder;
                for (int i = 0, l = segments.length - 1; i < l; i++) {
                    MemoryItem child = current.getChild(segments[i]);
                    if (child == null) {
                        child = new MemoryFolder(segments[i]);
                        current.addChild(child);
                    }
                    current = (MemoryFolder)child;
                }

                final String name = segments[segments.length - 1];
                if (zipEntry.isDirectory()) {
                    if (current.getChild(name) == null) {
                        current.addChild(new MemoryFolder(name));
                    }
                } else if (".project".equals(name)) {
                    JsonParser jp = new JsonParser();
                    jp.parse(noCloseZip);
                    Property[] array = (Property[])ObjectBuilder.createArray(PropertyImpl[].class, jp.getJsonObject());
                    if (array.length > 0) {
                        List<Property> list = new ArrayList<Property>(array.length);
                        Collections.addAll(list, array);
                        boolean hasMimeType = false;
                        for (int i = 0, size = list.size(); i < size && !hasMimeType; i++) {
                            Property property = list.get(i);
                            if ("vfs:mimeType".equals(property.getName())
                                && !(property.getValue() == null || property.getValue().isEmpty())) {
                                hasMimeType = true;
                            }
                        }

                        if (!hasMimeType) {
                            list.add(new PropertyImpl("vfs:mimeType", Project.PROJECT_MIME_TYPE));
                        }

                        current.updateProperties(list);
                    } else {
                        current.updateProperties(
                                Collections.<Property>singletonList(new PropertyImpl("vfs:mimeType", Project.PROJECT_MIME_TYPE)));
                    }
                } else {
                    MemoryItem child = current.getChild(name);
                    if (child != null && overwrite && child.isFile()) {
                        if (((MemoryFile)child).isLocked()) {
                            throw new LockException("Unable import from zip, item '" + child.getPath() + "'. Item is locked. ");
                        }
                        if (!hasPermissions(child, BasicPermissions.WRITE.value())) {
                            throw new LockException(
                                    "Unable import from zip, cannot overwrite '" + child.getPath() + "'. Operation not permitted. ");
                        }
                        ((MemoryFile)child).setContent(noCloseZip);
                    } else {
                        MemoryFile f = new MemoryFile(name, MediaTypes.INSTANCE.getMediaType(name), noCloseZip);
                        current.addChild(f);
                    }
                }
                zip.closeEntry();
            }
            context.putItem(folder);
        } catch (JsonException e) {
            throw new VirtualFileSystemException(e.getMessage(), e);
        } finally {
            if (zip != null) {
                zip.close();
            }
        }
    }

    @Path("downloadfile/{id}")
    @Override
    public Response downloadFile(@PathParam("id") String id) throws VirtualFileSystemException {
        ContentStream content = getContent(id);
        return Response //
                .ok(content.getStream(), content.getMimeType()) //
                .lastModified(content.getLastModificationDate()) //
                .header(HttpHeaders.CONTENT_LENGTH, Long.toString(content.getLength())) //
                .header("Content-Disposition", "attachment; filename=\"" + content.getFileName() + "\"") //
                .build();
    }

    @Path("uploadfile/{parentId}")
    @Override
    public Response uploadFile(@PathParam("parentId") String parentId, //
                               java.util.Iterator<FileItem> formData //
                              ) throws VirtualFileSystemException, IOException {
        try {
            MemoryItem parent = getItemById(parentId);
            if (!parent.isFolder()) {
                throw new InvalidArgumentException("Unable upload file. Item specified as parent is not a folder. ");
            }
            if (!hasPermissions(parent, BasicPermissions.WRITE.value())) {
                throw new PermissionDeniedException("Unable upload file in folder '" + parent.getPath() +
                                                    "'. Operation not permitted. ");
            }

            FileItem contentItem = null;
            MediaType mediaType = null;
            String name = null;
            boolean overwrite = false;

            while (formData.hasNext()) {
                FileItem item = formData.next();
                if (!item.isFormField()) {
                    if (contentItem == null) {
                        contentItem = item;
                    } else {
                        throw new InvalidArgumentException("More then one upload file is found but only one should be. ");
                    }
                } else if ("mimeType".equals(item.getFieldName())) {
                    String m = item.getString().trim();
                    if (m.length() > 0) {
                        mediaType = MediaType.valueOf(m);
                    }
                } else if ("name".equals(item.getFieldName())) {
                    name = item.getString().trim();
                } else if ("overwrite".equals(item.getFieldName())) {
                    overwrite = Boolean.parseBoolean(item.getString().trim());
                }
            }

            if (contentItem == null) {
                throw new InvalidArgumentException("Cannot find file for upload. ");
            }

            if (name == null || name.isEmpty()) {
                name = contentItem.getName();
            }

            if (mediaType == null) {
                String contentType = contentItem.getContentType();
                mediaType = contentType != null ? MediaType.valueOf(contentType) : MediaType.APPLICATION_OCTET_STREAM_TYPE;
            }
            try {
                MemoryFile file = new MemoryFile(name, mediaType.toString(), contentItem.getInputStream());
                ((MemoryFolder)parent).addChild(file);
                context.putItem(file);
                if (listeners != null) {
                    listeners.notifyListeners(new ChangeEvent(
                            this, file.getId(), file.getPath(), file.getMediaType(), ChangeType.CREATED, getCurrentUserId()));
                }
            } catch (ItemAlreadyExistException e) {
                if (!overwrite) {
                    throw new ItemAlreadyExistException("Unable upload file. File with the same name exists. ");
                }
                MemoryItem object = ((MemoryFolder)parent).getChild(name);
                if (!object.isFile()) {
                    throw new ItemAlreadyExistException(
                            "Unable upload file. Item with the same name exists but it is not a file. ");
                }
                MemoryFile file = (MemoryFile)object;
                file.setMediaType(mediaType.toString());
                file.setContent(contentItem.getInputStream());
                if (listeners != null) {
                    listeners.notifyListeners(new ChangeEvent(
                            this, file.getId(), file.getPath(), file.getMediaType(), ChangeType.CONTENT_UPDATED, getCurrentUserId()));
                }
            }
            return Response.ok("", MediaType.TEXT_HTML).build();
        } catch (VirtualFileSystemException e) {
            HtmlErrorFormatter.sendErrorAsHTML(e);
            // never thrown
            throw e;
        } catch (IOException e) {
            HtmlErrorFormatter.sendErrorAsHTML(e);
            // never thrown
            throw e;
        }
    }

    @Path("downloadzip/{folderId}")
    @Override
    public Response downloadZip(@PathParam("folderId") String folderId) throws IOException, VirtualFileSystemException {
        ContentStream zip = exportZip(folderId);
        return Response //
                .ok(zip.getStream(), zip.getMimeType()) //
                .lastModified(zip.getLastModificationDate()) //
                .header(HttpHeaders.CONTENT_LENGTH, Long.toString(zip.getLength())) //
                .header("Content-Disposition", "attachment; filename=\"" + zip.getFileName() + "\"") //
                .build();
    }

    @Path("uploadzip/{parentId}")
    @Override
    public Response uploadZip(@PathParam("parentId") String parentId, //
                              Iterator<FileItem> formData) throws IOException, VirtualFileSystemException {
        try {
            FileItem contentItem = null;
            boolean overwrite = false;
            while (formData.hasNext()) {
                FileItem item = formData.next();
                if (!item.isFormField()) {
                    if (contentItem == null) {
                        contentItem = item;
                    } else {
                        throw new InvalidArgumentException("More then one upload file is found but only one should be. ");
                    }
                } else if ("overwrite".equals(item.getFieldName())) {
                    overwrite = Boolean.parseBoolean(item.getString().trim());
                }
            }
            if (contentItem == null) {
                throw new InvalidArgumentException("Cannot find file for upload. ");
            }
            importZip(parentId, contentItem.getInputStream(), overwrite);
            return Response.ok("", MediaType.TEXT_HTML).build();
        } catch (VirtualFileSystemException e) {
            HtmlErrorFormatter.sendErrorAsHTML(e);
            // never thrown
            throw e;
        } catch (IOException e) {
            HtmlErrorFormatter.sendErrorAsHTML(e);
            // never thrown
            throw e;
        }
    }

    @Path("watch/start/{projectId}")
    @Override
    public void startWatchUpdates(@PathParam("projectId") String projectId) throws VirtualFileSystemException {
        if (listeners == null) {
            throw new VirtualFileSystemException("EventListenerList is not configured properly. ");
        }
        MemoryItem object = getItemById(projectId);
        if (!object.isProject()) {
            throw new InvalidArgumentException("Item is not a project. ");
        }
        if (listeners.addEventListener(
                ProjectUpdateEventFilter.newFilter(vfsId, (MemoryFolder)object), new ProjectUpdateListener(projectId))) {
            object.updateProperties(Arrays.<Property>asList(new PropertyImpl("vfs:lastUpdateTime", "0")));
        }
    }

    @Path("watch/stop/{projectId}")
    @Override
    public void stopWatchUpdates(@PathParam("projectId") String projectId) throws VirtualFileSystemException {
        if (listeners != null) {
            MemoryItem object = getItemById(projectId);
            if (!object.isProject()) {
                return;
            }
            if (!listeners.removeEventListener(ProjectUpdateEventFilter.newFilter(vfsId, (MemoryFolder)object),
                                               new ProjectUpdateListener(projectId))) {
                throw new InvalidArgumentException("'" + object.getName() + "' is not under watching. ");
            }
        }
    }

   /* =========================================================================== */
   /* =========================================================================== */

    private MemoryItem getItemById(String id) throws VirtualFileSystemException {
        MemoryItem object = context.getItem(id);
        if (object == null) {
            throw new ItemNotFoundException("Object '" + id + "' does not exists. ");
        }
        if (!hasPermissions(object, BasicPermissions.READ.value())) {
            throw new PermissionDeniedException("Access denied to object " + id + ". ");
        }
        return object;
    }

    private boolean hasPermissions(MemoryItem object, String... permissions) {
        return hasPermissions(object, getCurrentUserId(), Arrays.asList(permissions));
    }

    private boolean hasPermissions(MemoryItem object, String userId, Collection<String> checkPermissions) {
        Map<String, Set<String>> objectPermissions = object.getPermissions();
        Set<String> anyUserPermissions = objectPermissions.get(VirtualFileSystemInfo.ANY_PRINCIPAL);
        if (anyUserPermissions != null
            && (anyUserPermissions.contains(BasicPermissions.ALL.value())
                || anyUserPermissions.containsAll(checkPermissions))) {
            return true;
        }
        Set<String> userPermissions = objectPermissions.get(userId);
        return userPermissions != null
               && (userPermissions.contains(BasicPermissions.ALL.value())
                   || userPermissions.containsAll(checkPermissions));
    }

    private String getCurrentUserId() {
        ConversationState cs = ConversationState.getCurrent();
        if (cs != null) {
            return cs.getIdentity().getUserId();
        }
        return VirtualFileSystemInfo.ANONYMOUS_PRINCIPAL;
    }

    private MemoryItem getItemByPath(String path) throws VirtualFileSystemException {
        MemoryItem object = context.getItemByPath(path);
        if (object == null) {
            throw new ItemNotFoundException("Object '" + path + "' does not exists. ");
        }
        if (!hasPermissions(object, BasicPermissions.READ.value())) {
            throw new PermissionDeniedException("Access denied to object " + path + ". ");
        }
        return object;
    }

    private void checkName(String name) throws InvalidArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidArgumentException("Item's name is not set. ");
        }
    }

    private Item fromMemoryItem(MemoryItem memoryItem, PropertyFilter propertyFilter) throws VirtualFileSystemException {
        return fromMemoryItem(memoryItem, propertyFilter, true);
    }

    private Item fromMemoryItem(MemoryItem object, PropertyFilter propertyFilter, boolean addLinks)
            throws VirtualFileSystemException {
        final String id = object.getId();
        final String name = object.getName();
        final String path = object.getPath();
        final boolean isRoot = object.getParent() == null;
        final String parentId = isRoot ? null : object.getParent().getId();
        final String mediaType = object.getMediaType();
        final long created = object.getCreationDate();
        if (object.isFile()) {
            MemoryFile file = (MemoryFile)object;
            final String versionId = file.getVersionId();
            final String latestVersionId = file.getLatestVersionId();
            final boolean locked = file.isLocked();
            final long length = file.getContent().getLength();
            final long modified = file.getLastModificationDate();
            return new FileImpl(id, name, path, parentId, created, modified, versionId, mediaType, length,
                                locked, file.getProperties(propertyFilter),
                                addLinks ? LinksHelper
                                        .createFileLinks(baseUri, vfsId, id, latestVersionId, path, mediaType, locked, parentId) : null);
        }

        MemoryFolder folder = (MemoryFolder)object;
        if (folder.isProject()) {
            String projectType = null;
            List<Property> properties = folder.getProperties(PropertyFilter.valueOf("vfs:projectType"));
            if (!properties.isEmpty()) {
                List<String> values = properties.get(0).getValue();
                if (!(values == null || values.isEmpty())) {
                    projectType = values.get(0);
                }
            }

            return new ProjectImpl(id, name, mediaType, path, parentId, created, folder.getProperties(propertyFilter),
                                   addLinks ? LinksHelper.createProjectLinks(baseUri, vfsId, id, parentId) : null, projectType);
        }

        return new FolderImpl(id, name, mediaType == null ? Folder.FOLDER_MIME_TYPE : mediaType, path, parentId, created,
                              object.getProperties(propertyFilter),
                              addLinks ? LinksHelper.createFolderLinks(baseUri, vfsId, id, isRoot, parentId) : null);
    }

    @Override
    public String toString() {
        return "MemoryFileSystem{" +
               "vfsId='" + vfsId + '\'' +
               ", baseUri=" + baseUri +
               '}';
    }
}
