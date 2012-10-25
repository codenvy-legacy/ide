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
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.client.project.packaging.ProjectTreeParser.ParsingCompleteListener;
import org.exoplatform.ide.client.project.packaging.model.PackageItem;
import org.exoplatform.ide.client.project.packaging.model.ProjectItem;
import org.exoplatform.ide.client.project.packaging.model.ResourceDirectoryItem;
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
import com.google.gwt.event.dom.client.HasClickHandlers;
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

      Object getSelectedObject();

      void goToItem(List<Object> itemList);

      /*
       * Link with Editor
       */

      /**
       * Returns Link with Editor button.
       * 
       * @return Link with Editor button
       */
      HasClickHandlers getLinkWithEditorButton();

      /**
       * Enables or disables Link with Editor button.
       * 
       * @param enabled <b>true</b> makes Link with Editor button enabled, <b>false</b> makes disabled
       */
      void setLinkWithEditorButtonEnabled(boolean enabled);

      /**
       * Adds or removes selection of Link with Editor button.
       * 
       * @param selected <b>true</b> makes button selected, <b>false</b> otherwise
       */
      void setLinkWithEditorButtonSelected(boolean selected);

   }

   private static final String RECEIVE_CHILDREN_ERROR_MSG =
            org.exoplatform.ide.client.IDE.ERRORS_CONSTANT.workspaceReceiveChildrenError();

   private static final String MESSAGE_LOAD_PROJECT = "Loading project structure...";
   
   private static final String MESSAGE_UPDATE_PROJECT = "Updating project structure...";
   
   private Display display;

   private ProjectModel openedProject;

   private ProjectItem projectItem;

   private Item selectedItem;

   private Item itemToSelect;

   ProjectTreeParser treeParser;

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
         if (!isProjectAcceptable())
         {
            return;
         }

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
      if (event.getView() instanceof Display && openedProject != null)
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

      if (!isProjectAcceptable())
      {
         IDE.getInstance().closeView(display.asView().getId());
         return;
      }

      openProject();
   }

   private boolean isProjectAcceptable()
   {
      String projectType = openedProject.getProjectType();
      if (ProjectResolver.APP_ENGINE_JAVA.equals(projectType) || ProjectResolver.SERVLET_JSP.equals(projectType)
         || ProjectResolver.SPRING.equals(projectType) || ProjectType.JAVA.value().equals(projectType)
         || ProjectType.JSP.value().equals(projectType) || ProjectType.AWS.value().equals(projectType)
         || ProjectType.JAR.value().equals(projectType))
      {
         return true;
      }

      return false;
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

      System.out.println(">> on select item");
      System.out.println(">> item href > " + event.getItemHref());
   }

}
