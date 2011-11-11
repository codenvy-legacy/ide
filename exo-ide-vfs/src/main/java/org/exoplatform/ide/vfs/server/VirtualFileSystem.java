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

import org.apache.commons.fileupload.FileItem;
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
import org.exoplatform.ide.vfs.shared.LockToken;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * Virtual file system abstraction.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface VirtualFileSystem
{
   /**
    * Create copy of item <code>id</code> in <code>parentId</code> folder.
    * 
    * @param id id of source item
    * @param parentId id of parent for new copy
    * @return newly created copy of item
    * @throws ItemNotFoundException if <code>source</code> or <code>parentId</code> does not exist
    * @throws ConstraintException if <code>parentId</code> if not a folder
    * @throws ItemAlreadyExistException if <code>parentId</code> already contains item with the same name
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    */
   @POST
   @Path("copy")
   @Produces({MediaType.APPLICATION_JSON})
   Item copy(String id, String parentId) throws ItemNotFoundException, ConstraintException, ItemAlreadyExistException,
      PermissionDeniedException, VirtualFileSystemException;

   /**
    * Create new File in specified folder.
    * 
    * @param parentId id of parent for new File
    * @param name name of File
    * @param mediaType media type of content
    * @param content content of File
    * @return newly created file
    * @throws ItemNotFoundException if <code>parentId</code> does not exist
    * @throws InvalidArgumentException if any of following conditions are met:
    *            <ul>
    *            <li><code>parentId</code> if not a folder</li>
    *            <li><code>name</code> is not specified</li>
    *            </ul>
    * @throws ItemAlreadyExistException if <code>parentId</code> already contains item with the same name
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    */
   @POST
   @Path("file")
   @Produces({MediaType.APPLICATION_JSON})
   File createFile(String parentId, String name, MediaType mediaType, InputStream content)
      throws ItemNotFoundException, InvalidArgumentException, ItemAlreadyExistException, PermissionDeniedException,
      VirtualFileSystemException;

   /**
    * Create new folder in specified folder.
    * 
    * @param parentId id of parent for new folder
    * @param name name of new folder. If name is string separated by '/' all nonexistent parent folders must be created.
    * @return newly created folder
    * @throws ItemNotFoundException if <code>parentId</code> does not exist
    * @throws InvalidArgumentException if any of following conditions are met:
    *            <ul>
    *            <li><code>parentId</code> if not a folder</li>
    *            <li><code>name</code> is not specified</li>
    *            </ul>
    * @throws ItemAlreadyExistException if <code>parentId</code> already contains item with the same name
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    */
   @POST
   @Path("folder")
   @Produces({MediaType.APPLICATION_JSON})
   Folder createFolder(String parentId, String name) throws ItemNotFoundException, InvalidArgumentException,
      ItemAlreadyExistException, PermissionDeniedException, VirtualFileSystemException;

   /**
    * Create new project in specified folder.
    * 
    * NOTE: It should NOT be allowable to create project inside project
    * 
    * @param parentId parent's folder id
    * @param name project name
    * @param type project type
    * @param properties
    * @return newly created project
    * @throws ItemNotFoundException if <code>parentId</code> does not exist
    * @throws InvalidArgumentException if any of following conditions are met:
    *            <ul>
    *            <li><code>parentId</code> if not a folder</li>
    *            <li><code>name</code> is not specified</li>
    *            <li><code>type</code> is not specified</li>
    *            </ul>
    * @throws ItemAlreadyExistException if <code>parentId</code> already contains item with the same name
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    */
   @POST
   @Path("project")
   @Produces({MediaType.APPLICATION_JSON})
   Project createProject(String parentId, String name, String type, List<ConvertibleProperty> properties)
      throws ItemNotFoundException, InvalidArgumentException, ItemAlreadyExistException, PermissionDeniedException,
      VirtualFileSystemException;

   /**
    * Delete item <code>id</code>. If item is folder then all children of this folder should be removed or
    * ConstraintException must be thrown.
    * 
    * @param id id of item to be removed
    * @param lockToken lock token. This lock token will be used if <code>id</code> is locked. Pass <code>null</code> if
    *           there is no lock token, e.g. item is not locked
    * @throws ItemNotFoundException if <code>id</code> does not exist
    * @throws ConstraintException if item is folder which has children and implementation is not supported removing not
    *            empty folders
    * @throws LockException if item <code>id</code> is locked and <code>lockToken</code> is <code>null</code> or does
    *            not matched
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    */
   @POST
   @Path("delete")
   void delete(String id, String lockToken) throws ItemNotFoundException, ConstraintException, LockException,
      PermissionDeniedException, VirtualFileSystemException;

   /**
    * Get ACL applied to <code>id</code>. If there is no any ACL applied to item this method must return empty list.
    * Example of JSON response:
    * 
    * <pre>
    * [{"principal":"john","permissions":["all"]},{"principal":"marry","permissions":["read"]}]
    * </pre>
    * 
    * Such JSON message means:
    * <ul>
    * <li>principal "john" has "all" permissions</li>
    * <li>principal "marry" has "read" permission only</li>
    * </ul>
    * 
    * @param id id of item
    * @return ACL applied to item or(and) inherited from its parent
    * @throws NotSupportedException if ACL is not supported
    * @throws ItemNotFoundException if <code>id</code> does not exist
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    * @see VirtualFileSystemInfo#getAclCapability()
    */
   @GET
   @Path("acl")
   @Produces({MediaType.APPLICATION_JSON})
   List<AccessControlEntry> getACL(String id) throws NotSupportedException, ItemNotFoundException,
      PermissionDeniedException, VirtualFileSystemException;

   /**
    * Get children of specified folder. Example of JSON response:
    * 
    * <pre>
    * {
    *   "hasMoreItems":false,
    *   "items":[
    *       {
    *          "id":"/folder01/DOCUMENT01.txt",
    *          "type":"FILE",
    *          "path":"/folder01/DOCUMENT01.txt",
    *          "versionId":"current",
    *          "creationDate":1292574268440,
    *          "contentType":"text/plain",
    *          "length":100,
    *          "lastModificationDate":1292574268440
    *          "locked":false,
    *          "properties":[],
    *       }
    *   ],
    *   "numItems":1
    * }
    * 
    * </pre>
    * 
    * @param folderId folder's id
    * @param maxItems max number of items in response. If -1 then no limit of max items in result set
    * @param skipCount the skip items. Must be equals or greater then 0
    * @param propertyFilter only properties which are accepted by filter should be included in response. See
    *           {@link PropertyFilter#accept(String)}
    * @return list of children of specified folder
    * @throws ItemNotFoundException if <code>folderId</code> does not exist
    * @throws InvalidArgumentException if any of following conditions are met:
    *            <ul>
    *            <li><code>folderId</code> if not a folder</li>
    *            <li><code>skipCount</code> is negative or greater then total number of items</li>
    *            </ul>
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    */
   @GET
   @Path("children")
   @Produces({MediaType.APPLICATION_JSON})
   ItemList<Item> getChildren(String folderId, int maxItems, int skipCount, PropertyFilter propertyFilter)
      throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException;

   /**
    * Get binary content of File.
    * 
    * @param id id of File
    * @return content
    * @throws ItemNotFoundException if <code>id</code> does not exist
    * @throws InvalidArgumentException if <code>id</code> is not File
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    */
   @GET
   @Path("content")
   ContentStream getContent(String id) throws ItemNotFoundException, InvalidArgumentException,
      PermissionDeniedException, VirtualFileSystemException;

   /**
    * Get binary content of File by path.
    * 
    * @param path path of File
    * @param versionId version id for File item. If<code>null</code> content of latest version returned.
    * @return content
    * @throws ItemNotFoundException if <code>path</code> does not exist
    * @throws InvalidArgumentException if <code>path</code> is not File
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    */
   @GET
   @Path("contentbypath")
   ContentStream getContent(String path, String versionId) throws ItemNotFoundException, InvalidArgumentException,
      PermissionDeniedException, VirtualFileSystemException;

   /**
    * Get information about virtual file system and its capabilities.
    * 
    * @return info about this virtual file system
    * @throws VirtualFileSystemException if any errors occur in VFS
    */
   @GET
   @Produces({MediaType.APPLICATION_JSON})
   VirtualFileSystemInfo getInfo() throws VirtualFileSystemException;

   /**
    * Get item by id. Example of JSON response:
    * 
    * <pre>
    * {
    *   "id":"/folder01/DOCUMENT01.txt",
    *   "type":"FILE",
    *   "path":"/folder01/DOCUMENT01.txt",
    *   "versionId":"current",
    *   "creationDate":1292574268440,
    *   "contentType":"text/plain",
    *   "length":100,
    *   "lastModificationDate":1292574268440
    *   "locked":false,
    *   "properties":[],
    * }
    * </pre>
    * 
    * @param id id of item
    * @param propertyFilter only properties which are accepted by filter should be included in response. See
    *           {@link PropertyFilter#accept(String)}
    * @return item
    * @throws ItemNotFoundException if <code>id</code> does not exist
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    */
   @GET
   @Path("item")
   @Produces({MediaType.APPLICATION_JSON})
   Item getItem(String id, PropertyFilter propertyFilter) throws ItemNotFoundException, PermissionDeniedException,
      VirtualFileSystemException;

   /**
    * Get item by path.
    * 
    * @param path item path
    * @param versionId version id for File item. Must be <code>null</code> for Folders.
    * @param propertyFilter only properties which are accepted by filter should be included in response. See
    *           {@link PropertyFilter#accept(String)}
    * @return item
    * @throws ItemNotFoundException if <code>path</code> does not exist
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    */
   @GET
   @Path("itembypath")
   @Produces({MediaType.APPLICATION_JSON})
   Item getItemByPath(String path, String versionId, PropertyFilter propertyFilter) throws ItemNotFoundException,
      PermissionDeniedException, VirtualFileSystemException;

   /**
    * Get binary content of version of File item.
    * 
    * @param id id of item
    * @param versionId version id
    * @return content response
    * @throws ItemNotFoundException if <code>id</code> does not exist
    * @throws InvalidArgumentException if any of following conditions are met:
    *            <ul>
    *            <li><code>id</code> is not File</li>
    *            <li><code>versionId</code> points to version that does not exist</li>
    *            </ul>
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    */
   @GET
   @Path("version")
   ContentStream getVersion(String id, String versionId) throws ItemNotFoundException, InvalidArgumentException,
      PermissionDeniedException, VirtualFileSystemException;

   /**
    * Get list of versions of File. Even if File is not versionable result must contain at least one item (current
    * version of File). Example of JSON response:
    * 
    * <pre>
    * {
    *   "hasMoreItems":false,
    *   "items":[
    *       {
    *          "id":"/folder01/DOCUMENT01.txt",
    *          "type":"FILE",
    *          "path":"/folder01/DOCUMENT01.txt",
    *          "versionId":"1",
    *          "creationDate":1292574263440,
    *          "contentType":"text/plain",
    *          "length":56,
    *          "lastModificationDate":1292574263440
    *          "locked":false,
    *          "properties":[],
    *       }
    *       {
    *          "id":"/folder01/DOCUMENT01.txt",
    *          "type":"FILE",
    *          "path":"/folder01/DOCUMENT01.txt",
    *          "versionId":"2",
    *          "creationDate":1292574265640,
    *          "contentType":"text/plain",
    *          "length":83,
    *          "lastModificationDate":1292574265640
    *          "locked":false,
    *          "properties":[],
    *       }
    *       {
    *          "id":"/folder01/DOCUMENT01.txt",
    *          "type":"FILE",
    *          "path":"/folder01/DOCUMENT01.txt",
    *          "versionId":"current",
    *          "creationDate":1292574267340,
    *          "contentType":"text/plain",
    *          "length":100,
    *          "lastModificationDate":1292574268440
    *          "locked":false,
    *          "properties":[],
    *       }
    *   ],
    *   "numItems":1
    * }
    * </pre>
    * 
    * @param id id of File
    * @param maxItems max number of items in response. If -1 then no limit of max items in result set
    * @param skipCount the skip items. Must be equals or greater then 0
    * @param propertyFilter only properties which are accepted by filter should be included in response. See
    *           {@link PropertyFilter#accept(String)}
    * @return
    * @throws ItemNotFoundException if <code>id</code> does not exist
    * @throws InvalidArgumentException if any of following conditions are met:
    *            <ul>
    *            <li><code>id</code>item is not a File</li>
    *            <li><code>skipCount</code> is negative or greater then total number of items</li>
    *            </ul>
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    */
   @GET
   @Path("version-history")
   @Produces({MediaType.APPLICATION_JSON})
   ItemList<File> getVersions(String id, int maxItems, int skipCount, PropertyFilter propertyFilter)
      throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException;

   /**
    * Place lock on File item. Example of JSON response if locking is successful:
    * 
    * <pre>
    * {"lockToken":"f37ed0b2c0a8006600afbefda74c2dac"}
    * </pre>
    * 
    * @param id item to be locked
    * @return lock token
    * @throws NotSupportedException if locking is not supported
    * @throws ItemNotFoundException if <code>id</code> does not exist
    * @throws InvalidArgumentException if <code>id</code> is not File item
    * @throws LockException if item already locked
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    * @see VirtualFileSystemInfo#getLockCapability()
    */
   @POST
   @Path("lock")
   @Produces({MediaType.APPLICATION_JSON})
   LockToken lock(String id) throws NotSupportedException, ItemNotFoundException, InvalidArgumentException,
      LockException, PermissionDeniedException, VirtualFileSystemException;

   /**
    * Move item <code>id</code> in <code>newparentId</code> folder. Example of JSON response:
    * 
    * <pre>
    * {"id":"/TESTROOT/NEW_PARENT/DOCUMENT01.txt"}
    * </pre>
    * 
    * @param id id of item to be moved
    * @param parentId id of new parent
    * @param lockToken lock token. This lock token will be used if <code>id</code> is locked. Pass <code>null</code> if
    *           there is no lock token, e.g. item is not locked
    * @return moved item
    * @throws ItemNotFoundException if <code>id</code> or <code>newparentId</code> does not exist
    * @throws ConstraintException if <code>newparentId</code> if not a folder
    * @throws ItemAlreadyExistException if <code>newparentId</code> already contains item with the same name
    * @throws LockException if item <code>id</code> is locked and <code>lockToken</code> is <code>null</code> or does
    *            not matched
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    */
   @POST
   @Path("move")
   @Produces({MediaType.APPLICATION_JSON})
   Item move(String id, String parentId, String lockToken) throws ItemNotFoundException, ConstraintException,
      ItemAlreadyExistException, LockException, PermissionDeniedException, VirtualFileSystemException;

   /**
    * Rename and(or) set content type for Item.
    * 
    * @param id id of Item to be updated
    * @param mediaType new media type. May be not specified if not need to change media type, e.g. need rename only
    * @param newname new name of Item. May be not specified if not need to change name, e.g. need update media type only
    * @param lockToken lock token. This lock token will be used if <code>id</code> is locked. Pass <code>null</code> if
    *           there is no lock token, e.g. item is not locked
    * @return renamed item
    * @throws ItemNotFoundException if <code>id</code> does not exist
    * @throws LockException if item <code>id</code> is locked and <code>lockToken</code> is <code>null</code> or does
    *            not matched
    * @throws ConstraintException if file can't be updated cause to any constraint
    * @throws ItemAlreadyExistException if parent folder already contains item with specified name
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    */
   @POST
   @Path("rename")
   @Produces({MediaType.APPLICATION_JSON})
   Item rename(String id, MediaType mediaType, String newname, String lockToken) throws ItemNotFoundException,
      LockException, ConstraintException, ItemAlreadyExistException, PermissionDeniedException,
      VirtualFileSystemException;

   /**
    * Executes a SQL query statement against the contents of virtual file system.
    * 
    * @param query set of opaque parameters of query statement. Set of parameters that can be passed by client and how
    *           SQL statement (in case of SQL storage )created from this parameters is implementation specific
    * @param maxItems max number of items in response. If -1 then no limit of max items in result set
    * @param skipCount the skip items. Must be equals or greater the 0
    * @param propertyFilter only properties which are accepted by filter should be included in response. See
    *           {@link PropertyFilter#accept(String)}
    * @return query result
    * @throws NotSupportedException query is not supported at all or specified query type is not supported, e.g. if full
    *            text query is not supported but corresponded parameter specified
    * @throws InvalidArgumentException if any of following conditions are met:
    *            <ul>
    *            <li>set of parameters is incorrect and as result is not possible to create correct SQL statement</li>
    *            <li><code>skipCount</code> is negative or greater then total number of items</li>
    *            </ul>
    * @throws VirtualFileSystemException if any other errors occur
    * @see VirtualFileSystemInfo#getQueryCapability()
    */
   @POST
   @Path("search")
   @Produces({MediaType.APPLICATION_JSON})
   ItemList<Item> search(MultivaluedMap<String, String> query, int maxItems, int skipCount,
      PropertyFilter propertyFilter) throws NotSupportedException, InvalidArgumentException, VirtualFileSystemException;

   /**
    * Execute a SQL query statement against the contents of virtual file system.
    * 
    * @param statement query statement
    * @param maxItems max number of items in response. If -1 then no limit of max items in result set
    * @param skipCount the skip items. Must be equals or greater the 0
    * @return query result
    * @throws NotSupportedException if query is not supported at all of specified query type is not supported, e.g. if
    *            full text query is not supported but CONTAINS clause specified
    * @throws InvalidArgumentException if any of following conditions are met:
    *            <ul>
    *            <li>query statement syntax is invalid</li>
    *            <li><code>skipCount</code> is negative or greater then total number of items</li>
    *            </ul>
    * @throws VirtualFileSystemException if any other errors occur
    * @see VirtualFileSystemInfo#getQueryCapability()
    */
   @GET
   @Path("search")
   @Produces({MediaType.APPLICATION_JSON})
   ItemList<Item> search(String statement, int maxItems, int skipCount) throws NotSupportedException,
      InvalidArgumentException, VirtualFileSystemException;

   /**
    * Remove lock from item.
    * 
    * @param id id of item to be unlocked
    * @param lockToken lock token
    * @throws NotSupportedException if locking is not supported
    * @throws ItemNotFoundException if <code>id</code> does not exist
    * @throws LockException if item is not locked or <code>lockToken</code> is <code>null</code> or does not matched
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    */
   @POST
   @Path("unlock")
   void unlock(String id, String lockToken) throws NotSupportedException, ItemNotFoundException, LockException,
      PermissionDeniedException, VirtualFileSystemException;

   /**
    * Update ACL of item. Example of JSON message:
    * 
    * <pre>
    * [{"principal":"john","permissions":["all"]},{"principal":"marry","permissions":["read"]}]
    * </pre>
    * 
    * JSON message as above will set "all" permissions for principal "john" and "read" permission only for principal
    * "marry".
    * 
    * @param id id of item for ACL updates
    * @param acl ACL to be applied to item. If method {@link AccessControlEntry#getPermissions()} for any principal
    *           return empty set of permissions then all permissions for this principal will be removed.
    * @param override if <code>true</code> then previous ACL will be overridden, if <code>false</code> then specified
    *           ACL will be merged with previous if any. If such parameters is not specified then behavior is
    *           implementation specific
    * @param lockToken lock token. This lock token will be used if <code>id</code> is locked. Pass <code>null</code> if
    *           there is no lock token, e.g. item is not locked
    * @throws NotSupportedException if ACL is not supported at all or managing of ACL is not supported
    * @throws ItemNotFoundException if <code>id</code> does not exist
    * @throws LockException if item <code>id</code> is locked and <code>lockToken</code> is <code>null</code> or does
    *            not matched
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    * @see VirtualFileSystemInfo#getAclCapability()
    */
   @POST
   @Path("acl")
   @Consumes({MediaType.APPLICATION_JSON})
   void updateACL(String id, List<AccessControlEntry> acl, Boolean override, String lockToken)
      throws NotSupportedException, ItemNotFoundException, LockException, PermissionDeniedException,
      VirtualFileSystemException;

   /**
    * Update binary content of File.
    * 
    * @param id id of File
    * @param mediaType media type of content
    * @param newcontent new content of File
    * @param lockToken lock token. This lock token will be used if <code>id</code> is locked. Pass <code>null</code> if
    *           there is no lock token, e.g. item is not locked
    * @throws ItemNotFoundException if <code>id</code> does not exist
    * @throws InvalidArgumentException if <code>id</code> is not File
    * @throws LockException if item <code>id</code> is locked and <code>lockToken</code> is <code>null</code> or does
    *            not matched
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    */
   @POST
   @Path("content")
   void updateContent(String id, MediaType mediaType, InputStream newcontent, String lockToken)
      throws ItemNotFoundException, InvalidArgumentException, LockException, PermissionDeniedException,
      VirtualFileSystemException;

   /**
    * Update properties of item.
    * 
    * @param id id of item to be updated
    * @param properties new properties
    * @param lockToken lock token. This lock token will be used if <code>id</code> is locked. Pass <code>null</code> if
    *           there is no lock token, e.g. item is not locked
    * @throws ItemNotFoundException if <code>id</code> does not exist
    * @throws LockException if item <code>id</code> is locked and <code>lockToken</code> is <code>null</code> or does
    *            not matched
    * @throws ConstraintException if property can't be updated cause to any constraint, e.g. property is read only
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    */
   @POST
   @Path("item")
   @Consumes({MediaType.APPLICATION_JSON})
   void updateItem(String id, List<ConvertibleProperty> properties, String lockToken) throws ItemNotFoundException,
      LockException, PermissionDeniedException, VirtualFileSystemException;

   /**
    * Export content of <code>folderId</code> to ZIP archive.
    * 
    * @param folderId folder for ZIP
    * @return ZIP as stream
    * @throws ItemNotFoundException if <code>folderId</code> does not exist
    * @throws InvalidArgumentException if <code>folderId</code> item is not a Folder
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws IOException if any i/o errors occur
    * @throws VirtualFileSystemException if any other errors occur
    */
   @GET
   @Path("export")
   @Produces("application/zip")
   ContentStream exportZip(String folderId) throws ItemNotFoundException, InvalidArgumentException,
      PermissionDeniedException, IOException, VirtualFileSystemException;

   /**
    * Import ZIP content.
    * 
    * @param parentId id of folder to unzip
    * @param in ZIP content
    * @param overwrite overwrite or not existing files. If such parameters is not specified then behavior is
    *           implementation specific
    * @throws ItemNotFoundException if <code>parentId</code> does not exist
    * @throws InvalidArgumentException if <code>parentId</code> item is not a Folder
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws IOException if any i/o errors occur
    * @throws VirtualFileSystemException if any other errors occur
    */
   @POST
   @Path("import")
   @Consumes("application/zip")
   public void importZip(String parentId, InputStream in, Boolean overwrite) throws ItemNotFoundException,
      InvalidArgumentException, PermissionDeniedException, IOException, VirtualFileSystemException;

   /**
    * Download binary content of File. Response must contains 'Content-Disposition' header to force web browser save
    * file.
    * 
    * @param id id of File
    * @return Response with file content for download.
    * @throws ItemNotFoundException if <code>id</code> does not exist
    * @throws InvalidArgumentException if <code>id</code> is not File
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws VirtualFileSystemException if any other errors occur
    */
   @GET
   @Path("downloadfile")
   Response downloadFile(String id) throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException,
      VirtualFileSystemException;

   /**
    * Upload content of file. Content of file is part of 'multipart/form-data request', e.g. content sent from HTML
    * form.
    * 
    * @param parentId id of parent for new File
    * @param formData content of file and optional additional form fields. Set of additional field is implementation
    *           specific.
    * @return Response that represents response in HTML format.
    * @throws ItemNotFoundException if <code>parentId</code> does not exist
    * @throws InvalidArgumentException if any of following conditions are met:
    *            <ul>
    *            <li><code>parentId</code> if not a folder</li>
    *            <li>If form does not contain all required fields. Set of fields is implementation specific.</li>
    *            </ul>
    * @throws ItemAlreadyExistException if <code>parentId</code> already contains item with the same name. It is
    *            possible to prevent such type of exception by sending some form parameters that allow to overwrite file
    *            content.
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws IOException if any i/o errors occur
    * @throws VirtualFileSystemException if any other errors occur
    */
   @POST
   @Path("uploadfile")
   @Consumes({MediaType.MULTIPART_FORM_DATA})
   @Produces({MediaType.TEXT_HTML})
   Response uploadFile(String parentId, java.util.Iterator<FileItem> formData) throws ItemNotFoundException,
      InvalidArgumentException, ItemAlreadyExistException, PermissionDeniedException, VirtualFileSystemException,
      IOException;

   /**
    * Download content of <code>folderId</code> as ZIP archive. Response must contains 'Content-Disposition' header to
    * force web browser save file.
    * 
    * @param folderId folder for ZIP
    * @return Response with ZIPed content of folder
    * @throws ItemNotFoundException if <code>folderId</code> does not exist
    * @throws InvalidArgumentException if <code>folderId</code> item is not a Folder
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws IOException if any i/o errors occur
    * @throws VirtualFileSystemException if any other errors occur
    */
   @GET
   @Path("downloadzip")
   @Consumes("application/zip")
   Response downloadZip(String folderId) throws ItemNotFoundException, InvalidArgumentException,
      PermissionDeniedException, IOException, VirtualFileSystemException;

   /**
    * Import ZIP content. ZIP content is part of 'multipart/form-data request', e.g. content sent from HTML form.
    * 
    * @param parentId id of folder to unzip
    * @param formData contains ZIPed folder and add optional additional form fields. Set of additional field is
    *           implementation specific.
    * @return Response that represents response in HTML format.
    * @throws ItemNotFoundException if <code>parentId</code> does not exist
    * @throws InvalidArgumentException if <code>parentId</code> item is not a Folder
    * @throws PermissionDeniedException if user which perform operation has no permissions to do it
    * @throws IOException if any i/o errors occur
    * @throws VirtualFileSystemException if any other errors occur
    */
   @POST
   @Path("uploadzip")
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   @Produces({MediaType.TEXT_HTML})
   public Response uploadZip(String parentId, java.util.Iterator<FileItem> formData) throws ItemNotFoundException,
      InvalidArgumentException, PermissionDeniedException, IOException, VirtualFileSystemException;
}
