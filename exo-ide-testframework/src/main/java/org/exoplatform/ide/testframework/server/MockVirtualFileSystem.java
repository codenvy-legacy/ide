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
package org.exoplatform.ide.testframework.server;

import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.ConvertibleProperty;
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
import org.exoplatform.ide.vfs.shared.LockToken;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/

public class MockVirtualFileSystem implements VirtualFileSystem
{

   @Override
   public Item copy(String id, String parentId) throws ItemNotFoundException, ConstraintException,
      PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   public File createFile(String parentId, String name, MediaType mediaType, InputStream content)
      throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   public Folder createFolder(String parentId, String name) throws ItemNotFoundException, InvalidArgumentException,
      PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   public Project createProject(String parentId, String name, String type, List<ConvertibleProperty> properties)
      throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   public void delete(String id, String lockToken) throws ItemNotFoundException, ConstraintException, LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
   }

   @Override
   public List<AccessControlEntry> getACL(String id) throws NotSupportedException, ItemNotFoundException,
      PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   public ItemList<Item> getChildren(String folderId, int maxItems, int skipCount, PropertyFilter propertyFilter)
      throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   public ContentStream getContent(String id) throws ItemNotFoundException, InvalidArgumentException,
      PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getContentResponse(java.lang.String)
    */
   @Override
   public Response getContentResponse(String id) throws ItemNotFoundException, InvalidArgumentException,
      PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   public VirtualFileSystemInfo getInfo() throws VirtualFileSystemException
   {
      return null;
   }

   @Override
   public Item getItem(String id, PropertyFilter propertyFilter) throws ItemNotFoundException,
      PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   public ContentStream getVersion(String id, String versionId) throws ItemNotFoundException, InvalidArgumentException,
      PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   /**
    * @see org.exoplatform.ide.vfs.server.VirtualFileSystem#getVersionResponse(java.lang.String, java.lang.String)
    */
   @Override
   public Response getVersionResponse(String id, String versionId) throws ItemNotFoundException,
      InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   public ItemList<File> getVersions(String id, int maxItems, int skipCount, PropertyFilter propertyFilter)
      throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   public LockToken lock(String id) throws NotSupportedException, ItemNotFoundException, InvalidArgumentException,
      LockException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   public Item move(String id, String parentId, String lockToken) throws ItemNotFoundException,
      ConstraintException, LockException, PermissionDeniedException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   public Item rename(String id, MediaType mediaType, String newname, String lockToken)
      throws ItemNotFoundException, InvalidArgumentException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      return null;
   }

   @Override
   public ItemList<Item> search(MultivaluedMap<String, String> query, int maxItems, int skipCount,
      PropertyFilter propertyFilter) throws NotSupportedException, InvalidArgumentException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   public ItemList<Item> search(String statement, int maxItems, int skipCount) throws NotSupportedException,
      InvalidArgumentException, VirtualFileSystemException
   {
      return null;
   }

   @Override
   public void unlock(String id, String lockToken) throws NotSupportedException, ItemNotFoundException, LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
   }

   @Override
   public void updateACL(String id, List<AccessControlEntry> acl, Boolean override, String lockToken)
      throws NotSupportedException, ItemNotFoundException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
   }

   @Override
   public void updateContent(String id, MediaType mediaType, InputStream newcontent, String lockToken)
      throws ItemNotFoundException, InvalidArgumentException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
   }

   @Override
   public void updateItem(String id, List<ConvertibleProperty> properties, String lockToken)
      throws ItemNotFoundException, LockException, PermissionDeniedException, VirtualFileSystemException
   {
   }
}
