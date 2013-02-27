/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.client.project;

import com.google.gwt.user.client.Window;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.framework.application.IDELoader;
import org.exoplatform.ide.client.framework.event.AllFilesClosedEvent;
import org.exoplatform.ide.client.framework.event.AllFilesClosedHandler;
import org.exoplatform.ide.client.framework.event.CloseAllFilesEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.CloseProjectEvent;
import org.exoplatform.ide.client.framework.project.CloseProjectHandler;
import org.exoplatform.ide.client.framework.project.OpenProjectEvent;
import org.exoplatform.ide.client.framework.project.OpenProjectHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.api.IDEProject;
import org.exoplatform.ide.client.framework.project.api.ProjectBuilder;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedEvent;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class ProjectProcessor implements OpenProjectHandler, CloseProjectHandler, AllFilesClosedHandler,
   RefreshBrowserHandler, ItemsSelectedHandler
{

   private IDEProject openedProject;

   private List<Item> selectedItems;

   public ProjectProcessor()
   {
      IDE.addHandler(OpenProjectEvent.TYPE, this);
      IDE.addHandler(CloseProjectEvent.TYPE, this);
      IDE.addHandler(RefreshBrowserEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   @Override
   public void onOpenProject(OpenProjectEvent event)
   {
      if (openedProject != null)
      {
         return;
      }

      openedProject = ProjectBuilder.createProject(event.getProject());

      IDELoader.show("Loading project...");
      openedProject.refresh(openedProject, new AsyncCallback<Folder>()
      {
         @Override
         public void onFailure(Throwable caught)
         {
            caught.printStackTrace();

            IDELoader.hide();
            IDE.fireEvent(new ExceptionThrownEvent(caught));
         }

         @Override
         public void onSuccess(Folder result)
         {
            IDELoader.hide();
            IDE.fireEvent(new ProjectOpenedEvent(openedProject));
         }
      });

   }

   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   private List<FolderModel> foldersToBeRefreshed = new ArrayList<FolderModel>();

   private List<FolderModel> refreshedFolders = new ArrayList<FolderModel>();

   private Item itemToBeSelectedAfterRefreshing;

   @Override
   public void onRefreshBrowser(RefreshBrowserEvent event)
   {
      foldersToBeRefreshed.clear();
      for (Folder f : event.getFolders())
      {
         foldersToBeRefreshed.add((FolderModel)f);
      }

      if (foldersToBeRefreshed.isEmpty() && !selectedItems.isEmpty())
      {
         for (Item i : selectedItems)
         {
            if (i instanceof FolderModel)
            {
               foldersToBeRefreshed.add((FolderModel)i);
            }
            else if (i instanceof FileModel)
            {
               foldersToBeRefreshed.add(((FileModel)i).getParent());
            }
         }
      }

      itemToBeSelectedAfterRefreshing = event.getItemToSelect();
      if (itemToBeSelectedAfterRefreshing == null && selectedItems.size() == 1)
      {
         itemToBeSelectedAfterRefreshing = selectedItems.get(0);
      }

      refreshedFolders.clear();
      refreshFolders();
   }

   private Item getItemToSelect(Item item)
   {
      while (true)
      {
         try
         {
            openedProject.getResource(item.getPath());
            return item;
         }
         catch (Exception e)
         {
            //e.printStackTrace();
            if (item instanceof FolderModel)
            {
               item = ((FolderModel)item).getParent();
            }
            else if (item instanceof FileModel)
            {
               item = ((FileModel)item).getParent();
            }
            else
            {
               return null;
            }
         }
      }
   }

   private void refreshFolders()
   {
      if (foldersToBeRefreshed.size() == 0)
      {
         while (!refreshedFolders.isEmpty())
         {
            if (itemToBeSelectedAfterRefreshing != null)
            {
               itemToBeSelectedAfterRefreshing = getItemToSelect(itemToBeSelectedAfterRefreshing);
            }

            final Folder folder = refreshedFolders.remove(0);
            IDE.fireEvent(new TreeRefreshedEvent(folder, itemToBeSelectedAfterRefreshing));
            Scheduler.get().scheduleDeferred(new ScheduledCommand()
            {
               @Override
               public void execute()
               {
                  IDE.fireEvent(new FolderRefreshedEvent(folder));
               }
            });
         }

         return;
      }

      FolderModel folder = foldersToBeRefreshed.remove(0);

      IDELoader.show("Refreshing...");
      openedProject.refresh(folder, new AsyncCallback<Folder>()
      {
         @Override
         public void onSuccess(Folder result)
         {
            IDELoader.hide();
            refreshedFolders.add((FolderModel)result);
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand()
            {
               @Override
               public void execute()
               {
                  refreshFolders();
               }
            });
         }

         @Override
         public void onFailure(Throwable caught)
         {
            IDELoader.hide();
            //IDE.fireEvent(new ExceptionThrownEvent(caught));
         }
      });

   }
   
   @Override
   public void onCloseProject(CloseProjectEvent event)
   {
      IDE.removeHandler(AllFilesClosedEvent.TYPE, this);
      
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            IDE.addHandler(AllFilesClosedEvent.TYPE, ProjectProcessor.this);
            
            Scheduler.get().scheduleDeferred(new ScheduledCommand()
            {
               @Override
               public void execute()
               {
                  IDE.fireEvent(new CloseAllFilesEvent());
               }
            });
         }
      });
      
//      IDE.addHandler(AllFilesClosedEvent.TYPE, this);
//      
//      Scheduler.get().scheduleDeferred(new ScheduledCommand()
//      {
//         @Override
//         public void execute()
//         {
//            IDE.fireEvent(new CloseAllFilesEvent());
//         }
//      });
   }

   @Override
   public void onAllFilesClosed(AllFilesClosedEvent event)
   {
      IDE.removeHandler(AllFilesClosedEvent.TYPE, this);
      
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            final IDEProject closedProject = openedProject;
            openedProject = null;
            IDE.fireEvent(new ProjectClosedEvent(closedProject));
         }
      });      
   }

}
