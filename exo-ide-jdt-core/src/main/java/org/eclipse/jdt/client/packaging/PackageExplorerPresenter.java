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
package org.eclipse.jdt.client.packaging;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.client.packaging.ProjectTreeParser.ParsingCompleteListener;
import org.eclipse.jdt.client.packaging.model.PackageItem;
import org.eclipse.jdt.client.packaging.model.ProjectItem;
import org.eclipse.jdt.client.packaging.model.ResourceDirectoryItem;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.application.IDELoader;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemEvent;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemHandler;
import org.exoplatform.ide.client.framework.project.PackageExplorerDisplay;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
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

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class PackageExplorerPresenter implements ShowPackageExplorerHandler, ViewOpenedHandler, ViewClosedHandler,
   ProjectOpenedHandler, ProjectClosedHandler, RefreshBrowserHandler, SelectItemHandler
{

//   private static final String RECEIVE_CHILDREN_ERROR_MSG =
//            org.exoplatform.ide.client.IDE.ERRORS_CONSTANT.workspaceReceiveChildrenError();

   private static final String RECEIVE_CHILDREN_ERROR_MSG = "Service is not deployed.<br>Parent folder not found.";
   
   private static final String MESSAGE_LOAD_PROJECT = "Loading project structure...";
   
   private static final String MESSAGE_UPDATE_PROJECT = "Updating project structure...";
   
   private PackageExplorerDisplay display;

   private ProjectModel openedProject;

   private ProjectItem projectItem;

   private Item selectedItem;

   private Item itemToSelect;

   private ProjectTreeParser treeParser;
   
   private static PackageExplorerPresenter instance;
   
   public static PackageExplorerPresenter getInstance()
   {
      return instance;
   }

   public PackageExplorerPresenter()
   {
      instance = this;
      
      IDE.getInstance().addControl(new ShowPackageExplorerControl(), Docking.TOOLBAR);

      IDE.addHandler(ShowPackageExplorerEvent.TYPE, this);

      IDE.addHandler(ViewOpenedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);

      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);

      IDE.addHandler(RefreshBrowserEvent.TYPE, this);
      IDE.addHandler(SelectItemEvent.TYPE, this);
   }
   
   public ProjectItem getProjectItem()
   {
      return projectItem;
   }

   @Override
   public void onShowPackageExplorer(ShowPackageExplorerEvent event)
   {
      if (display == null)
      {
         if (openedProject == null)
         {
            return;
         }
         
         if (!ProjectTypes.contains(openedProject))
         {
            return;
         }
         
         display = GWT.create(PackageExplorerDisplay.class);
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
            display.getBrowserTree().setValue(itemToOpen);
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
      if (event.getView() instanceof PackageExplorerDisplay && openedProject != null)
      {
         Scheduler.get().scheduleDeferred(new ScheduledCommand()
         {
            @Override
            public void execute()
            {
               openProject();
            }
         });
      }
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof PackageExplorerDisplay)
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

      if (!ProjectTypes.contains(openedProject))
      {
         IDE.getInstance().closeView(display.asView().getId());
         return;
      }

      openProject();
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

   private void openProject()
   {
      projectItem = new ProjectItem(openedProject);
      display.setPackageExplorerTreeVisible(true);
      display.getBrowserTree().setValue(projectItem);

      itemToSelect = openedProject;

      updateProjectTree(MESSAGE_LOAD_PROJECT);
   }

   private void updateProjectTree(final String loaderMessage)
   {
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            IDELoader.show(loaderMessage);

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

   private void projectTreeReceived()
   {
      treeParser = new ProjectTreeParser(openedProject, projectItem);
      treeParser.parseProjectStructure(new ParsingCompleteListener()
      {
         @Override
         public void onParseComplete()
         {
            display.getBrowserTree().setValue(null);
            display.getBrowserTree().setValue(projectItem);
            
            if (itemToSelect == null)
            {
               itemToSelect = openedProject;
            }

            List<Object> itemList = treeParser.getItemList(itemToSelect);
            display.goToItem(itemList);
         }
      });
   }

   @Override
   public void onRefreshBrowser(RefreshBrowserEvent event)
   {
      if (display == null)
      {
         return;
      }
      
      itemToSelect = event.getItemToSelect();
      if (itemToSelect == null)
      {
         itemToSelect = selectedItem;
      }

      if (itemToSelect == null)
      {
         itemToSelect = event.getFolders().get(0);
      }

      if (itemToSelect == null)
      {
         itemToSelect = openedProject;
      }
      
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            updateProjectTree(MESSAGE_UPDATE_PROJECT);
         }
      });

   }

   @Override
   public void onSelectItem(SelectItemEvent event)
   {
      if (display == null)
      {
         return;
      }

      System.out.println(">> select item " + event.getItemId());
   }

}
