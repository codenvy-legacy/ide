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
package org.exoplatform.ide.client.module.navigation.control.newitem;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.component.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.browser.BrowserPanel;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateFolderEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler;
import org.exoplatform.ide.client.panel.event.PanelDeselectedEvent;
import org.exoplatform.ide.client.panel.event.PanelDeselectedHandler;
import org.exoplatform.ide.client.panel.event.PanelSelectedEvent;
import org.exoplatform.ide.client.panel.event.PanelSelectedHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CreateFolderControl extends SimpleControl implements ItemsSelectedHandler, PanelSelectedHandler, PanelDeselectedHandler
{

   private boolean folderItemSelected = true;

   private boolean browserPanelSelected = true;

   public final static String ID = "File/New/Create Folder...";

   public CreateFolderControl(HandlerManager eventBus)
   {
      super(ID);
      setTitle("Folder...");
      setPrompt("Create Folder...");
      setDelimiterBefore(true);
      setImages(IDEImageBundle.INSTANCE.newFolder(), IDEImageBundle.INSTANCE.newFolderDisabled());
      setEvent(new CreateFolderEvent());

      eventBus.addHandler(PanelSelectedEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(PanelDeselectedEvent.TYPE, this);
   }

   private void updateEnabling()
   {
      if (!browserPanelSelected)
      {
         setEnabled(false);
         return;
      }
      if (folderItemSelected)
      {
         setEnabled(true);
      }
      else
      {
         setEnabled(false);
      }
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() != 1)
      {
         folderItemSelected = false;
         updateEnabling();
         return;
      }

      folderItemSelected = true;
      updateEnabling();

   }

   public void onPanelSelected(PanelSelectedEvent event)
   {
      if (BrowserPanel.ID.equals(event.getPanelId())) {
         browserPanelSelected = true;
         updateEnabling();
      }
   }

   /**
    * @see org.exoplatform.ide.client.panel.event.PanelDeselectedHandler#onPanelDeselected(org.exoplatform.ide.client.panel.event.PanelDeselectedEvent)
    */
   public void onPanelDeselected(PanelDeselectedEvent event)
   {
      if (BrowserPanel.ID.equals(event.getPanelId())) {
         browserPanelSelected = false;
         updateEnabling();
      }
   }

}
