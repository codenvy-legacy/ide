/**
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
 *
 */

package org.exoplatform.ide.client.application.phases;

import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.discovery.DiscoveryService;
import org.exoplatform.ide.client.framework.discovery.event.DefaultEntryPointReceivedEvent;
import org.exoplatform.ide.client.framework.discovery.event.DefaultEntryPointReceivedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;

import com.google.gwt.event.shared.HandlerManager;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class LoadDefaultEntryPointPhase extends Phase implements DefaultEntryPointReceivedHandler
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

      eventBus.addHandler(DefaultEntryPointReceivedEvent.TYPE, this);
   }

   protected void execute()
   {
      /*
       * get default entry point
       */
      DiscoveryService.getInstance().getDefaultEntryPoint();

   }

   /**
    * @see org.exoplatform.ide.client.framework.discovery.event.DefaultEntryPointReceivedHandler#onDefaultEntryPointReceived(org.exoplatform.ide.client.framework.discovery.event.DefaultEntryPointReceivedEvent)
    */
   public void onDefaultEntryPointReceived(DefaultEntryPointReceivedEvent event)
   {
      applicationConfiguration.setDefaultEntryPoint(event.getDefaultEntryPoint());
      new CheckEntryPointPhase(eventBus, applicationConfiguration, applicationSettings);
   }

}
