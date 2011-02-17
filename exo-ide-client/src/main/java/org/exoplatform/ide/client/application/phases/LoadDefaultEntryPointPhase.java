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
package org.exoplatform.ide.client.application.phases;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;

import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.discovery.DefaultEntryPointCallback;
import org.exoplatform.ide.client.framework.discovery.DiscoverableCallback;
import org.exoplatform.ide.client.framework.discovery.DiscoveryService;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class LoadDefaultEntryPointPhase extends Phase
{

   private HandlerManager eventBus;

   private IDEConfiguration applicationConfiguration;

   private ApplicationSettings applicationSettings;
   
   public LoadDefaultEntryPointPhase(HandlerManager eventBus, IDEConfiguration applicationConfiguration,
      ApplicationSettings applicationSettings)
   {
      this.eventBus = eventBus;
      this.applicationConfiguration = applicationConfiguration;
      this.applicationSettings = applicationSettings;
   }

   protected void execute()
   {
      /*
       * get default entry point
       */
      DiscoveryService.getInstance().getDefaultEntryPoint(new DefaultEntryPointCallback(eventBus)
      {
         
         @Override
         public void onResponseReceived(Request request, Response response)
         {
            applicationConfiguration.setDefaultEntryPoint(this.getDefaultEntryPoint());
            
            DiscoveryService.getInstance().getIsDiscoverable(new DiscoverableCallback(eventBus)
            {
               
               @Override
               public void onResponseReceived(Request request, Response response)
               {
                  new CheckEntryPointPhase(eventBus, applicationConfiguration, applicationSettings);
               }
            });
         }
      });
   }

}
