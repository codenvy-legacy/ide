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
package org.exoplatform.ide.vfs.server;

import org.exoplatform.ide.vfs.ConstraintException;
import org.exoplatform.ide.vfs.LockException;
import org.exoplatform.ide.vfs.ObjectNotFoundException;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.Document;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Property;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;


/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: VirtualFileSystem.java 63633 2010-12-03 16:07:20Z andrew00x $
 */
@Path("/vfs")
public interface VirtualFileSystem
{
   /**
    * Makes copy of the item identified by itemId under new parent
    * 
    * @param itemId
    * @param newparentId
    * @return new itemId
    * @throws ConstraintException - ?
    * @throws ObjectNotFoundException if source object does not exist
    * @throws LockException - ?
    */
   @Path("/copy/{item}/{newParent}")
   @POST
   @Produces("text/plain")
   String copy(String itemId, String newParentId) throws ConstraintException, ObjectNotFoundException,
      LockException;

   /**
    * Creates document
    * 
    * @param parentIdentifier
    * @param properties
    * @param content
    * @return
    * @throws ConstraintException
    * @throws ObjectNotFoundException
    * @throws LockException
    */
   @Path("/document/{parent}/{name}")
   @POST
   @Produces("text/plain")
   String createDocument(@PathParam("parent") String parentId, @PathParam("name") String name, @QueryParam("mimeType") String mimeType, InputStream content)
      throws ConstraintException, ObjectNotFoundException, LockException;

   @Path("/folder/{parent}/{name}")
   @POST
   @Produces("text/plain")
   String createFolder(@PathParam("parent") String parentId, @PathParam("name") String name) throws ConstraintException,
      ObjectNotFoundException, LockException;
   
   @Path("/delete/{item}")
   @POST
   void delete(@PathParam("item") String itemId, @QueryParam("lockToken") String lockToken) throws ConstraintException, ObjectNotFoundException, LockException;

   @Path("/acl/{item}")
   @GET
   @Produces("text/json")
   List<AccessControlEntry> getACL(String itemId) throws ConstraintException, ObjectNotFoundException;

   /**
    * Get children from specified parent.
    * 
    * @param parentIdentifier parent identifier
    * @return iterator over children of specified parent
    * @throws ConstraintException if object may not have children, e.g. since it
    *            is Document but not Folder object
    * @throws ObjectNotFoundException if object corresponded to
    *            <code>parentIdentifier</code> does not exists
    */
   @Path("/children/{folder}")
   @GET
   @Produces("text/json")
   List<Item> getChildren(@PathParam("folder") String folderId, @QueryParam("offset") int offset, @QueryParam("numItems") int numItems) throws ConstraintException, ObjectNotFoundException;

   /**
    * Get content of Document.
    * Method is responsible for setting an appropriate mime-type
    * 
    * @param identifier identifier of Document
    * @return content or <code>null</code> if object has no content
    * @throws ConstraintException if object may not have content, e.g. since it
    *            is Folder but not Document object
    * @throws ObjectNotFoundException if object corresponded to
    *            <code>identifier</code> does not exists
    */
   @Path("/document/{document}")
   @GET
   Response getContent(@PathParam("document") String documentId) throws ConstraintException, ObjectNotFoundException;

   /**
    * Get object by identifier.
    * 
    * @param identifier the object's identifier
    * @return object
    * @throws ObjectNotFoundException if object corresponded to
    *            <code>parentIdentifier</code> does not exists
    */
   @Path("/children/{item}")
   @GET
   @Produces("text/json")
   Item getItem(@PathParam("item") String itemId) throws ObjectNotFoundException;

   // TODO exception if version id is incorrect
   @Path("/version/{document}/{version}")
   @GET
   Response getVersion(@PathParam("document") String documentId, 
         @PathParam("version") String versionId, @QueryParam("offset") int offset, @QueryParam("numItems") int numItems) throws ObjectNotFoundException;

   @Path("/versions/{document}")
   @GET
   @Produces("text/json")
   List<Document> getVersions(@PathParam("document") String documentId) throws ObjectNotFoundException;

   @Path("/lock/{document}")
   @POST
   @Produces("text/plain")
   String lock(@PathParam("document") String documentId) throws ObjectNotFoundException, LockException;

   @Path("/move/{item}/{newParent}")
   @POST
   @Produces("text/plain")
   String move(String itemId, String newParentId,  @QueryParam("lockToken") String lockToken) throws ConstraintException, ObjectNotFoundException,
      LockException;
   
   @Path("/query")
   @GET
   @Produces("text/json")
   List<Item> query(@QueryParam("text") String text, @QueryParam("path") String path);

   @Path("/unlock/{document}")
   @POST
   void unlock(@PathParam("document") String documentId, @QueryParam("lockToken") String lockToken) throws ObjectNotFoundException, LockException;

   @Path("/acl/{item}")
   @POST
   @Consumes("text/json")
   void updateACL(@PathParam("item") String itemId, List<AccessControlEntry> acl, 
         @QueryParam("override") Boolean override, @QueryParam("lockToken") String lockToken) throws ConstraintException,
      ObjectNotFoundException, LockException;

   @Path("/document/{document}")
   @POST
   void updateContent(@PathParam("document") String documentId, InputStream newcontent, @QueryParam("lockToken") String lockToken) throws ConstraintException, ObjectNotFoundException,
      LockException;

   @Path("/item/{item}")
   @POST
   @Consumes("text/json")
   void updateProperties(@PathParam("item") String itemId, List<Property<?>> properties, @QueryParam("lockToken") String lockToken) throws ObjectNotFoundException,
      LockException;
}
