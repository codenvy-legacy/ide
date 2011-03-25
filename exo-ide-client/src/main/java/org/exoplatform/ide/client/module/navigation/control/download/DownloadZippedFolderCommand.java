/*
 * Copyright (C) 2010 eXo Platform SAS.
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
 */
package org.exoplatform.ide.client.module.navigation.control.download;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.module.navigation.control.MultipleSelectionItemsCommand;
import org.exoplatform.ide.client.navigation.event.DownloadZippedFolderEvent;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"administrators", "developers"})
public class DownloadZippedFolderCommand extends MultipleSelectionItemsCommand implements ItemsSelectedHandler
{

   private final static String ID = "File/Download Zipped Folder...";

   private boolean oneItemSelected = true;

   public DownloadZippedFolderCommand()
   {
      super(ID);
      setTitle("Download Zipped Folder...");
      setPrompt("Download Zipped Folder...");
      setImages(IDEImageBundle.INSTANCE.downloadFolder(), IDEImageBundle.INSTANCE.downloadFolderDisabled());
      setEvent(new DownloadZippedFolderEvent());
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
