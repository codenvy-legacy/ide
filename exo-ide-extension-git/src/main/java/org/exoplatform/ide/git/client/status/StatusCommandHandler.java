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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconEvent;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedEvent;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.git.client.GitClientBundle;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.client.marshaller.StatusResponse;
import org.exoplatform.ide.git.shared.GitFile;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;

import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ImageResource;

/**
 * Handler to process actions with displaying the status of the Git work tree.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 28, 2011 3:58:20 PM anya $
 *
 */
public class StatusCommandHandler extends GitPresenter implements ShowWorkTreeStatusHandler, FolderRefreshedHandler
{
   /**
    * @param eventBus event handler
    */
   public StatusCommandHandler()
   {
      IDE.addHandler(ShowWorkTreeStatusEvent.TYPE, this);
      IDE.addHandler(FolderRefreshedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.git.client.status.ShowWorkTreeStatusHandler#onShowWorkTreeStatus(org.exoplatform.ide.git.client.status.ShowWorkTreeStatusEvent)
    */
   @Override
   public void onShowWorkTreeStatus(ShowWorkTreeStatusEvent event)
   {
      if (makeSelectionCheck())
      {
         getStatusText(((ItemContext)selectedItems.get(0)).getProject(), selectedItems.get(0));
      }
   }

   /**
    * Get the status for Git work tree and display it on success.
    * 
    * @param workTree the location of the ".git" folder
    * @param item item in work tree
    */
   private void getStatusText(ProjectModel project, Item item)
   {
      if (project == null)
         return;
      String[] fileFilter = null;
      if (item instanceof Folder)
      {
         //Remove last "/" from path:
         String path =
            item.getPath().endsWith("/") ? item.getPath().substring(0, item.getPath().length() - 1) : item.getPath();
         path = URL.decodePathSegment(path);
         //Check selected item in workspace tree is not the root of the Git repository tree:
         if (!(item instanceof ProjectModel))
         {
            //Add filter to display status for the selected folder:
            path = (path.startsWith("/")) ? path.replaceFirst("/", "") : "";
            fileFilter = new String[]{path};
         }
      }
      GitClientService.getInstance().statusText(vfs.getId(), project.getId(), false, fileFilter,
         new AsyncRequestCallback<StatusResponse>()
         {

            @Override
            protected void onSuccess(StatusResponse result)
            {
               if (result.getWorkTreeStatus() == null)
                  return;
               String status = result.getWorkTreeStatus();
               status = status.replace("\n", "<br>");
               IDE.fireEvent(new OutputEvent(status, OutputMessage.Type.INFO));
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               String errorMessage =
                  (exception.getMessage() != null) ? exception.getMessage() : GitExtension.MESSAGES.statusFailed();
               IDE.fireEvent(new OutputEvent(errorMessage, OutputMessage.Type.ERROR));
            }
         });
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
      ItemList<Item> children =
         (folder instanceof ProjectModel) ? ((ProjectModel)folder).getChildren() : ((FolderModel)folder).getChildren();

      if (children == null || children.getItems().size() <= 0 || folder.getId() == null || folder.getId().isEmpty())
         return;

      if (folder instanceof ItemContext && ((ItemContext)folder).getProject() != null)
      {
         getStatus(((ItemContext)folder).getProject(), folder);
      }
   }

   /**
    * Get the files in different state of Git cycle and mark them in browser tree.
    * 
    * @param workDir working directory
    * @param folder folder to be updated
    */
   private void getStatus(final ProjectModel project, final Folder folder)
   {
      GitClientService.getInstance().status(vfs.getId(), project.getId(), new AsyncRequestCallback<StatusResponse>()
      {
         @Override
         protected void onSuccess(StatusResponse result)
         {
            Map<Item, Map<TreeIconPosition, ImageResource>> treeNodesToUpdate =
               new HashMap<Item, Map<TreeIconPosition, ImageResource>>();

            List<Item> itemsToCheck = new ArrayList<Item>();
            ItemList<Item> children =
               (folder instanceof ProjectModel) ? ((ProjectModel)folder).getChildren() : ((FolderModel)folder)
                  .getChildren();
            itemsToCheck.addAll(children.getItems());
            itemsToCheck.add(folder);
            for (Item item : itemsToCheck)
            {
               String path = URL.decodePathSegment(item.getPath());
               String pattern = path.replaceFirst(project.getPath(), "");
               pattern = (pattern.startsWith("/")) ? pattern.replaceFirst("/", "") : pattern;
               Map<TreeIconPosition, ImageResource> map = new HashMap<TreeIconPosition, ImageResource>();
               if (pattern.length() == 0 || "/".equals(pattern))
               {
                  map.put(TreeIconPosition.BOTTOMRIGHT, GitClientBundle.INSTANCE.repositoryRoot());
               }
               else if (contains(result.getChangedNotCommited(), pattern))
               {
                  map.put(TreeIconPosition.BOTTOMRIGHT, GitClientBundle.INSTANCE.itemNotCommited());
               }
               else if (contains(result.getChangedNotUpdated(), pattern))
               {
                  map.put(TreeIconPosition.BOTTOMRIGHT, GitClientBundle.INSTANCE.itemChanged());
               }
               else if (contains(result.getUntracked(), pattern))
               {
                  map.put(TreeIconPosition.BOTTOMRIGHT, GitClientBundle.INSTANCE.itemNew());
               }
               else
               {
                  if (item instanceof File)
                  {
                     map.put(TreeIconPosition.BOTTOMRIGHT, GitClientBundle.INSTANCE.itemInRepository());
                  }
               }
               treeNodesToUpdate.put(item, map);
            }
            IDE.fireEvent(new AddItemTreeIconEvent(treeNodesToUpdate));
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
