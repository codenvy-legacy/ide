/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.server;

import org.exoplatform.ide.vfs.server.exceptions.ConstraintException;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemAlreadyExistException;
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
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.ACLCapability;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.QueryCapability;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
@Path("vfs/mock")
public class MockVFS implements VirtualFileSystem
{
   private UriInfo uriInfo;

   public MockVFS(@Context UriInfo uriInfo)
   {
      this.uriInfo = uriInfo;
   }

   @Override
   @Path("copy/{id:.*}")
   public Item copy(@PathParam("id") String id, //
      @QueryParam("parentId") String parentId) throws ItemNotFoundException, ConstraintException,
      PermissionDeniedException, ItemAlreadyExistException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   @Path("file/{parentId:.*}")
   @Produces({MediaType.APPLICATION_JSON})
   public File createFile(@PathParam("parentId") String parentId, @QueryParam("name") String name,
      @DefaultValue(MediaType.APPLICATION_OCTET_STREAM) @HeaderParam("Content-Type") MediaType mediaType,
      InputStream content) throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException,
      ItemAlreadyExistException, VirtualFileSystemException
   {
      long len = 0;
      try
      {
         len = content.available();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      File newFile = createFile(parentId, name, mediaType, len);
      return newFile;
   }

   /**
    * @param parentId
    * @param name
    * @param mediaType
    * @param len
    * @return
    */
   private File createFile(String parentId, String name, MediaType mediaType, long len)
   {
      File newFile = new File();
      newFile.setId(System.currentTimeMillis() + "");
      newFile.setCreationDate(System.currentTimeMillis());
      newFile.setLastModificationDate(System.currentTimeMillis());
      newFile.setLength(len);
      newFile.setLocked(false);
      newFile.setMimeType(mediaType.toString());
      newFile.setName(name);
      newFile.setParentId(parentId);
      newFile.setPath("/");
      newFile.setVersionId("versionId");
      return newFile;
   }

   @Path("folder/{parentId:.*}")
   @Produces({MediaType.APPLICATION_JSON})
   public Folder createFolder(@PathParam("parentId") String parentId, @QueryParam("name") String name)
      throws ItemNotFoundException, InvalidArgumentException, ItemAlreadyExistException, PermissionDeniedException,
      VirtualFileSystemException
   {
      return new Folder(UUID.randomUUID().toString(), name, Folder.FOLDER_MIME_TYPE, "/path", parentId,
         System.currentTimeMillis(), Collections.EMPTY_LIST, new HashMap<String, Link>());
   }

   @SuppressWarnings("unchecked")
   @Path("project/{parentId:.*}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces({MediaType.APPLICATION_JSON})
   public Project createProject(@PathParam("parentId") String parentId, @QueryParam("name") String name,
      @QueryParam("type") String type, List<ConvertibleProperty> properties) throws ItemNotFoundException,
      InvalidArgumentException, ItemAlreadyExistException, PermissionDeniedException, VirtualFileSystemException
   {
      return new Project(UUID.randomUUID().toString(), name, Folder.FOLDER_MIME_TYPE, "/path", parentId,
         System.currentTimeMillis(), Collections.EMPTY_LIST, new HashMap<String, Link>(), type);
   }

   @Override
   @Path("delete/{id:.*}")
   public void delete(@PathParam("id") String id, //
      @QueryParam("lockToken") String lockToken) throws ItemNotFoundException, ConstraintException, LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
      if (id.equals("locked-file"))
      {
         if (lockToken == null || !lockToken.equals("100"))
            throw new LockException("message");
      }
   }

   @Override
   @Path("acl/{id:.*}")
   public List<AccessControlEntry> getACL(@PathParam("id") String id) throws NotSupportedException,
      ItemNotFoundException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   @Path("children/{id:.*}")
   public ItemList<Item> getChildren(@PathParam("id") String folderId, //
      @DefaultValue("-1") @QueryParam("maxItems") int maxItems, //
      @QueryParam("skipCount") int skipCount, //
      @DefaultValue(PropertyFilter.NONE) @QueryParam("propertyFilter") PropertyFilter propertyFilter)
      throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      List<Item> items = new ArrayList<Item>();
      items.add(createFile(folderId, UUID.randomUUID().toString(), MediaType.APPLICATION_JSON_TYPE, 10));
      ItemList<Item> itemList = new ItemList<Item>(items);
      itemList.setNumItems(items.size());
      return itemList;
   }

   @Override
   public ContentStream getContent(@PathParam("id") String id) throws ItemNotFoundException, InvalidArgumentException,
      PermissionDeniedException, VirtualFileSystemException
   {
      ByteArrayInputStream b = new ByteArrayInputStream("Hello, world!".getBytes());
      return new ContentStream("", b, "text/plain", b.available(), new Date());
   }

