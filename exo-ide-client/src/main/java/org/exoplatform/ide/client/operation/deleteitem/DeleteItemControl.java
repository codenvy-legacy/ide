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
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
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
   ViewActivatedHandler, ViewVisibilityChangedHandler, ProjectOpenedHandler, ProjectClosedHandler
{

   private static final String ID = "File/Delete...";

   private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.deleteItemsTitleControl();

   private static final String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.deleteItemsPromptControl();

   /**
    * Current workspace's href.
    */
   private VirtualFileSystemInfo vfsInfo = null;

   /**
    * Current active view.
    */
   private View activeView;

   private boolean isBrowserPanelVisible;

   private boolean isProjectExplorerVisible;

   private List<Item> selectedItems;

   private boolean isProjectOpened = false;

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
      setShowInContextMenu(true);
   }

   /**
    * @see org.exoplatform.ide.client.navigation.control.MultipleSelectionItemsControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ViewActivatedEvent.TYPE, this);
      IDE.addHandler(ViewVisibilityChangedEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
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
      updateState();
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent)
    */
   @Override
   public void onViewVisibilityChanged(ViewVisibilityChangedEvent event)
   {
      View view = event.getView();

      if (view instanceof NavigatorDisplay || view instanceof ProjectExplorerDisplay)
      {
         isBrowserPanelVisible = view.isViewVisible();

         if (view instanceof ProjectExplorerDisplay)
         {
            isProjectExplorerVisible = view.isViewVisible();
         }
      }

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
    * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
    */
   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      isProjectOpened = false;
      updateState();
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
    */
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      isProjectOpened = true;
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

      if (!isProjectOpened && isProjectExplorerVisible)
      {
         setVisible(false);
         return;
      }

      if (!isBrowserPanelVisible)
      {
         setVisible(false);
         return;
      }

      setVisible(true);

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

      boolean browserPanelSelected =
         (activeView instanceof NavigatorDisplay || activeView instanceof ProjectExplorerDisplay);
      setEnabled(browserPanelSelected);
      setShowInContextMenu(browserPanelSelected);
   }

}
