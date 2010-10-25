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
package org.exoplatform.ide.client.module.navigation.control.upload;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.ui.client.component.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.browser.BrowserPanel;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler;
import org.exoplatform.ide.client.module.navigation.event.upload.UploadFileEvent;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.panel.event.PanelDeselectedEvent;
import org.exoplatform.ide.client.panel.event.PanelDeselectedHandler;
import org.exoplatform.ide.client.panel.event.PanelSelectedEvent;
import org.exoplatform.ide.client.panel.event.PanelSelectedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"administrators", "developers"})
public class UploadFileCommand extends SimpleControl implements IDEControl, ItemsSelectedHandler, PanelSelectedHandler,
   EntryPointChangedHandler, PanelDeselectedHandler
{

   private final static String ID = "File/Upload File...";

   private final static String TITLE = "Upload...";

   private final static String PROMPT = "Upload File...";

   private boolean browserPanelSelected = true;

   private List<Item> selectedItems = new ArrayList<Item>();

   public UploadFileCommand()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setDelimiterBefore(true);
      //setIcon(Images.MainMenu.UPLOAD);
      setImages(IDEImageBundle.INSTANCE.upload(), IDEImageBundle.INSTANCE.uploadDisabled());
      setEvent(new UploadFileEvent(false));
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(PanelSelectedEvent.TYPE, this);
      eventBus.addHandler(EntryPointChangedEvent.TYPE, this);
      eventBus.addHandler(PanelDeselectedEvent.TYPE, this);
   }
   
   private void updateEnabling()
   {
      if (browserPanelSelected)
      {
         if (selectedItems.size() == 1)
         {
            setEnabled(true);
         }
         else
         {
            setEnabled(false);
         }
      }
      else
      {
         setEnabled(false);
      }
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
      updateEnabling();
   }

   public void onPanelSelected(PanelSelectedEvent event)
   {
      if (BrowserPanel.ID.equals(event.getPanelId())) {
         browserPanelSelected = true;
         updateEnabling();
      }
   }

   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      if (event.getEntryPoint() != null)
      {
         setVisible(true);
      }
      else
      {
         setVisible(false);
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
