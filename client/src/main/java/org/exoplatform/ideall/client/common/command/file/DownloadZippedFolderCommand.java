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
import org.exoplatform.ideall.client.application.component.SimpleCommand;
import org.exoplatform.ideall.client.event.file.DownloadZippedFolderEvent;
import org.exoplatform.ideall.client.event.file.ItemSelectedEvent;
import org.exoplatform.ideall.client.event.file.ItemSelectedHandler;
import org.exoplatform.ideall.client.model.Folder;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class DownloadZippedFolderCommand extends SimpleCommand implements ItemSelectedHandler
{

   private final static String ID = "File/Download Zipped Folder";

   private final static String TITLE = "Download Zipped Folder";

   private Folder selectedFolder;

   public DownloadZippedFolderCommand()
   {
      super(ID, TITLE, Images.MainMenu.DOWNLOAD_FOLDER, new DownloadZippedFolderEvent());
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
      if (!(event.getSelectedItem() instanceof Folder))
      {
         setEnabled(false);
         return;
      }

      selectedFolder = (Folder)event.getSelectedItem();
      setEnabled(true);
   }

}
