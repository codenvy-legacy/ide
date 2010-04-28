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
package org.exoplatform.ideall.client.common.command.file.download;

import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.application.component.IDECommand;
import org.exoplatform.ideall.client.browser.event.ItemsSelectedEvent;
import org.exoplatform.ideall.client.browser.event.ItemsSelectedHandler;
import org.exoplatform.ideall.client.common.command.MultipleSelectionItemsCommand;
import org.exoplatform.ideall.client.event.file.DownloadZippedFolderEvent;
import org.exoplatform.ideall.client.model.vfs.api.Folder;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class DownloadZippedFolderCommand extends MultipleSelectionItemsCommand implements ItemsSelectedHandler
{

   private final static String ID = "File/Download Zipped Folder...";

   private boolean oneItemSelected = true;

   public DownloadZippedFolderCommand()
   {
      super(ID);
      setTitle("Download Zipped Folder...");
      setPrompt("Download Zipped Folder...");
      setIcon(Images.MainMenu.DOWNLOAD_FOLDER);
      setEvent(new DownloadZippedFolderEvent());
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
      super.onRegisterHandlers();
      addHandler(ItemsSelectedEvent.TYPE, this);
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() != 1 || !(event.getSelectedItems().get(0) instanceof Folder))
      {
         oneItemSelected = false;
         updateEnabling();
      }
      else
      {
         oneItemSelected = true;
         updateEnabling();
      }
   }

   @Override
   protected void updateEnabling()
   {
      if (browserSelected && oneItemSelected)
      {
         setEnabled(true);
      }
      else
      {
         setEnabled(false);
      }
   }

}
