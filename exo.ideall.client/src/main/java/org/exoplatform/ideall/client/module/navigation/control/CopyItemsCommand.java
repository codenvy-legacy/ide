/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.module.navigation.control;

import org.exoplatform.ideall.client.IDEImageBundle;
import org.exoplatform.ideall.client.browser.event.ItemsSelectedEvent;
import org.exoplatform.ideall.client.browser.event.ItemsSelectedHandler;
import org.exoplatform.ideall.client.event.edit.CopyItemsEvent;
import org.exoplatform.ideall.vfs.api.Item;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class CopyItemsCommand extends MultipleSelectionItemsCommand implements ItemsSelectedHandler
{

   public static final String ID = "Edit/Copy Item(s)";
   
   private Item selectedItem;

   private boolean copyReady = false;
   
   public CopyItemsCommand()
   {
      super(ID);
      setTitle("Copy Item(s)");
      setPrompt("Copy Selected Item(s)");
      setImages(IDEImageBundle.INSTANCE.copy(), IDEImageBundle.INSTANCE.copyDisabled());
      setEvent(new CopyItemsEvent());
   }

   @Override
   protected void onInitializeApplication()
   {
      setVisible(true);
      setEnabled(false);
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(ItemsSelectedEvent.TYPE, this);
      super.onRegisterHandlers();
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if(event.getSelectedItems().size() != 0)
      {
         selectedItem = event.getSelectedItems().get(0);
         copyReady = isItemsInSameFolder(event.getSelectedItems());
         updateEnabling();
      }
   }
   
   @Override
   protected void updateEnabling()
   {
      if (!browserSelected)
      {
         setEnabled(false);
         return;
      }

      if (selectedItem == null)
      {
         setEnabled(false);
         return;
      }
      
      if(copyReady)
      {
         setEnabled(true);
      }
      else
      {
         setEnabled(false);
      }
     
   }

}
