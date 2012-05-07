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
package org.exoplatform.ide.client.operation.deleteitem;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.NavigatorDisplay;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"administrators", "developers"})
public class DeleteItemControl extends SimpleControl implements IDEControl, ItemsSelectedHandler, VfsChangedHandler,
   ViewActivatedHandler
{

   private static final String ID = "File/Delete...";

   private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.deleteItemsTitleControl();

   private static final String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.deleteItemsPromptControl();

   /**
    * Current workspace's href.
    */
   private VirtualFileSystemInfo vfsInfo;

   /**
    * Current active view.
    */
   private View activeView;

   private boolean navigatorSelected;

   private List<Item> selectedItems;

   /**
    * 
    */
   public DeleteItemControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(IDEImageBundle.INSTANCE.delete(), IDEImageBundle.INSTANCE.deleteDisabled());
      setEvent(new DeleteItemEvent());
   }

   /**
    * @see org.exoplatform.ide.client.navigation.control.MultipleSelectionItemsControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ViewActivatedEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      vfsInfo = event.getVfsInfo();
      updateState();
   }

   @Override
   public void onViewActivated(ViewActivatedEvent event)
   {
      activeView = event.getView();
      navigatorSelected = activeView instanceof NavigatorDisplay || activeView instanceof ProjectExplorerDisplay;
      updateState();
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      this.selectedItems = event.getSelectedItems();
      updateState();
   }

   /**
    * Update control's state.
    */
   protected void updateState()
   {
      if (vfsInfo == null)
      {
         setVisible(false);
         return;
      }
      setVisible(true);

      setShowInContextMenu(navigatorSelected);

      if (selectedItems == null || selectedItems.size() != 1)
      {
         setEnabled(false);
         return;
      }

      if (selectedItems.get(0).getId().equals(vfsInfo.getRoot().getId()))
      {
         setEnabled(false);
         return;
      }

      if (activeView instanceof ProjectExplorerDisplay && selectedItems.get(0) instanceof ProjectModel)
      {
         setEnabled(false);
         return;
      }

      setEnabled(navigatorSelected);
   }

}