   @Override
   public ContentStream getContent(String path, String versionId) throws ItemNotFoundException,
      InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   @Path("content/{id:.*}")
   public Response getContentResponse(@PathParam("id") String id) throws ItemNotFoundException,
      InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      ContentStream content = getContent(id);
      return Response.ok(content.getStream(), content.getMimeType()).lastModified(content.getLastModificationDate())
         .build();
   }

   @Override
   public Response getContentResponse(String path, String versionId) throws ItemNotFoundException,
      InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   public VirtualFileSystemInfo getInfo()
   {
      try
      {
         VirtualFileSystemInfo info =
            new VirtualFileSystemInfo("mock", true, true, "ANONIM", "ANY", Collections.<String> emptyList(),
               ACLCapability.MANAGE, QueryCapability.BOTHCOMBINED, createUrlTemplates(), new Folder());
         return info;
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return new VirtualFileSystemInfo();
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

   private URI createURI(String rel, String id, String... query)
   {
      UriBuilder uriBuilder = uriInfo.getBaseUriBuilder();
      /*      List<String> matchedURIs = uriInfo.getMatchedURIs();
            int n = matchedURIs.size();
            for (int i = n - 1; i > 0; i--)
               uriBuilder.path(matchedURIs.get(i));
      */
      //      uriBuilder.path(MockVFS.class, "getVFS");
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
      /*      URI uri = uriBuilder.build(); */
      URI uri = uriBuilder.build("db", "ws");
      return uri;
   }

   @Override
   @Path("item/{id:.*}")
   public Item getItem(@PathParam("id") String id, //
      @DefaultValue("*") @QueryParam("propertyFilter") PropertyFilter propertyFilter) throws ItemNotFoundException,
      PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getItemByPath(java.lang.String, java.lang.String,
    *      org.exoplatform.ide.vfs.server.PropertyFilter)
    */
   @Override
   public Item getItemByPath(String path, String versionId, PropertyFilter propertyFilter)
      throws ItemNotFoundException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   public ContentStream getVersion(@PathParam("id") String id, //
      @PathParam("versionId") String versionId) throws ItemNotFoundException, InvalidArgumentException,
      PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   @Path("version/{id:.*}/{versionId}")
   public Response getVersionResponse(@PathParam("id") String id, //
      @PathParam("versionId") String versionId) throws ItemNotFoundException, InvalidArgumentException,
      PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   @Path("version-history/{id:.*}")
   public ItemList<File> getVersions(@PathParam("id") String id,
      @DefaultValue("-1") @QueryParam("maxItems") int maxItems, @QueryParam("skipCount") int skipCount,
      @DefaultValue("*") @QueryParam("propertyFilter") PropertyFilter propertyFilter) throws ItemNotFoundException,
      InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   @Path("lock/{id:.*}")
   public LockToken lock(@PathParam("id") String id) throws NotSupportedException, ItemNotFoundException,
      InvalidArgumentException, LockException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   @Path("move/{id:.*}")
   public Item move(@PathParam("id") String id, @QueryParam("parentId") String parentId,
      @QueryParam("lockToken") String lockToken) throws ItemNotFoundException, ConstraintException, LockException,
      ItemAlreadyExistException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   @Path("rename/{id:.*}")
   public Item rename(@PathParam("id") String id, //
      @QueryParam("mediaType") MediaType mediaType, //
      @QueryParam("newname") String newname, //
      @QueryParam("lockToken") String lockToken) throws ItemNotFoundException, InvalidArgumentException, LockException,
      ItemAlreadyExistException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
   public ItemList<Item> search(MultivaluedMap<String, String> query, //
      @DefaultValue("-1") @QueryParam("maxItems") int maxItems, //
      @QueryParam("skipCount") int skipCount, //
      @DefaultValue("*") @QueryParam("propertyFilter") PropertyFilter propertyFilter) throws NotSupportedException,
      InvalidArgumentException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   public ItemList<Item> search(@QueryParam("statement") String statement, //
      @DefaultValue("-1") @QueryParam("maxItems") int maxItems, //
      @QueryParam("skipCount") int skipCount) throws NotSupportedException, InvalidArgumentException,
      VirtualFileSystemException
   {
      return null;
   }

   @Override
   @Path("unlock/{id:.*}")
   public void unlock(@PathParam("id") String id, //
      @QueryParam("lockToken") String lockToken) throws NotSupportedException, ItemNotFoundException, LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
   }

   @Override
   @Path("acl/{id:.*}")
   public void updateACL(@PathParam("id") String id, //
      List<AccessControlEntry> acl, //
      @DefaultValue("false") @QueryParam("override") Boolean override, //
      @QueryParam("lockToken") String lockToken) throws NotSupportedException, ItemNotFoundException, LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
   }

   @Override
   @Path("content/{id:.*}")
   public void updateContent(
      @PathParam("id") String id, //
      @DefaultValue(MediaType.APPLICATION_OCTET_STREAM) @HeaderParam("Content-Type") MediaType mediaType,
      InputStream newcontent, //
      @QueryParam("lockToken") String lockToken) throws ItemNotFoundException, InvalidArgumentException, LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
      if (id.equals("locked-file"))
      {
         if (lockToken == null || !lockToken.equals("100"))
         {
            throw new LockException("message");
         }
      }
   }

   @Override
   @Path("item/{id:.*}")
   public void updateItem(@PathParam("id") String id, //
      List<ConvertibleProperty> properties, //
      @QueryParam("lockToken") String lockToken) throws ItemNotFoundException, LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
   }

   @Override
   public InputStream exportZip(String folderId) throws ItemNotFoundException, InvalidArgumentException,
      PermissionDeniedException, IOException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   public void importZip(String parentId, InputStream in, Boolean overwrite) throws ItemNotFoundException,
      InvalidArgumentException, PermissionDeniedException, IOException, VirtualFileSystemException
   {
   }
}
