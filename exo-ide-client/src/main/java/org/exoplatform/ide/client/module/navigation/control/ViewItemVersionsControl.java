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
package org.exoplatform.ide.client.module.navigation.control;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.browser.BrowserPanel;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler;
import org.exoplatform.ide.client.module.navigation.event.versioning.ViewItemVersionsEvent;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.Item;
import org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedHandler;
import org.exoplatform.ide.client.panel.event.PanelSelectedEvent;
import org.exoplatform.ide.client.panel.event.PanelSelectedHandler;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 27, 2010 $
 *
 */
public class ViewItemVersionsControl extends IDEControl implements ItemsSelectedHandler, ItemDeletedHandler,
   PanelSelectedHandler, EntryPointChangedHandler
{

   private static final String ID = "View/Versions...";

   private final String TITLE = "Versions...";

   private final String PROMPT = "View Item Versions...";

   private boolean browserPanelSelected = true;

   private Item selectedItem;

   private String entryPoint;

   /**
    * @param id
    * @param eventBus
    */
   public ViewItemVersionsControl(HandlerManager eventBus)
   {
      super(ID, eventBus);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setEvent(new ViewItemVersionsEvent());
      setImages(IDEImageBundle.INSTANCE.viewVersions(), IDEImageBundle.INSTANCE.viewVersionsDisabled());
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(ItemsSelectedEvent.TYPE, this);
      addHandler(ItemDeletedEvent.TYPE, this);

      addHandler(PanelSelectedEvent.TYPE, this);
      addHandler(EntryPointChangedEvent.TYPE, this);
   }

   @Override
   protected void onInitializeApplication()
   {
      setVisible(true);
      updateEnabling();
   }

   private void updateEnabling()
   {
      if (!browserPanelSelected)
      {
         setEnabled(false);
         return;
      }

      if (selectedItem == null)
      {
         setEnabled(false);
         return;
      }

      if (selectedItem.getHref().equals(entryPoint))
      {
         setEnabled(false);
      }
      else
      {
         setEnabled(selectedItem instanceof File);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler#onEntryPointChanged(org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent)
    */
   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      entryPoint = event.getEntryPoint();
   }

   /**
    * @see org.exoplatform.ide.client.panel.event.PanelSelectedHandler#onPanelSelected(org.exoplatform.ide.client.panel.event.PanelSelectedEvent)
    */
   public void onPanelSelected(PanelSelectedEvent event)
   {
      browserPanelSelected = BrowserPanel.ID.equals(event.getPanelId()) ? true : false;
      updateEnabling();
   }

   /**
    * @see org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedHandler#onItemDeleted(org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedEvent)
    */
   public void onItemDeleted(ItemDeletedEvent event)
   {
      selectedItem = null;
      updateEnabling();
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent)
    */
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() != 1)
      {
         setEnabled(false);
         return;
      }

      selectedItem = event.getSelectedItems().get(0);
      updateEnabling();
   }

}
