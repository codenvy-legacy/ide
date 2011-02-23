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
package org.exoplatform.ide.client.module.preferences;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.component.AboutForm;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.control.event.ControlsUpdatedEvent;
import org.exoplatform.ide.client.framework.control.event.ControlsUpdatedHandler;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.discovery.DiscoveryCallback;
import org.exoplatform.ide.client.framework.discovery.DiscoveryService;
import org.exoplatform.ide.client.framework.discovery.EntryPoint;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.hotkeys.CustomizeHotKeysPanel;
import org.exoplatform.ide.client.hotkeys.HotKeyManagerImpl;
import org.exoplatform.ide.client.model.discovery.DiscoveryServiceImpl;
import org.exoplatform.ide.client.module.preferences.control.CustomizeHotKeysCommand;
import org.exoplatform.ide.client.module.preferences.control.CustomizeToolbarCommand;
import org.exoplatform.ide.client.module.preferences.control.SelectWorkspaceCommand;
import org.exoplatform.ide.client.module.preferences.control.ShowAboutCommand;
import org.exoplatform.ide.client.module.preferences.event.CustomizeHotKeysEvent;
import org.exoplatform.ide.client.module.preferences.event.CustomizeHotKeysHandler;
import org.exoplatform.ide.client.module.preferences.event.SelectWorkspaceEvent;
import org.exoplatform.ide.client.module.preferences.event.SelectWorkspaceHandler;
import org.exoplatform.ide.client.module.preferences.event.ShowAboutDialogEvent;
import org.exoplatform.ide.client.module.preferences.event.ShowAboutDialogHandler;
import org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter;
import org.exoplatform.ide.client.restdiscovery.control.RestServicesDiscoveryControl;
import org.exoplatform.ide.client.toolbar.customize.CustomizeToolbarForm;
import org.exoplatform.ide.client.toolbar.customize.event.CustomizeToolbarEvent;
import org.exoplatform.ide.client.toolbar.customize.event.CustomizeToolbarHandler;
import org.exoplatform.ide.client.workspace.SelectWorkspaceForm;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class PreferencesModule implements InitializeServicesHandler, ApplicationSettingsReceivedHandler,
   ControlsUpdatedHandler, EditorFileOpenedHandler, EditorFileClosedHandler,
   SelectWorkspaceHandler, CustomizeToolbarHandler, CustomizeHotKeysHandler, ShowAboutDialogHandler
{

   private HandlerManager eventBus;

   protected Handlers handlers;

   private IDEConfiguration applicationConfiguration;

   private ApplicationSettings applicationSettings;

   private List<Control> controls;

   private Map<String, File> openedFiles = new HashMap<String, File>();

   private Map<String, String> lockTokens;

   public PreferencesModule(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      eventBus.addHandler(InitializeServicesEvent.TYPE, this);
      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      eventBus.addHandler(ControlsUpdatedEvent.TYPE, this);

      eventBus.fireEvent(new RegisterControlEvent(new SelectWorkspaceCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new CustomizeToolbarCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new CustomizeHotKeysCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new ShowAboutCommand(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new RestServicesDiscoveryControl()));

      handlers.addHandler(ShowAboutDialogEvent.TYPE, this);
      handlers.addHandler(ControlsUpdatedEvent.TYPE, this);
      handlers.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      handlers.addHandler(EditorFileOpenedEvent.TYPE, this);
      handlers.addHandler(EditorFileClosedEvent.TYPE, this);
      handlers.addHandler(SelectWorkspaceEvent.TYPE, this);
      handlers.addHandler(CustomizeToolbarEvent.TYPE, this);
      handlers.addHandler(CustomizeHotKeysEvent.TYPE, this);
      new RestServicesDiscoveryPresenter(eventBus);
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();

      if (applicationSettings.getValueAsMap("lock-tokens") == null)
      {
         applicationSettings.setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }

      lockTokens = applicationSettings.getValueAsMap("lock-tokens");
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

   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   public void onSelectWorkspace(SelectWorkspaceEvent event)
   {
      DiscoveryService.getInstance().getEntryPoints(new DiscoveryCallback()
      {
         @Override
         protected void onSuccess(List<EntryPoint> result)
         {
            new SelectWorkspaceForm(eventBus, applicationSettings, result, openedFiles, lockTokens);
         }
      });
   }

   public void onCustomizeToolBar(CustomizeToolbarEvent event)
   {
      if (controls == null || applicationSettings == null)
      {
         return;
      }

      new CustomizeToolbarForm(eventBus, applicationSettings, controls);
   }

   public void onCustomizeHotKeys(CustomizeHotKeysEvent event)
   {
      new CustomizeHotKeysPanel(eventBus, applicationSettings, controls);
   }

   public void onShowAboutDialog(ShowAboutDialogEvent event)
   {
      new AboutForm(eventBus);
   }

}
