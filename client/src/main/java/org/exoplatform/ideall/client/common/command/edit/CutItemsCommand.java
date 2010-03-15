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
package org.exoplatform.ideall.client.common.command.edit;

import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.common.command.MultipleSelectionItemsCommand;
import org.exoplatform.ideall.client.event.edit.CutItemsEvent;
import org.exoplatform.ideall.client.event.file.SelectedItemsEvent;
import org.exoplatform.ideall.client.event.file.SelectedItemsHandler;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class CutItemsCommand extends MultipleSelectionItemsCommand implements SelectedItemsHandler
{

   private static final String ID = "Edit/Cut Item(s)";

   public CutItemsCommand()
   {
      super(ID);
      setTitle("Cut Item(s)");
      setPrompt("Cut Selected Item(s)");
      setIcon(Images.Edit.CUT_FILE);
      setEvent(new CutItemsEvent());
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
      addHandler(SelectedItemsEvent.TYPE, this);
   }

   public void onItemsSelected(SelectedItemsEvent event)
   {
      if (!isItemsInSameFolder(event.getSelectedItems()))
      {
         setEnabled(false);
         return;
      }

      if (isSelectedWorkspace(event.getSelectedItems()))
      {
         setEnabled(false);
         return;
      }

      setEnabled(true);
   }
}
