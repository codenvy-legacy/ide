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
package org.exoplatform.ide.davmount;

import org.everrest.exoplatform.servlet.EverrestExoServlet;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryEntry;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

@SuppressWarnings("serial")
public class DavMountRestServlet extends EverrestExoServlet
{
   
   @Override
   protected void afterInit(ServletConfig config) throws ServletException
   {
      super.afterInit(config);
      ExoContainer container;
      try
      {
         container = getContainer();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
      final RepositoryService repositoryService =
         (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
      final WebDavFolderBinder davBinder =
         (WebDavFolderBinder)container.getComponentInstanceOfType(WebDavFolderBinder.class);;
      new Thread()
      {
         public void run()
         {
            try
            {
               Thread.sleep(5000);
            }
            catch (InterruptedException ignored)
            {
            }

            List<RepositoryEntry> repositoryConfigurations =
               repositoryService.getConfig().getRepositoryConfigurations();
            for (final RepositoryEntry re : repositoryConfigurations)
            {
               davBinder.bindRepository(re.getName(), true);
            }
         }
      }.start();
   }

   @Override
   public void destroy()
   {
      super.destroy();
      ExoContainer container;
      try
      {
         container = getContainer();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
      RepositoryService repositoryService =
         (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
      WebDavFolderBinder davBinder = (WebDavFolderBinder)container.getComponentInstanceOfType(WebDavFolderBinder.class);;
      List<RepositoryEntry> repositoryConfigurations = repositoryService.getConfig().getRepositoryConfigurations();
      for (final RepositoryEntry re : repositoryConfigurations)
      {
         davBinder.unbindRepository(re.getName(), true);
      }
   }
}
