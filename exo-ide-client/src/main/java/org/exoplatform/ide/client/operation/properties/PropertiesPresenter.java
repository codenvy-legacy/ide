/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.operation.properties;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.vfs.api.File;
import org.exoplatform.ide.client.framework.module.vfs.api.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ide.client.framework.module.vfs.api.event.ItemPropertiesReceivedHandler;
import org.exoplatform.ide.client.framework.module.vfs.api.event.ItemPropertiesSavedEvent;
import org.exoplatform.ide.client.framework.module.vfs.api.event.ItemPropertiesSavedHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class PropertiesPresenter implements ItemPropertiesSavedHandler, ItemPropertiesReceivedHandler,
   EditorActiveFileChangedHandler
{

   public interface Display
   {
      void refreshProperties(File file);
   }

   private Display display;

   private Handlers handlers;

   private File activeFile;

   public PropertiesPresenter(HandlerManager eventBus)
   {
      handlers = new Handlers(eventBus);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      display = d;
      handlers.addHandler(ItemPropertiesSavedEvent.TYPE, this);
      handlers.addHandler(ItemPropertiesReceivedEvent.TYPE, this);
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   private void refreshProperties(File file)
   {
      refresh.cancel();
      if (file == null)
      {
         return;
      }
      activeFile = file;
      
      refresh.schedule(200);
   }
   
   Timer refresh = new Timer()
   {
      
      @Override
      public void run()
      {
         display.refreshProperties(activeFile);
      }
   };

   public void onItemPropertiesSaved(ItemPropertiesSavedEvent event)
   {
      if (event.getItem() instanceof File)
         refreshProperties((File)event.getItem());
   }

   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   {
      if (event.getItem() instanceof File)
         refreshProperties((File)event.getItem());
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      refreshProperties(event.getFile());
   }

}
