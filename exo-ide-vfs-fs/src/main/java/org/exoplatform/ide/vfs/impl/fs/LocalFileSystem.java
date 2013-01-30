/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.impl.fs;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemFactory;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemAlreadyExistException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.LockException;
import org.exoplatform.ide.vfs.server.exceptions.NotSupportedException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.FileImpl;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.FolderImpl;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.ItemListImpl;
import org.exoplatform.ide.vfs.shared.ItemNode;
import org.exoplatform.ide.vfs.shared.ItemNodeImpl;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.LinkImpl;
import org.exoplatform.ide.vfs.shared.LockToken;
import org.exoplatform.ide.vfs.shared.LockTokenImpl;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.ProjectImpl;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfoImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class LocalFileSystem implements VirtualFileSystem
{
   private static final String ROOT_ID = "$root$";
   private static final String FAKE_VERSION_ID = "0";

   private final String vfsId;
   private final URI baseUri;
   private final EventListenerList listeners;
   private final MountPoint mountPoint;

   private VirtualFileSystemInfoImpl vfsInfo;

   public LocalFileSystem(String vfsId, URI baseUri, EventListenerList listeners, MountPoint mountPoint)
   {
      this.vfsId = vfsId;
      this.baseUri = baseUri;
      this.listeners = listeners;
      this.mountPoint = mountPoint;
   }

   @Path("copy/{id}")
   @Override
   public Item copy(@PathParam("id") String id, //
                    @QueryParam("parentId") String parentId) throws VirtualFileSystemException
   {
      VirtualFile virtualFileCopy = idToVirtualFile(id).copyTo(idToVirtualFile(parentId));
      Item copy = fromVirtualFile(virtualFileCopy, PropertyFilter.ALL_FILTER);
      if (listeners != null)
      {
         listeners.notifyListeners(
            new ChangeEvent(this, copy.getId(), copy.getPath(), copy.getMimeType(), ChangeEvent.ChangeType.CREATED));
      }
      return copy;
   }

   @Path("file/{parentId}")
   @Override
   public File createFile(@PathParam("parentId") String parentId, //
                          @QueryParam("name") String name, //
                          @DefaultValue(MediaType.APPLICATION_OCTET_STREAM) @HeaderParam("Content-Type") MediaType mediaType, //
                          InputStream content) throws VirtualFileSystemException
   {
      VirtualFile newVirtualFile = idToVirtualFile(parentId)
         .createFile(name, mediaType != null ? mediaType.toString() : null, content);
      File file = (File)fromVirtualFile(newVirtualFile, PropertyFilter.ALL_FILTER);
      if (listeners != null)
      {
         listeners.notifyListeners(
            new ChangeEvent(this, file.getId(), file.getPath(), file.getMimeType(), ChangeEvent.ChangeType.CREATED));
      }
      return file;
   }

   @Path("folder/{parentId}")
   @Override
   public Folder createFolder(@PathParam("parentId") String parentId, //
                              @QueryParam("name") String name) throws VirtualFileSystemException
   {
      VirtualFile newVirtualFile = idToVirtualFile(parentId).createFolder(name);
      Folder folder = (Folder)fromVirtualFile(newVirtualFile, PropertyFilter.ALL_FILTER);
      if (listeners != null)
      {
         listeners.notifyListeners(new ChangeEvent(this, folder.getId(), folder.getPath(), folder.getMimeType(),
            ChangeEvent.ChangeType.CREATED));
      }
      return folder;
   }

   @Override
   public Project createProject(String parentId, String name, String type, List<Property> properties)
      throws VirtualFileSystemException
   {
      VirtualFile newVirtualFile = idToVirtualFile(parentId).createProject(name, properties);
      Project project = (Project)fromVirtualFile(newVirtualFile, PropertyFilter.ALL_FILTER);
      if (listeners != null)
      {
         listeners.notifyListeners(new ChangeEvent(this, project.getId(), project.getPath(), project.getMimeType(),
            ChangeEvent.ChangeType.CREATED));
      }
      return project;
   }

   @Path("delete/{id}")
   @Override
   public void delete(@PathParam("id") String id, //
                      @QueryParam("lockToken") String lockToken) throws VirtualFileSystemException
   {
      VirtualFile virtualFile = idToVirtualFile(id);

      final String path = virtualFile.getPath();
      final String mediaType = virtualFile.getMediaType();

      virtualFile.delete(lockToken);

      if (listeners != null)
      {
         listeners.notifyListeners(new ChangeEvent(this, id, path, mediaType, ChangeEvent.ChangeType.DELETED));
      }
   }

   @Path("acl/{id}")
   @Override
   public List<AccessControlEntry> getACL(@PathParam("id") String id) throws VirtualFileSystemException
   {
      return idToVirtualFile(id).getACL();
   }

   @Path("children/{id}")
   @Override
   public ItemList<Item> getChildren(@PathParam("id") String folderId, //
                                     @DefaultValue("-1") @QueryParam("maxItems") int maxItems, //
                                     @QueryParam("skipCount") int skipCount, //
                                     @QueryParam("itemType") String itemType, //
                                     @DefaultValue(PropertyFilter.NONE) @QueryParam("propertyFilter") PropertyFilter propertyFilter)
      throws VirtualFileSystemException
   {
      if (skipCount < 0)
      {
         throw new InvalidArgumentException("'skipCount' parameter is negative. ");
      }

      ItemType itemTypeType;
      if (itemType != null)
      {
         try
         {
            itemTypeType = ItemType.fromValue(itemType);
         }
         catch (IllegalArgumentException e)
         {
            throw new InvalidArgumentException(String.format("Unknown type: %s", itemType));
         }
      }
      else
      {
         itemTypeType = null;
      }

      VirtualFile virtualFile = idToVirtualFile(folderId);

      List<VirtualFile> children = virtualFile.getChildren();
      int totalNumber = children.size();
      if (itemTypeType != null)
      {
         Iterator<VirtualFile> iterator = children.iterator();
         while (iterator.hasNext())
         {
            VirtualFile next = iterator.next();
            if ((itemTypeType == ItemType.FILE && !next.isFile())
               || (itemTypeType == ItemType.FOLDER && !next.isFolder())
               || (itemTypeType == ItemType.PROJECT && !next.isProject()))
            {
               iterator.remove();
            }
         }
      }
      if (skipCount > 0)
      {
         if (skipCount > children.size())
         {
            throw new InvalidArgumentException("'skipCount' parameter is greater then total number of items. ");
         }
         children.subList(0, skipCount).clear();
      }

      List<Item> list;
      boolean hasMoreItems;
      if (maxItems > 0)
      {
         list = new ArrayList<Item>();
         Iterator<VirtualFile> iterator = children.iterator();
         for (int count = 0; count < maxItems && iterator.hasNext(); count++)
         {
            list.add(fromVirtualFile(iterator.next(), propertyFilter));
         }
         hasMoreItems = iterator.hasNext();
      }
      else
      {
         list = new ArrayList<Item>(children.size());
         for (VirtualFile aChildren : children)
         {
            list.add(fromVirtualFile(aChildren, propertyFilter));
         }
         hasMoreItems = false;
      }

      ItemList<Item> result = new ItemListImpl<Item>(list);
      result.setNumItems(totalNumber);
      result.setHasMoreItems(hasMoreItems);

      return result;
   }

   @Path("tree/{id}")
   @Override
   public ItemNode getTree(@PathParam("id") String folderId,
                           @DefaultValue("-1") @QueryParam("depth") int depth,
                           @DefaultValue(PropertyFilter.NONE) @QueryParam("propertyFilter") PropertyFilter propertyFilter)
      throws VirtualFileSystemException
   {
      VirtualFile virtualFile = idToVirtualFile(folderId);
      if (!virtualFile.isFolder())
      {
         throw new InvalidArgumentException(
            String.format("Unable get tree. Item '%s' is not a folder. ", virtualFile.getPath()));
      }
      return new ItemNodeImpl(fromVirtualFile(virtualFile, propertyFilter, false),
         getTreeLevel(virtualFile, depth, propertyFilter));
   }

   private List<ItemNode> getTreeLevel(VirtualFile virtualFile, int depth, PropertyFilter propertyFilter)
      throws VirtualFileSystemException
   {
      if (depth == 0 || !virtualFile.isFolder())
      {
         return null;
      }
      List<VirtualFile> children = virtualFile.getChildren();
      List<ItemNode> level = new ArrayList<ItemNode>(children.size());
      for (VirtualFile i : children)
      {
         level.add(new ItemNodeImpl(fromVirtualFile(i, propertyFilter, false), getTreeLevel(i, depth - 1, propertyFilter)));
      }
      return level;
   }

   @Path("content/{id}")
   @Override
   public ContentStream getContent(@PathParam("id") String id) throws VirtualFileSystemException
   {
      return idToVirtualFile(id).getContent();
   }

   @Path("contentbypath/{path:.*}")
   @Override
   public ContentStream getContent(@PathParam("path") String path, //
                                   @QueryParam("versionId") String versionId) throws VirtualFileSystemException
   {
      return getVirtualFileByPath(path).getContent();
   }

   @Override
   public VirtualFileSystemInfo getInfo() throws VirtualFileSystemException
   {
      if (vfsInfo == null)
      {
         BasicPermissions[] basicPermissions = BasicPermissions.values();
         List<String> permissions = new ArrayList<String>(basicPermissions.length);
         for (BasicPermissions bp : basicPermissions)
         {
            permissions.add(bp.value());
         }
         // TODO : update capabilities when implement locks, ACL and query.
         vfsInfo =
            new VirtualFileSystemInfoImpl(this.vfsId, false, false, VirtualFileSystemInfo.ANONYMOUS_PRINCIPAL,
               VirtualFileSystemInfo.ANY_PRINCIPAL, permissions, VirtualFileSystemInfoImpl.ACLCapability.NONE,
               VirtualFileSystemInfoImpl.QueryCapability.NONE, createUrlTemplates(),
               (Folder)fromVirtualFile(mountPoint.getRoot(), PropertyFilter.ALL_FILTER));
      }
      return vfsInfo;
   }

   private Map<String, Link> createUrlTemplates()
   {
      Map<String, Link> templates = new LinkedHashMap<String, Link>();

      templates.put(Link.REL_ITEM, //
         new LinkImpl(createURI("item", "[id]"), Link.REL_ITEM, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_ITEM_BY_PATH, //
         new LinkImpl(createURI("itembypath", "[path]"), Link.REL_ITEM_BY_PATH, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_TREE, //
         new LinkImpl(createURI("tree", "[id]"), Link.REL_TREE, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_CREATE_FILE, //
         new LinkImpl(createURI("file", "[parentId]", "name", "[name]"), //
            Link.REL_CREATE_FILE, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_CREATE_FOLDER, //
         new LinkImpl(createURI("folder", "[parentId]", "name", "[name]"), //
            Link.REL_CREATE_FOLDER, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_CREATE_PROJECT, //
         new LinkImpl(createURI("project", "[parentId]", "name", "[name]", "type", "[type]"), //
            Link.REL_CREATE_PROJECT, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_COPY, //
         new LinkImpl(createURI("copy", "[id]", "parentId", "[parentId]"), //
            Link.REL_COPY, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_MOVE, //
         new LinkImpl(createURI("move", "[id]", "parentId", "[parentId]", "lockToken", "[lockToken]"), //
            Link.REL_MOVE, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_LOCK, //
         new LinkImpl(createURI("lock", "[id]"), //
            Link.REL_LOCK, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_UNLOCK, //
         new LinkImpl(createURI("unlock", "[id]", "lockToken", "[lockToken]"), //
            Link.REL_UNLOCK, null));

      templates.put(
         Link.REL_SEARCH_FORM, //
         new LinkImpl(createURI("search", null, "maxItems", "[maxItems]", "skipCount", "[skipCount]", "propertyFilter",
            "[propertyFilter]"), //
            Link.REL_SEARCH_FORM, MediaType.APPLICATION_JSON));

      templates.put(
         Link.REL_SEARCH, //
         new LinkImpl(createURI("search", null, "statement", "[statement]", "maxItems", "[maxItems]", "skipCount",
            "[skipCount]"), //
            Link.REL_SEARCH, MediaType.APPLICATION_JSON));

      return templates;
   }

   @Path("item/{id}")
   @Override
   public Item getItem(@PathParam("id") String id, //
                       @DefaultValue("*") @QueryParam("propertyFilter") PropertyFilter propertyFilter)
      throws VirtualFileSystemException
   {
      return fromVirtualFile(idToVirtualFile(id), propertyFilter);
   }

   @Path("itembypath/{path:.*}")
   @Override
   public Item getItemByPath(@PathParam("path") String path, //
                             @QueryParam("versionId") String versionId, //
                             @DefaultValue("*") @QueryParam("propertyFilter") PropertyFilter propertyFilter)
      throws VirtualFileSystemException
   {
      VirtualFile virtualFile = getVirtualFileByPath(path);
      if (virtualFile.isFile())
      {
         if (!(versionId == null || FAKE_VERSION_ID.equals(versionId)))
         {
            throw new NotSupportedException("Versioning is not supported. ");
         }
      }
      else if (versionId != null)
      {
         throw new InvalidArgumentException(
            String.format("Object '%s' is not a file. Version ID must not be set. ", path));
      }

      return fromVirtualFile(virtualFile, propertyFilter);
   }

   @Path("version/{id}/{versionId}")
   @Override
   public ContentStream getVersion(@PathParam("id") String id, //
                                   @PathParam("versionId") String versionId) throws VirtualFileSystemException
   {
      if (!(versionId == null || FAKE_VERSION_ID.equals(versionId)))
      {
         throw new NotSupportedException("Versioning is not supported. ");
      }
      return idToVirtualFile(id).getContent();
   }

   @Path("version-history/{id}")
   @Override
   public ItemList<File> getVersions(@PathParam("id") String id, //
                                     @DefaultValue("-1") @QueryParam("maxItems") int maxItems, //
                                     @QueryParam("skipCount") int skipCount, //
                                     @DefaultValue("*") @QueryParam("propertyFilter") PropertyFilter propertyFilter //
   ) throws VirtualFileSystemException
   {
      if (skipCount < 0)
      {
         throw new InvalidArgumentException("'skipCount' parameter is negative. ");
      }
      if (skipCount > 1)
      {
         // Since we don't support versioning we always have only one version.
         throw new InvalidArgumentException("'skipCount' parameter is greater then total number of items. ");
      }

      VirtualFile virtualFile = idToVirtualFile(id);
      if (!virtualFile.isFile())
      {
         throw new InvalidArgumentException(String.format("Object '%s' is not a file. ", virtualFile.getName()));
      }
      ItemList<File> versions = (maxItems < 0 || (maxItems - skipCount) > 0)
         ? new ItemListImpl<File>(Collections.singletonList((File)fromVirtualFile(virtualFile, propertyFilter)))
         : new ItemListImpl<File>(Collections.<File>emptyList());
      versions.setHasMoreItems(false);
      return versions;
   }

   @Path("lock/{id}")
   @Override
   public LockToken lock(@PathParam("id") String id) throws NotSupportedException, VirtualFileSystemException
   {
      return new LockTokenImpl(idToVirtualFile(id).lock());
   }

   @Path("move/{id}")
   @Override
   public Item move(@PathParam("id") String id, //
                    @QueryParam("parentId") String parentId, //
                    @QueryParam("lockToken") String lockToken) throws VirtualFileSystemException
   {

      VirtualFile moved = idToVirtualFile(id).moveTo(idToVirtualFile(parentId), lockToken);
      // TODO : listeners
      return fromVirtualFile(moved, PropertyFilter.ALL_FILTER);
   }

   @Path("rename/{id}")
   @Override
   public Item rename(@PathParam("id") String id, //
                      @QueryParam("mediaType") MediaType newMediaType, //
                      @QueryParam("newname") String newName, //
                      @QueryParam("lockToken") String lockToken) throws VirtualFileSystemException
   {
      if ((newName == null || newName.isEmpty()) && newMediaType == null)
      {
         // Nothing to do. Return unchanged object.
         return getItem(id, PropertyFilter.ALL_FILTER);
      }

      VirtualFile renamed = idToVirtualFile(id)
         .rename(newName, newMediaType == null ? null : newMediaType.toString(), lockToken);

      // TODO
//      if (listeners != null)
//      {
//         listeners.notifyListeners(
//            new ChangeEvent(this, renamed.getId(), renamed.getPath(), renamed.getMimeType(), ChangeEvent.ChangeType.RENAMED));
//      }
      return fromVirtualFile(renamed, PropertyFilter.ALL_FILTER);
   }

   @Override
   public ItemList<Item> search(MultivaluedMap<String, String> query, int maxItems, int skipCount, PropertyFilter propertyFilter) throws NotSupportedException, InvalidArgumentException, VirtualFileSystemException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public ItemList<Item> search(String statement, int maxItems, int skipCount) throws NotSupportedException, InvalidArgumentException, VirtualFileSystemException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Path("unlock/{id}")
   @Override
   public void unlock(@PathParam("id") String id, //
                      @QueryParam("lockToken") String lockToken //
   ) throws NotSupportedException, VirtualFileSystemException
   {
      idToVirtualFile(id).unlock(lockToken);
   }

   @Path("acl/{id}")
   @Override
   public void updateACL(@PathParam("id") String id, //
                         List<AccessControlEntry> acl, //
                         @DefaultValue("false") @QueryParam("override") Boolean override, //
                         @QueryParam("lockToken") String lockToken //
   ) throws VirtualFileSystemException
   {
      VirtualFile virtualFile = idToVirtualFile(id);
      virtualFile.updateACL(acl, override, lockToken);
      if (listeners != null)
      {
         listeners.notifyListeners(new ChangeEvent(this, virtualFileToId(virtualFile), virtualFile.getPath(),
            virtualFile.getMediaType(), ChangeEvent.ChangeType.ACL_UPDATED));
      }
   }

   @Path("content/{id}")
   @Override
   public void updateContent(
      @PathParam("id") String id, //
      @DefaultValue(MediaType.APPLICATION_OCTET_STREAM) @HeaderParam("Content-Type") MediaType mediaType,
      InputStream newContent, //
      @QueryParam("lockToken") String lockToken) throws VirtualFileSystemException
   {
      VirtualFile virtualFile = idToVirtualFile(id);
      virtualFile.updateContent(mediaType != null ? mediaType.toString() : null, newContent, lockToken);
      if (listeners != null)
      {
         listeners.notifyListeners(new ChangeEvent(this, virtualFileToId(virtualFile), virtualFile.getPath(),
            virtualFile.getMediaType(), ChangeEvent.ChangeType.CONTENT_UPDATED));
      }
   }

   @Override
   public Item updateItem(String id, List<Property> properties, String lockToken) throws ItemNotFoundException, LockException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public ContentStream exportZip(String folderId) throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, IOException, VirtualFileSystemException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public void importZip(String parentId, InputStream in, Boolean overwrite) throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, IOException, VirtualFileSystemException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   @Path("downloadfile/{id}")
   @Override
   public Response downloadFile(@PathParam("id") String id) throws VirtualFileSystemException
   {
      ContentStream content = getContent(id);
      return Response
         .ok(content.getStream(), content.getMimeType())
         .lastModified(content.getLastModificationDate())
         .header(HttpHeaders.CONTENT_LENGTH, Long.toString(content.getLength()))
         .header("Content-Disposition", "attachment; filename=\"" + content.getFileName() + '"')
         .build();
   }

   @Override
   public Response uploadFile(String parentId, Iterator<FileItem> formData) throws ItemNotFoundException, InvalidArgumentException, ItemAlreadyExistException, PermissionDeniedException, VirtualFileSystemException, IOException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public Response downloadZip(String folderId) throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, IOException, VirtualFileSystemException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public Response uploadZip(String parentId, Iterator<FileItem> formData) throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, IOException, VirtualFileSystemException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public void startWatchUpdates(String projectId) throws ItemNotFoundException, InvalidArgumentException, VirtualFileSystemException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public void stopWatchUpdates(String projectId) throws ItemNotFoundException, InvalidArgumentException, VirtualFileSystemException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   /* ==================================================================== */

   private VirtualFile getVirtualFileByPath(String path) throws VirtualFileSystemException
   {
      return mountPoint.getVirtualFile(path);
   }

   private VirtualFile idToVirtualFile(String id) throws VirtualFileSystemException
   {
      if (ROOT_ID.equals(id))
      {
         return mountPoint.getRoot();
      }
      final String path;
      try
      {
         path = new String(Base64.decodeBase64(id), "UTF-8");
      }
      catch (UnsupportedEncodingException e)
      {
         // Should never happen.
         throw new IllegalStateException(e.getMessage(), e);
      }

      try
      {
         return getVirtualFileByPath(path);
      }
      catch (ItemNotFoundException e)
      {
         throw new ItemNotFoundException(String.format("Object '%s' does not exists. ", id));
      }
   }

   private String virtualFileToId(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      if (virtualFile.isRoot())
      {
         return ROOT_ID;
      }
      try
      {
         return Base64.encodeBase64URLSafeString(virtualFile.getPath().getBytes("UTF-8"));
      }
      catch (UnsupportedEncodingException e)
      {
         // Should never happen.
         throw new IllegalStateException(e.getMessage(), e);
      }

   }

   private Item fromVirtualFile(VirtualFile virtualFile, PropertyFilter propertyFilter) throws VirtualFileSystemException
   {
      return fromVirtualFile(virtualFile, propertyFilter, true);
   }

   private Item fromVirtualFile(VirtualFile virtualFile, PropertyFilter propertyFilter, boolean addLinks)
      throws VirtualFileSystemException
   {
      final String id = virtualFileToId(virtualFile);
      final String name = virtualFile.getName();
      final String path = virtualFile.getPath();
      final boolean isRoot = virtualFile.isFolder() && virtualFile.isRoot();
      final String parentId = isRoot ? null : virtualFileToId(virtualFile.getParent());
      final String mediaType = virtualFile.getMediaType();
      final long created = virtualFile.getCreationDate();

      if (virtualFile.isFile())
      {
         final boolean locked = virtualFile.isLocked();
         final long length = virtualFile.getLength();
         final long modified = virtualFile.getLastModificationDate();
         return new FileImpl(id, name, path, parentId, created, modified, FAKE_VERSION_ID, mediaType, length, locked,
            virtualFile.getProperties(propertyFilter),
            addLinks ? createFileLinks(id, path, mediaType, locked, parentId) : null);
      }

      if (virtualFile.isProject())
      {
         String projectType = virtualFile.getFirstPropertyValue("vfs:projectType");
         return new ProjectImpl(id, name, mediaType == null ? Project.PROJECT_MIME_TYPE : mediaType, path,
            parentId, created, virtualFile.getProperties(propertyFilter),
            addLinks ? createProjectLinks(id, parentId) : null, projectType);
      }

      return new FolderImpl(id, name, mediaType == null ? Folder.FOLDER_MIME_TYPE : mediaType, path,
         parentId, created, virtualFile.getProperties(propertyFilter),
         addLinks ? createFolderLinks(id, isRoot, parentId) : null);
   }

   private Map<String, Link> createFileLinks(String id,
                                             String path,
                                             String mediaType,
                                             boolean locked,
                                             String parentId) throws VirtualFileSystemException
   {
      Map<String, Link> links = new LinkedHashMap<String, Link>();

      links.put(Link.REL_SELF, //
         new LinkImpl(createURI("item", id), Link.REL_SELF, MediaType.APPLICATION_JSON));

      links.put(Link.REL_ACL, //
         new LinkImpl(createURI("acl", id), Link.REL_ACL, MediaType.APPLICATION_JSON));

      links.put(Link.REL_CONTENT, //
         new LinkImpl(createURI("content", id), Link.REL_CONTENT, mediaType));

      links.put(Link.REL_DOWNLOAD_FILE, //
         new LinkImpl(createURI("downloadfile", id), Link.REL_DOWNLOAD_FILE, mediaType));

      links.put(Link.REL_CONTENT_BY_PATH, //
         new LinkImpl(createURI("contentbypath", path.substring(1)), Link.REL_CONTENT_BY_PATH, mediaType));

      links.put(Link.REL_VERSION_HISTORY, //
         new LinkImpl(createURI("version-history", id), Link.REL_VERSION_HISTORY, MediaType.APPLICATION_JSON));

      // Always have only one versioning since is not supported.
      links.put(Link.REL_CURRENT_VERSION, //
         new LinkImpl(createURI("item", id), Link.REL_CURRENT_VERSION, MediaType.APPLICATION_JSON));

      if (locked)
      {
         links.put(Link.REL_UNLOCK, //
            new LinkImpl(createURI("unlock", id, "lockToken", "[lockToken]"), Link.REL_UNLOCK, null));
      }
      else
      {
         links.put(Link.REL_LOCK, //
            new LinkImpl(createURI("lock", id), Link.REL_LOCK, MediaType.APPLICATION_JSON));
      }

      links.put(Link.REL_DELETE, //
         new LinkImpl(locked
            ? createURI("delete", id, "lockToken", "[lockToken]")
            : createURI("delete", id),
            Link.REL_DELETE, null));

      links.put(Link.REL_COPY, //
         new LinkImpl(createURI("copy", id, "parentId", "[parentId]"), Link.REL_COPY, MediaType.APPLICATION_JSON));

      links.put(Link.REL_MOVE, //
         new LinkImpl(locked
            ? createURI("move", id, "parentId", "[parentId]", "lockToken", "[lockToken]")
            : createURI("move", id, "parentId", "[parentId]"),
            Link.REL_MOVE, MediaType.APPLICATION_JSON));

      links.put(Link.REL_PARENT, //
         new LinkImpl(createURI("item", parentId), Link.REL_PARENT, MediaType.APPLICATION_JSON));

      links.put(Link.REL_RENAME, //
         new LinkImpl(locked
            ? createURI("rename", id, "newname", "[newname]", "mediaType", "[mediaType]", "lockToken", "[lockToken]")
            : createURI("rename", id, "newname", "[newname]", "mediaType", "[mediaType]"),
            Link.REL_RENAME, MediaType.APPLICATION_JSON));

      return links;
   }

   private Map<String, Link> createFolderLinks(String id, boolean isRoot, String parentId)
      throws VirtualFileSystemException
   {
      Map<String, Link> links = createBaseFolderLinks(id, isRoot, parentId);

      links.put(Link.REL_CREATE_PROJECT, //
         new LinkImpl(createURI("project", id, "name", "[name]", "type", "[type]"), Link.REL_CREATE_PROJECT,
            MediaType.APPLICATION_JSON));

      return links;
   }

   private Map<String, Link> createProjectLinks(String id, String parentId) throws VirtualFileSystemException
   {
      return createBaseFolderLinks(id, false, parentId);
   }

   private Map<String, Link> createBaseFolderLinks(String id, boolean isRoot, String parentId)
      throws VirtualFileSystemException
   {
      Map<String, Link> links = new LinkedHashMap<String, Link>();

      links.put(Link.REL_SELF, //
         new LinkImpl(createURI("item", id), Link.REL_SELF, MediaType.APPLICATION_JSON));

      links.put(Link.REL_ACL, //
         new LinkImpl(createURI("acl", id), Link.REL_ACL, MediaType.APPLICATION_JSON));

      links.put(Link.REL_DELETE, //
         new LinkImpl(createURI("delete", id), Link.REL_DELETE, null));

      links.put(Link.REL_COPY, //
         new LinkImpl(createURI("copy", id, "parentId", "[parentId]"), Link.REL_COPY, MediaType.APPLICATION_JSON));

      links.put(Link.REL_MOVE, //
         new LinkImpl(createURI("move", id, "parentId", "[parentId]"), Link.REL_MOVE, MediaType.APPLICATION_JSON));

      if (!isRoot)
      {
         links.put(Link.REL_PARENT, //
            new LinkImpl(createURI("item", parentId), Link.REL_PARENT, MediaType.APPLICATION_JSON));
      }

      links.put(
         Link.REL_RENAME, //
         new LinkImpl(createURI("rename", id, "newname", "[newname]", "mediaType", "[mediaType]"),
            Link.REL_RENAME, MediaType.APPLICATION_JSON));

      links.put(Link.REL_CHILDREN, //
         new LinkImpl(createURI("children", id), Link.REL_CHILDREN, MediaType.APPLICATION_JSON));

      links.put(Link.REL_TREE, //
         new LinkImpl(createURI("tree", id), Link.REL_TREE, MediaType.APPLICATION_JSON));

      links.put(Link.REL_CREATE_FOLDER, //
         new LinkImpl(createURI("folder", id, "name", "[name]"), Link.REL_CREATE_FOLDER, MediaType.APPLICATION_JSON));

      links.put(Link.REL_CREATE_FILE, //
         new LinkImpl(createURI("file", id, "name", "[name]"), Link.REL_CREATE_FILE, MediaType.APPLICATION_JSON));

      links.put(Link.REL_UPLOAD_FILE, //
         new LinkImpl(createURI("uploadfile", id), Link.REL_UPLOAD_FILE, MediaType.TEXT_HTML));

      links.put(Link.REL_EXPORT, //
         new LinkImpl(createURI("export", id), Link.REL_EXPORT, "application/zip"));

      links.put(Link.REL_IMPORT, //
         new LinkImpl(createURI("import", id), Link.REL_IMPORT, "application/zip"));

      links.put(Link.REL_DOWNLOAD_ZIP, //
         new LinkImpl(createURI("downloadzip", id), Link.REL_DOWNLOAD_ZIP, "application/zip"));

      links.put(Link.REL_UPLOAD_ZIP, //
         new LinkImpl(createURI("uploadzip", id), Link.REL_UPLOAD_ZIP, MediaType.TEXT_HTML));

      return links;
   }

   private String createURI(String rel, String id, String... query)
   {
      UriBuilder uriBuilder = UriBuilder.fromUri(baseUri);
      uriBuilder.path(VirtualFileSystemFactory.class, "getFileSystem");
      uriBuilder.path(rel);
      if (id != null)
      {
         uriBuilder.path(id);
      }
      if (query != null && query.length > 0)
      {
         for (int i = 0; i < query.length; i++)
         {
            String name = query[i];
            String value = i < query.length ? query[++i] : "";
            uriBuilder.queryParam(name, value);
         }
      }

      URI uri = uriBuilder.build(vfsId);

      return uri.toString();
   }
}
