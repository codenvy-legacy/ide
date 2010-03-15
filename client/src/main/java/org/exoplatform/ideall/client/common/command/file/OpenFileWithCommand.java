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
package org.exoplatform.ideall.client.common.command.file;

import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.application.component.IDECommand;
import org.exoplatform.ideall.client.event.file.SelectedItemsEvent;
import org.exoplatform.ideall.client.event.file.SelectedItemsHandler;
import org.exoplatform.ideall.client.event.file.OpenFileWithEvent;
import org.exoplatform.ideall.client.model.File;



/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class OpenFileWithCommand extends IDECommand implements SelectedItemsHandler
{
   public static final String ID = "File/Open File With...";

   public OpenFileWithCommand()
   {
      super(ID);
      setTitle("Open With...");
      setPrompt("Open File With...");
      setIcon(Images.MainMenu.OPENWITH);
      setEvent(new OpenFileWithEvent());
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
      if (event.getSelectedItems().size() !=1 || !(event.getSelectedItems().get(0) instanceof File))
      {
         setEnabled(false);
         return;
      }
      setEnabled(true);
   }
}

