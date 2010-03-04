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
import org.exoplatform.ideall.client.application.component.SimpleCommand;
import org.exoplatform.ideall.client.event.file.ItemSelectedEvent;
import org.exoplatform.ideall.client.event.file.ItemSelectedHandler;
import org.exoplatform.ideall.client.event.file.OpenFileWithEvent;
import org.exoplatform.ideall.client.model.File;



/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class OpenFileWithCommand extends SimpleCommand implements ItemSelectedHandler
{
   public static final String ID = "File/Open With...";

   public static final String TITLE = "Open With...";
   
   public OpenFileWithCommand()
   {
      super(ID, TITLE, Images.MainMenu.OPENWITH, new OpenFileWithEvent());
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
      addHandler(ItemSelectedEvent.TYPE, this);
   }

   public void onItemSelected(ItemSelectedEvent event)
   {
      if (!(event.getSelectedItem() instanceof File))
      {
         setEnabled(false);
         return;
      }
      setEnabled(true);
   }
}

