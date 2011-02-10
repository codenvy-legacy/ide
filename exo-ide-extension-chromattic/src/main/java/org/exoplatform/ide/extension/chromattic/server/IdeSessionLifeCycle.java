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
package org.exoplatform.ide.extension.chromattic.server;

import org.chromattic.spi.jcr.SessionLifeCycle;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;

import javax.jcr.Credentials;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class IdeSessionLifeCycle implements SessionLifeCycle
{

   public Session login() throws RepositoryException
   {
      try
      {
         return getSessionProvider().getSession(getDefaultWorkspaceName(), getRepository());
      }
      catch (RepositoryConfigurationException e)
      {
         e.printStackTrace();
         throw new RuntimeException(e); 
      }
   }

   public Session login(String workspace) throws RepositoryException
   {
      try
      {
         return getSessionProvider().getSession(workspace, getRepository());
      }
      catch (RepositoryConfigurationException e)
      {
         e.printStackTrace();
         throw new RuntimeException(e); 
      }
   }

   public Session login(Credentials credentials, String workspace) throws RepositoryException
   {
      try
      {
         return getRepository().login(credentials, workspace);
      }
      catch (RepositoryConfigurationException e)
      {
         e.printStackTrace();
         throw new RuntimeException(e); 
      }
   }

   public Session login(Credentials credentials) throws RepositoryException
   {
      try
      {
         return getRepository().login(credentials);
      }
      catch (RepositoryConfigurationException e)
      {
         e.printStackTrace();
         throw new RuntimeException(e); 
      }
   }

   public void save(Session session) throws RepositoryException
   {
      session.save();
   }

   public void close(Session session)
   {
      session.logout();
   }

   private ManageableRepository getRepository() throws RepositoryException, RepositoryConfigurationException
   {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      RepositoryService repositoryService =
         (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
      return repositoryService.getCurrentRepository() != null ? repositoryService.getCurrentRepository()
         : repositoryService.getDefaultRepository();
   }

   private String getDefaultWorkspaceName() throws RepositoryException, RepositoryConfigurationException
   {
      return getRepository().getConfiguration().getDefaultWorkspaceName();
   }

   private SessionProvider getSessionProvider()
   {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      ThreadLocalSessionProviderService sessionProviderService =
         (ThreadLocalSessionProviderService)container
            .getComponentInstanceOfType(ThreadLocalSessionProviderService.class);
      return sessionProviderService.getSessionProvider(null);
   }

}
