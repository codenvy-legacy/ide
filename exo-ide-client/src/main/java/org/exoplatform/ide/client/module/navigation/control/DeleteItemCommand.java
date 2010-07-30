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
package org.exoplatform.ide.client.module.navigation.control;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.module.navigation.event.DeleteItemEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler;
import org.exoplatform.ide.client.module.vfs.api.Item;
import org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedHandler;
import org.exoplatform.ide.client.panel.event.PanelSelectedEvent;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class DeleteItemCommand extends MultipleSelectionItemsCommand implements ItemsSelectedHandler,
   ItemDeletedHandler
{

   private static final String ID = "File/Delete...";

   private static final String TITLE = "Delete...";

   private static final String PROMPT = "Delete Item(s)...";

   private Item selectedItem;

   public DeleteItemCommand(HandlerManager eventBus)
   {
      super(ID, eventBus);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(IDEImageBundle.INSTANCE.delete(), IDEImageBundle.INSTANCE.deleteDisabled());
      setEvent(new DeleteItemEvent());
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(ItemsSelectedEvent.TYPE, this);
      addHandler(ItemDeletedEvent.TYPE, this);

      super.onRegisterHandlers();
   }

   @Override
   protected void onInitializeApplication()
   {
      setVisible(true);
      updateEnabling();
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {

      if (!isItemsInSameFolder(event.getSelectedItems()))
      {
         setEnabled(false);
         return;
      }
      if (event.getSelectedItems().size() != 0)
      {
         selectedItem = event.getSelectedItems().get(0);
         updateEnabling();
      }
   }

   public void onItemDeleted(ItemDeletedEvent event)
   {
      selectedItem = null;
      updateEnabling();
   }

   @Override
   public void onPanelSelected(PanelSelectedEvent event)
   {
      super.onPanelSelected(event);
      updateEnabling();
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

      setEnabled(true);

   }

}
