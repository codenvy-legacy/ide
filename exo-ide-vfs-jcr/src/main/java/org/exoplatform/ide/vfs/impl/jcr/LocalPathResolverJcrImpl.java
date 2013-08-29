/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.vfs.impl.jcr;

import org.exoplatform.ide.vfs.server.LocalPathResolver;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.LocalPathResolveException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;

import javax.jcr.RepositoryException;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class LocalPathResolverJcrImpl implements LocalPathResolver
{
   private final RepositoryService repositoryService;

   public LocalPathResolverJcrImpl(RepositoryService repositoryService)
   {
      this.repositoryService = repositoryService;
   }

   @Override
   public String resolve(VirtualFileSystem vfs, String id) throws LocalPathResolveException
   {
      if (vfs == null)
      {
         throw new LocalPathResolveException(
            "Cannot resolve path on the local filesystem. Virtual filesystem is not initialized. ");
      }
      if (id == null || id.length() == 0)
      {
         throw new LocalPathResolveException(
            "Cannot resolve path on the local filesystem. Item id may not be null or empty. ");
      }
      String fsRootPath = System.getProperty("org.exoplatform.ide.server.fs-root-path");
      if (fsRootPath == null)
      {
         throw new LocalPathResolveException("Cannot resolve path on the local filesystem. Root path may not be null. ");
      }
      ManageableRepository repository;
      try
      {
         repository = repositoryService.getCurrentRepository();
      }
      catch (RepositoryException e)
      {
         throw new LocalPathResolveException("Cannot resolve path on the local filesystem. " + e.getMessage(), e);
      }
      String repositoryName = repository.getConfiguration().getName();
      if (!fsRootPath.endsWith("/"))
      {
         fsRootPath += '/'; // unix like path only!
      }
      Item item;
      String vfsId;
      try
      {
         vfsId = vfs.getInfo().getId();
         item = vfs.getItem(id, PropertyFilter.NONE_FILTER);
      }
      catch (VirtualFileSystemException e)
      {
         throw new LocalPathResolveException("Cannot resolve path on the local filesystem", e);
      }
      return fsRootPath //
         + repositoryName //
         + '/' //
         + vfsId //
         + item.getPath();
   }
}
