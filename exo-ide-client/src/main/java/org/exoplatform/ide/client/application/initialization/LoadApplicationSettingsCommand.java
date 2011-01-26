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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.application.ControlsRegistration;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.event.GetApplicationSettingsEvent;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.client.model.settings.SettingsService;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class LoadApplicationSettingsCommand implements Command, ApplicationSettingsReceivedHandler,
   UserInfoReceivedHandler
{

   private HandlerManager eventBus;

   private Handlers handlers;

   private IDEConfiguration ideConfiguration;

   private UserInfo userInfo;

   private ControlsRegistration controls;

   public LoadApplicationSettingsCommand(HandlerManager eventBus, ControlsRegistration controls,
      IDEConfiguration ideConfiguration)
   {
      this.eventBus = eventBus;
      this.controls = controls;
      this.ideConfiguration = ideConfiguration;

      handlers = new Handlers(eventBus);
      handlers.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      handlers.addHandler(UserInfoReceivedEvent.TYPE, this);
   }

   @Override
   public void execute()
   {
      new SettingsService(eventBus, ideConfiguration.getRegistryURL(), userInfo.getName(), IDELoader.getInstance());
      eventBus.fireEvent(new GetApplicationSettingsEvent());
   }

   @Override
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      ApplicationSettings applicationSettings = event.getApplicationSettings();

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
   }

   @Override
   public void onUserInfoReceived(UserInfoReceivedEvent event)
   {
      userInfo = event.getUserInfo();
   }

}
