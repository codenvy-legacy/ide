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
package org.exoplatform.ideall.client.common.command.file;

import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.application.component.IDECommand;
import org.exoplatform.ideall.client.browser.event.BrowserPanelDeselectedEvent;
import org.exoplatform.ideall.client.browser.event.BrowserPanelDeselectedHandler;
import org.exoplatform.ideall.client.browser.event.BrowserPanelSelectedEvent;
import org.exoplatform.ideall.client.browser.event.BrowserPanelSelectedHandler;
import org.exoplatform.ideall.client.event.file.CreateFolderEvent;
import org.exoplatform.ideall.client.event.file.SelectedItemsEvent;
import org.exoplatform.ideall.client.event.file.SelectedItemsHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CreateNewFolderCommand extends IDECommand implements BrowserPanelSelectedHandler,
   BrowserPanelDeselectedHandler, SelectedItemsHandler
{

   private boolean browserPanelSelected = true;

   private final static String ID = "File/New/Folder...";

   public CreateNewFolderCommand()
   {
      super(ID);
      setTitle("Folder...");
      setPrompt("Create Folder...");
      setIcon(Images.MainMenu.NEW_FOLDER);
      setEvent(new CreateFolderEvent());
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(BrowserPanelSelectedEvent.TYPE, this);
      addHandler(BrowserPanelDeselectedEvent.TYPE, this);
      addHandler(SelectedItemsEvent.TYPE, this);
   }

   @Override
   protected void onInitializeApplication()
   {
      setVisible(true);
      updateEnabling();
   }

   private void updateEnabling()
   {
      if (browserPanelSelected)
      {
         setEnabled(true);
      }
      else
      {
         setEnabled(false);
      }
   }

   public void onBrowserPanelSelected(BrowserPanelSelectedEvent event)
   {
      browserPanelSelected = true;
      updateEnabling();
   }

   public void onBrowserPanelDeselected(BrowserPanelDeselectedEvent event)
   {
      browserPanelSelected = false;
      updateEnabling();
   }

   public void onItemsSelected(SelectedItemsEvent event)
   {
      if (event.getSelectedItems().size() != 1)
      {
         browserPanelSelected = false;
         updateEnabling();
      }
      else
      {
         browserPanelSelected = true;
         updateEnabling();
      }
   }

}
