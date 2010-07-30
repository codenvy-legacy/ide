/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.module.preferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.component.command.Control;
import org.exoplatform.ideall.client.IDELoader;
import org.exoplatform.ideall.client.component.AboutForm;
import org.exoplatform.ideall.client.framework.application.ApplicationConfiguration;
import org.exoplatform.ideall.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ideall.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ideall.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ideall.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ideall.client.framework.application.event.RegisterEventHandlersEvent;
import org.exoplatform.ideall.client.framework.application.event.RegisterEventHandlersHandler;
import org.exoplatform.ideall.client.framework.control.event.ControlsUpdatedEvent;
import org.exoplatform.ideall.client.framework.control.event.ControlsUpdatedHandler;
import org.exoplatform.ideall.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ideall.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ideall.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ideall.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ideall.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ideall.client.framework.module.IDEModule;
import org.exoplatform.ideall.client.hotkeys.CustomizeHotKeysPanel;
import org.exoplatform.ideall.client.hotkeys.HotKeyManagerImpl;
import org.exoplatform.ideall.client.model.discovery.DiscoveryService;
import org.exoplatform.ideall.client.model.discovery.DiscoveryServiceImpl;
import org.exoplatform.ideall.client.model.discovery.event.EntryPointsReceivedEvent;
import org.exoplatform.ideall.client.model.discovery.event.EntryPointsReceivedHandler;
import org.exoplatform.ideall.client.model.settings.ApplicationSettings;
import org.exoplatform.ideall.client.model.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ideall.client.model.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ideall.client.module.preferences.control.CustomizeHotKeysCommand;
import org.exoplatform.ideall.client.module.preferences.control.CustomizeToolbarCommand;
import org.exoplatform.ideall.client.module.preferences.control.SelectWorkspaceCommand;
import org.exoplatform.ideall.client.module.preferences.control.ShowAboutCommand;
import org.exoplatform.ideall.client.module.preferences.event.CustomizeHotKeysEvent;
import org.exoplatform.ideall.client.module.preferences.event.CustomizeHotKeysHandler;
import org.exoplatform.ideall.client.module.preferences.event.SelectWorkspaceEvent;
import org.exoplatform.ideall.client.module.preferences.event.SelectWorkspaceHandler;
import org.exoplatform.ideall.client.module.preferences.event.ShowAboutDialogEvent;
import org.exoplatform.ideall.client.module.preferences.event.ShowAboutDialogHandler;
import org.exoplatform.ideall.client.module.vfs.api.File;
import org.exoplatform.ideall.client.toolbar.customize.CustomizeToolbarForm;
import org.exoplatform.ideall.client.toolbar.customize.event.CustomizeToolbarEvent;
import org.exoplatform.ideall.client.toolbar.customize.event.CustomizeToolbarHandler;
import org.exoplatform.ideall.client.workspace.SelectWorkspaceForm;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class PreferencesModule implements IDEModule, InitializeServicesHandler, ApplicationSettingsReceivedHandler,
   ControlsUpdatedHandler, EntryPointsReceivedHandler, RegisterEventHandlersHandler, EntryPointChangedHandler,
   EditorFileOpenedHandler, EditorFileClosedHandler, SelectWorkspaceHandler, CustomizeToolbarHandler,
   CustomizeHotKeysHandler, ShowAboutDialogHandler
{

   private HandlerManager eventBus;

   protected Handlers handlers;

   private ApplicationConfiguration applicationConfiguration;

   private ApplicationSettings applicationSettings;

   private List<Control> controls;

   private String currentEntryPoint;

   private Map<String, File> openedFiles = new HashMap<String, File>();

   public PreferencesModule(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      eventBus.addHandler(InitializeServicesEvent.TYPE, this);
      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      eventBus.addHandler(ControlsUpdatedEvent.TYPE, this);

      eventBus.fireEvent(new RegisterControlEvent(new SelectWorkspaceCommand(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new CustomizeToolbarCommand(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new CustomizeHotKeysCommand(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new ShowAboutCommand(eventBus)));

      handlers.addHandler(RegisterEventHandlersEvent.TYPE, this);
      handlers.addHandler(EntryPointChangedEvent.TYPE, this);
      handlers.addHandler(ShowAboutDialogEvent.TYPE, this);
      handlers.addHandler(ControlsUpdatedEvent.TYPE, this);
      handlers.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();
   }

   public void onInitializeServices(InitializeServicesEvent event)
   {
      applicationConfiguration = event.getApplicationConfiguration();
      new DiscoveryServiceImpl(eventBus, IDELoader.getInstance(), applicationConfiguration.getContext());
      new HotKeyManagerImpl(eventBus, applicationSettings, controls);
   }

   public void onControlsUpdated(ControlsUpdatedEvent event)
   {
      controls = event.getControls();
   }

   public void onRegisterEventHandlers(RegisterEventHandlersEvent event)
   {
      handlers.addHandler(EntryPointsReceivedEvent.TYPE, this);
      handlers.addHandler(EditorFileOpenedEvent.TYPE, this);
      handlers.addHandler(EditorFileClosedEvent.TYPE, this);
      handlers.addHandler(SelectWorkspaceEvent.TYPE, this);
      handlers.addHandler(CustomizeToolbarEvent.TYPE, this);
      handlers.addHandler(CustomizeHotKeysEvent.TYPE, this);
   }

   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      currentEntryPoint = event.getEntryPoint();
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
      DiscoveryService.getInstance().getEntryPoints();
   }

   public void onEntryPointsReceived(EntryPointsReceivedEvent event)
   {
      new SelectWorkspaceForm(eventBus, currentEntryPoint, event.getEntryPointList(), openedFiles);
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
