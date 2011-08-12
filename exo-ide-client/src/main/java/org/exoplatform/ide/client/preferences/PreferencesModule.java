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
package org.exoplatform.ide.client.preferences;

import java.util.List;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.about.AboutIDEPresenter;
import org.exoplatform.ide.client.about.ShowAboutControl;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.control.event.ControlsUpdatedEvent;
import org.exoplatform.ide.client.framework.control.event.ControlsUpdatedHandler;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.hotkeys.HotKeyManagerImpl;
import org.exoplatform.ide.client.model.discovery.DiscoveryServiceImpl;
import org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryControl;
import org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter;
import org.exoplatform.ide.client.toolbar.CustomizeToolbarPresenter;
import org.exoplatform.ide.client.workspace.SelectWorkspaceControl;
import org.exoplatform.ide.client.workspace.SelectWorkspacePresenter;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class PreferencesModule implements InitializeServicesHandler, ControlsUpdatedHandler,
   ApplicationSettingsReceivedHandler
{

   private HandlerManager eventBus;

   private IDEConfiguration applicationConfiguration;

   private ApplicationSettings applicationSettings;

   private List<Control> controls;

   public PreferencesModule(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(InitializeServicesEvent.TYPE, this);
      eventBus.addHandler(ControlsUpdatedEvent.TYPE, this);
      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);

      /*
       * Select Workspace ability.
       */
      new SelectWorkspacePresenter(eventBus);

      /*
       * Customizing of Toollbars.
       */
      new CustomizeToolbarPresenter(eventBus);

      /*
       * About IDE.
       */
      eventBus.fireEvent(new RegisterControlEvent(new ShowAboutControl(eventBus)));
      new AboutIDEPresenter(eventBus);

      /*
       * Rest Services Discovery.
       */
      eventBus.fireEvent(new RegisterControlEvent(new RestServicesDiscoveryControl()));
      new RestServicesDiscoveryPresenter(eventBus);
   }

   public void onInitializeServices(InitializeServicesEvent event)
   {
      applicationConfiguration = event.getApplicationConfiguration();
      new DiscoveryServiceImpl(eventBus, IDELoader.getInstance(), applicationConfiguration.getContext());
      new HotKeyManagerImpl(eventBus, controls, applicationSettings);
   }

   public void onControlsUpdated(ControlsUpdatedEvent event)
   {
      controls = event.getControls();
   }

   @Override
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();
   }

}
