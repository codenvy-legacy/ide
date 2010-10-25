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
package org.exoplatform.ide.client.module.navigation.control;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.module.navigation.event.edit.CutItemsEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.vfs.Item;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
@RolesAllowed({"administrators", "developers"})
public class CutItemsCommand extends MultipleSelectionItemsCommand implements ItemsSelectedHandler
{

   private static final String ID = "Edit/Cut Item(s)";

   private boolean cutReady = false;

   private Item selectedItem;

   public CutItemsCommand()
   {
      super(ID);
      setTitle("Cut Item(s)");
      setPrompt("Cut Selected Item(s)");
      setDelimiterBefore(true);
      setImages(IDEImageBundle.INSTANCE.cut(), IDEImageBundle.INSTANCE.cutDisabled());
      setEvent(new CutItemsEvent());
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.control.MultipleSelectionItemsCommand#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   @Override
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      super.initialize(eventBus);
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

      if (cutReady)
      {
         setEnabled(true);
      }
      else
      {
         setEnabled(false);
      }
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() != 0)
      {
         selectedItem = event.getSelectedItems().get(0);
         cutReady = isItemsInSameFolder(event.getSelectedItems());
         updateEnabling();
      }
      else
      {
         setEnabled(false);
         return;
      }
   }

}
