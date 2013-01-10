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

import org.apache.commons.fileupload.FileItem;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemFactory;
import org.exoplatform.ide.vfs.server.exceptions.ConstraintException;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemAlreadyExistException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.LockException;
import org.exoplatform.ide.vfs.server.exceptions.NotSupportedException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryItem;
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
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.LinkImpl;
import org.exoplatform.ide.vfs.shared.LockToken;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.ProjectImpl;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfoImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class PlainFileSystem implements VirtualFileSystem
{
   private static final String FAKE_VERSION_ID = "0";

   private final String vfsId;
   private final URI baseUri;
   private final PlainFileSystemContext fsContext;
   private final EventListenerList listeners;
   private VirtualFileSystemInfoImpl vfsInfo;

   public PlainFileSystem(String vfsId, URI baseUri, PlainFileSystemContext fsContext, EventListenerList listeners)
   {
      this.vfsId = vfsId;
      this.baseUri = baseUri;
      this.fsContext = fsContext;
      this.listeners = listeners;
   }

   @Override
   public Item copy(String id, String parentId) throws ItemNotFoundException, ConstraintException, ItemAlreadyExistException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Path("file/{parentId}")
   @Override
   public File createFile(@PathParam("parentId") String parentId, //
                          @QueryParam("name") String name, //
                          @DefaultValue(MediaType.APPLICATION_OCTET_STREAM) @HeaderParam("Content-Type") MediaType mediaType, //
                          InputStream content) throws VirtualFileSystemException
   {
      checkName(name);
      VirtualFile parent = getVirtualFileById(parentId);
      if (ItemType.FOLDER != parent.getType())
      {
         throw new InvalidArgumentException("Unable create new file. Item specified as parent is not a folder. ");
      }
      return (File)fromVirtualFile(fsContext.createFile(parent, name, mediaType.toString(), content),
         PropertyFilter.ALL_FILTER);
   }

   @Path("folder/{parentId}")
   @Override
   public Folder createFolder(@PathParam("parentId") String parentId, //
                              @QueryParam("name") String name) throws VirtualFileSystemException
   {
      checkName(name);
      VirtualFile parent = getVirtualFileById(parentId);
      if (ItemType.FOLDER != parent.getType())
      {
         throw new InvalidArgumentException("Unable create folder. Item specified as parent is not a folder. ");
      }
      return (Folder)fromVirtualFile(fsContext.createFolder(parent, name), PropertyFilter.ALL_FILTER);
   }

   @Override
   public Project createProject(String parentId, String name, String type, List<Property> properties) throws ItemNotFoundException, InvalidArgumentException, ItemAlreadyExistException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Path("delete/{id}")
   @Override
   public void delete(@PathParam("id") String id, //
                      @QueryParam("lockToken") String lockToken) throws VirtualFileSystemException
   {
      // TODO : check for locking
      fsContext.delete(getVirtualFileById(id));
   }

   @Override
   public List<AccessControlEntry> getACL(String id) throws NotSupportedException, ItemNotFoundException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
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
            throw new InvalidArgumentException("Unknown type: " + itemType);
         }
      }
      else
      {
         itemTypeType = null;
      }

      VirtualFile virtualFile = getVirtualFileById(folderId);
      if (ItemType.FOLDER != virtualFile.getType())
      {
         throw new InvalidArgumentException("Unable get children. Item '" + virtualFile.getPath().getName() +
            "' is not a folder. ");
      }

      List<VirtualFile> children = fsContext.getChildren(virtualFile);
      int totalNumber = children.size();
      if (itemTypeType != null)
      {
         Iterator<VirtualFile> iterator = children.iterator();
         while (iterator.hasNext())
         {
            VirtualFile next = iterator.next();
            if (itemTypeType != next.getType())
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

      List<Item> l;
      boolean hasMoreItems;
      if (maxItems > 0)
      {
         l = new ArrayList<Item>();
         Iterator<VirtualFile> iterator = children.iterator();
         for (int count = 0; count < maxItems && iterator.hasNext(); count++)
         {
            l.add(fromVirtualFile(iterator.next(), propertyFilter));
         }
         hasMoreItems = iterator.hasNext();
      }
      else
      {
         l = new ArrayList<Item>(children.size());
         for (VirtualFile aChildren : children)
         {
            l.add(fromVirtualFile(aChildren, propertyFilter));
         }
         hasMoreItems = false;
      }

      ItemList<Item> il = new ItemListImpl<Item>(l);
      il.setNumItems(totalNumber);
      il.setHasMoreItems(hasMoreItems);

      return il;
   }

   @Override
   public ItemNode getTree(String folderId, int depth, PropertyFilter propertyFilter) throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Path("content/{id}")
   @Override
   public ContentStream getContent(@PathParam("id") String id) throws VirtualFileSystemException
   {
      return getContent(getVirtualFileById(id));
   }

   @Path("contentbypath/{path:.*}")
   @Override
   public ContentStream getContent(@PathParam("path") String path, //
                                   @QueryParam("versionId") String versionId) throws VirtualFileSystemException
   {
      return getContent(getVirtualFileByPath(path));
   }

   @Override
   public VirtualFileSystemInfo getInfo() throws VirtualFileSystemException
   {
      if (vfsInfo == null)
      {
         VirtualFileSystemInfoImpl.BasicPermissions[] basicPermissions = VirtualFileSystemInfoImpl.BasicPermissions.values();
         List<String> permissions = new ArrayList<String>(basicPermissions.length);
         for (VirtualFileSystemInfoImpl.BasicPermissions bp : basicPermissions)
         {
            permissions.add(bp.value());
         }
         Folder root = (Folder)fromVirtualFile(fsContext.getRoot(), PropertyFilter.ALL_FILTER);
         // TODO : update capabilities when implement locks ans query.
         vfsInfo =
            new VirtualFileSystemInfoImpl(this.vfsId, false, false, VirtualFileSystemInfo.ANONYMOUS_PRINCIPAL,
               VirtualFileSystemInfo.ANY_PRINCIPAL, permissions, VirtualFileSystemInfoImpl.ACLCapability.NONE,
               VirtualFileSystemInfoImpl.QueryCapability.NONE, createUrlTemplates(), root);
      }
      return vfsInfo;
   }

   private Map<String, Link> createUrlTemplates()
   {
      Map<String, Link> templates = new HashMap<String, Link>();

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
      return fromVirtualFile(getVirtualFileById(id), propertyFilter);
   }

   @Path("itembypath/{path:.*}")
   @Override
   public Item getItemByPath(@PathParam("path") String path, //
                             @QueryParam("versionId") String versionId, //
                             @DefaultValue("*") @QueryParam("propertyFilter") PropertyFilter propertyFilter)
      throws VirtualFileSystemException
   {
      VirtualFile virtualFile = getVirtualFileByPath(path);
      if (ItemType.FILE == virtualFile.getType())
      {
         if (!(versionId == null || FAKE_VERSION_ID.equals(versionId)))
         {
            throw new NotSupportedException("Versioning is not supported. ");
         }
      }
      else if (versionId != null)
      {
         throw new InvalidArgumentException("Object " + path + " is not a file. Version ID must not be set. ");
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
      return getContent(getVirtualFileById(id));
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

      VirtualFile virtualFile = getVirtualFileById(id);
      if (ItemType.FILE == virtualFile.getType())
      {
         ItemList<File> versions = (maxItems < 0 || (maxItems - skipCount) > 0)
            ? new ItemListImpl<File>(Collections.singletonList((File)fromVirtualFile(virtualFile, propertyFilter)))
            : new ItemListImpl<File>(Collections.<File>emptyList());
         versions.setHasMoreItems(false);
         return versions;
      }
      throw new InvalidArgumentException("Object '" + virtualFile.getPath().getName() + "' is not a file. ");
   }

   @Override
   public LockToken lock(String id) throws NotSupportedException, ItemNotFoundException, InvalidArgumentException, LockException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public Item move(String id, String parentId, String lockToken) throws ItemNotFoundException, ConstraintException, ItemAlreadyExistException, LockException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public Item rename(String id, MediaType mediaType, String newname, String lockToken) throws ItemNotFoundException, LockException, ConstraintException, ItemAlreadyExistException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
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

   @Override
   public void unlock(String id, String lockToken) throws NotSupportedException, ItemNotFoundException, LockException, PermissionDeniedException, VirtualFileSystemException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public void updateACL(String id, List<AccessControlEntry> acl, Boolean override, String lockToken) throws NotSupportedException, ItemNotFoundException, LockException, PermissionDeniedException, VirtualFileSystemException
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public void updateContent(String id, MediaType mediaType, InputStream newcontent, String lockToken) throws ItemNotFoundException, InvalidArgumentException, LockException, PermissionDeniedException, VirtualFileSystemException
   {
      //To change body of implemented methods use File | Settings | File Templates.
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

   @Override
   public Response downloadFile(String id) throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
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

   // TODO : move in utils
   private void checkName(String name) throws InvalidArgumentException
   {
      if (name == null || name.trim().isEmpty())
      {
         throw new InvalidArgumentException("Item's name is not set. ");
      }
   }

   private VirtualFile getVirtualFileByPath(String path) throws VirtualFileSystemException
   {
      VirtualFile virtualFile = fsContext.getVirtualFileByPath(path);
      if (virtualFile == null)
      {
         throw new ItemNotFoundException("Object '" + path + "' does not exists. ");
      }
      return virtualFile;
   }

   private VirtualFile getVirtualFileById(String id) throws VirtualFileSystemException
   {
      VirtualFile virtualFile = fsContext.getVirtualFileById(id);
      if (virtualFile == null)
      {
         throw new ItemNotFoundException("Object '" + id + "' does not exists. ");
      }
      return virtualFile;
   }

   private ContentStream getContent(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      if (ItemType.FILE != virtualFile.getType())
      {
         throw new InvalidArgumentException("Unable get content. Item '" + virtualFile.getPath() + "' is not a file. ");
      }
      return fsContext.getContent(virtualFile);
   }

   private Item fromVirtualFile(VirtualFile virtualFile, PropertyFilter propertyFilter) throws VirtualFileSystemException
   {
      return fromVirtualFile(virtualFile, propertyFilter, true);
   }

   private Item fromVirtualFile(VirtualFile virtualFile, PropertyFilter propertyFilter, boolean addLinks)
      throws VirtualFileSystemException
   {
      String mediaType = virtualFile.getMediaType();
      if (ItemType.FILE == virtualFile.getType())
      {
         return new FileImpl(virtualFile.getId(), virtualFile.getPath().getName(), virtualFile.getPath().toString(),
            fsContext.getParent(virtualFile).getId(), virtualFile.getCreationDate(),
            virtualFile.getLastModificationDate(), FAKE_VERSION_ID, virtualFile.getMediaType(), virtualFile.getLength(),
            fsContext.isLocked(virtualFile), getProperties(virtualFile, propertyFilter),
            addLinks ? createFileLinks(virtualFile) : null);
      }

      if (ItemType.PROJECT == virtualFile.getType())
      {
         String projectType = virtualFile.getFirstProperty("vfs:projectType");
         return new ProjectImpl(virtualFile.getId(), virtualFile.getPath().getName(),
            mediaType == null ? ProjectImpl.FOLDER_MIME_TYPE : mediaType, virtualFile.getPath().toString(),
            fsContext.getParent(virtualFile).getId(), virtualFile.getCreationDate(),
            getProperties(virtualFile, propertyFilter), addLinks ? createProjectLinks(virtualFile) : null, projectType);
      }

      return new FolderImpl(virtualFile.getId(), virtualFile.getPath().getName(),
         mediaType == null ? Folder.FOLDER_MIME_TYPE : mediaType, virtualFile.getPath().toString(),
         fsContext.getParent(virtualFile).getId(), virtualFile.getCreationDate(),
         getProperties(virtualFile, propertyFilter), addLinks ? createFolderLinks(virtualFile) : null);
   }

   private List<Property> getProperties(VirtualFile virtualFile, PropertyFilter propertyFilter)
   {
      return Collections.emptyList(); // TODO
   }

   private Map<String, Link> createFileLinks(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      Map<String, Link> links = createBaseLinks(virtualFile);
      String id = virtualFile.getId();

      links.put(Link.REL_CONTENT, //
         new LinkImpl(createURI("content", id), Link.REL_CONTENT, virtualFile.getMediaType()));

      links.put(Link.REL_DOWNLOAD_FILE, //
         new LinkImpl(createURI("downloadfile", id), Link.REL_DOWNLOAD_FILE, virtualFile.getMediaType()));

      links.put(Link.REL_CONTENT_BY_PATH, //
         new LinkImpl(createURI("contentbypath", virtualFile.getPath().toString().substring(1)),
            Link.REL_CONTENT_BY_PATH, virtualFile.getMediaType()));

      links.put(Link.REL_VERSION_HISTORY, //
         new LinkImpl(createURI("version-history", id), Link.REL_VERSION_HISTORY, MediaType.APPLICATION_JSON));

      // Always have only one versioning since is not supported.
      links.put(Link.REL_CURRENT_VERSION, //
         new LinkImpl(createURI("item", virtualFile.getId()), Link.REL_CURRENT_VERSION, MediaType.APPLICATION_JSON));

      if (fsContext.isLocked(virtualFile))
      {
         links.put(Link.REL_UNLOCK, //
            new LinkImpl(createURI("unlock", id, "lockToken", "[lockToken]"), Link.REL_UNLOCK, null));
      }
      else
      {
         links.put(Link.REL_LOCK, //
            new LinkImpl(createURI("lock", id), Link.REL_LOCK, MediaType.APPLICATION_JSON));
      }

      return links;
   }

   private Map<String, Link> createFolderLinks(VirtualFile folder) throws VirtualFileSystemException
   {
      Map<String, Link> links = createBaseFolderLinks(folder);
      String id = folder.getId();

      links.put(Link.REL_CREATE_PROJECT, //
         new LinkImpl(createURI("project", id, "name", "[name]", "type", "[type]"), Link.REL_CREATE_PROJECT,
            MediaType.APPLICATION_JSON));

      return links;
   }

   private Map<String, Link> createProjectLinks(VirtualFile project) throws VirtualFileSystemException
   {
      return createBaseFolderLinks(project);
   }

   private Map<String, Link> createBaseFolderLinks(VirtualFile folder) throws VirtualFileSystemException
   {
      Map<String, Link> links = createBaseLinks(folder);
      String id = folder.getId();

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

   private Map<String, Link> createBaseLinks(VirtualFile virtualFile) throws VirtualFileSystemException
   {
      Map<String, Link> links = new HashMap<String, Link>();
      String id = virtualFile.getId();

      links.put(Link.REL_SELF, //
         new LinkImpl(createURI("item", id), Link.REL_SELF, MediaType.APPLICATION_JSON));

      links.put(Link.REL_ACL, //
         new LinkImpl(createURI("acl", id), Link.REL_ACL, MediaType.APPLICATION_JSON));

      // Root folder can't be moved copied and has not parent.
      if (!virtualFile.getPath().isRoot())
      {
         links.put(Link.REL_DELETE, //
            new LinkImpl(ItemType.FILE == virtualFile.getType() && fsContext.isLocked(virtualFile)
               ? createURI("delete", id, "lockToken", "[lockToken]") : createURI("delete", id),
               Link.REL_DELETE, null));

         links.put(Link.REL_COPY, //
            new LinkImpl(createURI("copy", id, "parentId", "[parentId]"), Link.REL_COPY, MediaType.APPLICATION_JSON));

         links.put(Link.REL_MOVE, //
            new LinkImpl(ItemType.FILE == virtualFile.getType() && fsContext.isLocked(virtualFile)
               ? createURI("move", id, "parentId", "[parentId]", "lockToken", "[lockToken]")
               : createURI("move", id, "parentId", "[parentId]"),
               Link.REL_MOVE, MediaType.APPLICATION_JSON));

         links.put(Link.REL_PARENT, //
            new LinkImpl(createURI("item", fsContext.getParent(virtualFile).getId()), Link.REL_PARENT,
               MediaType.APPLICATION_JSON));

         links.put(
            Link.REL_RENAME, //
            new LinkImpl(createURI("rename", id, "newname", "[newname]", "mediaType", "[mediaType]", "lockToken",
               "[lockToken]"), Link.REL_RENAME, MediaType.APPLICATION_JSON));
      }
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
