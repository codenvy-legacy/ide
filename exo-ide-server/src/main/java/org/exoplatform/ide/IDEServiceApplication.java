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
package org.exoplatform.ide;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.conversationstate.RestConversationState;
import org.exoplatform.ide.discovery.RepositoryDiscoveryService;
import org.exoplatform.ide.download.DownloadContentService;
import org.exoplatform.ide.template.TemplatesRestService;
import org.exoplatform.ide.upload.LoopbackContentService;
import org.exoplatform.ide.upload.UploadService;
import org.exoplatform.ide.upload.UploadServiceExceptionMapper;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.registry.RegistryService;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Jan 12, 2011 5:24:37 PM evgen $
 *
 */
public class IDEServiceApplication extends Application
{

   private final Set<Class<?>> classes = new HashSet<Class<?>>();

   private final Set<Object> objects = new HashSet<Object>();

   public IDEServiceApplication(RepositoryService repositoryService, RegistryService registryService,
      ThreadLocalSessionProviderService sessionProviderService, InitParams initParams)
   {
      String entryPoint = null;
      boolean discoverable = true;
      String workspace = null;
      if (initParams != null)
      {
         if (initParams.getValueParam("defaultEntryPoint") != null)
         {
            entryPoint = initParams.getValueParam("defaultEntryPoint").getValue();
         }
         if (initParams.getValueParam("workspace") != null)
         {
            entryPoint = initParams.getValueParam("workspace").getValue();
         }

         if (initParams.getValueParam("discoverable") != null)
         {
            discoverable = Boolean.parseBoolean(initParams.getValueParam("discoverable").getValue());
         }
      }

      objects.add(new RepositoryDiscoveryService(repositoryService, sessionProviderService, entryPoint, discoverable));
      objects.add(new UploadServiceExceptionMapper());

      objects.add(new IDEConfigurationService(repositoryService, registryService, sessionProviderService, entryPoint,
         discoverable, workspace));
      objects.add(new TemplatesRestService(repositoryService, sessionProviderService, workspace));

      classes.add(LoopbackContentService.class);
      classes.add(DownloadContentService.class);
      classes.add(RestConversationState.class);
      classes.add(UploadService.class);
   }

   /**
    * @see javax.ws.rs.core.Application#getClasses()
    */
   @Override
   public Set<Class<?>> getClasses()
   {
      return classes;
   }

   /**
    * @see javax.ws.rs.core.Application#getSingletons()
    */
   @Override
   public Set<Object> getSingletons()
   {
      return objects;
   }

}
