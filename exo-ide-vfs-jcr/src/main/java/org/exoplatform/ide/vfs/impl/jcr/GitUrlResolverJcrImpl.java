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

import org.exoplatform.ide.vfs.server.GitUrlResolver;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.GitUrlResolveException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;

import javax.jcr.RepositoryException;
import javax.ws.rs.core.UriInfo;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class GitUrlResolverJcrImpl implements GitUrlResolver
{
   private final RepositoryService repositoryService;

   public GitUrlResolverJcrImpl(RepositoryService repositoryService)
   {
      this.repositoryService = repositoryService;
   }

   @Override
   public String resolve(UriInfo uriInfo, VirtualFileSystem vfs, String id) throws GitUrlResolveException
   {
      try
      {
         if (vfs == null)
         {
            throw new GitUrlResolveException("Can't resolve Git Url : Virtual file system not initialized");
         }
         if (id == null || id.length() == 0)
         {
            throw new GitUrlResolveException("Can't resolve Git Url. Item path may not be null or empty");
         }
         String gitServer = System.getProperty("org.exoplatform.ide.git.server");
         if (gitServer == null)
         {
            throw new GitUrlResolveException("Can't resolve Git Url. Git server path may not be null.");
         }
         ManageableRepository repository = repositoryService.getCurrentRepository();
         String repositoryName = repository.getConfiguration().getName();
         if (!gitServer.endsWith("/"))
         {
            gitServer += "/";
         }
         Item item = null;
         String vfsId = null;
         try
         {
            vfsId = vfs.getInfo().getId();
            item = vfs.getItem(id, PropertyFilter.NONE_FILTER);
         }
         catch (VirtualFileSystemException e)
         {
            throw new GitUrlResolveException("Can't resolve Git Url", e);
         }
         StringBuilder result = new StringBuilder();
         // Set schema hardcode to "http", 
         // it because we have not valid certificate on test servers
         // and Jenkins can't clone source from this servers (IDE-2072)
         result.append("http")
            .append("://")
            .append(uriInfo.getBaseUri().getHost());
         int port = uriInfo.getBaseUri().getPort();
         if (port != 80 && port != 443 && port != -1)
         {
            result.append(':').append(port);
         }
         result.append('/')
            .append(gitServer)
            .append(repositoryName)
            .append('/')
            .append(vfsId)
            .append(item.getPath());

         return result.toString();
      }
      catch (RepositoryException e)
      {
         throw new GitUrlResolveException("Can't resolve  Git Url", e);
      }
   }
}
