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

import org.exoplatform.ide.vfs.model.AccessControlEntry;
import org.exoplatform.ide.vfs.model.Item;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface VirtualFileSystem
{
   ObjectId copy(ObjectId identifier, ObjectId newparent) throws ConstraintException, ObjectNotFoundException,
      LockException;

   ObjectId createDocument(ObjectId parentIdentifier, Map<String, Property<?>> properties, Content content)
      throws ConstraintException, ObjectNotFoundException, LockException;

   ObjectId createFolder(ObjectId parentIdentifier, Map<String, Property<?>> properties) throws ConstraintException,
      ObjectNotFoundException, LockException;

   void delete(ObjectId identifier) throws ConstraintException, ObjectNotFoundException, LockException;

   List<AccessControlEntry> getACL(ObjectId identifier) throws ConstraintException, ObjectNotFoundException;

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
   ItemsIterator<Item> getChildren(ObjectId parentIdentifier) throws ConstraintException, ObjectNotFoundException;

   /**
    * Get binary content of Document.
    * 
    * @param identifier identifier of Document
    * @return content or <code>null</code> if object has no content
    * @throws ConstraintException if object may not have content, e.g. since it
    *            is Folder but not Document object
    * @throws ObjectNotFoundException if object corresponded to
    *            <code>identifier</code> does not exists
    */
   Content getContent(ObjectId identifier) throws ConstraintException, ObjectNotFoundException;

   /**
    * Get object by identifier.
    * 
    * @param identifier the object's identifier
    * @return object
    * @throws ObjectNotFoundException if object corresponded to
    *            <code>parentIdentifier</code> does not exists
    */
   Item getItem(ObjectId identifier) throws ObjectNotFoundException;

   // TODO exception if version id is incorrect
   Item getVersion(ObjectId identifier, VersionId versionIdentifier) throws ObjectNotFoundException;

   ItemsIterator<Item> getVersions(ObjectId identifier) throws ObjectNotFoundException;

   void lock(ObjectId identifier) throws ObjectNotFoundException, LockException;

   ObjectId move(ObjectId identifier, ObjectId newparent) throws ConstraintException, ObjectNotFoundException,
      LockException;

   ItemsIterator<Item> query(Query query);

   void unlock(ObjectId identifier) throws ObjectNotFoundException, LockException;

   void updateACL(ObjectId identifier, List<AccessControlEntry> acl, Boolean override) throws ConstraintException,
      ObjectNotFoundException, LockException;

   void updateContent(ObjectId identifier, Content newcontent) throws ConstraintException, ObjectNotFoundException,
      LockException;

   void updateProperties(ObjectId identifier, List<Property<?>> properties) throws ObjectNotFoundException,
      LockException;
}
