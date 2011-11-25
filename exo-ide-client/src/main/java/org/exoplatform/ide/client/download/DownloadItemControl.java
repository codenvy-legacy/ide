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
package org.exoplatform.ide.client.download;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"administrators", "developers"})
public class DownloadItemControl extends SimpleControl implements IDEControl, ItemsSelectedHandler
{

   enum Type {
      FILE, ZIP
   }

   private static final String ID_FILE = "File/Download File...";

   private static final String TITLE_FILE = IDE.IDE_LOCALIZATION_CONSTANT.downloadTitleControl();

   private static final String PROMPT_FILE = IDE.IDE_LOCALIZATION_CONSTANT.downloadPromptControl();

   private static final String ID_ZIP = "File/Download Zipped Folder...";

   private static final String TITLE_ZIP = IDE.IDE_LOCALIZATION_CONSTANT.downloadZippedFolderControl();

   private static final String PROMPT_ZIP = IDE.IDE_LOCALIZATION_CONSTANT.downloadZippedFolderControl();

   private boolean downloadZip;

   /**
    * 
    */
   public DownloadItemControl(boolean downloadZip)
   {
      super(downloadZip ? ID_ZIP : ID_FILE);

      this.downloadZip = downloadZip;

      if (downloadZip)
      {
         setTitle(TITLE_ZIP);
         setPrompt(PROMPT_ZIP);
         setImages(IDEImageBundle.INSTANCE.downloadFolder(), IDEImageBundle.INSTANCE.downloadFolderDisabled());
      }
      else
      {
         setTitle(TITLE_FILE);
         setPrompt(PROMPT_FILE);
         setImages(IDEImageBundle.INSTANCE.downloadFile(), IDEImageBundle.INSTANCE.downloadFileDisabled());
      }
      setEvent(new DownloadItemEvent());
   }

   /**
    * @see org.exoplatform.ide.client.navigation.control.MultipleSelectionItemsCommand#initialize()
    */
   @Override
   public void initialize()
   {
      setVisible(true);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   private boolean isApplicableFor(Item item)
   {
      if (downloadZip && (item instanceof FolderModel || item instanceof ProjectModel))
      {
         return true;
      }
      else if (!downloadZip && item instanceof FileModel)
      {
         return true;
      }

      return false;
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() == 1 && isApplicableFor(event.getSelectedItems().get(0)))
      {
         setEnabled(true);
      }
      else
      {
         setEnabled(false);
      }
   }

}
