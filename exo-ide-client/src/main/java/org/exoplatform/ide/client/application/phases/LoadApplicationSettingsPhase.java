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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.component.menu.event.UpdateMainMenuEvent;
import org.exoplatform.gwtframework.ui.client.component.statusbar.event.UpdateStatusBarEvent;
import org.exoplatform.gwtframework.ui.client.component.toolbar.event.UpdateToolbarEvent;
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.application.ControlsRegistration;
import org.exoplatform.ide.client.framework.application.ApplicationConfiguration;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.event.GetApplicationSettingsEvent;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.model.settings.SettingsService;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class LoadApplicationSettingsPhase extends Phase implements ApplicationSettingsReceivedHandler
{

   private HandlerManager eventBus;

   private Handlers handlers;

   private ApplicationConfiguration applicationConfiguration;

   private ApplicationSettings applicationSettings;

   private ControlsRegistration controls;

   private UserInfo userInfo;

   public LoadApplicationSettingsPhase(HandlerManager eventBus, ApplicationConfiguration applicationConfiguration,
      UserInfo userInfo, ControlsRegistration controls)
   {
      this.eventBus = eventBus;
      this.applicationConfiguration = applicationConfiguration;
      this.userInfo = userInfo;
      this.controls = controls;

      handlers = new Handlers(eventBus);
      handlers.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
   }

   @Override
   protected void execute()
   {
      new SettingsService(eventBus, applicationConfiguration.getRegistryURL(), userInfo.getName(),
         IDELoader.getInstance());

      eventBus.fireEvent(new GetApplicationSettingsEvent());
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();

      /*
       * verify toolbar items
       */

      applicationSettings.setValue("toolbar-default-items", controls.getToolbarDefaultControls(), Store.NONE);
      if (applicationSettings.getValueAsList("toolbar-items") == null)
      {
         List<String> toolbarItems = new ArrayList<String>();
         toolbarItems.addAll(controls.getToolbarDefaultControls());
         applicationSettings.setValue("toolbar-items", toolbarItems, Store.REGISTRY);
      }

      new Timer()
      {
         @Override
         public void run()
         {
            initServices();
         }
      }.schedule(Phase.DELAY_BETWEEN_PHASES);
   }

   private void initServices()
   {
      eventBus.fireEvent(new InitializeServicesEvent(applicationConfiguration, IDELoader.getInstance()));

      new Timer()
      {
         @Override
         public void run()
         {
            /*
             * Updating top menu
             */
            eventBus.fireEvent(new UpdateMainMenuEvent(controls.getRegisteredControls()));
            eventBus.fireEvent(new UpdateStatusBarEvent(controls.getStatusBarControls(), controls
               .getRegisteredControls()));

            List<String> toolbarItems = applicationSettings.getValueAsList("toolbar-items");
            if (toolbarItems == null)
            {
               toolbarItems = new ArrayList<String>();
               toolbarItems.addAll(controls.getToolbarDefaultControls());
            }

            eventBus.fireEvent(new UpdateToolbarEvent(toolbarItems, controls.getRegisteredControls()));
            eventBus.fireEvent(new UpdateStatusBarEvent(controls.getStatusBarControls(), controls
               .getRegisteredControls()));

//            new CheckEntryPointPhase(eventBus, applicationConfiguration, applicationSettings);
            new LoadDefaultEntryPointPhase(eventBus, applicationConfiguration, applicationSettings);

         }
      }.schedule(Phase.DELAY_BETWEEN_PHASES);
   }

}
