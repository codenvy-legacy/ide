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

import org.exoplatform.ide.vfs.server.AccessControlEntry;
import org.exoplatform.ide.vfs.server.Document;
import org.exoplatform.ide.vfs.server.Folder;
import org.exoplatform.ide.vfs.server.InputProperty;
import org.exoplatform.ide.vfs.server.Item;
import org.exoplatform.ide.vfs.server.ItemList;
import org.exoplatform.ide.vfs.server.LazyIterator;
import org.exoplatform.ide.vfs.server.LockToken;
import org.exoplatform.ide.vfs.server.ObjectId;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.Type;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemInfo;
import org.exoplatform.ide.vfs.server.VirtualFileSystemInfo.ACLCapability;
import org.exoplatform.ide.vfs.server.VirtualFileSystemInfo.BasicPermissions;
import org.exoplatform.ide.vfs.server.VirtualFileSystemInfo.LockCapability;
import org.exoplatform.ide.vfs.server.VirtualFileSystemInfo.QueryCapability;
import org.exoplatform.ide.vfs.server.exceptions.ConstraintException;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.LockException;
import org.exoplatform.ide.vfs.server.exceptions.NotSupportedException;
import org.exoplatform.ide.vfs.server.exceptions.ObjectNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

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
import javax.jcr.version.VersionException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
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

   private VirtualFileSystemInfo vfsInfo;

   public JcrFileSystem(Session session, ItemType2NodeTypeResolver mediaType2NodeTypeResolver)
   {
      this.session = session;
      this.itemType2NodeTypeResolver = mediaType2NodeTypeResolver;
   }

   /*
    * Since we are use path of JCR node as identifier we need such method for
    * creation item in root folder. Path in this case may be:
    * 
    * 1. .../document/?[Query_Parameters] --- SLASH
    * 2. .../document?[Query_Parameters]  --- NO SLASH
    * 
    * Path without slash at the end does not match to method
    * createDocument(String, String, MediaType, InputStream, List<String>, UriInfo).
    * It is possible to create special template to fix this issue:
    * 
    * document{X:(/)?}/{parent:.*} --- Make final slash optional if {parent} is not specified (Root item)  
    * 
    * But to keep URI template more simple and clear is better to have one more
    * method with template matched to root folder.
    */
   @POST
   @Path("document")
   @Produces({MediaType.APPLICATION_JSON})
   public ObjectId _createDocument(@QueryParam("name") String name,
      @DefaultValue(MediaType.APPLICATION_OCTET_STREAM) @QueryParam("mediaType") MediaType mediaType,
      InputStream content, @QueryParam("lockTokens") List<String> lockTokens, @Context UriInfo ext)
      throws ObjectNotFoundException, InvalidArgumentException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      return createDocument("/", name, mediaType, content, lockTokens, ext);
   }

   /*
    * This method has the same purposes as _createDocument(String, MediaType, InputStream, List<String>, UriInfo)
    */
   @POST
   @Path("folder")
   @Produces({MediaType.APPLICATION_JSON})
   public ObjectId _createFolder(@QueryParam("name") String name, @QueryParam("lockTokens") List<String> lockTokens,
      @Context UriInfo ext) throws ObjectNotFoundException, InvalidArgumentException, LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
      return createFolder("/", name, lockTokens, ext);
   }

   // ------------------- VirtualFileSystem ---------------------

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#copy(java.lang.String,
    *      java.lang.String, java.util.List)
    */
   @Path("copy/{identifier:.*}")
   public ObjectId copy(@PathParam("identifier") String identifier, @QueryParam("parent") String parent,
      @QueryParam("lockTokens") List<String> lockTokens) throws ObjectNotFoundException, ConstraintException,
      LockException, PermissionDeniedException, VirtualFileSystemException
   {
      ItemData object = getItemData(identifier);
      ItemData folder = getItemData(parent);
      if (Type.FOLDER != folder.getType())
         throw new InvalidArgumentException("Object " + parent + " is not a folder. ");
      ItemData newobject = object.copyTo((FolderData)folder, lockTokens);
      return new ObjectId(newobject.getId());
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#createDocument(java.lang.String,
    *      java.lang.String, javax.ws.rs.core.MediaType, java.io.InputStream,
    *      java.util.List, javax.ws.rs.core.UriInfo)
    */
   @Path("document/{parent:.*}")
   //@Path("document{X:(/)?}{parent:.*}")
   public ObjectId createDocument(@PathParam("parent") String parent, @QueryParam("name") String name,
      @DefaultValue(MediaType.APPLICATION_OCTET_STREAM) @QueryParam("mediaType") MediaType mediaType,
      InputStream content, @QueryParam("lockTokens") List<String> lockTokens, @Context UriInfo ext)
      throws ObjectNotFoundException, InvalidArgumentException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      checkName(name);
      ItemData parentData = getItemData(parent);
      if (Type.FOLDER != parentData.getType())
         throw new InvalidArgumentException("Item specified as parent is not a folder. ");
      DocumentData newdoc =
         ((FolderData)parentData).createDocument(name, itemType2NodeTypeResolver.getDocumentNodeTypeName(mediaType),
            itemType2NodeTypeResolver.getDocumentContentNodeTypeName(mediaType), mediaType, content, lockTokens);
      return new ObjectId(newdoc.getId());
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#createFolder(java.lang.String,
    *      java.lang.String, java.util.List, javax.ws.rs.core.UriInfo)
    */
   @Path("folder/{parent:.*}")
   //@Path("folder{X:(/)?}{parent:.*}")
   public ObjectId createFolder(@PathParam("parent") String parent, @QueryParam("name") String name,
      @QueryParam("lockTokens") List<String> lockTokens, @Context UriInfo ext) throws ObjectNotFoundException,
      InvalidArgumentException, LockException, PermissionDeniedException, VirtualFileSystemException
   {
      checkName(name);
      ItemData parentData = getItemData(parent);
      if (Type.FOLDER != parentData.getType())
         throw new InvalidArgumentException("Item specified as parent is not a folder. ");
      FolderData newfolder =
         ((FolderData)parentData).createFolder(name, itemType2NodeTypeResolver.getFolderNodeTypeName(), lockTokens);
      return new ObjectId(newfolder.getId());
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#delete(java.lang.String,
    *      java.util.List)
    */
   @Path("delete/{identifier:.*}")
   public void delete(@PathParam("identifier") String identifier, @QueryParam("lockTokens") List<String> lockTokens)
      throws ObjectNotFoundException, ConstraintException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      getItemData(identifier).delete(lockTokens);
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getACL(java.lang.String)
    */
   @Path("acl/{identifier:.*}")
   public List<AccessControlEntry> getACL(@PathParam("identifier") String identifier) throws NotSupportedException,
      ObjectNotFoundException, PermissionDeniedException, VirtualFileSystemException
   {
      return getItemData(identifier).getACL();
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getChildren(java.lang.String,
    *      int, int, org.exoplatform.ide.vfs.server.PropertyFilter)
    */
   @Path("children/{identifier:.*}")
   public ItemList<Item> getChildren(@PathParam("identifier") String parent,
      @DefaultValue("-1") @QueryParam("maxItems") int maxItems, @QueryParam("skipCount") int skipCount,
      @DefaultValue("*") @QueryParam("propertyFilter") PropertyFilter propertyFilter) throws ObjectNotFoundException,
      InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      if (skipCount < 0)
         throw new InvalidArgumentException("'skipCount' parameter is negative. ");

      ItemData data = getItemData(parent);
      if (Type.FOLDER != data.getType())
         throw new InvalidArgumentException("Object " + parent + " is not a folder. ");

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

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getContent(java.lang.String)
    */
   @Path("content/{identifier:.*}")
   public Response getContent(@PathParam("identifier") String identifier) throws ObjectNotFoundException,
      InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      ItemData data = getItemData(identifier);
      if (Type.DOCUMENT != data.getType())
         throw new InvalidArgumentException("Object " + identifier + " is not a document. ");
      DocumentData docData = (DocumentData)data;
      // TODO : cache control, last modification date, etc ??
      return Response.ok(docData.getContent(), docData.getContenType()).build();
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getVfsInfo(javax.ws.rs.core.UriInfo)
    */
   public VirtualFileSystemInfo getVfsInfo(@Context UriInfo uriInfo)
   {
      if (vfsInfo == null)
      {
         BasicPermissions[] basicPermissions = BasicPermissions.values();
         List<String> permissions = new ArrayList<String>(basicPermissions.length);
         for (BasicPermissions bp : basicPermissions)
            permissions.add(bp.value());
         vfsInfo =
            new VirtualFileSystemInfo(true, org.exoplatform.services.security.IdentityConstants.ANONIM,
               org.exoplatform.services.security.IdentityConstants.ANY, permissions, LockCapability.DEEP,
               ACLCapability.MANAGE, QueryCapability.BOTHCOMBINED, "", "/");
      }
      return vfsInfo;
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getItem(java.lang.String,
    *      org.exoplatform.ide.vfs.server.PropertyFilter)
    */
   @Path("item/{identifier:.*}")
   public Item getItem(@PathParam("identifier") String identifier,
      @DefaultValue("*") @QueryParam("propertyFilter") PropertyFilter propertyFilter) throws ObjectNotFoundException,
      PermissionDeniedException, VirtualFileSystemException
   {
      return fromItemData(getItemData(identifier), propertyFilter);
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getVersion(java.lang.String,
    *      org.exoplatform.ide.vfs.VersionId)
    */
   @Path("version/{identifier:.*}/{versionIdentifier}")
   public Response getVersion(@PathParam("identifier") String identifier,
      @PathParam("versionIdentifier") String versionIdentifier) throws ObjectNotFoundException,
      InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      DocumentData versionData = getDocumentData(identifier, versionIdentifier);
      // TODO : cache control, last modification date, etc ??
      return Response.ok(versionData.getContent(), versionData.getContenType()).build();
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getVersions(java.lang.String,
    *      int, int, org.exoplatform.ide.vfs.server.PropertyFilter)
    */
   @Path("versions/{identifier:.*}")
   public ItemList<Document> getVersions(@PathParam("identifier") String identifier,
      @DefaultValue("-1") @QueryParam("maxItems") int maxItems, @QueryParam("skipCount") int skipCount,
      @DefaultValue("*") @QueryParam("propertyFilter") PropertyFilter propertyFilter) throws ObjectNotFoundException,
      InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      if (skipCount < 0)
         throw new InvalidArgumentException("'skipCount' parameter is negative. ");

      ItemData data = getItemData(identifier);
      if (Type.DOCUMENT != data.getType())
         throw new InvalidArgumentException("Object " + identifier + " is not a document. ");

      DocumentData docData = (DocumentData)data;
      LazyIterator<DocumentData> versions = docData.getAllVersions();
      try
      {
         if (skipCount > 0)
            versions.skip(skipCount);
      }
      catch (NoSuchElementException nse)
      {
         throw new InvalidArgumentException("'skipCount' parameter is greater then total number of items. ");
      }

      List<Document> l = new ArrayList<Document>();
      for (int count = 0; versions.hasNext() && (maxItems < 0 || count < maxItems); count++)
         l.add((Document)fromItemData(versions.next(), propertyFilter));

      ItemList<Document> il = new ItemList<Document>(l);
      il.setNumItems(versions.size());
      il.setHasMoreItems(versions.hasNext());

      return il;
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#lock(java.lang.String,
    *      java.lang.Boolean)
    */
   @Path("lock/{identifier:.*}")
   public LockToken lock(@PathParam("identifier") String identifier,
      @DefaultValue("true") @QueryParam("isDeep") Boolean isDeep) throws NotSupportedException,
      ObjectNotFoundException, LockException, PermissionDeniedException, VirtualFileSystemException
   {
      return new LockToken(getItemData(identifier).lock(isDeep));
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#move(java.lang.String,
    *      java.lang.String, java.util.List)
    */
   @Path("move/{identifier:.*}")
   public ObjectId move(@PathParam("identifier") String identifier, @QueryParam("newparent") String newparent,
      @QueryParam("lockTokens") List<String> lockTokens) throws ObjectNotFoundException, ConstraintException,
      LockException, PermissionDeniedException, VirtualFileSystemException
   {
      ItemData object = getItemData(identifier);
      ItemData folder = getItemData(newparent);
      if (Type.FOLDER != folder.getType())
         throw new InvalidArgumentException("Object " + newparent + " is not a folder. ");
      String id = object.moveTo((FolderData)folder, lockTokens);
      return new ObjectId(id);
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#rename(java.lang.String,
    *      javax.ws.rs.core.MediaType, java.lang.String, java.util.List)
    */
   @Override
   @Path("rename/{identifier:.*}")
   public ObjectId rename(@PathParam("identifier") String identifier, @QueryParam("mediaType") MediaType mediaType,
      @QueryParam("newname") String newname, @QueryParam("lockTokens") List<String> lockTokens)
      throws ObjectNotFoundException, InvalidArgumentException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      ItemData data = getItemData(identifier);
      if (Type.DOCUMENT != data.getType())
         throw new InvalidArgumentException("Object " + identifier + " is not a document. ");
      ((DocumentData)data).rename(newname, mediaType, lockTokens);
      return new ObjectId(data.getId());
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#search(javax.ws.rs.core.MultivaluedMap,
    *      int, int)
    */
   @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
   public ItemList<Item> search(MultivaluedMap<String, String> query,
      @DefaultValue("-1") @QueryParam("maxItems") int maxItems, @QueryParam("skipCount") int skipCount)
      throws NotSupportedException, InvalidArgumentException, VirtualFileSystemException
   {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT ");
      List<String> properties = query.get("properties");
      if (properties == null || properties.size() == 0)
      {
         sql.append('*');
      }
      else
      {
         for (int i = 0; i < properties.size(); i++)
         {
            if (i > 0)
               sql.append(',');
            sql.append(properties.get(i));
         }
      }

      sql.append(" FROM ");
      List<String> types = query.get("types");
      if (types == null || types.size() == 0)
      {
         sql.append("nt:base");
      }
      else
      {
         // TODO : type mapping
         for (int i = 0; i < types.size(); i++)
         {
            if (i > 0)
               sql.append(',');
            sql.append(types.get(i));
         }
      }
      String path = query.getFirst("path");
      String contains = query.getFirst("contains");
      boolean isPath = (path != null && path.length() > 0);
      boolean isContains = (contains != null && contains.length() > 0);
      if (isPath || isContains)
      {
         sql.append(" WHERE");
         if (isPath)
            sql.append(" jcr:path LIKE '").append(path).append("/%'");
         if (isContains)
         {
            if (isPath)
               sql.append(" AND");
            sql.append(" CONTAINS(., \'").append(contains).append("\')");
         }
      }
      //System.out.println(">>>>> SQL: " + sql.toString());
      return search(sql.toString(), maxItems, skipCount);
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#search(java.lang.String,
    *      int, int)
    */
   public ItemList<Item> search(@QueryParam("statement") String statement,
      @DefaultValue("-1") @QueryParam("maxItems") int maxItems, @QueryParam("skipCount") int skipCount)
      throws NotSupportedException, InvalidArgumentException, VirtualFileSystemException
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
         List<Item> l = new ArrayList<Item>();
         for (int count = 0; nodes.hasNext() && (maxItems < 0 || count < maxItems); count++)
            l.add(fromItemData(ItemData.fromNode(nodes.nextNode()), new PropertyFilter(propertyFilter.toString())));

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
    *      java.util.List)
    */
   @Path("unlock/{identifier:.*}")
   public void unlock(@PathParam("identifier") String identifier, @QueryParam("lockTokens") List<String> lockTokens)
      throws NotSupportedException, ObjectNotFoundException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      getItemData(identifier).unlock(lockTokens);
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#updateACL(java.lang.String,
    *      java.util.List, java.lang.Boolean, java.util.List)
    */
   @Path("acl/{identifier:.*}")
   public void updateACL(@PathParam("identifier") String identifier, List<AccessControlEntry> acl,
      @DefaultValue("false") @QueryParam("override") Boolean override, @QueryParam("lockTokens") List<String> lockTokens)
      throws NotSupportedException, ObjectNotFoundException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      getItemData(identifier).updateACL(acl, override, lockTokens);
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#updateContent(java.lang.String,
    *      javax.ws.rs.core.MediaType, java.io.InputStream, java.util.List)
    */
   @Path("content/{identifier:.*}")
   public void updateContent(@PathParam("identifier") String identifier,
      @DefaultValue(MediaType.APPLICATION_OCTET_STREAM) @QueryParam("mediaType") MediaType mediaType,
      InputStream newcontent, @QueryParam("lockTokens") List<String> lockTokens) throws ObjectNotFoundException,
      InvalidArgumentException, LockException, PermissionDeniedException, VirtualFileSystemException
   {
      ItemData data = getItemData(identifier);
      if (Type.DOCUMENT != data.getType())
         throw new InvalidArgumentException("Object " + identifier + " is not document. ");
      ((DocumentData)data).setContent(newcontent, mediaType, lockTokens);
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#updateProperties(java.lang.String,
    *      java.util.Collection, java.util.List)
    */
   @Path("properties/{identifier:.*}")
   public void updateProperties(@PathParam("identifier") String identifier, List<InputProperty> properties,
      @QueryParam("lockTokens") List<String> lockTokens) throws ObjectNotFoundException, LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
      //System.out.println(">>>>>>>>> " + properties);
      if (properties == null || properties.size() == 0)
         return;
      ItemData data = getItemData(identifier);
      data.updateProperties(properties, lockTokens);
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
      if (data.getType() == Type.DOCUMENT)
      {
         DocumentData docData = (DocumentData)data;
         return new Document(docData.getId(), docData.getName(), docData.getPath(), docData.getCreationDate(),
            docData.getLastModificationDate(), docData.getVersionId(), docData.getContenType(),
            docData.getContenLength(), docData.isLocked(), docData.getProperties(propertyFilter));
      }
      return new Folder(data.getId(), data.getName(), data.getPath(), data.getCreationDate(),
         data.getLastModificationDate(), data.isLocked(), data.getProperties(propertyFilter));
   }

   private ItemData getItemData(String identifier) throws ObjectNotFoundException, PermissionDeniedException,
      VirtualFileSystemException
   {
      try
      {
         String path = (identifier.charAt(0) != '/') ? ("/" + identifier) : identifier;
         return ItemData.fromNode((Node)session.getItem(path));
      }
      catch (PathNotFoundException e)
      {
         throw new ObjectNotFoundException("Oject " + identifier + " does not exists. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Access denied to object " + identifier + ". ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   private DocumentData getDocumentData(String identifier, String versionIdentifier) throws ObjectNotFoundException,
      InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         String path = (identifier.charAt(0) != '/') ? ("/" + identifier) : identifier;
         ItemData data = ItemData.fromNode((Node)session.getItem(path));
         if (Type.DOCUMENT != data.getType())
            throw new InvalidArgumentException("Object " + identifier + " is not a document. ");
         if (DocumentData.CURRENT_VERSION_ID.equals(versionIdentifier))
            return (DocumentData)data;
         // If not document versionable then any version ID is not acceptable.
         if (!(data.getNode().isNodeType("mix:versionable")))
            throw new InvalidArgumentException("Version " + versionIdentifier + " does not exist. ");
         try
         {
            return (DocumentData)ItemData.fromNode(data.getNode().getVersionHistory().getVersion(versionIdentifier)
               .getNode("jcr:frozenNode"));
         }
         catch (VersionException e)
         {
            throw new InvalidArgumentException("Version " + versionIdentifier + " does not exist. ");
         }
      }
      catch (PathNotFoundException e)
      {
         throw new ObjectNotFoundException("Oject " + identifier + " does not exists. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Access denied to object " + identifier + ". ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }
}
