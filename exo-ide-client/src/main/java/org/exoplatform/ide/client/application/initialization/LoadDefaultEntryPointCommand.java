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
package org.exoplatform.ide.client.application.initialization;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.application.phases.CheckEntryPointPhase;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.discovery.DefaultEntryPointCallback;
import org.exoplatform.ide.client.framework.discovery.DiscoveryService;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class LoadDefaultEntryPointCommand implements Command, ApplicationSettingsReceivedHandler
{

   private HandlerManager eventBus;

   private IDEConfiguration applicationConfiguration;

   private ApplicationSettings applicationSettings;

   private Handlers handlers;

   public LoadDefaultEntryPointCommand(HandlerManager eventBus, IDEConfiguration applicationConfiguration)
   {
      this.eventBus = eventBus;
      this.applicationConfiguration = applicationConfiguration;

      handlers = new Handlers(eventBus);
      handlers.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
   }

   @Override
   public void execute()
   {
      /*
       * get default entry point
       */
      DiscoveryService.getInstance().getDefaultEntryPoint(new DefaultEntryPointCallback()
      {
         @Override
         protected void onSuccess(String result)
         {
            applicationConfiguration.setDefaultEntryPoint(result);
            new CheckEntryPointPhase(eventBus, applicationConfiguration, applicationSettings);
         }
      });
   }

   @Override
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      this.applicationSettings = event.getApplicationSettings();
   }

}
