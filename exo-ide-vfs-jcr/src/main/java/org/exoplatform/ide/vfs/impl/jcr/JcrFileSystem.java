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
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.LockToken;
import org.exoplatform.ide.vfs.shared.Project;
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
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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

   protected final Repository repository;
   protected final String workspaceName;
   protected final ItemType2NodeTypeResolver itemType2NodeTypeResolver;
   protected final UriInfo uriInfo;

   private VirtualFileSystemInfo vfsInfo;

   public JcrFileSystem(Repository repository, String workspaceName,
      ItemType2NodeTypeResolver itemType2NodeTypeResolver, UriInfo uriInfo)
   {
      this.repository = repository;
      this.workspaceName = workspaceName;
      this.itemType2NodeTypeResolver = itemType2NodeTypeResolver;
      this.uriInfo = uriInfo;
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#copy(java.lang.String, java.lang.String)
    */
   @Path("copy/{id:.*}")
   public Response copy(@PathParam("id") String id, //
      @QueryParam("parentId") String parentId //
   ) throws ItemNotFoundException, ConstraintException, PermissionDeniedException, VirtualFileSystemException
   {
      Session ses = session();
      try
      {
         ItemData object = getItemData(ses, id);
         ItemData folder = getItemData(ses, parentId);
         if (ItemType.FOLDER != folder.getType())
            throw new InvalidArgumentException("Unable copy. Item specified as parent is not a folder. ");
         ItemData newobject = object.copyTo((FolderData)folder);
         return Response.created(createURI("item", newobject.getId())).build();
      }
      finally
      {
         ses.logout();
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#createFile(java.lang.String, java.lang.String,
    *      javax.ws.rs.core.MediaType, java.io.InputStream)
    */
   @Path("file{S:(/)?}{parentId:.*}")
   @Produces({MediaType.APPLICATION_JSON})
   public Response createFile(@PathParam("parentId") String parentId, //
      @QueryParam("name") String name, //
      @DefaultValue(MediaType.APPLICATION_OCTET_STREAM) @HeaderParam("Content-Type") MediaType mediaType, //
      InputStream content //
   ) throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      checkName(name);
      Session ses = session();
      try
      {
         ItemData parentData = getItemData(ses, parentId);
         if (ItemType.FOLDER != parentData.getType())
            throw new InvalidArgumentException("Unable create file. Item specified as parent is not a folder. ");
         FileData newfile =
            ((FolderData)parentData).createFile(name, itemType2NodeTypeResolver.getFileNodeType(mediaType),
               itemType2NodeTypeResolver.getFileContentNodeType(mediaType), mediaType,
               itemType2NodeTypeResolver.getFileMixins(mediaType), null, content);
         return Response.created(createURI("item", newfile.getId()))
            .entity(fromItemData(newfile, PropertyFilter.ALL_FILTER)).build();
      }
      finally
      {
         ses.logout();
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#createFolder(java.lang.String, java.lang.String)
    */
   @Path("folder{S:(/)?}{parentId:.*}")
   @Produces({MediaType.APPLICATION_JSON})
   public Response createFolder(@PathParam("parentId") String parentId, //
      @QueryParam("name") String name //
   ) throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      checkName(name);
      Session ses = session();
      try
      {
         ItemData parentData = getItemData(ses, parentId);
         if (ItemType.FOLDER != parentData.getType())
            throw new InvalidArgumentException("Unable create folder. Item specified as parent is not a folder. ");
         FolderData newfolder =
            ((FolderData)parentData).createFolder(name, itemType2NodeTypeResolver.getFolderNodeType(null),
               itemType2NodeTypeResolver.getFolderMixins(null), null);
         return Response.created(createURI("item", newfolder.getId()))
            .entity(fromItemData(newfolder, PropertyFilter.ALL_FILTER)).build();
      }
      finally
      {
         ses.logout();
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#createProject(java.lang.String, java.lang.String,
    *      java.lang.String, java.util.List)
    */
   @Path("project{S:(/)?}{parentId:.*}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces({MediaType.APPLICATION_JSON})
   public Response createProject(@PathParam("parentId") String parentId, //
      @QueryParam("name") String name, //
      @QueryParam("type") String type, //
      List<ConvertibleProperty> properties //
   ) throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      checkName(name);
      Session ses = session();
      try
      {
         ItemData parentData = getItemData(ses, parentId);
         if (ItemType.FOLDER != parentData.getType())
            throw new InvalidArgumentException("Unable to create project. Item specified as parent is not a folder. ");
         if (type == null)
            throw new InvalidArgumentException("Unable to create project. Project type missed. ");
         if (properties == null)
            properties = new ArrayList<ConvertibleProperty>(1);
         properties.add(new ConvertibleProperty("type", type));
         FolderData newproject =
            ((FolderData)parentData).createFolder(name, itemType2NodeTypeResolver.getFolderNodeType("project"),
               itemType2NodeTypeResolver.getFolderMixins("project"), properties);
         return Response.created(createURI("item", newproject.getId()))
            .entity(fromItemData(newproject, PropertyFilter.ALL_FILTER)).build();
      }
      finally
      {
         ses.logout();
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#convertToProject(java.lang.String, java.lang.String)
    */
   @Path("convert-to-project/{folderId:.*}")
   @Produces({MediaType.APPLICATION_JSON})
   public Response convertToProject(@PathParam("folderId") String folderId, //
      @QueryParam("type") String type //
   ) throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      Session ses = session();
      try
      {
         ItemData folderData = getItemData(ses, folderId);
         if (ItemType.FOLDER != folderData.getType())
            throw new InvalidArgumentException("Unable convert to project. Item specified is not a folder. ");
         if (type == null)
            throw new InvalidArgumentException("Unable convert to project. Project type missed. ");
         Node node = folderData.getNode();
         node.addMixin("vfs:project");
         node.setProperty("type", type);
         ses.save();
         folderData = null;
         ProjectData projectData = (ProjectData)ItemData.fromNode(node);
         return Response.created(createURI("item", projectData.getId()))
            .entity(fromItemData(projectData, PropertyFilter.ALL_FILTER)).build();
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable convert to project " + folderId + ". Operation not permitted. ");
      }
      catch (RepositoryException re)
      {
         throw new VirtualFileSystemException(re.getMessage(), re);
      }
      finally
      {
         ses.logout();
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#delete(java.lang.String, java.lang.String)
    */
   @Path("delete/{id:.*}")
   public void delete(@PathParam("id") String id, //
      @QueryParam("lockToken") String lockToken //
   ) throws ItemNotFoundException, ConstraintException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      Session ses = session();
      try
      {
         getItemData(ses, id).delete(lockToken);
      }
      finally
      {
         ses.logout();
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getACL(java.lang.String)
    */
   @Path("acl/{id:.*}")
   public List<AccessControlEntry> getACL(@PathParam("id") String id) throws NotSupportedException,
      ItemNotFoundException, PermissionDeniedException, VirtualFileSystemException
   {
      Session ses = session();
      try
      {
         return getItemData(ses, id).getACL();
      }
      finally
      {
         ses.logout();
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getChildren(java.lang.String, int, int,
    *      org.exoplatform.ide.vfs.server.PropertyFilter)
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

      Session ses = session();
      try
      {
         ItemData data = getItemData(ses, folderId);
         if (ItemType.FOLDER != data.getType())
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

         List<Item> l = new ArrayList<Item>();
         for (int count = 0; children.hasNext() && (maxItems < 0 || count < maxItems); count++)
            l.add(fromItemData(children.next(), propertyFilter));

         ItemList<Item> il = new ItemList<Item>(l);
         il.setNumItems(children.size());
         il.setHasMoreItems(children.hasNext());

         return il;
      }
      finally
      {
         ses.logout();
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getContent(java.lang.String)
    */
   @Path("content/{id:.*}")
   public Response getContent(@PathParam("id") String id) throws ItemNotFoundException, InvalidArgumentException,
      PermissionDeniedException, VirtualFileSystemException
   {
      Session ses = session();
      try
      {
         ItemData data = getItemData(ses, id);
         if (ItemType.FILE != data.getType())
            throw new InvalidArgumentException("Unable get content. Item " + id + " is not a file. ");
         FileData fileData = (FileData)data;
         return Response.ok(fileData.getContent(), fileData.getContenType())
            .lastModified(new java.util.Date(fileData.getLastModificationDate())).build();
      }
      finally
      {
         ses.logout();
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getVfsInfo()
    */
   public VirtualFileSystemInfo getVfsInfo() throws VirtualFileSystemException
   {
      if (vfsInfo == null)
      {
         BasicPermissions[] basicPermissions = BasicPermissions.values();
         List<String> permissions = new ArrayList<String>(basicPermissions.length);
         for (BasicPermissions bp : basicPermissions)
            permissions.add(bp.value());
         Session ses = session();
         Folder root;
         try
         {
            root = (Folder)fromItemData(getItemData(ses, ""), PropertyFilter.valueOf(PropertyFilter.ALL));
         }
         finally
         {
            ses.logout();
         }
         vfsInfo =
            new VirtualFileSystemInfo(this.workspaceName, true, true,
               org.exoplatform.services.security.IdentityConstants.ANONIM,
               org.exoplatform.services.security.IdentityConstants.ANY, permissions, ACLCapability.MANAGE,
               QueryCapability.BOTHCOMBINED, createUrlTemplates(), root);
      }
      return vfsInfo;
   }

   private Map<String, Link> createUrlTemplates()
   {
      Map<String, Link> templates = new HashMap<String, Link>();

      templates.put(Link.REL_CREATE_FILE, //
         new Link(createURI("file", "[parentId]", "name", "[name]").toString(), //
            Link.REL_CREATE_FILE, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_CREATE_FOLDER, //
         new Link(createURI("folder", "[parentId]", "name", "[name]").toString(), //
            Link.REL_CREATE_FOLDER, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_CREATE_PROJECT, //
         new Link(createURI("project", "[parentId]", "name", "[name]", "type", "[type]").toString(), //
            Link.REL_CREATE_PROJECT, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_COPY, //
         new Link(createURI("copy", "[id]", "parentId", "[parentId]").toString(), //
            Link.REL_COPY, null));

      templates.put(Link.REL_MOVE, //
         new Link(createURI("move", "[id]", "parentId", "[parentId]", "lockToken", "[lockToken]").toString(), //
            Link.REL_MOVE, null));

      templates.put(Link.REL_LOCK, //
         new Link(createURI("lock", "[id]").toString(), //
            Link.REL_LOCK, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_UNLOCK, //
         new Link(createURI("unlock", "[id]", "lockToken", "[lockToken]").toString(), //
            Link.REL_UNLOCK, null));

      templates.put(
         Link.REL_SEARCH_FORM, //
         new Link(createURI("search", null, "maxItems", "[maxItems]", "skipCount", "[skipCount]", "propertyFilter",
            "[propertyFilter]").toString(), //
            Link.REL_SEARCH_FORM, MediaType.APPLICATION_JSON));

      templates.put(
         Link.REL_SEARCH, //
         new Link(createURI("search", null, "statement", "[statement]", "maxItems", "[maxItems]", "skipCount",
            "[skipCount]").toString(), //
            Link.REL_SEARCH, MediaType.APPLICATION_JSON));

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
      Session ses = session();
      try
      {
         return fromItemData(getItemData(ses, id), propertyFilter);
      }
      finally
      {
         ses.logout();
      }
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
      Session ses = session();
      try
      {
         ItemData data = getItemData(ses, id);
         if (ItemType.FILE != data.getType())
            throw new InvalidArgumentException("Object " + id + " is not a file. ");
         FileData versionData = ((FileData)data).getVersion(versionId);
         return Response.ok(versionData.getContent(), versionData.getContenType())
            .lastModified(new java.util.Date(versionData.getLastModificationDate())).build();
      }
      finally
      {
         ses.logout();
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getVersions(java.lang.String, int, int,
    *      org.exoplatform.ide.vfs.server.PropertyFilter)
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

      Session ses = session();
      try
      {
         ItemData data = getItemData(ses, id);
         if (ItemType.FILE != data.getType())
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
      finally
      {
         ses.logout();
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#lock(java.lang.String)
    */
   @Path("lock/{id:.*}")
   public LockToken lock(@PathParam("id") String id) throws NotSupportedException, ItemNotFoundException,
      InvalidArgumentException, LockException, PermissionDeniedException, VirtualFileSystemException
   {
      Session ses = session();
      try
      {
         ItemData itemData = getItemData(ses, id);
         if (ItemType.FILE != itemData.getType())
            throw new InvalidArgumentException("Locking allowed for Files only. ");
         return new LockToken(((FileData)itemData).lock());
      }
      finally
      {
         ses.logout();
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#move(java.lang.String, java.lang.String, java.lang.String)
    */
   @Path("move/{id:.*}")
   public Response move(@PathParam("id") String id, //
      @QueryParam("parentId") String parentId, //
      @QueryParam("lockToken") String lockToken //
   ) throws ItemNotFoundException, ConstraintException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      Session ses = session();
      try
      {
         ItemData object = getItemData(ses, id);
         ItemData folder = getItemData(ses, parentId);
         if (ItemType.FOLDER != folder.getType())
            throw new InvalidArgumentException("Object " + parentId + " is not a folder. ");
         String movedId = object.moveTo((FolderData)folder, lockToken);
         return Response.created(createURI("item", movedId)).build();
      }
      finally
      {
         ses.logout();
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#rename(java.lang.String, javax.ws.rs.core.MediaType,
    *      java.lang.String, java.lang.String)
    */
   @Path("rename/{id:.*}")
   public Response rename(@PathParam("id") String id, //
      @QueryParam("mediaType") MediaType mediaType, //
      @QueryParam("newname") String newname, //
      @QueryParam("lockToken") String lockToken //
   ) throws ItemNotFoundException, InvalidArgumentException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      Session ses = session();
      try
      {
         ItemData data = getItemData(ses, id);
         if (ItemType.FILE != data.getType())
            throw new InvalidArgumentException("Object " + id + " is not a file. ");
         ((FileData)data).rename(newname, mediaType, lockToken);
         return Response.created(createURI("item", data.getId())).build();
      }
      finally
      {
         ses.logout();
      }

   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#search(javax.ws.rs.core.MultivaluedMap, int, int)
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

      if (query != null)
      {
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
      }
      //System.out.println(">>>>> SQL: " + sql.toString());

      Session ses = session();
      try
      {
         QueryManager queryManager = ses.getWorkspace().getQueryManager();
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
      finally
      {
         ses.logout();
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#search(java.lang.String, int, int)
    */
   public ItemList<Item> search(@QueryParam("statement") String statement, //
      @DefaultValue("-1") @QueryParam("maxItems") int maxItems, //
      @QueryParam("skipCount") int skipCount //
   ) throws NotSupportedException, InvalidArgumentException, VirtualFileSystemException
   {
      if (skipCount < 0)
         throw new InvalidArgumentException("'skipCount' parameter is negative. ");
      Session ses = session();
      try
      {
         QueryManager queryManager = ses.getWorkspace().getQueryManager();
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
      finally
      {
         ses.logout();
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#unlock(java.lang.String, java.lang.String)
    */
   @Path("unlock/{id:.*}")
   public void unlock(@PathParam("id") String id, //
      @QueryParam("lockToken") String lockToken //
   ) throws NotSupportedException, ItemNotFoundException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      Session ses = session();
      try
      {
         ItemData itemData = getItemData(ses, id);
         if (itemData.getType() != ItemType.FILE)
            throw new LockException("Object is not locked. "); // Folder can't be locked.
         ((FileData)itemData).unlock(lockToken);
      }
      finally
      {
         ses.logout();
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#updateACL(java.lang.String, java.util.List,
    *      java.lang.Boolean, java.lang.String)
    */
   @Path("acl/{id:.*}")
   public void updateACL(@PathParam("id") String id, //
      List<AccessControlEntry> acl, //
      @DefaultValue("false") @QueryParam("override") Boolean override, //
      @QueryParam("lockToken") String lockToken //
   ) throws NotSupportedException, ItemNotFoundException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      Session ses = session();
      try
      {
         getItemData(ses, id).updateACL(acl, override, lockToken);
      }
      finally
      {
         ses.logout();
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#updateContent(java.lang.String, javax.ws.rs.core.MediaType,
    *      java.io.InputStream, java.lang.String)
    */
   @Path("content/{id:.*}")
   public void updateContent(
      @PathParam("id") String id, //
      @DefaultValue(MediaType.APPLICATION_OCTET_STREAM) @HeaderParam("Content-Type") MediaType mediaType,
      InputStream newcontent, //
      @QueryParam("lockToken") String lockToken //
   ) throws ItemNotFoundException, InvalidArgumentException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      Session ses = session();
      try
      {
         ItemData data = getItemData(ses, id);
         if (ItemType.FILE != data.getType())
            throw new InvalidArgumentException("Object " + id + " is not file. ");
         ((FileData)data).setContent(newcontent, mediaType, lockToken);
      }
      finally
      {
         ses.logout();
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#update(java.lang.String, java.util.Collection,
    *      java.util.List)
    */
   @Path("item/{id:.*}")
   public void updateItem(@PathParam("id") String id, //
      List<ConvertibleProperty> properties, //
      @QueryParam("lockToken") String lockToken //
   ) throws ItemNotFoundException, LockException, PermissionDeniedException, VirtualFileSystemException
   {
      Session ses = session();
      try
      {
         ItemData data = getItemData(ses, id);
         data.updateProperties(properties, lockToken);
      }
      finally
      {
         ses.logout();
      }
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
      if (data.getType() == ItemType.FILE)
      {
         FileData fileData = (FileData)data;
         return new File(fileData.getId(), fileData.getName(), fileData.getPath(), fileData.getParentId(),
            fileData.getCreationDate(), fileData.getLastModificationDate(), fileData.getVersionId(),
            fileData.getContenType(), fileData.getContenLength(), fileData.isLocked(),
            fileData.getProperties(propertyFilter), createFileLinks(fileData));
      }

      if (data instanceof ProjectData)
      {
         ProjectData projectData = (ProjectData)data;
         return new Project(projectData.getId(), projectData.getName(), Project.PROJECT_MIME_TYPE,
            projectData.getPath(), projectData.getParentId(), projectData.getCreationDate(),
            projectData.getProperties(propertyFilter), createProjectLinks(projectData), projectData.getProjectType());
      }

      return new Folder(data.getId(), data.getName(), Folder.FOLDER_MIME_TYPE, data.getPath(), data.getParentId(),
         data.getCreationDate(), data.getProperties(propertyFilter), createFolderLinks((FolderData)data));
   }

   private Map<String, Link> createFileLinks(FileData file) throws VirtualFileSystemException
   {
      Map<String, Link> links = createBaseLinks(file);
      String id = file.getId();

      links.put(Link.REL_CONTENT, //
         new Link(createURI("content", id).toString(), //
            Link.REL_CONTENT, file.getContenType()));

      links.put(Link.REL_VERSION_HISTORY, //
         new Link(createURI("version-history", id).toString(), //
            Link.REL_VERSION_HISTORY, MediaType.APPLICATION_JSON));

      links.put(Link.REL_CURRENT_VERSION, // 
         new Link(createURI("item", file.getCurrentVersionId()).toString(), //
            Link.REL_CURRENT_VERSION, MediaType.APPLICATION_JSON));

      if (file.isLocked())
      {
         links.put(Link.REL_UNLOCK, //
            new Link(createURI("unlock", id, "lockToken", "[lockToken]").toString(), //
               Link.REL_UNLOCK, null));
      }
      else
      {
         links.put(Link.REL_LOCK, //
            new Link(createURI("lock", id).toString(), //
               Link.REL_LOCK, MediaType.APPLICATION_JSON));
      }

      return links;
   }

   private Map<String, Link> createFolderLinks(FolderData folder) throws VirtualFileSystemException
   {
      Map<String, Link> links = createBaseLinks(folder);
      String id = folder.getId();

      links.put(Link.REL_CHILDREN, //
         new Link(createURI("children", id).toString(), //
            Link.REL_CHILDREN, MediaType.APPLICATION_JSON));

      links.put(Link.REL_CREATE_FOLDER, //
         new Link(createURI("folder", id, "name", "[name]").toString(), //
            Link.REL_CREATE_FOLDER, MediaType.APPLICATION_JSON));

      links.put(Link.REL_CREATE_FILE, //
         new Link(createURI("file", id, "name", "[name]").toString(), //
            Link.REL_CREATE_FILE, MediaType.APPLICATION_JSON));

      links.put(Link.REL_CREATE_PROJECT, //
         new Link(createURI("project", id, "name", "[name]", "type", "[type]").toString(), //
            Link.REL_CREATE_PROJECT, MediaType.APPLICATION_JSON));

      return links;
   }

   private Map<String, Link> createProjectLinks(ProjectData project) throws VirtualFileSystemException
   {
      Map<String, Link> links = createBaseLinks(project);
      String id = project.getId();

      links.put(Link.REL_CHILDREN, //
         new Link(createURI("children", id).toString(), //
            Link.REL_CHILDREN, MediaType.APPLICATION_JSON));

      links.put(Link.REL_CREATE_FOLDER, //
         new Link(createURI("folder", id, "name", "[name]").toString(), //
            Link.REL_CREATE_FOLDER, MediaType.APPLICATION_JSON));

      links.put(Link.REL_CREATE_FILE, //
         new Link(createURI("file", id, "name", "[name]").toString(), //
            Link.REL_CREATE_FILE, MediaType.APPLICATION_JSON));

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

      links.put(Link.REL_DELETE, //
         new Link(createURI("delete", id, "lockToken", "[lockToken]").toString(), //
            Link.REL_DELETE, null));

      links.put(Link.REL_COPY, //
         new Link(createURI("copy", id, "parentId", "[parentId]").toString(), //
            Link.REL_COPY, null));

      links.put(Link.REL_MOVE, //
         new Link(createURI("move", id, "parentId", "[parentId]", "lockToken", "[lockToken]").toString(), //
            Link.REL_MOVE, null));

      String parentId = data.getParentId();
      if (parentId != null)
         links.put(Link.REL_PARENT, //
            new Link(createURI("item", parentId).toString(), Link.REL_PARENT, MediaType.APPLICATION_JSON));

      return links;
   }

   private URI createURI(String rel, String id, String... query)
   {
      UriBuilder uriBuilder = uriInfo.getBaseUriBuilder();
      uriBuilder.path(JcrFileSystemFactory.class, "getVFS");
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

      URI uri = uriBuilder.build(workspaceName);

      return uri;
   }

   private ItemData getItemData(Session session, String id) throws ItemNotFoundException, PermissionDeniedException,
      VirtualFileSystemException
   {
      try
      {
         return ItemData.fromNode(((id == null || id.length() == 0) ? session.getRootNode() : (Node)session.getItem((id
            .charAt(0) != '/') ? ("/" + id) : id)));
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

   private Session session() throws VirtualFileSystemException
   {
      try
      {
         return repository.login(workspaceName);
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }
}
