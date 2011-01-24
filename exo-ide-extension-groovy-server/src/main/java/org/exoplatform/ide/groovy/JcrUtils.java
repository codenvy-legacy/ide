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
package org.exoplatform.ide.groovy;

import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class JcrUtils
{

   /**
    * Get current repository
    * @return current repository or default repository if current repository is null
    * @throws RepositoryException
    * @throws RepositoryConfigurationException
    */
   public static ManageableRepository getRepository(RepositoryService repositoryService) throws RepositoryException,
      RepositoryConfigurationException
   {
      return repositoryService.getCurrentRepository() != null ? repositoryService.getCurrentRepository()
         : repositoryService.getDefaultRepository();
   }

   /**
    * Get JCR Session on current repository
    * 
    * @param repositoryService
    * @param sessionProviderService
    * @param wsName
    * @return
    * @throws RepositoryException
    * @throws RepositoryConfigurationException
    */
   public static Session getSession(RepositoryService repositoryService,
      ThreadLocalSessionProviderService sessionProviderService, String wsName) throws RepositoryException,
      RepositoryConfigurationException
   {
      ManageableRepository repo = getRepository(repositoryService);
      SessionProvider sp = sessionProviderService.getSessionProvider(null);
      if (sp == null)
         throw new RepositoryException("SessionProvider is not properly set. Make the application calls"
            + "SessionProviderService.setSessionProvider(..) somewhere before ("
            + "for instance in Servlet Filter for WEB application)");

      return sp.getSession(wsName, repo);
   }

}
