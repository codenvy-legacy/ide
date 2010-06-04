/**
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.exoplatform.ideall.client.common;

import org.exoplatform.ideall.client.application.component.AbstractApplicationComponent;
import org.exoplatform.ideall.client.component.AboutForm;
import org.exoplatform.ideall.client.event.help.ShowAboutDialogEvent;
import org.exoplatform.ideall.client.event.help.ShowAboutDialogHandler;
import org.exoplatform.ideall.client.hotkeys.CustomizeHotKeysPanel;
import org.exoplatform.ideall.client.hotkeys.event.CustomizeHotKeysEvent;
import org.exoplatform.ideall.client.hotkeys.event.CustomizeHotKeysHandler;
import org.exoplatform.ideall.client.toolbar.customize.CustomizeToolbarForm;
import org.exoplatform.ideall.client.toolbar.customize.event.CustomizeToolbarEvent;
import org.exoplatform.ideall.client.toolbar.customize.event.CustomizeToolbarHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class HelpActionsComponent extends AbstractApplicationComponent implements
   ShowAboutDialogHandler, CustomizeToolbarHandler, CustomizeHotKeysHandler
{

   public HelpActionsComponent()
   {
      super(new HelpActionsComponentInitializer());
   }

   @Override
   protected void onInitializeComponent()
   {
      handlers.addHandler(ShowAboutDialogEvent.TYPE, this);      
   }

   @Override
   protected void registerHandlers()
   {
      handlers.addHandler(CustomizeToolbarEvent.TYPE, this);
      handlers.addHandler(CustomizeHotKeysEvent.TYPE, this);
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
