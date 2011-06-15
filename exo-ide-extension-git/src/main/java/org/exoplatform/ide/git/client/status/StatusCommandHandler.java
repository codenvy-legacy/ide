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
package org.exoplatform.ide.git.client.status;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconEvent;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedEvent;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.git.client.GitClientBundle;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.marshaller.StatusResponse;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponse;
import org.exoplatform.ide.git.shared.GitFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler to process actions with displaying the status of the Git work tree.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 28, 2011 3:58:20 PM anya $
 *
 */
public class StatusCommandHandler implements ShowWorkTreeStatusHandler, ItemsSelectedHandler, FolderRefreshedHandler
{
   /**
    * Events handler.
    */
   private HandlerManager eventBus;

   /**
    * Selected items in the browser tree.
    */
   private List<Item> selectedItems;

   /**
    * @param eventBus event handler
    */
   public StatusCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(ShowWorkTreeStatusEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(FolderRefreshedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.git.client.status.ShowWorkTreeStatusHandler#onShowWorkTreeStatus(org.exoplatform.ide.git.client.status.ShowWorkTreeStatusEvent)
    */
   @Override
   public void onShowWorkTreeStatus(ShowWorkTreeStatusEvent event)
   {
      if (selectedItems == null || selectedItems.size() != 1)
      {
         Dialogs.getInstance().showInfo("Please, select one folder in browser tree.");
      }
      getGitWorkTreeLocation(selectedItems.get(0));
   }

   /**
    * Calls the find root of the work tree direcory.
    * 
    * @param item item from which to start search (may be contained in work tree)
    */
   private void getGitWorkTreeLocation(final Item item)
   {
      GitClientService.getInstance().getWorkDir(item.getHref(), new AsyncRequestCallback<WorkDirResponse>()
      {

         @Override
         protected void onSuccess(WorkDirResponse result)
         {
            getStatusText(result.getWorkDir(), item);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            Dialogs.getInstance().showInfo(GitExtension.MESSAGES.notGitRepository());
         }
      });
   }

   /**
    * Get the status for Git work tree and display it on success.
    * 
    * @param workTree the location of the ".git" folder
    * @param item item in work tree
    */
   private void getStatusText(String workTree, Item item)
   {
      if (workTree == null)
         return;
      //Get the location of the parent for ".git" directory:
      workTree = workTree.endsWith("/.git") ? workTree.substring(0, workTree.lastIndexOf("/.git")) : workTree;
      String[] fileFilter = null;
      if (item instanceof Folder)
      {
         //Remove last "/" from path:
         String href =
            item.getHref().endsWith("/") ? item.getHref().substring(0, item.getHref().length() - 1) : item.getHref();
         //Check selected item in workspace tree is not the root of the Git repository tree:
         if (!workTree.equals(href))
         {
            //Add filter to display status for the selected folder: 
            fileFilter = new String[]{href.replace(workTree + "/", "")};
         }
      }

      GitClientService.getInstance().statusText(workTree, false, fileFilter, new AsyncRequestCallback<StatusResponse>()
      {

         @Override
         protected void onSuccess(StatusResponse result)
         {
            if (result.getWorkTreeStatus() == null)
               return;
            String status = result.getWorkTreeStatus();
            status = status.replace("\n", "<br>");
            eventBus.fireEvent(new OutputEvent(status, OutputMessage.Type.INFO));
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMessage = (exception.getMessage() != null) ? exception.getMessage() :GitExtension.MESSAGES.statusFailed();
            eventBus.fireEvent(new OutputEvent(errorMessage, OutputMessage.Type.ERROR));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler#onFolderRefreshed(org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedEvent)
    */
   @Override
   public void onFolderRefreshed(FolderRefreshedEvent event)
   {
      updateBrowserTreeStatus(event.getFolder());
   }

   /**
    * Get working directory of the Git repository and if found - get status.
    * 
    * @param folder
    */
   private void updateBrowserTreeStatus(final Folder folder)
   {
      if (folder == null || folder.getChildren() == null || folder.getChildren().size() <= 0
         || folder.getHref() == null)
         return;

      GitClientService.getInstance().getWorkDir(folder.getHref(), new AsyncRequestCallback<WorkDirResponse>()
      {
         @Override
         protected void onSuccess(WorkDirResponse result)
         {
            String workDir = result.getWorkDir();
            workDir = (workDir.endsWith("/.git")) ? workDir.substring(0, workDir.lastIndexOf("/.git")) : workDir;
            getStatus(workDir, folder);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
         }
      });
   }

   /**
    * Get the files in different state of Git cycle and mark them in browser tree.
    * 
    * @param workDir working directory
    * @param folder folder to be updated
    */
   private void getStatus(final String workDir, final Folder folder)
   {
      GitClientService.getInstance().status(workDir, new AsyncRequestCallback<StatusResponse>()
      {

         @Override
         protected void onSuccess(StatusResponse result)
         {
            Map<Item, Map<TreeIconPosition, String>> treeNodesToUpdate =
               new HashMap<Item, Map<TreeIconPosition, String>>();

            List<Item> itemsToCheck = new ArrayList<Item>();
            itemsToCheck.addAll(folder.getChildren());
            itemsToCheck.add(folder);
            for (Item item : itemsToCheck)
            {
               String pattern = item.getHref().replaceFirst(workDir + "/", "");
               Map<TreeIconPosition, String> map = new HashMap<TreeIconPosition, String>();
               if (pattern.length() == 0 || "/".equals(pattern))
               {
                  map.put(TreeIconPosition.BOTTOMRIGHT, GitClientBundle.INSTANCE.repositoryRoot().getURL());
               }
               else if (contains(result.getChangedNotCommited(), pattern))
               {
                  map.put(TreeIconPosition.BOTTOMRIGHT, GitClientBundle.INSTANCE.itemNotCommited().getURL());
               }
               else if (contains(result.getChangedNotUpdated(), pattern))
               {
                  map.put(TreeIconPosition.BOTTOMRIGHT, GitClientBundle.INSTANCE.itemChanged().getURL());
               }
               else if (contains(result.getUntracked(), pattern))
               {
                  map.put(TreeIconPosition.BOTTOMRIGHT, GitClientBundle.INSTANCE.itemNew().getURL());
               }
               else
               {
                  if (item instanceof File)
                  {
                     map.put(TreeIconPosition.BOTTOMRIGHT, GitClientBundle.INSTANCE.itemInRepoditory().getURL());
                  }
               }
               treeNodesToUpdate.put(item, map);
            }
            eventBus.fireEvent(new AddItemTreeIconEvent(treeNodesToUpdate));
         }

         @Override
         protected void onFailure(Throwable exception)
         {
         }
      });
   }

   /**
    * Check whether files from Git status contain the match with pointed pattern. 
    * 
    * @param files files in status
    * @param pattern pattern to compare
    * @return pattern matchers one of the files in the list or not
    */
   private boolean contains(List<GitFile> files, String pattern)
   {
      for (GitFile file : files)
      {
         if (pattern.equals(file.getPath()))
            return true;
      }
      return false;
   }
}
