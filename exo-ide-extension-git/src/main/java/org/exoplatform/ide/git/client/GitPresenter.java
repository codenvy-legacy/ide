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

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponse;
import org.exoplatform.ide.vfs.shared.Item;

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
public abstract class GitPresenter implements ItemsSelectedHandler
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
    * Working directory of the Git repository.
    */
   protected String workDir;

   /**
    * @param eventBus
    */
   public GitPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      this.selectedItems = event.getSelectedItems();
   }

   public void getWorkDir()
   {
      if (selectedItems == null || selectedItems.size() <= 0)
      {
         Dialogs.getInstance().showInfo(GitExtension.MESSAGES.selectedItemsFail());
         return;
      }

      //First get the working directory of the repository if exists:
      GitClientService.getInstance().getWorkDir(selectedItems.get(0).getId(),
         new AsyncRequestCallback<WorkDirResponse>()
         {
            @Override
            protected void onSuccess(WorkDirResponse result)
            {
               workDir = result.getWorkDir();
               workDir = (workDir.endsWith("/.git")) ? workDir.substring(0, workDir.lastIndexOf("/.git")) : workDir;
               onWorkDirReceived();
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               Dialogs.getInstance().showError(GitExtension.MESSAGES.notGitRepository());
            }
         });
   }

   /**
    * Perform actions, when location of the working directory is successfully received.
    */
   public abstract void onWorkDirReceived();
}
