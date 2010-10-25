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
import org.exoplatform.ide.client.event.edit.ItemsToPasteSelectedEvent;
import org.exoplatform.ide.client.event.edit.ItemsToPasteSelectedHandler;
import org.exoplatform.ide.client.event.edit.PasteItemsCompleteEvent;
import org.exoplatform.ide.client.event.edit.PasteItemsCompleteHandler;
import org.exoplatform.ide.client.module.navigation.event.edit.PasteItemsEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class PasteItemsCommand extends MultipleSelectionItemsCommand implements ItemsToPasteSelectedHandler,
   PasteItemsCompleteHandler, ItemsSelectedHandler
{
   public static final String ID = "Edit/Paste Item(s)";

   private boolean pastePrepared = false;

   public PasteItemsCommand()
   {
      super(ID);
      setTitle("Paste Item(s)");
      setPrompt("Paste Selected Item(s)");
      setImages(IDEImageBundle.INSTANCE.paste(), IDEImageBundle.INSTANCE.pasteDisabled());
      setEvent(new PasteItemsEvent());
   }
   
   /**
    * @see org.exoplatform.ide.client.module.navigation.control.MultipleSelectionItemsCommand#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   @Override
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(ItemsToPasteSelectedEvent.TYPE, this);
      eventBus.addHandler(PasteItemsCompleteEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      super.initialize(eventBus);
   }

   public void onItemsToPasteSelected(ItemsToPasteSelectedEvent event)
   {
      pastePrepared = true;
      setEnabled(true);
   }

   public void onPasteItemsComlete(PasteItemsCompleteEvent event)
   {
      setEnabled(false);
      pastePrepared = false;
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() == 1)
      {
         updateEnabling();
      }
      else
      {
         setEnabled(false);
      }

   }

   @Override
   protected void updateEnabling()
   {
      if (browserSelected)
      {
         if (pastePrepared)
         {
            setEnabled(true);
         }
         else
         {
            setEnabled(false);
         }

      }
      else
      {
         setEnabled(false);
      }
   }

}
