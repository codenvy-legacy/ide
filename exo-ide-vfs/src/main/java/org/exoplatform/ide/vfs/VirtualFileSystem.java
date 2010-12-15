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
package org.exoplatform.ide.vfs;

import org.exoplatform.ide.vfs.exceptions.ConstraintException;
import org.exoplatform.ide.vfs.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.exceptions.LockException;
import org.exoplatform.ide.vfs.exceptions.NotSupportedException;
import org.exoplatform.ide.vfs.exceptions.ObjectNotFoundException;
import org.exoplatform.ide.vfs.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.exceptions.VirtualFileSystemException;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Virtual file system abstraction.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface VirtualFileSystem
{
   /**
    * Create copy of object <code>source</code> in <code>parent</code> folder.
    * 
    * @param source identifier of source object
    * @param parent parent for new copy
    * @param lockTokens lock tokens. This lock tokens will be used if
    *           <code>parent</code> is locked. Pass <code>null</code> or empty
    *           list if there is no lock tokens
    * @return identifier of newly created object
    * @throws ObjectNotFoundException if <code>source</code> or
    *            <code>parent</code> does not exist
    * @throws InvalidArgumentException if any of following conditions are met:
    *            <ul>
    *            <li><code>parent</code> if not a folder</li>
    *            <li><code>parent</code> already contains item with the same
    *            name</li>
    *            </ul>
    * @throws LockException if <code>parent</code> is locked and
    *            <code>lockTokens</code> is <code>null</code> or does not
    *            contains matched lock tokens
    * @throws PermissionDeniedException if user which perform operation has not
    *            permissions to do it
    */
   @POST
   @Path("copy")
   @Produces({MediaType.APPLICATION_JSON})
   ObjectId copy(String source, String parent, List<String> lockTokens) throws ObjectNotFoundException,
      InvalidArgumentException, LockException, PermissionDeniedException;

   /**
    * Create new document in specified folder.
    * 
    * @param parent parent for new document
    * @param name name of document
    * @param mediaType media type of content
    * @param content content of document
    * @param lockTokens lock tokens. This lock tokens will be used if
    *           <code>parent</code> is locked. Pass <code>null</code> or empty
    *           list if there is no lock tokens
    * @param ext UriInfo that may contain other optional query parameters
    * @return identifier of newly created document
    * @throws ObjectNotFoundException if <code>parent</code> does not exist
    * @throws InvalidArgumentException if any of following conditions are met:
    *            <ul>
    *            <li><code>parent</code> if not a folder</li>
    *            <li><code>name</code> is not specified</li>
    *            <li><code>parent</code> already contains item with the same
    *            name</li>
    *            </ul>
    * @throws LockException if <code>parent</code> is locked and
    *            <code>lockTokens</code> is <code>null</code> or does not
    *            contains matched lock tokens
    * @throws PermissionDeniedException if user which perform operation has not
    *            permissions to do it
    * @throws VirtualFileSystemException if any other errors occurs
    */
   @POST
   @Path("document")
   @Produces({MediaType.APPLICATION_JSON})
   ObjectId createDocument(String parent, String name, MediaType mediaType, InputStream content,
      List<String> lockTokens, @Context UriInfo ext) throws ObjectNotFoundException, InvalidArgumentException,
      LockException, PermissionDeniedException, VirtualFileSystemException;

   /**
    * Create new folder in specified folder.
    * 
    * @param parent parent for new folder
    * @param name name of new folder
    * @param lockTokens lock tokens. This lock tokens will be used if
    *           <code>parent</code> is locked. Pass <code>null</code> or empty
    *           list if there is no lock tokens
    * @param ext UriInfo that may contain other optional query parameters
    * @return identifier of newly created folder
    * @throws ObjectNotFoundException if <code>parent</code> does not exist
    * @throws InvalidArgumentException if any of following conditions are met:
    *            <ul>
    *            <li><code>parent</code> if not a folder</li>
    *            <li><code>name</code> is not specified</li>
    *            <li><code>parent</code> already contains item with the same
    *            name</li>
    *            </ul>
    * @throws LockException if <code>parent</code> is locked and
    *            <code>lockTokens</code> is <code>null</code> or does not
    *            contains matched lock tokens
    * @throws PermissionDeniedException if user which perform operation has not
    *            permissions to do it
    * @throws VirtualFileSystemException if any other errors occurs
    */
   @POST
   @Path("folder")
   @Produces({MediaType.APPLICATION_JSON})
   ObjectId createFolder(String parent, String name, List<String> lockTokens, @Context UriInfo ext)
      throws ObjectNotFoundException, InvalidArgumentException, LockException, PermissionDeniedException,
      VirtualFileSystemException;

   /**
    * Delete object <code>identifier</code>. If object is folder then all
    * children of this folder should be removed or ConstraintException must be
    * thrown.
    * 
    * @param identifier identifier of object to be removed
    * @param lockTokens lock tokens. This lock tokens will be used if
    *           <code>identifier</code> is locked. Pass <code>null</code> or
    *           empty list if there is no lock tokens
    * @throws ObjectNotFoundException if <code>identifier</code> does not exist
    * @throws ConstraintException if object is folder which has children and
    *            implementation is not supported removing not empty folders
    * @throws LockException if object <code>identifier</code> is locked
    *            (directly or indirectly) and <code>lockTokens</code> is
    *            <code>null</code> or does not contains matched lock tokens
    * @throws PermissionDeniedException if user which perform operation has not
    *            permissions to do it
    * @throws VirtualFileSystemException if any other errors occurs
    */
   @POST
   @Path("delete")
   void delete(String identifier, List<String> lockTokens) throws ObjectNotFoundException, ConstraintException,
      LockException, PermissionDeniedException, VirtualFileSystemException;

   /**
    * Get ACL applied to <code>identifier</code>. If there is no any ACL applied
    * to object this method must return empty list.
    * 
    * @param identifier identifier of object
    * @return ACL applied to object or(and) inherited from its parent
    * @throws NotSupportedException if ACL is not supported
    * @throws ObjectNotFoundException if <code>identifier</code> does not exist
    * @throws PermissionDeniedException if user which perform operation has not
    *            permissions to do it
    * @throws VirtualFileSystemException if any other errors occurs
    * @see VirtualFileSystemInfo#getAclCapability()
    */
   @GET
   @Path("acl")
   @Produces({MediaType.APPLICATION_JSON})
   List<AccessControlEntry> getACL(String identifier) throws NotSupportedException, ObjectNotFoundException,
      PermissionDeniedException, VirtualFileSystemException;

   /**
    * Get children of specified folder.
    * 
    * @param parent identifier of parent folder
    * @param maxItems max number of items in response. If -1 then no limit of
    *           max items in result set
    * @param skipCount the skip items. Must be equals or greater then 0
    * @param propertyFilter only properties which are accepted by filter should
    *           be included in response. See
    *           {@link PropertyFilter#accept(String)}
    * @return list of children of specified parent
    * @throws ObjectNotFoundException if <code>parent</code> does not exist
    * @throws InvalidArgumentException if any of following conditions are met:
    *            <ul>
    *            <li><code>parent</code> if not a folder</li>
    *            <li><code>skipCount</code> is negative or greater then total
    *            number of items</li>
    *            </ul>
    * @throws PermissionDeniedException if user which perform operation has not
    *            permissions to do it
    * @throws VirtualFileSystemException if any other errors occurs
    */
   @GET
   @Path("children")
   @Produces({MediaType.APPLICATION_JSON})
   ItemList<Item> getChildren(String parent, int maxItems, int skipCount, PropertyFilter propertyFilter)
      throws ObjectNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException;

   /**
    * Get binary content of document.
    * 
    * @param identifier identifier of document
    * @return content response
    * @throws ObjectNotFoundException if <code>identifier</code> does not exist
    * @throws InvalidArgumentException if <code>identifier</code> is not
    *            document
    * @throws PermissionDeniedException if user which perform operation has not
    *            permissions to do it
    * @throws VirtualFileSystemException if any other errors occurs
    */
   @GET
   @Path("content")
   Response getContent(String identifier) throws ObjectNotFoundException, InvalidArgumentException,
      PermissionDeniedException, VirtualFileSystemException;

   /**
    * Get information about virtual file system and its capabilities.
    * 
    * @return info about this virtual file system
    */
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   VirtualFileSystemInfo getVfsInfo(@javax.ws.rs.core.Context UriInfo uriInfo);

   /**
    * Get object by identifier.
    * 
    * @param identifier identifier of object
    * @param propertyFilter only properties which are accepted by filter should
    *           be included in response. See
    *           {@link PropertyFilter#accept(String)}
    * @return object
    * @throws ObjectNotFoundException if <code>identifier</code> does not exist
    * @throws PermissionDeniedException if user which perform operation has not
    *            permissions to do it
    * @throws VirtualFileSystemException if any other errors occurs
    */
   @GET
   @Path("properties")
   @Produces({MediaType.APPLICATION_JSON})
   Item getItem(String identifier, PropertyFilter propertyFilter) throws ObjectNotFoundException,
      PermissionDeniedException, VirtualFileSystemException;

   /**
    * Get binary content of version of object.
    * 
    * @param identifier identifier of object
    * @param versionIdentifier version id
    * @return content response
    * @throws ObjectNotFoundException if <code>identifier</code> does not exist
    * @throws InvalidArgumentException if any of following conditions are met:
    *            <ul>
    *            <li><code>identifier</code> is not document</li>
    *            <li><code>versionIdentifier</code> points to version that does
    *            not exist</li>
    *            </ul>
    * @throws PermissionDeniedException if user which perform operation has not
    *            permissions to do it
    * @throws VirtualFileSystemException if any other errors occurs
    */
   @GET
   @Path("version")
   Response getVersion(String identifier, String versionIdentifier) throws ObjectNotFoundException,
      InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException;

   /**
    * Get list of versions of document. Even if document is not versionable
    * result must contain at least one item (current version of document).
    * 
    * @param identifier identifier of document
    * @param maxItems max number of items in response. If -1 then no limit of
    *           max items in result set
    * @param skipCount the skip items. Must be equals or greater then 0
    * @param propertyFilter only properties which are accepted by filter should
    *           be included in response. See
    *           {@link PropertyFilter#accept(String)}
    * @return
    * @throws ObjectNotFoundException if <code>identifier</code> does not exist
    * @throws InvalidArgumentException if any of following conditions are met:
    *            <ul>
    *            <li><code>identifier</code>O if not a document</li>
    *            <li><code>skipCount</code> is negative or greater then total
    *            number of items</li>
    *            </ul>
    * @throws PermissionDeniedException if user which perform operation has not
    *            permissions to do it
    * @throws VirtualFileSystemException if any other errors occurs
    */
   @GET
   @Path("versions")
   @Produces({MediaType.APPLICATION_JSON})
   ItemList<Document> getVersions(String identifier, int maxItems, int skipCount, PropertyFilter propertyFilter)
      throws ObjectNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException;

   /**
    * Place lock on object.
    * 
    * @param identifier object to be locked
    * @param isDeep if <code>true</code> this lock will apply to this object and
    *           all its descendants (if object is folder). If <code>false</code>
    *           , it applies only to this object. If parameter is not specified
    *           then it is implementation specific
    * @return lock token
    * @throws NotSupportedException if locking is not supported or
    *            <code>isDeep == true</code> but deep locking is not supported
    * @throws ObjectNotFoundException if <code>identifier</code> does not exist
    * @throws LockException if object already locked
    * @throws PermissionDeniedException if user which perform operation has not
    *            permissions to do it
    * @throws VirtualFileSystemException if any other errors occurs
    * @see VirtualFileSystemInfo#getLockCapability()
    */
   @POST
   @Path("lock")
   @Produces({MediaType.APPLICATION_JSON})
   LockToken lock(String identifier, Boolean isDeep) throws NotSupportedException, ObjectNotFoundException,
      LockException, PermissionDeniedException, VirtualFileSystemException;

   /**
    * Move object <code>identifier</code> in <code>newparent</code> folder.
    * 
    * @param identifier identifier of object to be moved
    * @param newparent parent
    * @param lockTokens lock tokens. This lock tokens will be used if
    *           <code>newparent</code> or <code>identifier</code> is locked.
    *           Pass <code>null</code> or empty list if there is no lock tokens
    * @return identifier of moved object
    * @throws ObjectNotFoundException if <code>identifier</code> or
    *            <code>newparent</code> does not exist
    * @throws InvalidArgumentException if any of following conditions are met:
    *            <ul>
    *            <li><code>parent</code> if not a folder</li>
    *            <li><code>parent</code> already contains item with the same
    *            name</li>
    *            </ul>
    * @throws LockException if object <code>identifier</code> or
    *            <code>newparent</code> is locked and <code>lockTokens</code> is
    *            <code>null</code> or does not contains matched lock tokens
    * @throws PermissionDeniedException if user which perform operation has not
    *            permissions to do it
    * @throws VirtualFileSystemException if any other errors occurs
    */
   @POST
   @Path("move")
   @Produces({MediaType.APPLICATION_JSON})
   ObjectId move(String identifier, String newparent, List<String> lockTokens) throws ObjectNotFoundException,
      LockException, PermissionDeniedException, VirtualFileSystemException;

   /**
    * Executes a SQL query statement against the contents of virtual file
    * system.
    * 
    * @param query set of opaque parameters of query statement. Set of
    *           parameters that can be passed by client and how SQL statement
    *           created from this parameters is implementation specific
    * @param maxItems max number of items in response. If -1 then no limit of
    *           max items in result set
    * @param skipCount the skip items. Must be equals or greater the 0
    * @return query result
    * @throws NotSupportedException query is not supported at all or specified
    *            query type is not supported, e.g. if full text query is not
    *            supported but corresponded parameter specified
    * @throws InvalidArgumentException if any of following conditions are met:
    *            <ul>
    *            <li>set of parameters is incorrect and as result is not
    *            possible to create correct SQL statement</li>
    *            <li><code>skipCount</code> is negative or greater then total
    *            number of items</li>
    *            </ul>
    * @see VirtualFileSystemInfo#getQueryCapability()
    */
   @POST
   @Path("query")
   @Produces({MediaType.APPLICATION_JSON})
   ItemList<Item> query(MultivaluedMap<String, String> query, int maxItems, int skipCount)
      throws NotSupportedException, InvalidArgumentException;

   /**
    * Execute a SQL query statement against the contents of virtual file system.
    * 
    * @param query query
    * @param maxItems max number of items in response. If -1 then no limit of
    *           max items in result set
    * @param skipCount the skip items. Must be equals or greater the 0
    * @return query result
    * @throws NotSupportedException if query is not supported at all of
    *            specified query type is not supported, e.g. if full text query
    *            is not supported but CONTAINS clause specified
    * @throws InvalidArgumentException if any of following conditions are met:
    *            <ul>
    *            <li>query statement syntax is invalid</li>
    *            <li><code>skipCount</code> is negative or greater then total
    *            number of items</li>
    *            </ul>
    * @see VirtualFileSystemInfo#getQueryCapability()
    */
   @GET
   @Path("query")
   @Produces({MediaType.APPLICATION_JSON})
   ItemList<Item> query(Query query, int maxItems, int skipCount) throws NotSupportedException,
      InvalidArgumentException;

   /**
    * Set content type for document object.
    * 
    * @param identifier identifier of document to be updated
    * @param mediaType new media type
    * @param lockTokens lock tokens. This lock tokens will be used if
    *           <code>identifier</code> is locked. Pass <code>null</code> or
    *           empty list if there is no lock tokens
    * @throws ObjectNotFoundException if <code>identifier</code> does not exist
    * @throws InvalidArgumentException if <code>identifier</code> is not
    *            document
    * @throws LockException if object <code>identifier</code> is locked
    *            (directly or indirectly) and <code>lockTokens</code> is
    *            <code>null</code> or does not contains matched lock tokens
    * @throws ConstraintException if property can't be updated cause to any
    *            constraint, e.g. property is read only
    * @throws PermissionDeniedException if user which perform operation has not
    *            permissions to do it
    */
   @POST
   @Path("mediatype")
   void setContentType(String identifier, MediaType mediaType, List<String> lockTokens) throws ObjectNotFoundException,
      InvalidArgumentException, LockException, PermissionDeniedException, VirtualFileSystemException;

   /**
    * Remove lock from object.
    * 
    * @param identifier identifier of object to be unlocked
    * @param lockTokens lock tokens
    * @throws NotSupportedException if locking is not supported
    * @throws ObjectNotFoundException if <code>identifier</code> does not exist
    * @throws LockException if object is not locked or <code>lockTokens</code>
    *            is <code>null</code> or does not contains matched lock tokens
    * @throws PermissionDeniedException if user which perform operation has not
    *            permissions to do it
    * @throws VirtualFileSystemException if any other errors occurs
    */
   @POST
   @Path("unlock")
   void unlock(String identifier, List<String> lockTokens) throws NotSupportedException, ObjectNotFoundException,
      LockException, PermissionDeniedException, VirtualFileSystemException;

   /**
    * Update ACL of object.
    * 
    * @param identifier identifier of object for ACL updates
    * @param acl ACL to be applied to object
    * @param override if <code>true</code> then previous ACL will be overridden,
    *           if <code>false</code> then specified ACL will be merged with
    *           previous if any. If such parameters is not specified then
    *           behavior is implementation specific
    * @param lockTokens lock tokens. This lock tokens will be used if
    *           <code>identifier</code> is locked. Pass <code>null</code> or
    *           empty list if there is no lock tokens
    * @throws NotSupportedException if ACL is not supported at all or managing
    *            of ACL is not supported
    * @throws ObjectNotFoundException if <code>identifier</code> does not exist
    * @throws LockException if object <code>identifier</code> is locked and
    *            <code>lockTokens</code> is <code>null</code> or does not
    *            contains matched lock tokens
    * @throws PermissionDeniedException if user which perform operation has not
    *            permissions to do it
    * @throws VirtualFileSystemException if any other errors occurs
    * @see VirtualFileSystemInfo#getAclCapability()
    */
   @POST
   @Path("acl")
   @Consumes({MediaType.APPLICATION_JSON})
   void updateACL(String identifier, List<AccessControlEntry> acl, Boolean override, List<String> lockTokens)
      throws NotSupportedException, ObjectNotFoundException, LockException, PermissionDeniedException,
      VirtualFileSystemException;

   /**
    * Update binary content of document.
    * 
    * @param identifier identifier of document
    * @param mediaType media type of content
    * @param newcontent new content of document
    * @param lockTokens lock tokens. This lock tokens will be used if
    *           <code>identifier</code> is locked. Pass <code>null</code> or
    *           empty list if there is no lock tokens
    * @throws ObjectNotFoundException if <code>identifier</code> does not exist
    * @throws InvalidArgumentException if <code>identifier</code> is not
    *            document
    * @throws LockException if object <code>identifier</code> is locked and
    *            <code>lockTokens</code> is <code>null</code> or does not
    *            contains matched lock tokens
    * @throws PermissionDeniedException if user which perform operation has not
    *            permissions to do it
    * @throws VirtualFileSystemException if any other errors occurs
    */
   @POST
   @Path("content")
   void updateContent(String identifier, MediaType mediaType, InputStream newcontent, List<String> lockTokens)
      throws ObjectNotFoundException, InvalidArgumentException, LockException, PermissionDeniedException,
      VirtualFileSystemException;

   /**
    * Update properties of object.
    * 
    * @param identifier identifier of object to be updated
    * @param properties new properties
    * @param lockTokens lock tokens. This lock tokens will be used if
    *           <code>identifier</code> is locked. Pass <code>null</code> or
    *           empty list if there is no lock tokens
    * @throws ObjectNotFoundException if <code>identifier</code> does not exist
    * @throws LockException if object <code>identifier</code> is locked
    *            (directly or indirectly) and <code>lockTokens</code> is
    *            <code>null</code> or does not contains matched lock tokens
    * @throws ConstraintException if property can't be updated cause to any
    *            constraint, e.g. property is read only
    * @throws PermissionDeniedException if user which perform operation has not
    *            permissions to do it
    * @throws VirtualFileSystemException if any other errors occurs
    */
   @POST
   @Path("properties")
   @Consumes({MediaType.APPLICATION_JSON})
   void updateProperties(String identifier, Collection<InputProperty> properties, List<String> lockTokens)
      throws ObjectNotFoundException, LockException, PermissionDeniedException, VirtualFileSystemException;
}
