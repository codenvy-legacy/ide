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
package org.exoplatform.ide.client.project.packaging;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemEvent;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.client.project.packaging.ProjectTreeParser.ParsingCompleteListener;
import org.exoplatform.ide.client.project.packaging.model.PackageItem;
import org.exoplatform.ide.client.project.packaging.model.ProjectItem;
import org.exoplatform.ide.client.project.packaging.model.ResourceDirectoryItem;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class PackageExplorerPresenter implements ShowPackageExplorerHandler, ViewOpenedHandler, ViewClosedHandler,
   ProjectOpenedHandler, ProjectClosedHandler, RefreshBrowserHandler, SelectItemHandler
{

   public interface Display extends IsView
   {

      /**
       * Change tree visibility.
       * 
       * @param visible <code>true</code> if visible
       */
      void setPackageExplorerTreeVisible(boolean visible);

      /**
       * @return {@link TreeGridItem}
       */
      TreeGridItem<Object> getBrowserTree();

      void selectItem(Object item);

      Object getSelectedObject();

   }

   private static final String RECEIVE_CHILDREN_ERROR_MSG = org.exoplatform.ide.client.IDE.ERRORS_CONSTANT
      .workspaceReceiveChildrenError();

   private static final String UPDATING_PROJECT_STRUCTURE_MESSAGE = "Updating project structure...";

   private Display display;

   //private ProjectModel project;

   private ProjectModel openedProject;

   private ProjectItem projectItem;

   private Item selectedItem;

   public PackageExplorerPresenter()
   {
      IDE.getInstance().addControl(new ShowPackageExplorerControl(), Docking.TOOLBAR);

      IDE.addHandler(ShowPackageExplorerEvent.TYPE, this);

      IDE.addHandler(ViewOpenedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);

      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);

      IDE.addHandler(RefreshBrowserEvent.TYPE, this);
      IDE.addHandler(SelectItemEvent.TYPE, this);
   }

   @Override
   public void onShowPackageExplorer(ShowPackageExplorerEvent event)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
         IDE.getInstance().openView(display.asView());
      }
      else
      {
         IDE.getInstance().closeView(display.asView().getId());
      }
   }

   private void bindDisplay()
   {
      display.getBrowserTree().addOpenHandler(new OpenHandler<Object>()
      {
         @Override
         public void onOpen(OpenEvent<Object> event)
         {
            Object itemToOpen = event.getTarget();
            if (!(itemToOpen instanceof ProjectItem))
            {
               display.getBrowserTree().setValue(itemToOpen);
            }
         }
      });

      display.getBrowserTree().addDoubleClickHandler(new DoubleClickHandler()
      {
         @Override
         public void onDoubleClick(DoubleClickEvent event)
         {
            Object selectedObject = display.getSelectedObject();
            if (selectedObject instanceof FileModel)
            {
               FileModel file = (FileModel)selectedObject;
               IDE.fireEvent(new OpenFileEvent(file));
            }
         }
      });

      display.getBrowserTree().addSelectionHandler(new SelectionHandler<Object>()
      {
         @Override
         public void onSelection(SelectionEvent<Object> event)
         {
            Scheduler.get().scheduleDeferred(new ScheduledCommand()
            {
               @Override
               public void execute()
               {
                  Object selectedObject = display.getSelectedObject();
                  if (selectedObject instanceof ProjectItem)
                  {
                     selectedItem = ((ProjectItem)selectedObject).getProject();
                  }
                  else if (selectedObject instanceof ResourceDirectoryItem)
                  {
                     selectedItem = ((ResourceDirectoryItem)selectedObject).getFolder();
                  }
                  else if (selectedObject instanceof PackageItem)
                  {
                     selectedItem = ((PackageItem)selectedObject).getPackageFolder();
                  }
                  else if (selectedObject instanceof FolderModel)
                  {
                     selectedItem = (FolderModel)selectedObject;
                  }
                  else if (selectedObject instanceof FileModel)
                  {
                     selectedItem = (FileModel)selectedObject;
                  }
                  else
                  {
                     selectedItem = null;
                  }

                  if (selectedItem != null)
                  {
                     List<Item> selectedItems = new ArrayList<Item>();
                     selectedItems.add(selectedItem);
                     IDE.fireEvent(new ItemsSelectedEvent(selectedItems, display.asView()));
                  }
               }
            });
         }
      });

   }

   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
      if (event.getView() instanceof Display && openedProject != null)
      {
         Scheduler.get().scheduleDeferred(new ScheduledCommand()
         {
            @Override
            public void execute()
            {
               loadProjectStructure();
            }
         });
      }
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
         projectItem = null;
      }
   }

   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      openedProject = new ProjectModel(event.getProject());

      if (display == null)
      {
         return;
      }

      loadProjectStructure();
   }

   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      openedProject = null;

      if (display == null)
      {
         return;
      }

      projectItem = null;
      display.getBrowserTree().setValue(null);
      display.setPackageExplorerTreeVisible(false);
   }

   private void loadProjectStructure()
   {
      projectItem = new ProjectItem(openedProject);
      display.setPackageExplorerTreeVisible(true);
      display.getBrowserTree().setValue(projectItem);

      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            IDELoader.show("Loading project structure...");

            try
            {
               ProjectTreeUnmarshaller unmarshaller = new ProjectTreeUnmarshaller(openedProject);
               AsyncRequestCallback<ProjectModel> callback = new AsyncRequestCallback<ProjectModel>(unmarshaller)
               {
                  @Override
                  protected void onSuccess(ProjectModel result)
                  {
                     IDELoader.hide();
                     projectTreeReceived();
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     IDELoader.hide();
                     IDE.fireEvent(new ExceptionThrownEvent("Error loading project structure"));
                     exception.printStackTrace();
                  }
               };

               VirtualFileSystem.getInstance().getProjectTree(openedProject, callback);
            }
            catch (Exception e)
            {
               IDELoader.hide();
               IDE.fireEvent(new ExceptionThrownEvent(e));
            }
         }
      });
   }

   ProjectTreeParser treeParser;

   private void projectTreeReceived()
   {
      treeParser = new ProjectTreeParser(openedProject, projectItem);
      treeParser.parseProjectStructure(new ParsingCompleteListener()
      {
         @Override
         public void onParseComplete()
         {
            display.getBrowserTree().setValue(projectItem);
            display.selectItem(projectItem);
         }
      });
   }

   private List<Folder> foldersToRefresh = new ArrayList<Folder>();

   private Item itemToSelect;

   @Override
   public void onRefreshBrowser(RefreshBrowserEvent event)
   {
      if (display == null)
      {
         return;
      }
      
//      System.out.println("on refresh browser");

//      if (event.getFolders() != null)
//      {
//         System.out.println("folders is not null");
//         System.out.println("folders size > " + event.getFolders().size());
//      }
//      else
//      {
//         System.out.println("folders not initialized");
//      }

      if (event.getFolders() != null)
      {
         foldersToRefresh = event.getFolders();
      }
      else
      {
         foldersToRefresh = new ArrayList<Folder>();

         if (selectedItem != null)
         {
            if (selectedItem instanceof FileModel)
            {
               foldersToRefresh.add(((FileModel)selectedItem).getParent());
            }
            else if (selectedItem instanceof Folder)
            {
               foldersToRefresh.add((Folder)selectedItem);
            }
         }
      }

      itemToSelect = event.getItemToSelect();

//      System.out.println("scheduling...................");

      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            updateFolders();
         }
      });
   }

   private void updateFolders()
   {
//      System.out.println("PackageExplorerPresenter.updateFolders()");
//      System.out.println("folders to refresh size > " + foldersToRefresh.size());

      if (foldersToRefresh.size() == 0)
      {
         if (itemToSelect != null)
         {
            // select item in the tree
         }

         return;
      }

      final Folder folder = foldersToRefresh.get(0);
      foldersToRefresh.remove(0);

      //      treeParser.updateFolder(folder, new FolderUpdateCompleteListener()
      //      {
      //         @Override
      //         public void onUpdateComplete(Object item)
      //         {
      //            System.out.println(">>>> ON Folder update complete");
      //            
      //            if (item != null)
      //            {
      //               System.out.println("item is not null. updating tree");
      //               display.getBrowserTree().setValue(item);
      //               
      //               Scheduler.get().scheduleDeferred(new ScheduledCommand()
      //               {
      //                  @Override
      //                  public void execute()
      //                  {
      //                     updateFolders();
      //                  }
      //               });
      //               
      //            }
      //         }
      //      });

//      refreshFolderProperties(folder);
      try
      {
         //display.changeFolderIcon(folder, true);
         IDELoader.show(UPDATING_PROJECT_STRUCTURE_MESSAGE);

//         System.out.println("get folder content > " + folder.getPath());

         VirtualFileSystem.getInstance().getChildren(folder,
            new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
            {
               @Override
               protected void onFailure(Throwable exception)
               {
                  IDELoader.hide();

                  itemToSelect = null;
                  foldersToRefresh.clear();
                  IDE.fireEvent(new ExceptionThrownEvent(exception, RECEIVE_CHILDREN_ERROR_MSG));
               }

               @Override
               protected void onSuccess(List<Item> result)
               {
                  IDELoader.hide();

                  folderContentReceived(folder, result);
               }
            });
      }
      catch (RequestException e)
      {
         IDELoader.hide();
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   //   /**
   //    * Refresh folder's properties.
   //    * 
   //    * @param folder
   //    */
   //   private void refreshFolderProperties(final Folder folder)
   //   {
   //      try
   //      {
   //         VirtualFileSystem.getInstance().getItemById(folder.getId(),
   //            new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper()))
   //            {
   //               @Override
   //               protected void onSuccess(ItemWrapper result)
   //               {
   //                  folder.getProperties().clear();
   //                  folder.getProperties().addAll(result.getItem().getProperties());
   //               }
   //
   //               protected void onFailure(Throwable exception)
   //               {
   //               }
   //            });
   //      }
   //      catch (RequestException e)
   //      {
   //      }
   //   }

   private void folderContentReceived(Folder folder, List<Item> children)
   {
//      System.out.println("received content of folder > " + folder.getPath());
      Object folderItem = treeParser.updateFolderStructure(folder, children);

      if (folderItem == null)
      {
         return;
      }

      // refresh tree if folder's tree node is opened
      display.getBrowserTree().setValue(folderItem);

   }

   @Override
   public void onSelectItem(SelectItemEvent event)
   {
      if (display == null)
      {
         return;
      }
      
//      System.out.println("on select item");
//      System.out.println("item href > " + event.getItemHref());
   }

}
