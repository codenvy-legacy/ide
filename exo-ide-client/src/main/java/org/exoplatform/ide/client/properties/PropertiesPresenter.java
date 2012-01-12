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
package org.exoplatform.ide.client.properties;

import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;

import com.google.gwt.core.client.GWT;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class PropertiesPresenter implements EditorActiveFileChangedHandler, ShowPropertiesHandler, ViewClosedHandler,
   FileSavedHandler
{

   public interface Display extends IsView
   {

      void showProperties(FileModel file);

   }

   private Display display;

   private FileModel file;

   public PropertiesPresenter()
   {
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(ShowPropertiesEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(FileSavedEvent.TYPE, this);

      IDE.getInstance().addControl(new ShowPropertiesControl(), Docking.TOOLBAR_RIGHT);
   }

   @Override
   public void onShowProperties(ShowPropertiesEvent event)
   {
      if (event.isShowProperties() && display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView((View)display);
         display.showProperties(file);
         return;
      }

      if (!event.isShowProperties() && display != null)
      {
         IDE.getInstance().closeView(display.asView().getId());
      }
   }

   private void refreshProperties(FileModel file)
   {
      if (this.file == null)
      {
         return;
      }

      if (!file.getId().equals(this.file.getId()))
      {
         return;
      }

      this.file = file;

      if (display != null)
      {
         display.showProperties(file);
      }
   }

   //TODO: need rework according new VFS
   //   public void onItemPropertiesSaved(ItemPropertiesSavedEvent event)
   //   {
   //      if (event.getItem() instanceof FileModel)
   //      {
   //         refreshProperties((FileModel)event.getItem());
   //      }
   //   }

   //   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   //   {
   //      if (event.getItem() instanceof File)
   //      {
   //         refreshProperties((File)event.getItem());
   //      }
   //   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      file = event.getFile();
      if (display != null)
      {
         if (file == null)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
         else
         {
            display.showProperties(file);
         }
      }
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.event.FileSavedHandler#onFileSaved(org.exoplatform.ide.client.framework.event.FileSavedEvent)
    */
   @Override
   public void onFileSaved(FileSavedEvent event)
   {
      refreshProperties(event.getFile());
   }

}
