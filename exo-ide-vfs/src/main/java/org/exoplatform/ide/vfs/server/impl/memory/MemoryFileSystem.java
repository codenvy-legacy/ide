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
import org.apache.commons.io.input.CountingInputStream;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonGenerator;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonWriter;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.commons.utils.MimeTypeResolver;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.ItemNodeImpl;
import org.exoplatform.ide.vfs.server.PropertyFilter;
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
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemRuntimeException;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFile;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFileSystemContext;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFolder;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryItem;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryItemVisitor;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryProject;
import org.exoplatform.ide.vfs.server.impl.memory.context.ObjectIdGenerator;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.ExitCodes;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.ItemNode;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.LockToken;
import org.exoplatform.ide.vfs.shared.LockTokenBean;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.services.security.ConversationState;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MemoryFileSystem implements VirtualFileSystem
{
   private final String vfsID;
   private final URI baseUri;
   private final MemoryFileSystemContext context;
   private final EventListenerList listeners;

   private VirtualFileSystemInfo vfsInfo;

   public MemoryFileSystem(URI baseUri, EventListenerList listeners, String vfsID, MemoryFileSystemContext context)
   {
      this.baseUri = baseUri;
      this.listeners = listeners;
      this.vfsID = vfsID;
      this.context = context;
   }

   @Path("copy/{id}")
   @Override
   public Item copy(@PathParam("id") String id, //
                    @QueryParam("parentId") String parentId) throws VirtualFileSystemException
   {
      MemoryItem object = getItemById(id);
      MemoryItem parent = getItemById(parentId);
      if (id.equals(parentId))
      {
         throw new InvalidArgumentException("Item cannot be copied to itself. ");
      }
      if (!(ItemType.PROJECT == parent.getType() || ItemType.FOLDER == parent.getType()))
      {
         throw new InvalidArgumentException("Unable copy item. Item specified as parent is not a folder or project. ");
      }
      if (ItemType.PROJECT == parent.getType() && ItemType.PROJECT == object.getType())
      {
         throw new ConstraintException(
            "Unable copy item. Item specified as parent is a project. Project cannot contains another project.");
      }
      if (!hasPermissions(parent, VirtualFileSystemInfo.BasicPermissions.WRITE.value()))
      {
         throw new PermissionDeniedException("Unable copy item '" + object.getPath() + "' to " + parent.getPath()
            + ". Operation not permitted. ");
      }
      MemoryItem objectCopy = object.copy((MemoryFolder)parent);
      context.addItem(objectCopy);
      Item copy = fromMemoryItem(objectCopy, PropertyFilter.ALL_FILTER);
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
      checkName(name);
      MemoryItem parent = getItemById(parentId);
      if (!(ItemType.FOLDER == parent.getType() || ItemType.PROJECT == parent.getType()))
      {
         throw new InvalidArgumentException("Unable create new file. Item specified as parent is not a folder or project. ");
      }
      if (!hasPermissions(parent, VirtualFileSystemInfo.BasicPermissions.WRITE.value()))
      {
         throw new PermissionDeniedException("Unable create file in folder '" + parent.getPath() +
            "'. Operation not permitted. ");
      }
      MemoryFile memoryFile;
      try
      {
         memoryFile = new MemoryFile(ObjectIdGenerator.generateId(), name, mediaType.toString(), content);
      }
      catch (IOException e)
      {
         throw new VirtualFileSystemException("Unable create file. " + e.getMessage(), e);
      }

      ((MemoryFolder)parent).addChild(memoryFile);
      context.addItem(memoryFile);
      File file = (File)fromMemoryItem(memoryFile, PropertyFilter.ALL_FILTER);
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
      checkName(name);
      MemoryItem parent = getItemById(parentId);
      if (!(ItemType.FOLDER == parent.getType() || ItemType.PROJECT == parent.getType()))
      {
         throw new InvalidArgumentException("Unable create folder. Item specified as parent is not a folder or project. ");
      }
      if (!hasPermissions(parent, VirtualFileSystemInfo.BasicPermissions.WRITE.value()))
      {
         throw new PermissionDeniedException("Unable create new folder in folder '" + parent.getPath() +
            "'. Operation not permitted. ");
      }
      MemoryFolder memoryFolder = new MemoryFolder(ObjectIdGenerator.generateId(), name);
      ((MemoryFolder)parent).addChild(memoryFolder);
      context.addItem(memoryFolder);
      Folder folder = (Folder)fromMemoryItem(memoryFolder, PropertyFilter.ALL_FILTER);
      if (listeners != null)
      {
         listeners.notifyListeners(
            new ChangeEvent(this, folder.getId(), folder.getPath(), folder.getMimeType(), ChangeEvent.ChangeType.CREATED));
      }
      return folder;
   }

   @Path("project/{parentId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Override
   public Project createProject(@PathParam("parentId") String parentId, //
                                @QueryParam("name") String name, //
                                @QueryParam("type") String type, //
                                List<Property> properties) throws VirtualFileSystemException
   {
      checkName(name);
      MemoryItem parent = getItemById(parentId);
      if (ItemType.PROJECT == parent.getType())
      {
         throw new ConstraintException("Unable create project. Item specified as parent is a project. "
            + "Project cannot contains another project.");
      }
      if (ItemType.FOLDER != parent.getType())
      {
         throw new InvalidArgumentException("Unable create project. Item specified as parent is not a folder. ");
      }
      if (!hasPermissions(parent, VirtualFileSystemInfo.BasicPermissions.WRITE.value()))
      {
         throw new PermissionDeniedException("Unable create new project in folder '" + parent.getPath() +
            "'. Operation not permitted. ");
      }
      if (properties == null)
      {
         properties = new ArrayList<Property>(2);
      }
      if (type != null)
      {
         properties.add(new Property("vfs:projectType", type));
      }
      properties.add(new Property("vfs:mimeType", Project.PROJECT_MIME_TYPE));
      MemoryProject memoryProject = new MemoryProject(ObjectIdGenerator.generateId(), name);
      memoryProject.updateProperties(properties);
      ((MemoryFolder)parent).addChild(memoryProject);
      context.addItem(memoryProject);
      Project project = (Project)fromMemoryItem(memoryProject, PropertyFilter.ALL_FILTER);
      if (listeners != null)
      {
         listeners.notifyListeners(
            new ChangeEvent(this, project.getId(), project.getPath(), project.getMimeType(), ChangeEvent.ChangeType.CREATED));
      }
      return project;
   }


   @Path("delete/{id}")
   @Override
   public void delete(@PathParam("id") String id, //
                      @QueryParam("lockToken") String lockToken) throws VirtualFileSystemException
   {
      MemoryItem object = getItemById(id);
      if (context.getRoot().equals(object))
      {
         throw new VirtualFileSystemException("Unable delete root folder. ");
      }
      if (!hasPermissions(object, VirtualFileSystemInfo.BasicPermissions.WRITE.value()))
      {
         throw new PermissionDeniedException("Unable delete item '" + object.getName() + "'. Operation not permitted. ");
      }
      MemoryFolder parent = object.getParent();
      if (ItemType.FILE == object.getType())
      {
         if (!((MemoryFile)object).isLockTokenMatched(lockToken))
         {
            throw new LockException("Unable delete item '" + object.getName() + "'. Item is locked. ");
         }
         parent.removeChild(object.getName());
         context.deleteItem(object.getId());
      }
      else
      {
         final List<MemoryItem> toDelete = new ArrayList<MemoryItem>();
         object.accept(new MemoryItemVisitor()
         {
            @Override
            public void visit(MemoryItem i) throws VirtualFileSystemException
            {
               if (i.getType() == ItemType.FILE)
               {
                  toDelete.add(i);
               }
               else
               {
                  for (MemoryItem ii : ((MemoryFolder)i).getChildren())
                  {
                     ii.accept(this);
                  }
                  toDelete.add(i);
               }
            }
         });

         List<String> ids = new ArrayList<String>(toDelete.size());
         for (MemoryItem i : toDelete)
         {
            if (!hasPermissions(i, VirtualFileSystemInfo.BasicPermissions.WRITE.value()))
            {
               throw new PermissionDeniedException("Unable delete item '" + i.getPath() + "'. Operation not permitted. ");
            }
            if (ItemType.FILE == i.getType() && ((MemoryFile)i).isLocked())
            {
               throw new LockException("Unable delete item '" + object.getName() +
                  "'. Child item '" + i.getPath() + "' is locked. ");
            }

            ids.add(i.getId());
         }
         // remove tree
         parent.removeChild(object.getName());
         context.deleteItems(ids);

      }
      if (listeners != null)
      {
         listeners.notifyListeners(
            new ChangeEvent(this, object.getId(), object.getPath(), object.getMediaType(), ChangeEvent.ChangeType.DELETED));
      }
   }

   @Path("acl/{id}")
   @Override
   public List<AccessControlEntry> getACL(@PathParam("id") String id) throws NotSupportedException, VirtualFileSystemException
   {
      return getItemById(id).getACL();
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

      MemoryItem object = getItemById(folderId);
      if (!(ItemType.FOLDER == object.getType() || ItemType.PROJECT == object.getType()))
      {
         throw new InvalidArgumentException("Unable get children. Item '" + object.getName()
            + "' is not a folder or project. ");
      }

      MemoryFolder folder = (MemoryFolder)object;
      List<MemoryItem> children = folder.getChildren();
      int totalNumber = children.size();
      if (itemTypeType != null)
      {
         Iterator<MemoryItem> iterator = children.iterator();
         while (iterator.hasNext())
         {
            MemoryItem next = iterator.next();
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
         Iterator<MemoryItem> iterator = children.iterator();
         for (int count = 0; count < maxItems && iterator.hasNext(); count++)
         {
            l.add(fromMemoryItem(iterator.next(), propertyFilter));
         }
         hasMoreItems = iterator.hasNext();
      }
      else
      {
         l = new ArrayList<Item>(children.size());
         for (MemoryItem aChildren : children)
         {
            l.add(fromMemoryItem(aChildren, propertyFilter));
         }
         hasMoreItems = false;
      }

      ItemList<Item> il = new ItemList<Item>(l);
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
      throws VirtualFileSystemException
   {
      MemoryItem folder = getItemById(folderId);
      if (!(ItemType.FOLDER == folder.getType() || ItemType.PROJECT == folder.getType()))
      {
         throw new InvalidArgumentException("Unable get tree. Item '" + folder.getName()
            + "' is not a folder or project. ");
      }
      return new ItemNodeImpl(fromMemoryItem(folder, propertyFilter, false), getTreeLevel(folder, depth, propertyFilter));
   }

   private List<ItemNode> getTreeLevel(MemoryItem object, int depth, PropertyFilter propertyFilter)
      throws VirtualFileSystemException
   {
      if (depth == 0 || object.getType() == ItemType.FILE)
      {
         return null;
      }
      MemoryFolder folder = (MemoryFolder)object;
      List<MemoryItem> children = folder.getChildren();
      List<ItemNode> level = new ArrayList<ItemNode>();
      for (MemoryItem i : children)
      {
         level.add(new ItemNodeImpl(fromMemoryItem(i, propertyFilter, false), getTreeLevel(i, depth - 1, propertyFilter)));
      }
      return level;
   }

   @Path("content/{id}")
   @Override
   public ContentStream getContent(@PathParam("id") String id) throws VirtualFileSystemException
   {
      MemoryItem object = getItemById(id);
      if (ItemType.FILE != object.getType())
      {
         throw new InvalidArgumentException("Unable get content. Item '" + object.getName() + "' is not a file. ");
      }
      return ((MemoryFile)object).getContent();
   }

   @Path("contentbypath/{path:.*}")
   @Override
   public ContentStream getContent(@PathParam("path") String path, //
                                   @QueryParam("versionId") String versionId) throws VirtualFileSystemException
   {
      MemoryItem object = getItemByPath(path);
      if (ItemType.FILE != object.getType())
      {
         throw new InvalidArgumentException("Unable get content. Item '" + path + "' is not a file. ");
      }
      if (!(versionId == null || versionId.equals(((MemoryFile)object).getVersionId())))
      {
         throw new NotSupportedException("Versioning is not supported. ");
      }
      return ((MemoryFile)object).getContent();
   }

   @Override
   public VirtualFileSystemInfo getInfo() throws VirtualFileSystemException
   {
      if (vfsInfo == null)
      {
         VirtualFileSystemInfo.BasicPermissions[] basicPermissions = VirtualFileSystemInfo.BasicPermissions.values();
         List<String> permissions = new ArrayList<String>(basicPermissions.length);
         for (VirtualFileSystemInfo.BasicPermissions bp : basicPermissions)
         {
            permissions.add(bp.value());
         }
         Folder root = (Folder)fromMemoryItem(context.getRoot(), PropertyFilter.ALL_FILTER);
         vfsInfo =
            new VirtualFileSystemInfo(this.vfsID, false, true, VirtualFileSystemInfo.ANONYMOUS_PRINCIPAL,
               VirtualFileSystemInfo.ANY_PRINCIPAL, permissions, VirtualFileSystemInfo.ACLCapability.MANAGE,
               VirtualFileSystemInfo.QueryCapability.NONE, createUrlTemplates(), root);
      }
      return vfsInfo;
   }

   private Map<String, Link> createUrlTemplates()
   {
      Map<String, Link> templates = new HashMap<String, Link>();

      templates.put(Link.REL_ITEM, //
         new Link(createURI("item", "[id]"), Link.REL_ITEM, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_ITEM_BY_PATH, //
         new Link(createURI("itembypath", "[path]"), Link.REL_ITEM_BY_PATH, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_CREATE_FILE, //
         new Link(createURI("file", "[parentId]", "name", "[name]"), //
            Link.REL_CREATE_FILE, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_CREATE_FOLDER, //
         new Link(createURI("folder", "[parentId]", "name", "[name]"), //
            Link.REL_CREATE_FOLDER, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_CREATE_PROJECT, //
         new Link(createURI("project", "[parentId]", "name", "[name]", "type", "[type]"), //
            Link.REL_CREATE_PROJECT, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_COPY, //
         new Link(createURI("copy", "[id]", "parentId", "[parentId]"), //
            Link.REL_COPY, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_MOVE, //
         new Link(createURI("move", "[id]", "parentId", "[parentId]", "lockToken", "[lockToken]"), //
            Link.REL_MOVE, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_LOCK, //
         new Link(createURI("lock", "[id]"), //
            Link.REL_LOCK, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_UNLOCK, //
         new Link(createURI("unlock", "[id]", "lockToken", "[lockToken]"), //
            Link.REL_UNLOCK, null));

      templates.put(
         Link.REL_SEARCH_FORM, //
         new Link(createURI("search", null, "maxItems", "[maxItems]", "skipCount", "[skipCount]", "propertyFilter",
            "[propertyFilter]"), //
            Link.REL_SEARCH_FORM, MediaType.APPLICATION_JSON));

      templates.put(
         Link.REL_SEARCH, //
         new Link(createURI("search", null, "statement", "[statement]", "maxItems", "[maxItems]", "skipCount",
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
      return fromMemoryItem(getItemById(id), propertyFilter);
   }

   @Path("itembypath/{path:.*}")
   @Override
   public Item getItemByPath(@PathParam("path") String path, //
                             @QueryParam("versionId") String versionId, //
                             @DefaultValue("*") @QueryParam("propertyFilter") PropertyFilter propertyFilter)
      throws VirtualFileSystemException
   {
      MemoryItem object = getItemByPath(path);
      if (ItemType.FILE == object.getType())
      {
         if (!(versionId == null || versionId.equals(((MemoryFile)object).getVersionId())))
         {
            throw new NotSupportedException("Versioning is not supported. ");
         }
      }
      else if (versionId != null)
      {
         throw new InvalidArgumentException("Object " + path + " is not a file. Version ID must not be set. ");
      }

      return fromMemoryItem(object, propertyFilter);
   }

   @Path("version/{id}/{versionId}")
   @Override
   public ContentStream getVersion(@PathParam("id") String id, //
                                   @PathParam("versionId") String versionId) throws VirtualFileSystemException
   {
      MemoryItem object = getItemById(id);
      if (ItemType.FILE != object.getType())
      {
         throw new InvalidArgumentException("Object '" + object.getName() + "' is not a file. ");
      }
      if (!(versionId == null || versionId.equals(((MemoryFile)object).getVersionId())))
      {
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

      MemoryItem object = getItemById(id);
      if (ItemType.FILE != object.getType())
      {
         throw new InvalidArgumentException("Object '" + object.getName() + "' is not a file. ");
      }
      ItemList<File> l;
      if (maxItems < 0 || (maxItems - skipCount) > 0)
      {
         l = new ItemList<File>(Collections.singletonList((File)fromMemoryItem(object, propertyFilter)));
         l.setHasMoreItems(false);
      }
      else
      {
         l = new ItemList<File>(Collections.<File>emptyList());
         l.setHasMoreItems(false);
      }
      return l;
   }

   @Path("lock/{id}")
   @Override
   public LockToken lock(@PathParam("id") String id) throws NotSupportedException, VirtualFileSystemException
   {
      MemoryItem object = getItemById(id);
      if (ItemType.FILE != object.getType())
      {
         throw new InvalidArgumentException("Locking allowed for Files only. ");
      }
      if (!hasPermissions(object, VirtualFileSystemInfo.BasicPermissions.WRITE.value()))
      {
         throw new PermissionDeniedException("Unable lock item '" + object.getName() + "'. Operation not permitted. ");
      }
      return new LockTokenBean(((MemoryFile)object).lock());
   }

   @Path("move/{id}")
   @Override
   public Item move(@PathParam("id") String id, //
                    @QueryParam("parentId") String parentId, //
                    @QueryParam("lockToken") String lockToken) throws VirtualFileSystemException
   {
      MemoryItem object = getItemById(id);
      MemoryItem parent = getItemById(parentId);
      if (context.getRoot().equals(object))
      {
         throw new InvalidArgumentException("Unable move root folder. ");
      }
      if (id.equals(parentId))
      {
         throw new InvalidArgumentException("Item cannot be moved to itself. ");
      }
      if (!(ItemType.PROJECT == parent.getType() || ItemType.FOLDER == parent.getType()))
      {
         throw new InvalidArgumentException("Unable move item. Item specified as parent is not a folder or project. ");
      }
      if (ItemType.PROJECT == parent.getType() && ItemType.PROJECT == object.getType())
      {
         throw new ConstraintException(
            "Unable move item. Item specified as parent is a project. Project cannot contains another project.");
      }
      if (!(hasPermissions(parent, VirtualFileSystemInfo.BasicPermissions.WRITE.value())
         && hasPermissions(object, VirtualFileSystemInfo.BasicPermissions.WRITE.value())))
      {
         throw new PermissionDeniedException("Unable move item '" + object.getPath() + "' to " + parent.getPath()
            + ". Operation not permitted. ");
      }

      if (object.getType() != ItemType.FILE)
      {
         // Be sure destination folder is not child (direct or not) of moved item.
         final String srcPath = object.getPath();
         final String destPath = parent.getPath();
         if (destPath.startsWith(srcPath))
         {
            throw new InvalidArgumentException("Unable move item " + srcPath + " to " + destPath +
               ". Item may not have itself as parent. ");
         }
         final List<MemoryItem> forMove = new ArrayList<MemoryItem>();
         object.accept(new MemoryItemVisitor()
         {
            @Override
            public void visit(MemoryItem i) throws VirtualFileSystemException
            {
               if (i.getType() == ItemType.FILE)
               {
                  forMove.add(i);
               }
               else
               {
                  for (MemoryItem ii : ((MemoryFolder)i).getChildren())
                  {
                     ii.accept(this);
                  }
                  forMove.add(i);
               }
            }
         });

         for (MemoryItem i : forMove)
         {
            if (!hasPermissions(i, VirtualFileSystemInfo.BasicPermissions.WRITE.value()))
            {
               throw new PermissionDeniedException("Unable move item '" + i.getPath() + "'. Operation not permitted. ");
            }
            if (ItemType.FILE == i.getType() && ((MemoryFile)i).isLocked())
            {
               throw new LockException("Unable move item '" + object.getName() +
                  "'. Child item '" + i.getPath() + "' is locked. ");
            }
         }
      }
      else
      {
         if (!((MemoryFile)object).isLockTokenMatched(lockToken))
         {
            throw new LockException("Unable move item " + object.getName() + ". Item is locked. ");
         }
      }

      final String name = object.getName();
      object.getParent().removeChild(name);
      ((MemoryFolder)parent).addChild(object);

      Item moved = fromMemoryItem(object, PropertyFilter.ALL_FILTER);
      if (listeners != null)
      {
         listeners.notifyListeners(
            new ChangeEvent(this, moved.getId(), moved.getPath(), moved.getMimeType(), ChangeEvent.ChangeType.MOVED));
      }
      return moved;
   }

   @Path("rename/{id}")
   @Override
   public Item rename(@PathParam("id") String id, //
                      @QueryParam("mediaType") MediaType newMediaType, //
                      @QueryParam("newname") String newName, //
                      @QueryParam("lockToken") String lockToken) throws VirtualFileSystemException
   {
      MemoryItem object = getItemById(id);
      if (context.getRoot().equals(object))
      {
         throw new InvalidArgumentException("Unable rename root folder. ");
      }

      if (!hasPermissions(object, VirtualFileSystemInfo.BasicPermissions.WRITE.value()))
      {
         throw new PermissionDeniedException("Unable rename item " + object.getName() + ". Operation not permitted. ");
      }

      if (ItemType.FILE == object.getType() && !((MemoryFile)object).isLockTokenMatched(lockToken))
      {
         throw new LockException("Unable rename item " + object.getName() + ". Item is locked. ");
      }

      if ((newName == null || newName.isEmpty()) && newMediaType == null)
      {
         // Nothing to do. Return unchanged object.
         return fromMemoryItem(object, PropertyFilter.ALL_FILTER);
      }

      final MemoryFolder parent = object.getParent();
      if (newMediaType != null
         && ItemType.PROJECT == parent.getType()
         && Project.PROJECT_MIME_TYPE.equals(newMediaType.getType() + '/' + newMediaType.getSubtype()))
      {
         throw new ConstraintException(
            "Unable change type of item. Item specified as parent is a project. Project cannot contains another project.");
      }

      if (!(newName == null || newName.isEmpty()))
      {
         parent.renameChild(object.getName(), newName);
         if (newMediaType != null)
         {
            object.setMediaType(newMediaType.toString());
         }
      }
      else
      {
         if (newMediaType != null)
         {
            object.setMediaType(newMediaType.toString());
         }
      }

      Item renamed = fromMemoryItem(object, PropertyFilter.ALL_FILTER);
      if (listeners != null)
      {
         listeners.notifyListeners(
            new ChangeEvent(this, renamed.getId(), renamed.getPath(), renamed.getMimeType(), ChangeEvent.ChangeType.RENAMED));
      }
      return renamed;
   }

   @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
   @Override
   public ItemList<Item> search(MultivaluedMap<String, String> query, //
                                @DefaultValue("-1") @QueryParam("maxItems") int maxItems, //
                                @QueryParam("skipCount") int skipCount, //
                                @DefaultValue("*") @QueryParam("propertyFilter") PropertyFilter propertyFilter //
   ) throws NotSupportedException, VirtualFileSystemException
   {
      throw new NotSupportedException("Not supported. ");
   }

   @Override
   public ItemList<Item> search(@QueryParam("statement") String statement, //
                                @DefaultValue("-1") @QueryParam("maxItems") int maxItems, //
                                @QueryParam("skipCount") int skipCount //
   ) throws NotSupportedException, VirtualFileSystemException
   {
      throw new NotSupportedException("Not supported. ");
   }

   @Path("unlock/{id}")
   @Override
   public void unlock(@PathParam("id") String id, //
                      @QueryParam("lockToken") String lockToken //
   ) throws NotSupportedException, VirtualFileSystemException
   {
      MemoryItem memoryFile = getItemById(id);
      if (!hasPermissions(memoryFile, VirtualFileSystemInfo.BasicPermissions.WRITE.value()))
      {
         throw new PermissionDeniedException("Unable unlock item '" + memoryFile.getName() +
            "'. Operation not permitted. ");
      }
      if (memoryFile.getType() != ItemType.FILE)
      {
         throw new LockException("Object is not locked. "); // Folder can't be locked.
      }
      ((MemoryFile)memoryFile).unlock(lockToken);
   }

   @Path("acl/{id}")
   @Override
   public void updateACL(@PathParam("id") String id, //
                         List<AccessControlEntry> acl, //
                         @DefaultValue("false") @QueryParam("override") Boolean override, //
                         @QueryParam("lockToken") String lockToken //
   ) throws NotSupportedException, VirtualFileSystemException
   {
      MemoryItem object = getItemById(id);
      if (ItemType.FILE == object.getType() && !((MemoryFile)object).isLockTokenMatched(lockToken))
      {
         throw new LockException("Unable update ACL of item '" + object.getName() + "'. Item is locked. ");
      }
      if (!hasPermissions(object, VirtualFileSystemInfo.BasicPermissions.WRITE.value()))
      {
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
      @QueryParam("lockToken") String lockToken) throws VirtualFileSystemException
   {
      MemoryItem mFile = getItemById(id);
      if (ItemType.FILE != mFile.getType())
      {
         throw new InvalidArgumentException("Object " + mFile.getName() + " is not file. ");
      }
      try
      {
         ((MemoryFile)mFile).setContent(newContent);
      }
      catch (IOException e)
      {
         throw new VirtualFileSystemException("Unable update content of file. " + e.getMessage(), e);
      }
      mFile.setMediaType(mediaType == null ? null : mediaType.toString());
   }

   @Path("item/{id}")
   @Override
   public Item updateItem(@PathParam("id") String id, //
                          List<Property> properties, //
                          @QueryParam("lockToken") String lockToken) throws VirtualFileSystemException
   {
      MemoryItem memoryItem = getItemById(id);
      memoryItem.updateProperties(properties);
      return fromMemoryItem(memoryItem, PropertyFilter.ALL_FILTER);
   }

   @Path("export/{folderId}")
   @Override
   public ContentStream exportZip(@PathParam("folderId") String folderId) throws IOException, VirtualFileSystemException
   {
      MemoryItem mItem = getItemById(folderId);
      if (!(ItemType.FOLDER == mItem.getType() || ItemType.PROJECT == mItem.getType()))
      {
         throw new InvalidArgumentException("Unable export to zip. Item is not a folder or project. ");
      }
      MemoryFolder exportFolder = (MemoryFolder)mItem;
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      try
      {
         ZipOutputStream zipOut = new ZipOutputStream(out);
         LinkedList<MemoryFolder> q = new LinkedList<MemoryFolder>();
         q.add(exportFolder);
         final String rootZipPath = exportFolder.getPath();
         byte[] b = new byte[1024];
         while (!q.isEmpty())
         {
            List<MemoryItem> children = q.pop().getChildren();
            for (MemoryItem current : children)
            {
               final String zipEntryName = current.getPath().substring(rootZipPath.length() + 1).replace('\\', '/');
               if (current.getType() == ItemType.FILE)
               {
                  zipOut.putNextEntry(new ZipEntry(zipEntryName));
                  InputStream in = null;
                  try
                  {
                     in = ((MemoryFile)current).getContent().getStream();
                     int r;
                     while ((r = in.read(b)) != -1)
                     {
                        zipOut.write(b, 0, r);
                     }
                  }
                  finally
                  {
                     if (in != null)
                     {
                        in.close();
                     }
                  }
               }
               else if (".project".equals(current.getName()))
               {
                  zipOut.putNextEntry(new ZipEntry(zipEntryName));
                  try
                  {
                     JsonWriter jw = new JsonWriter(zipOut);
                     JsonGenerator.createJsonArray(current.getProperties(PropertyFilter.ALL_FILTER)).writeTo(jw);
                     jw.flush();
                  }
                  catch (JsonException e)
                  {
                     throw new VirtualFileSystemRuntimeException(e.getMessage(), e);
                  }
               }
               else
               {
                  zipOut.putNextEntry(new ZipEntry(zipEntryName + '/'));
                  q.add((MemoryFolder)current);
               }
               zipOut.closeEntry();
            }
         }
         zipOut.close();
      }
      finally
      {
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
   ) throws VirtualFileSystemException, IOException
   {
      ZipInputStream zip = null;
      try
      {
         MemoryItem object = getItemById(parentId);
         if (!(ItemType.FOLDER == object.getType() || ItemType.PROJECT == object.getType()))
         {
            throw new InvalidArgumentException("Unable import from zip. "
               + "Item specified as parent is not a folder or project. ");
         }

         final ZipContent zipContent = spoolZipStream(in);

         if (zipContent.isProject && ItemType.PROJECT == object.getType())
         {
            throw new ConstraintException("Unable import from zip. Project cannot be imported to another one. ");
         }

         zip = new ZipInputStream(zipContent.data);
         // Wrap zip stream to prevent close it. We can pass stream to other method and it can read content of current
         // ZipEntry but not able to close original stream of ZIPed data.
         InputStream noCloseZip = new NotClosableInputStream(zip);
         MemoryFolder folder = (MemoryFolder)object;
         if (zipContent.isProject)
         {
            // If zip contains project change export folder to project.
            MemoryFolder parent = folder.getParent();
            parent.removeChild(folder.getName());
            folder = new MemoryProject(folder.getId(), folder.getName());
            parent.addChild(folder);
         }
         ZipEntry zipEntry;
         while ((zipEntry = zip.getNextEntry()) != null)
         {
            String zipEntryName = zipEntry.getName();
            String[] segments = zipEntryName.split("/");
            MemoryFolder current = folder;
            for (int i = 0, l = segments.length - 1; i < l; i++)
            {
               MemoryItem child = current.getChild(segments[i]);
               if (child == null)
               {
                  child = new MemoryFolder(ObjectIdGenerator.generateId(), segments[i]);
                  current.addChild(child);
               }
               current = (MemoryFolder)child;
            }

            final String name = segments[segments.length - 1];
            if (zipEntry.isDirectory())
            {
               if (current.getChild(name) != null)
               {
                  current.addChild(new MemoryFolder(ObjectIdGenerator.generateId(), name));
               }
            }
            else if (".project".equals(name))
            {
               List<Property> properties;
               try
               {
                  JsonParser jp = new JsonParser();
                  jp.parse(noCloseZip);
                  Property[] array = (Property[])ObjectBuilder.createArray(Property[].class, jp.getJsonObject());
                  properties = Arrays.asList(array);
               }
               catch (JsonException e)
               {
                  throw new VirtualFileSystemRuntimeException(e.getMessage(), e);
               }
               if (properties.size() > 0)
               {
                  current.updateProperties(properties);
               }
            }
            else
            {
               MemoryItem child = current.getChild(name);
               if (child != null && overwrite && ItemType.FILE == child.getType())
               {
                  ((MemoryFile)child).setContent(noCloseZip);
               }
               else
               {
                  //TODO final MediaType mediaType = Resolver.INSTANCE.getMediaType(name);
                  MemoryFile f = new MemoryFile(ObjectIdGenerator.generateId(), name, "*/*", null);
                  f.setContent(noCloseZip);
                  current.addChild(f);
               }
            }
            zip.closeEntry();
         }
         context.addItem(folder);
      }
      finally
      {
         if (zip != null)
         {
            zip.close();
         }
      }
   }

   /** Wrapper for ZipInputStream that make possible read content of ZipEntry but prevent close ZipInputStream. */
   private static final class NotClosableInputStream extends FilterInputStream
   {
      public NotClosableInputStream(InputStream delegate)
      {
         super(delegate);
      }

      /** @see java.io.InputStream#close() */
      @Override
      public void close() throws IOException
      {
      }
   }

   /** The threshold after that checking of ZIP ratio started. */
   private static final long ZIP_THRESHOLD = 1000000;
   /**
    * Max compression ratio. If the number of bytes uncompressed data is exceed the number
    * of bytes of compressed stream more than this ratio (and number of uncompressed data
    * is more than threshold) then VirtualFileSystemRuntimeException is thrown.
    */
   private static final int ZIP_RATIO = 100;

   /**
    * Spool content of zip in memory.
    *
    * @param src
    *    source zip
    * @return spool zip
    * @throws IOException
    *    if any i/o error occur
    */
   private ZipContent spoolZipStream(InputStream src) throws IOException
   {
      ByteArrayOutputStream inMemorySpool = new ByteArrayOutputStream();

      int r;
      byte[] buff = new byte[1024];
      while ((r = src.read(buff)) != -1)
      {
         inMemorySpool.write(buff, 0, r);
      }

      byte[] zipped = inMemorySpool.toByteArray();
      InputStream in = new ByteArrayInputStream(zipped);

      ZipInputStream zip = null;
      try
      {
         // Counts numbers of compressed data.
         final CountingInputStream compressedCounter = new CountingInputStream(in);
         zip = new ZipInputStream(compressedCounter);
         // Counts number of uncompressed data.
         CountingInputStream uncompressedCounter = new CountingInputStream(zip)
         {
            @Override
            public int read() throws IOException
            {
               int i = super.read();
               checkCompressionRatio();
               return i;
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException
            {
               int i = super.read(b, off, len);
               checkCompressionRatio();
               return i;
            }

            @Override
            public int read(byte[] b) throws IOException
            {
               int i = super.read(b);
               checkCompressionRatio();
               return i;
            }

            @Override
            public long skip(long length) throws IOException
            {
               long i = super.skip(length);
               checkCompressionRatio();
               return i;
            }

            private void checkCompressionRatio()
            {
               long uncompressedBytes = getByteCount(); // number of uncompressed bytes
               if (uncompressedBytes > ZIP_THRESHOLD)
               {
                  long compressedBytes = compressedCounter.getByteCount(); // number of compressed bytes
                  if (uncompressedBytes > (ZIP_RATIO * compressedBytes))
                  {
                     throw new VirtualFileSystemRuntimeException("Zip bomb detected. ");
                  }
               }
            }
         };

         boolean isProject = false;

         ZipEntry zipEntry;
         while ((zipEntry = zip.getNextEntry()) != null)
         {
            if (".project".equals(zipEntry.getName()))
            {
               isProject = true;
            }
            else if (!zipEntry.isDirectory())
            {
               while (uncompressedCounter.read(buff) != -1)
               {
                  // Read full data from stream to be able detect zip-bomb.
               }
            }
         }

         return new ZipContent(new ByteArrayInputStream(zipped), isProject);
      }
      finally
      {
         if (zip != null)
         {
            zip.close();
         }
      }
   }

   private static final class ZipContent
   {
      final InputStream data;
      final boolean isProject;

      ZipContent(InputStream data, boolean isProject)
      {
         this.data = data;
         this.isProject = isProject;
      }
   }

   @Path("downloadfile/{id}")
   @Override
   public Response downloadFile(@PathParam("id") String id) throws ItemNotFoundException, InvalidArgumentException,
      PermissionDeniedException, VirtualFileSystemException
   {
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
   ) throws ItemNotFoundException, InvalidArgumentException, ItemAlreadyExistException, PermissionDeniedException,
      VirtualFileSystemException, IOException
   {

      try
      {
         MemoryItem parent = getItemById(parentId);
         if (!(ItemType.FOLDER == parent.getType() || ItemType.PROJECT == parent.getType()))
         {
            throw new InvalidArgumentException("Unable upload file. Item specified as parent is not a folder or project. ");
         }

         FileItem contentItem = null;
         MediaType mediaType = null;
         String name = null;
         boolean overwrite = false;

         while (formData.hasNext())
         {
            FileItem item = formData.next();
            if (!item.isFormField())
            {
               if (contentItem == null)
               {
                  contentItem = item;
               }
               else
               {
                  throw new InvalidArgumentException("More then one upload file is found but only one should be. ");
               }
            }
            else if ("mimeType".equals(item.getFieldName()))
            {
               String m = item.getString().trim();
               if (m.length() > 0)
               {
                  mediaType = MediaType.valueOf(m);
               }
            }
            else if ("name".equals(item.getFieldName()))
            {
               name = item.getString().trim();
            }
            else if ("overwrite".equals(item.getFieldName()))
            {
               overwrite = Boolean.parseBoolean(item.getString().trim());
            }
         }

         if (contentItem == null)
         {
            throw new InvalidArgumentException("Cannot find file for upload. ");
         }

         if (name == null || name.isEmpty())
         {
            name = contentItem.getName();
         }

         if (mediaType == null)
         {
            String contentType = contentItem.getContentType();
            mediaType = contentType != null ? MediaType.valueOf(contentType) : MediaType.APPLICATION_OCTET_STREAM_TYPE;
         }
         MemoryFolder folder = (MemoryFolder)parent;
         try
         {
            MemoryFile file =
               new MemoryFile(ObjectIdGenerator.generateId(), name, mediaType.toString(), contentItem.getInputStream());
            if (listeners != null)
            {
               listeners.notifyListeners(
                  new ChangeEvent(this, file.getId(), file.getPath(), file.getMediaType(), ChangeEvent.ChangeType.CONTENT_UPDATED));
            }
         }
         catch (ItemAlreadyExistException e)
         {
            if (!overwrite)
            {
               throw new ItemAlreadyExistException("Unable upload file. File with the same name exists. ");
            }
            MemoryItem object = folder.getChild(name);
            if (ItemType.FILE != object.getType())
            {
               throw new ItemAlreadyExistException(
                  "Unable upload file. Item with the same name exists but it is not a file. ");
            }
            MemoryFile file = (MemoryFile)object;
            file.setMediaType(mediaType.toString());
            file.setContent(contentItem.getInputStream());
            if (listeners != null)
            {
               listeners.notifyListeners(
                  new ChangeEvent(this, file.getId(), file.getPath(), file.getMediaType(), ChangeEvent.ChangeType.CONTENT_UPDATED));
            }
         }
         return Response.ok("", MediaType.TEXT_HTML).build();
      }
      catch (VirtualFileSystemException e)
      {
         sendErrorAsHTML(e);
         // never thrown
         throw e;
      }
      catch (IOException e)
      {
         sendErrorAsHTML(e);
         // never thrown
         throw e;
      }
   }

   @Path("downloadzip/{folderId}")
   @Override
   public Response downloadZip(@PathParam("folderId") String folderId) throws ItemNotFoundException,
      InvalidArgumentException, PermissionDeniedException, IOException, VirtualFileSystemException
   {
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
                             Iterator<FileItem> formData) throws IOException, VirtualFileSystemException
   {
      try
      {
         FileItem contentItem = null;
         boolean overwrite = false;
         while (formData.hasNext())
         {
            FileItem item = formData.next();
            if (!item.isFormField())
            {
               if (contentItem == null)
               {
                  contentItem = item;
               }
               else
               {
                  throw new InvalidArgumentException("More then one upload file is found but only one should be. ");
               }
            }
            else if ("overwrite".equals(item.getFieldName()))
            {
               overwrite = Boolean.parseBoolean(item.getString().trim());
            }
         }
         if (contentItem == null)
         {
            throw new InvalidArgumentException("Cannot find file for upload. ");
         }
         importZip(parentId, contentItem.getInputStream(), overwrite);
         return Response.ok("", MediaType.TEXT_HTML).build();
      }
      catch (VirtualFileSystemException e)
      {
         sendErrorAsHTML(e);
         // never thrown
         throw e;
      }
      catch (IOException e)
      {
         sendErrorAsHTML(e);
         // never thrown
         throw e;
      }
   }

   @Override
   public void startWatchUpdates(String projectId) throws
      ItemNotFoundException, InvalidArgumentException, VirtualFileSystemException
   {
      // TODO
   }

   @Override
   public void stopWatchUpdates(String projectId) throws
      ItemNotFoundException, InvalidArgumentException, VirtualFileSystemException
   {
      // TODO
   }

   /* =========================================================================== */
   /* =========================================================================== */

   enum Resolver
   {
      INSTANCE;
      /*=====================================*/
      final MimeTypeResolver resolver;

      private Resolver()
      {
         resolver = new MimeTypeResolver();
         resolver.setDefaultMimeType("text/plain");
      }

      public MediaType getMediaType(String filename)
      {
         return MediaType.valueOf(resolver.getMimeType(filename));
      }
   }

   private MemoryItem getItemById(String id) throws VirtualFileSystemException
   {
      MemoryItem item = context.getItem(id);
      if (item == null)
      {
         throw new ItemNotFoundException("Object '" + id + "' does not exists. ");
      }
      if (!hasPermissions(item, VirtualFileSystemInfo.BasicPermissions.READ.value()))
      {
         throw new PermissionDeniedException("Access denied to object " + id + ". ");
      }
      return item;
   }

   private boolean hasPermissions(MemoryItem item, String... permissions)
   {
      List<AccessControlEntry> acl = item.getACL();
      if (acl.isEmpty())
      {
         return true;
      }
      final String user = getCurrentUser();
      for (AccessControlEntry ace : acl)
      {
         if (user.equals(ace.getPrincipal()))
         {
            Set<String> userPermissions = ace.getPermissions();
            if (userPermissions != null && !userPermissions.contains(VirtualFileSystemInfo.BasicPermissions.ALL.value()))
            {
               return userPermissions.containsAll(Arrays.asList(permissions));
            }
         }
      }
      return true;
   }

   private String getCurrentUser()
   {
      ConversationState cs = ConversationState.getCurrent();
      if (cs != null)
      {
         return cs.getIdentity().getUserId();
      }
      return VirtualFileSystemInfo.ANONYMOUS_PRINCIPAL;
   }

   private MemoryItem getItemByPath(String path) throws VirtualFileSystemException
   {
      if (path == null)
      {
         throw new IllegalArgumentException("Item path may not be null. ");
      }
      if ("/".equals(path))
      {
         return context.getRoot();
      }
      MemoryItem item = context.getRoot();
      String[] split = path.split("/");
      for (int i = 1, length = split.length; item != null && i < length; i++)
      {
         String name = split[i];
         if (ItemType.FOLDER == item.getType() || ItemType.PROJECT == item.getType())
         {
            item = ((MemoryFolder)item).getChild(name);
         }
      }
      if (item == null)
      {
         throw new ItemNotFoundException("Object '" + path + "' does not exists. ");
      }
      if (!hasPermissions(item, VirtualFileSystemInfo.BasicPermissions.READ.value()))
      {
         throw new PermissionDeniedException("Access denied to object " + path + ". ");
      }
      return item;
   }

   private void checkName(String name) throws InvalidArgumentException
   {
      if (name == null || name.trim().isEmpty())
      {
         throw new InvalidArgumentException("Item's name is not set. ");
      }
   }

   private Item fromMemoryItem(MemoryItem memoryItem, PropertyFilter propertyFilter) throws VirtualFileSystemException
   {
      return fromMemoryItem(memoryItem, propertyFilter, true);
   }

   private Item fromMemoryItem(MemoryItem memoryItem, PropertyFilter propertyFilter, boolean addLinks)
      throws VirtualFileSystemException
   {
      String mediaType = memoryItem.getMediaType();
      if (memoryItem.getType() == ItemType.FILE)
      {
         MemoryFile memoryFile = (MemoryFile)memoryItem;
         return new File(memoryFile.getId(), memoryFile.getName(), memoryFile.getPath(), memoryFile.getParent().getId(),
            memoryFile.getCreationDate(), memoryFile.getLastModificationDate(), memoryFile.getVersionId(),
            memoryFile.getMediaType(), memoryFile.getContent().getLength(), memoryFile.isLocked(),
            memoryFile.getProperties(propertyFilter), addLinks ? createFileLinks(memoryFile) : null);
      }

      if (memoryItem.getType() == ItemType.PROJECT)
      {
         MemoryProject memoryProject = (MemoryProject)memoryItem;
         return new Project(memoryProject.getId(), memoryProject.getName(),
            mediaType == null ? Project.FOLDER_MIME_TYPE : mediaType, memoryProject.getPath(),
            memoryProject.getParent().getId(), memoryProject.getCreationDate(),
            memoryProject.getProperties(propertyFilter), addLinks ? createProjectLinks(memoryProject) : null,
            memoryProject.getProjectType());
      }

      return new Folder(memoryItem.getId(), memoryItem.getName(),
         mediaType == null ? Folder.FOLDER_MIME_TYPE : mediaType, memoryItem.getPath(),
         memoryItem.getParent() == null ? null : memoryItem.getParent().getId(), memoryItem.getCreationDate(),
         memoryItem.getProperties(propertyFilter), addLinks ? createFolderLinks((MemoryFolder)memoryItem) : null);
   }

   private Map<String, Link> createFileLinks(MemoryFile file) throws VirtualFileSystemException
   {
      Map<String, Link> links = createBaseLinks(file);
      String id = file.getId();

      links.put(Link.REL_CONTENT, //
         new Link(createURI("content", id), Link.REL_CONTENT, file.getMediaType()));

      links.put(Link.REL_DOWNLOAD_FILE, //
         new Link(createURI("downloadfile", id), Link.REL_DOWNLOAD_FILE, file.getMediaType()));

      links.put(Link.REL_CONTENT_BY_PATH, //
         new Link(createURI("contentbypath", file.getPath().substring(1)), Link.REL_CONTENT_BY_PATH, file.getMediaType()));

      links.put(Link.REL_VERSION_HISTORY, //
         new Link(createURI("version-history", id), Link.REL_VERSION_HISTORY, MediaType.APPLICATION_JSON));

      links.put(Link.REL_CURRENT_VERSION, //
         new Link(createURI("item", file.getLatestVersionId()), Link.REL_CURRENT_VERSION, MediaType.APPLICATION_JSON));

      if (file.isLocked())
      {
         links.put(Link.REL_UNLOCK, //
            new Link(createURI("unlock", id, "lockToken", "[lockToken]"), Link.REL_UNLOCK, null));
      }
      else
      {
         links.put(Link.REL_LOCK, //
            new Link(createURI("lock", id), Link.REL_LOCK, MediaType.APPLICATION_JSON));
      }

      return links;
   }

   private Map<String, Link> createFolderLinks(MemoryFolder folder) throws VirtualFileSystemException
   {
      Map<String, Link> links = createBaseFolderLinks(folder);
      String id = folder.getId();

      links.put(Link.REL_CREATE_PROJECT, //
         new Link(createURI("project", id, "name", "[name]", "type", "[type]"), Link.REL_CREATE_PROJECT,
            MediaType.APPLICATION_JSON));

      return links;
   }

   private Map<String, Link> createProjectLinks(MemoryProject project) throws VirtualFileSystemException
   {
      return createBaseFolderLinks(project);
   }

   private Map<String, Link> createBaseFolderLinks(MemoryFolder folder) throws VirtualFileSystemException
   {
      Map<String, Link> links = createBaseLinks(folder);
      String id = folder.getId();

      links.put(Link.REL_CHILDREN, //
         new Link(createURI("children", id), Link.REL_CHILDREN, MediaType.APPLICATION_JSON));

      links.put(Link.REL_CREATE_FOLDER, //
         new Link(createURI("folder", id, "name", "[name]"), Link.REL_CREATE_FOLDER, MediaType.APPLICATION_JSON));

      links.put(Link.REL_CREATE_FILE, //
         new Link(createURI("file", id, "name", "[name]"), Link.REL_CREATE_FILE, MediaType.APPLICATION_JSON));

      links.put(Link.REL_UPLOAD_FILE, //
         new Link(createURI("uploadfile", id), Link.REL_UPLOAD_FILE, MediaType.TEXT_HTML));

      links.put(Link.REL_EXPORT, //
         new Link(createURI("export", id), Link.REL_EXPORT, "application/zip"));

      links.put(Link.REL_IMPORT, //
         new Link(createURI("import", id), Link.REL_IMPORT, "application/zip"));

      links.put(Link.REL_DOWNLOAD_ZIP, //
         new Link(createURI("downloadzip", id), Link.REL_DOWNLOAD_ZIP, "application/zip"));

      links.put(Link.REL_UPLOAD_ZIP, //
         new Link(createURI("uploadzip", id), Link.REL_UPLOAD_ZIP, MediaType.TEXT_HTML));

      return links;
   }

   private Map<String, Link> createBaseLinks(MemoryItem data) throws VirtualFileSystemException
   {
      Map<String, Link> links = new HashMap<String, Link>();
      String id = data.getId();

      links.put(Link.REL_SELF, //
         new Link(createURI("item", id), Link.REL_SELF, MediaType.APPLICATION_JSON));

      links.put(Link.REL_ACL, //
         new Link(createURI("acl", id), Link.REL_ACL, MediaType.APPLICATION_JSON));

      MemoryFolder parent = data.getParent();
      // Root folder can't be moved copied and has not parent.
      if (parent != null)
      {
         links.put(Link.REL_DELETE, //
            new Link(ItemType.FILE == data.getType() && ((MemoryFile)data).isLocked()
               ? createURI("delete", id, "lockToken", "[lockToken]") : createURI("delete", id),
               Link.REL_DELETE, null));

         links.put(Link.REL_COPY, //
            new Link(createURI("copy", id, "parentId", "[parentId]"), Link.REL_COPY, MediaType.APPLICATION_JSON));

         links.put(Link.REL_MOVE, //
            new Link(ItemType.FILE == data.getType() && ((MemoryFile)data).isLocked()
               ? createURI("move", id, "parentId", "[parentId]", "lockToken", "[lockToken]")
               : createURI("move", id, "parentId", "[parentId]"),
               Link.REL_MOVE, MediaType.APPLICATION_JSON));

         links.put(Link.REL_PARENT, //
            new Link(createURI("item", parent.getId()), Link.REL_PARENT, MediaType.APPLICATION_JSON));

         links.put(
            Link.REL_RENAME, //
            new Link(createURI("rename", id, "newname", "[newname]", "mediaType", "[mediaType]", "lockToken",
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

      URI uri = uriBuilder.build(vfsID);

      return uri.toString();
   }

   /**
    * Throws WebApplicationException that contains error message in HTML format.
    *
    * @param e
    *    exception
    */
   private void sendErrorAsHTML(Exception e)
   {
      // GWT framework (used on client side) requires result in HTML format if use HTML forms.
      if (e instanceof ItemAlreadyExistException)
      {
         throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.ITEM_EXISTS),
            MediaType.TEXT_HTML).build());
      }
      else if (e instanceof ItemNotFoundException)
      {
         throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.ITEM_NOT_FOUND),
            MediaType.TEXT_HTML).build());
      }
      else if (e instanceof InvalidArgumentException)
      {
         throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.INVALID_ARGUMENT),
            MediaType.TEXT_HTML).build());
      }
      else if (e instanceof ConstraintException)
      {
         throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.CONSTRAINT),
            MediaType.TEXT_HTML).build());
      }
      else if (e instanceof PermissionDeniedException)
      {
         throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.NOT_PERMITTED),
            MediaType.TEXT_HTML).build());
      }
      else if (e instanceof LockException)
      {
         throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.LOCK_CONFLICT),
            MediaType.TEXT_HTML).build());
      }
      throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.INTERNAL_ERROR),
         MediaType.TEXT_HTML).build());
   }

   private String formatAsHtml(String message, int exitCode)
   {
      return "<pre>Code: " + exitCode + " Text: " + message + "</pre>";
   }
}
