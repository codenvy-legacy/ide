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

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ideall.client.component.AboutForm;
import org.exoplatform.ideall.client.hotkeys.CustomizeHotKeysPanel;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.discovery.DiscoveryService;
import org.exoplatform.ideall.client.model.discovery.event.EntryPointsReceivedEvent;
import org.exoplatform.ideall.client.model.discovery.event.EntryPointsReceivedHandler;
import org.exoplatform.ideall.client.module.preferences.event.CustomizeHotKeysEvent;
import org.exoplatform.ideall.client.module.preferences.event.CustomizeHotKeysHandler;
import org.exoplatform.ideall.client.module.preferences.event.SelectWorkspaceEvent;
import org.exoplatform.ideall.client.module.preferences.event.SelectWorkspaceHandler;
import org.exoplatform.ideall.client.module.preferences.event.ShowAboutDialogEvent;
import org.exoplatform.ideall.client.module.preferences.event.ShowAboutDialogHandler;
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

public class PreferencesModuleEventHandler implements ShowAboutDialogHandler, CustomizeToolbarHandler,
   CustomizeHotKeysHandler, SelectWorkspaceHandler, EntryPointsReceivedHandler
{

   private HandlerManager eventBus;

   private ApplicationContext context;

   protected Handlers handlers;

   public PreferencesModuleEventHandler(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;

      handlers = new Handlers(eventBus);

      handlers.addHandler(ShowAboutDialogEvent.TYPE, this);
      handlers.addHandler(CustomizeToolbarEvent.TYPE, this);
      handlers.addHandler(CustomizeHotKeysEvent.TYPE, this);
      handlers.addHandler(SelectWorkspaceEvent.TYPE, this);
      handlers.addHandler(EntryPointsReceivedEvent.TYPE, this);
   }

   public void onSelectWorkspace(SelectWorkspaceEvent event)
   {
      DiscoveryService.getInstance().getEntryPoints();
   }

   public void onEntryPointsReceived(EntryPointsReceivedEvent event)
   {
      new SelectWorkspaceForm(eventBus, context, event.getEntryPointList());
   }

   public void onShowAboutDialog(ShowAboutDialogEvent event)
   {
      new AboutForm(eventBus);
   }

   public void onCustomizeToolBar(CustomizeToolbarEvent event)
   {
      new CustomizeToolbarForm(eventBus, context);
   }

   public void onCustomizeHotKeys(CustomizeHotKeysEvent event)
   {
      new CustomizeHotKeysPanel(eventBus, context);
   }

}
