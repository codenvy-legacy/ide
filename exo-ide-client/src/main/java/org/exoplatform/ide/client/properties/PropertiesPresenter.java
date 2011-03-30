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

import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewDisplay;
import org.exoplatform.ide.client.framework.ui.gwt.ViewEx;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemPropertiesReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.event.ItemPropertiesSavedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemPropertiesSavedHandler;
import org.exoplatform.ide.client.properties.event.ShowPropertiesEvent;
import org.exoplatform.ide.client.properties.event.ShowPropertiesHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class PropertiesPresenter implements ItemPropertiesSavedHandler, ItemPropertiesReceivedHandler,
   EditorActiveFileChangedHandler, ShowPropertiesHandler, ViewClosedHandler
{

   public interface Display extends ViewDisplay
   {

      String ID = "ideFilePropertiesView";

      void showProperties(File file);

   }

   private Display display;

   private File file;

   public PropertiesPresenter(HandlerManager eventBus)
   {
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(ItemPropertiesSavedEvent.TYPE, this);
      eventBus.addHandler(ItemPropertiesReceivedEvent.TYPE, this);
      eventBus.addHandler(ShowPropertiesEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }

   @Override
   public void onShowProperties(ShowPropertiesEvent event)
   {
      if (event.isShowProperties() && display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView((ViewEx)display);
         display.showProperties(file);
         return;
      }

      if (!event.isShowProperties() && display != null)
      {
         IDE.getInstance().closeView(Display.ID);
      }
   }

   private void refreshProperties(File file)
   {
      if (this.file == null)
      {
         return;
      }

      if (!file.getHref().equals(this.file.getHref()))
      {
         return;
      }

      this.file = file;

      if (display != null)
      {
         display.showProperties(file);
      }
   }

   public void onItemPropertiesSaved(ItemPropertiesSavedEvent event)
   {
      if (event.getItem() instanceof File)
      {
         refreshProperties((File)event.getItem());
      }
   }

   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   {
      if (event.getItem() instanceof File)
      {
         refreshProperties((File)event.getItem());
      }
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      file = event.getFile();

      if (display != null)
      {
         display.showProperties(file);
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

}
