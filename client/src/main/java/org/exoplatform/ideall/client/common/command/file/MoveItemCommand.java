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
import org.exoplatform.ideall.client.event.file.MoveItemEvent;
import org.exoplatform.ideall.client.event.file.SelectedItemsEvent;
import org.exoplatform.ideall.client.event.file.SelectedItemsHandler;
import org.exoplatform.ideall.client.model.vfs.api.Item;
import org.exoplatform.ideall.client.model.vfs.api.Workspace;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemDeletedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemDeletedHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class MoveItemCommand extends IDECommand implements SelectedItemsHandler, ItemDeletedHandler,
   BrowserPanelSelectedHandler, BrowserPanelDeselectedHandler
{

   private static final String ID = "File/Move File\\Folder...";

   private boolean browserPanelSelected = true;

   private Item selectedItem;

   public MoveItemCommand()
   {
      super(ID);
      setTitle("Move...");
      setPrompt("Move Item...");
      setIcon(Images.MainMenu.MOVE);
      setEvent(new MoveItemEvent());
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(SelectedItemsEvent.TYPE, this);
      addHandler(ItemDeletedEvent.TYPE, this);

      addHandler(BrowserPanelSelectedEvent.TYPE, this);
      addHandler(BrowserPanelDeselectedEvent.TYPE, this);
   }

   @Override
   protected void onInitializeApplication()
   {
      setVisible(true);
      updateEnabling();
   }

   public void onItemsSelected(SelectedItemsEvent event)
   {
      if (context.getSelectedItems().size() != 1)
      {
         setEnabled(false);
         return;
      }

      // setEnabled(true);
      selectedItem = event.getSelectedItems().get(0);
      updateEnabling();
   }

   public void onItemDeleted(ItemDeletedEvent event)
   {
      selectedItem = null;
      updateEnabling();
   }

   private void updateEnabling()
   {
      if (!browserPanelSelected)
      {
         setEnabled(false);
         return;
      }

      if (selectedItem == null)
      {
         setEnabled(false);
         return;
      }

      if (selectedItem instanceof Workspace)
      {
         setEnabled(false);
      }
      else
      {
         setEnabled(true);
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

}
