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
package org.exoplatform.ide.vfs.impl.jcr;

import org.exoplatform.ide.vfs.server.ConvertibleProperty;
import org.exoplatform.ide.vfs.server.LazyIterator;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.ConstraintException;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.LockException;
import org.exoplatform.ide.vfs.server.exceptions.NotSupportedException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.LockToken;
import org.exoplatform.ide.vfs.shared.Type;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.ACLCapability;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.QueryCapability;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class JcrFileSystem implements VirtualFileSystem
{
   static final Set<String> SKIPPED_QUERY_PROPERTIES = new HashSet<String>(Arrays.asList("jcr:path", "jcr:score"));

   protected final Session session;

   protected final ItemType2NodeTypeResolver itemType2NodeTypeResolver;

   protected final UriInfo uriInfo;

   private VirtualFileSystemInfo vfsInfo;

   public JcrFileSystem(Session session, ItemType2NodeTypeResolver mediaType2NodeTypeResolver, UriInfo uriInfo)
   {
      this.session = session;
      this.itemType2NodeTypeResolver = mediaType2NodeTypeResolver;
      this.uriInfo = uriInfo;
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#copy(java.lang.String,
    *      java.lang.String)
    */
   @Path("copy/{id:.*}")
   public Response copy(@PathParam("id") String id, //
      @QueryParam("parentId") String parentId //
   ) throws ItemNotFoundException, ConstraintException, PermissionDeniedException, VirtualFileSystemException
   {
      ItemData object = getItemData(id);
      ItemData folder = getItemData(parentId);
      if (Type.FOLDER != folder.getType())
         throw new InvalidArgumentException("Unable copy. Item specified as parent is not a folder. ");
      ItemData newobject = object.copyTo((FolderData)folder);
      return Response.created(createURI("item", newobject.getId())).build();
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#createFile(java.lang.String,
    *      java.lang.String, javax.ws.rs.core.MediaType, java.io.InputStream)
    */
   @Path("file/{parentId:.*}")
   public Response createFile(@PathParam("parentId") String parentId, //
      @QueryParam("name") String name, //
      @DefaultValue(MediaType.APPLICATION_OCTET_STREAM) @QueryParam("mediaType") MediaType mediaType, //
      InputStream content //
   ) throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      checkName(name);
      ItemData parentData = getItemData(parentId);
      if (Type.FOLDER != parentData.getType())
         throw new InvalidArgumentException("Unable create file. Item specified as parent is not a folder. ");
      FileData newfile =
         ((FolderData)parentData).createFile(name, itemType2NodeTypeResolver.getFileNodeType(mediaType),
            itemType2NodeTypeResolver.getFileContentNodeType(mediaType), mediaType, null, null, content);
      return Response.created(createURI("item", newfile.getId())).build();
   }

   /*
    * Since we are use path of JCR node as id we need such method for
    * creation item in root folder. Path in this case may be:
    * 
    * 1. .../file/?[Query_Parameters] --- SLASH
    * 2. .../file?[Query_Parameters]  --- NO SLASH
    * 
    * Path without slash at the end does not match to method
    * createFile(String, String, MediaType, InputStream).
    * It is possible to create special template to fix this issue:
    * 
    * file{X:(/)?}/{parent:.*} --- Make final slash optional if {parent} is not specified (Root item)  
    * 
    * But to keep URI template more simple and clear is better to have one more
    * method with template matched to root folder.
    */
   @POST
   @Path("file")
   public Response createFile(@QueryParam("name") String name, //
      @DefaultValue(MediaType.APPLICATION_OCTET_STREAM) @QueryParam("mediaType") MediaType mediaType, //
      InputStream content) throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException,
      VirtualFileSystemException
   {
      return createFile("/", name, mediaType, content);
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#createFolder(java.lang.String,
    *      java.lang.String)
    */
   @Path("folder/{parentId:.*}")
   public Response createFolder(@PathParam("parentId") String parentId, //
      @QueryParam("name") String name //
   ) throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      checkName(name);
      ItemData parentData = getItemData(parentId);
      if (Type.FOLDER != parentData.getType())
         throw new InvalidArgumentException("Unable create folder. Item specified as parent is not a folder. ");
      FolderData newfolder =
         ((FolderData)parentData).createFolder(name, itemType2NodeTypeResolver.getFolderNodeType(null), null, null);
      return Response.created(createURI("item", newfolder.getId())).build();
   }

   /*
    * This method has the same purposes as createFile(String, MediaType, InputStream)
    */
   @POST
   @Path("folder")
   public Response createFolder(@QueryParam("name") String name) throws ItemNotFoundException,
      InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      return createFolder("/", name);
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#createProject(java.lang.String,
    *      java.lang.String, java.lang.String, java.util.List)
    */
   @Override
   public Response createProject(String parentId, String name, String type, List<ConvertibleProperty> properties)
      throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      checkName(name);
      ItemData parentData = getItemData(parentId);
      if (Type.FOLDER != parentData.getType())
         throw new InvalidArgumentException("Unable create folder. Item specified as parent is not a folder. ");
      FolderData newproject =
         ((FolderData)parentData).createFolder(name, itemType2NodeTypeResolver.getFolderNodeType(type),
            itemType2NodeTypeResolver.getFolderMixins(type), properties);
      return Response.created(createURI("item", newproject.getId())).build();
   }

   public Response createProject(String name, String type, List<ConvertibleProperty> properties)
      throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      return createProject("/", name, type, properties);
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#delete(java.lang.String,
    *      java.lang.String)
    */
   @Path("delete/{id:.*}")
   public void delete(@PathParam("id") String id, //
      @QueryParam("lockToken") String lockToken //
   ) throws ItemNotFoundException, ConstraintException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      getItemData(id).delete(lockToken);
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getACL(java.lang.String)
    */
   @Path("acl/{id:.*}")
   public List<AccessControlEntry> getACL(@PathParam("id") String id) throws NotSupportedException,
      ItemNotFoundException, PermissionDeniedException, VirtualFileSystemException
   {
      return getItemData(id).getACL();
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getChildren(java.lang.String,
    *      int, int, org.exoplatform.ide.vfs.server.PropertyFilter)
    */
   @Path("children/{id:.*}")
   public ItemList<Item> getChildren(@PathParam("id") String folderId, //
      @DefaultValue("-1") @QueryParam("maxItems") int maxItems, //
      @QueryParam("skipCount") int skipCount, //
      @DefaultValue(PropertyFilter.NONE) @QueryParam("propertyFilter") PropertyFilter propertyFilter //
   ) throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      if (skipCount < 0)
         throw new InvalidArgumentException("'skipCount' parameter is negative. ");

      ItemData data = getItemData(folderId);
      if (Type.FOLDER != data.getType())
         throw new InvalidArgumentException("Unable get children. Item " + folderId + " is not a folder. ");

      FolderData folderData = (FolderData)data;
      LazyIterator<ItemData> children = folderData.getChildren();
      try
      {
         if (skipCount > 0)
            children.skip(skipCount);
      }
      catch (NoSuchElementException nse)
      {
         throw new InvalidArgumentException("'skipCount' parameter is greater then total number of items. ");
      }

      // TODO Make improvement  for large amount of items
      List<Item> l = new ArrayList<Item>();
      for (int count = 0; children.hasNext() && (maxItems < 0 || count < maxItems); count++)
         l.add(fromItemData(children.next(), propertyFilter));

      ItemList<Item> il = new ItemList<Item>(l);
      il.setNumItems(children.size());
      il.setHasMoreItems(children.hasNext());

      return il;
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getContent(java.lang.String)
    */
   @Path("content/{id:.*}")
   public Response getContent(@PathParam("id") String id) throws ItemNotFoundException, InvalidArgumentException,
      PermissionDeniedException, VirtualFileSystemException
   {
      ItemData data = getItemData(id);
      if (Type.FILE != data.getType())
         throw new InvalidArgumentException("Unable get content. Item " + id + " is not a file. ");
      FileData fileData = (FileData)data;
      // TODO : cache control, last modification date, etc ??
      return Response.ok(fileData.getContent(), fileData.getContenType()).build();
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getVfsInfo()
    */
   public VirtualFileSystemInfo getVfsInfo()
   {
      if (vfsInfo == null)
      {
         BasicPermissions[] basicPermissions = BasicPermissions.values();
         List<String> permissions = new ArrayList<String>(basicPermissions.length);
         for (BasicPermissions bp : basicPermissions)
            permissions.add(bp.value());
         vfsInfo =
            new VirtualFileSystemInfo(true, true, org.exoplatform.services.security.IdentityConstants.ANONIM,
               org.exoplatform.services.security.IdentityConstants.ANY, permissions, ACLCapability.MANAGE,
               QueryCapability.BOTHCOMBINED, "", "/", createUrlTemplates());
      }
      return vfsInfo;
   }

   private Map<String, Link> createUrlTemplates()
   {
      Map<String, Link> templates = new HashMap<String, Link>();
      templates.put(Link.REL_CREATE_FILE, //
         new Link(createURI("file", "[parentId]", "name", "[name]", "mediaType", "[mediaType]").toString(), //
            Link.REL_CREATE_FILE, //
            MediaType.WILDCARD));
      templates.put(Link.REL_CREATE_FOLDER, //
         new Link(createURI("folder", "[parentId]", "name", "[name]").toString(), //
            Link.REL_CREATE_FOLDER, //
            null));
      templates.put(Link.REL_COPY, //
         new Link(createURI("copy", "[id]", "parentId", "[parentId]").toString(), //
            Link.REL_MOVE, //
            null));
      templates.put(Link.REL_MOVE, //
         new Link(createURI("move", "[id]", "parentId", "[parentId]", "lockToken", "[lockToken]").toString(), //
            Link.REL_MOVE, //
            null));
      templates.put(Link.REL_LOCK, //
         new Link(createURI("lock", "[id]").toString(), //
            Link.REL_LOCK, //
            null));
      templates.put(Link.REL_UNLOCK, //
         new Link(createURI("unlock", "[id]", "lockToken", "[lockToken]").toString(), //
            Link.REL_UNLOCK, //
            null));
      templates.put(
         Link.REL_SEARCH, //
         new Link(createURI("search", null, "maxItems", "[maxItems]", "skipCount", "[skipCount]", "propertyFilter",
            "[propertyFilter]").toString(), //
            Link.REL_SEARCH, //
            MediaType.APPLICATION_FORM_URLENCODED));
      return templates;
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getItem(java.lang.String,
    *      org.exoplatform.ide.vfs.server.PropertyFilter)
    */
   @Path("item/{id:.*}")
   public Item getItem(@PathParam("id") String id, //
      @DefaultValue("*") @QueryParam("propertyFilter") PropertyFilter propertyFilter //
   ) throws ItemNotFoundException, PermissionDeniedException, VirtualFileSystemException
   {
      return fromItemData(getItemData(id), propertyFilter);
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getVersion(java.lang.String,
    *      org.exoplatform.ide.vfs.VersionId)
    */
   @Path("version/{id:.*}/{versionId}")
   public Response getVersion(@PathParam("id") String id, //
      @PathParam("versionId") String versionId //
   ) throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      ItemData data = getItemData(id);
      if (Type.FILE != data.getType())
         throw new InvalidArgumentException("Object " + id + " is not a file. ");
      FileData versionData = ((FileData)data).getVersion(versionId);
      // TODO : cache control, last modification date, etc ??
      return Response.ok(versionData.getContent(), versionData.getContenType()).build();
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getVersions(java.lang.String,
    *      int, int, org.exoplatform.ide.vfs.server.PropertyFilter)
    */
   @Path("version-history/{id:.*}")
   public ItemList<File> getVersions(@PathParam("id") String id, //
      @DefaultValue("-1") @QueryParam("maxItems") int maxItems, //
      @QueryParam("skipCount") int skipCount, //
      @DefaultValue("*") @QueryParam("propertyFilter") PropertyFilter propertyFilter //
   ) throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      if (skipCount < 0)
         throw new InvalidArgumentException("'skipCount' parameter is negative. ");

      ItemData data = getItemData(id);
      if (Type.FILE != data.getType())
         throw new InvalidArgumentException("Object " + id + " is not a file. ");

      FileData fileData = (FileData)data;
      LazyIterator<FileData> versions = fileData.getAllVersions();
      try
      {
         if (skipCount > 0)
            versions.skip(skipCount);
      }
      catch (NoSuchElementException nse)
      {
         throw new InvalidArgumentException("'skipCount' parameter is greater then total number of items. ");
      }

      List<File> l = new ArrayList<File>();
      for (int count = 0; versions.hasNext() && (maxItems < 0 || count < maxItems); count++)
         l.add((File)fromItemData(versions.next(), propertyFilter));

      ItemList<File> il = new ItemList<File>(l);
      il.setNumItems(versions.size());
      il.setHasMoreItems(versions.hasNext());

      return il;
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#lock(java.lang.String)
    */
   @Path("lock/{id:.*}")
   public LockToken lock(@PathParam("id") String id) throws NotSupportedException, ItemNotFoundException,
      InvalidArgumentException, LockException, PermissionDeniedException, VirtualFileSystemException
   {
      ItemData itemData = getItemData(id);
      if (itemData.getType() != Type.FILE)
         throw new InvalidArgumentException("Locking allowed for Files only. ");
      return new LockToken(((FileData)itemData).lock());
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#move(java.lang.String,
    *      java.lang.String, java.lang.String)
    */
   @Path("move/{id:.*}")
   public Response move(@PathParam("id") String id, //
      @QueryParam("parentId") String parentId, //
      @QueryParam("lockToken") String lockToken //
   ) throws ItemNotFoundException, ConstraintException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      ItemData object = getItemData(id);
      ItemData folder = getItemData(parentId);
      if (Type.FOLDER != folder.getType())
         throw new InvalidArgumentException("Object " + parentId + " is not a folder. ");
      String movedId = object.moveTo((FolderData)folder, lockToken);
      return Response.created(createURI("item", movedId)).build();
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#rename(java.lang.String,
    *      javax.ws.rs.core.MediaType, java.lang.String, java.lang.String)
    */
   @Path("rename/{id:.*}")
   public Response rename(@PathParam("id") String id, //
      @QueryParam("mediaType") MediaType mediaType, //
      @QueryParam("newname") String newname, //
      @QueryParam("lockToken") String lockToken //
   ) throws ItemNotFoundException, InvalidArgumentException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      ItemData data = getItemData(id);
      if (Type.FILE != data.getType())
         throw new InvalidArgumentException("Object " + id + " is not a file. ");
      ((FileData)data).rename(newname, mediaType, lockToken);
      return Response.created(createURI("item", data.getId())).build();
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#search(javax.ws.rs.core.MultivaluedMap,
    *      int, int)
    */
   @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
   public ItemList<Item> search(MultivaluedMap<String, String> query, //
      @DefaultValue("-1") @QueryParam("maxItems") int maxItems, //
      @QueryParam("skipCount") int skipCount, //
      @DefaultValue("*") @QueryParam("propertyFilter") PropertyFilter propertyFilter //
   ) throws NotSupportedException, InvalidArgumentException, VirtualFileSystemException
   {
      if (skipCount < 0)
         throw new InvalidArgumentException("'skipCount' parameter is negative. ");

      StringBuilder sql = new StringBuilder();
      sql.append("SELECT * FROM nt:resource");

      List<String> where = new ArrayList<String>();

      String path = query.getFirst("path");
      if (path != null && path.length() > 0)
         where.add("jcr:path LIKE '" + path + "/%'");

      String text = query.getFirst("text");
      if (text != null && text.length() > 0)
         where.add("CONTAINS(*, '" + text + "')");

      String mediaType = query.getFirst("mediaType");
      if (mediaType != null && mediaType.length() > 0)
         where.add("jcr:mimeType = '" + mediaType + "'");

      if (where.size() > 0)
      {
         sql.append(" WHERE ");
         for (int i = 0; i < where.size(); i++)
         {
            if (i > 0)
               sql.append(" AND ");
            sql.append(where.get(i));
         }
      }
      //System.out.println(">>>>> SQL: " + sql.toString());
      try
      {
         QueryManager queryManager = session.getWorkspace().getQueryManager();
         Query jcrQuery = queryManager.createQuery(sql.toString(), Query.SQL);
         QueryResult result = jcrQuery.execute();

         NodeIterator nodes = result.getNodes();
         try
         {
            if (skipCount > 0)
               nodes.skip(skipCount);
         }
         catch (NoSuchElementException nse)
         {
            throw new InvalidArgumentException("'skipCount' parameter is greater then total number of items. ");
         }

         String name = query.getFirst("name");
         Pattern namePattern = null;
         if (name != null && name.length() > 0)
         {
            StringBuilder pb = new StringBuilder();
            pb.append(".*");
            for (int i = 0; i < name.length(); i++)
            {
               char c = name.charAt(i);
               if (c == '*' || c == '?')
                  pb.append('.');
               if (".()[]^$|".indexOf(c) != -1)
                  pb.append('\\');
               pb.append(c);
            }
            pb.append(".*");
            namePattern = Pattern.compile(pb.toString(), Pattern.CASE_INSENSITIVE);
         }

         // TODO Make improvement  for large amount of items
         List<Item> l = new ArrayList<Item>();
         for (int count = 0; nodes.hasNext() && (maxItems < 0 || count < maxItems); count++)
         {
            ItemData data = ItemData.fromNode(nodes.nextNode());
            if (namePattern == null || namePattern.matcher(data.getName()).matches())
               l.add(fromItemData(data, propertyFilter));
         }

         ItemList<Item> il = new ItemList<Item>(l);
         if (namePattern == null) // Total number is unknown since we apply additional filtering by name. 
            il.setNumItems((int)nodes.getSize());
         il.setHasMoreItems(nodes.hasNext());

         return il;
      }
      catch (InvalidQueryException e)
      {
         throw new InvalidArgumentException(e.getMessage());
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#search(java.lang.String,
    *      int, int)
    */
   public ItemList<Item> search(@QueryParam("statement") String statement, //
      @DefaultValue("-1") @QueryParam("maxItems") int maxItems, //
      @QueryParam("skipCount") int skipCount //
   ) throws NotSupportedException, InvalidArgumentException, VirtualFileSystemException
   {
      if (skipCount < 0)
         throw new InvalidArgumentException("'skipCount' parameter is negative. ");
      try
      {
         QueryManager queryManager = session.getWorkspace().getQueryManager();
         Query query = queryManager.createQuery(statement, Query.SQL);
         QueryResult result = query.execute();
         StringBuilder propertyFilter = new StringBuilder();
         for (String n : result.getColumnNames())
         {
            if (SKIPPED_QUERY_PROPERTIES.contains(n))
               continue;
            if (propertyFilter.length() > 0)
               propertyFilter.append(',');
            propertyFilter.append(n);
         }
         NodeIterator nodes = result.getNodes();
         try
         {
            if (skipCount > 0)
               nodes.skip(skipCount);
         }
         catch (NoSuchElementException nse)
         {
            throw new InvalidArgumentException("'skipCount' parameter is greater then total number of items. ");
         }
         // TODO Make improvement  for large amount of items
         List<Item> l = new ArrayList<Item>();
         for (int count = 0; nodes.hasNext() && (maxItems < 0 || count < maxItems); count++)
            l.add(fromItemData(ItemData.fromNode(nodes.nextNode()), PropertyFilter.valueOf(propertyFilter.toString())));

         ItemList<Item> il = new ItemList<Item>(l);
         il.setNumItems((int)nodes.getSize());
         il.setHasMoreItems(nodes.hasNext());

         return il;
      }
      catch (InvalidQueryException e)
      {
         throw new InvalidArgumentException(e.getMessage());
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#unlock(java.lang.String,
    *      java.lang.String)
    */
   @Path("unlock/{id:.*}")
   public void unlock(@PathParam("id") String id, //
      @QueryParam("lockToken") String lockToken //
   ) throws NotSupportedException, ItemNotFoundException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      ItemData itemData = getItemData(id);
      if (itemData.getType() != Type.FILE)
         throw new LockException("Object is not locked. "); // Folder can't be locked.
      ((FileData)itemData).unlock(lockToken);
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#updateACL(java.lang.String,
    *      java.util.List, java.lang.Boolean, java.lang.String)
    */
   @Path("acl/{id:.*}")
   public void updateACL(@PathParam("id") String id, //
      List<AccessControlEntry> acl, //
      @DefaultValue("false") @QueryParam("override") Boolean override, //
      @QueryParam("lockToken") String lockToken //
   ) throws NotSupportedException, ItemNotFoundException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      getItemData(id).updateACL(acl, override, lockToken);
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#updateContent(java.lang.String,
    *      javax.ws.rs.core.MediaType, java.io.InputStream, java.lang.String)
    */
   @Path("content/{id:.*}")
   public void updateContent(@PathParam("id") String id, //
      @DefaultValue(MediaType.APPLICATION_OCTET_STREAM) @QueryParam("mediaType") MediaType mediaType, //
      InputStream newcontent, //
      @QueryParam("lockToken") String lockToken //
   ) throws ItemNotFoundException, InvalidArgumentException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      ItemData data = getItemData(id);
      if (Type.FILE != data.getType())
         throw new InvalidArgumentException("Object " + id + " is not file. ");
      ((FileData)data).setContent(newcontent, mediaType, lockToken);
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#update(java.lang.String,
    *      java.util.Collection, java.util.List)
    */
   @Path("item/{id:.*}")
   public void updateItem(@PathParam("id") String id, //
      List<ConvertibleProperty> properties, //
      @QueryParam("lockToken") String lockToken //
   ) throws ItemNotFoundException, LockException, PermissionDeniedException, VirtualFileSystemException
   {
      ItemData data = getItemData(id);
      data.updateProperties(properties, lockToken);
   }

   //-----------------------------------------

   private void checkName(String name) throws InvalidArgumentException
   {
      if (name == null || name.trim().length() == 0)
         throw new InvalidArgumentException("Item's name is not set. ");
   }

   private Item fromItemData(ItemData data, PropertyFilter propertyFilter) throws PermissionDeniedException,
      VirtualFileSystemException
   {
      if (data.getType() == Type.FILE)
      {
         FileData fileData = (FileData)data;
         return new File(fileData.getId(), fileData.getName(), fileData.getPath(), fileData.getCreationDate(),
            fileData.getLastModificationDate(), fileData.getVersionId(), fileData.getContenType(),
            fileData.getContenLength(), fileData.isLocked(), fileData.getProperties(propertyFilter),
            createFileLinks(fileData));
      }
      return new Folder(data.getId(), data.getName(), data.getPath(), data.getCreationDate(),
         data.getProperties(propertyFilter), createFolderLinks((FolderData)data));
   }

   private Map<String, Link> createFileLinks(FileData file) throws VirtualFileSystemException
   {
      Map<String, Link> links = createBaseLinks(file);
      String id = file.getId();
      links.put(Link.REL_CONTENT, //
         new Link(createURI("content", id).toString(), Link.REL_CONTENT, file.getContenType()));
      links.put(Link.REL_VERSION_HISTORY, //
         new Link(createURI("version-history", id).toString(), Link.REL_VERSION_HISTORY, MediaType.APPLICATION_JSON));
      links.put(Link.REL_CURRENT, // 
         new Link(createURI("item", file.getCurrentVersionId()).toString(), Link.REL_CURRENT,
            MediaType.APPLICATION_JSON));
      return links;
   }

   private Map<String, Link> createFolderLinks(FolderData folder) throws VirtualFileSystemException
   {
      Map<String, Link> links = createBaseLinks(folder);
      String id = folder.getId();
      links.put(Link.REL_CHILDREN, //
         new Link(createURI("children", id).toString(), Link.REL_CHILDREN, MediaType.APPLICATION_JSON));
      return links;
   }

   private Map<String, Link> createBaseLinks(ItemData data) throws VirtualFileSystemException
   {
      Map<String, Link> links = new HashMap<String, Link>();
      String id = data.getId();
      links.put(Link.REL_SELF, //
         new Link(createURI("item", id).toString(), Link.REL_SELF, MediaType.APPLICATION_JSON));
      links.put(Link.REL_ACL, //
         new Link(createURI("acl", id).toString(), Link.REL_ACL, MediaType.APPLICATION_JSON));
      return links;
   }

   private URI createURI(String rel, String id, String... query)
   {
      UriBuilder uriBuilder = uriInfo.getBaseUriBuilder();
      List<String> matchedURIs = uriInfo.getMatchedURIs();
      int n = matchedURIs.size();
      for (int i = n - 1; i > 0; i--)
         uriBuilder.path(matchedURIs.get(i));
      uriBuilder.path(rel);
      if (id != null)
         uriBuilder.path(id);
      if (query != null && query.length > 0)
      {
         for (int i = 0; i < query.length; i++)
         {
            String name = query[i];
            String value = i < query.length ? query[++i] : "";
            uriBuilder.queryParam(name, value);
         }
      }
      URI uri = uriBuilder.build();
      return uri;
   }

   private ItemData getItemData(String id) throws ItemNotFoundException, PermissionDeniedException,
      VirtualFileSystemException
   {
      try
      {
         String path = (id.charAt(0) != '/') ? ("/" + id) : id;
         return ItemData.fromNode((Node)session.getItem(path));
      }
      catch (PathNotFoundException e)
      {
         throw new ItemNotFoundException("Oject " + id + " does not exists. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Access denied to object " + id + ". ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }
}
