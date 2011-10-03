/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.git.client;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * Used as base for the most presenters, which work with Git.
 * Most of the presenters have to store selected item in browser tree
 * and to get the location the working directory. 
 * If the working directory not found - the reaction is common, the actions on success differs,
 * so {@link #onWorkDirReceived()} will have different implementations.  
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 20, 2011 2:08:46 PM anya $
 *
 */
public abstract class GitPresenter implements ItemsSelectedHandler, VfsChangedHandler
{
   /**
    * Events handler.
    */
   protected HandlerManager eventBus;

   /**
    * Selected items in browser tree.
    */
   protected List<Item> selectedItems;

   /**
    * Current virtual file system.
    */
   protected VirtualFileSystemInfo vfs;

   /**
    * @param eventBus
    */
   public GitPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(VfsChangedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      this.selectedItems = event.getSelectedItems();
   }

   protected boolean makeSelectionCheck()
   {
      if (selectedItems == null || selectedItems.size() <= 0)
      {
         Dialogs.getInstance().showInfo(GitExtension.MESSAGES.selectedItemsFail());
         return false;
      }

      if (!(selectedItems.get(0) instanceof ItemContext) || ((ItemContext)selectedItems.get(0)).getProject() == null)
      {
         //TODO change message:
         Dialogs.getInstance().showInfo("Project is not selected.");
         return false;
      }

      if (selectedItems.get(0).getPath().isEmpty() || selectedItems.get(0).getPath().equals("/"))
      {
         Dialogs.getInstance().showInfo(GitExtension.MESSAGES.selectedWorkace());
         return false;
      }

      return true;
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.vfs = event.getVfsInfo();
   }
}
