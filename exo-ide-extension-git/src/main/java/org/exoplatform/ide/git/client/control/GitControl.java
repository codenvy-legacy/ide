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
package org.exoplatform.ide.git.client.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedEvent;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * The common control for working with Git.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 15, 2011 10:06:58 AM anya $
 *
 */
public abstract class GitControl extends SimpleControl implements IDEControl, ItemsSelectedHandler, VfsChangedHandler,
   FolderRefreshedHandler
{

   enum EnableState {
      BEFORE_INIT, AFTER_INIT;
   }

   /**
    * Current workspace's href.
    */
   private VirtualFileSystemInfo workspace;

   /**
    * Variable, which indicated, when control must be enabled:
    * before initializing the git repository or after.
    * 
    * IDE-1252
    */
   private EnableState enableState = EnableState.AFTER_INIT;

   /**
    * @param id control's id
    */
   public GitControl(String id)
   {
      super(id);
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() != 1)
      {
         setEnabled(false);
         return;
      }

      final Item item = event.getSelectedItems().get(0);

      if (isWorkspaceSelected(item.getId()) || !isProjectSelected((ItemContext)item))
      {
         setEnabled(false);
         return;
      }

      updateControlState(((ItemContext)item).getProject());
   }

   protected boolean isWorkspaceSelected(String id)
   {
      return (workspace != null && workspace.getRoot().getId() != null && id != null && id.equals(workspace.getRoot()
         .getId()));
   }

   protected boolean isProjectSelected(ItemContext item)
   {
      return item.getProject() != null && item.getProject().getId() != null;
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.addHandler(FolderRefreshedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      setVisible(true);
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.workspace = event.getVfsInfo();
      if (workspace != null)
      {
         setVisible(true);
      }
      else
      {
         setVisible(false);
      }
   }

   /**
    * Set the state, where control must be enabled:
    * before initializing repository or after.
    * <p/>
    * IDE-1252
    * 
    * @param enableState
    */
   public void setEnableState(EnableState enableState)
   {
      this.enableState = enableState;
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler#onFolderRefreshed(org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedEvent)
    */
   @Override
   public void onFolderRefreshed(FolderRefreshedEvent event)
   {
      updateControlState(((ItemContext)event.getFolder()).getProject());
   }

   protected void updateControlState(ProjectModel project)
   {
      if (project == null || project.getChildren() == null)
      {
         return;
      }

      List<Item> itemList = project.getChildren().getItems();
      for (Item child : itemList)
      {
         if (".git".equals(child.getName()))
         {
            if (enableState == EnableState.AFTER_INIT)
               setEnabled(true);
            else
               setEnabled(false);
            return;
         }
      }

      if (enableState == EnableState.AFTER_INIT)
         setEnabled(false);
      else
         setEnabled(true);
   }
}
