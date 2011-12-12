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

package org.exoplatform.ide.client.project.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.AllFilesClosedEvent;
import org.exoplatform.ide.client.framework.event.AllFilesClosedHandler;
import org.exoplatform.ide.client.framework.event.CloseAllFilesEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.OpenProjectEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ShowProjectsPresenter implements ShowProjectsHandler, ViewClosedHandler, ProjectOpenedHandler,
   ProjectClosedHandler, AllFilesClosedHandler, VfsChangedHandler
{

   public interface Display extends IsView
   {

      ListGridItem<ProjectModel> getProjectsListGrid();

      List<ProjectModel> getSelectedItems();

      HasClickHandlers getOpenButton();

      HasClickHandlers getCancelButton();

      void setOpenButtonEnabled(boolean enabled);

   }

   /**
    * Instance of opened {@link Display}.
    */
   private Display display;

   /**
    * ID of current opened project.
    */
   private String currentOpenedProjectID;

   /**
    * Virtual File System info.
    */
   private VirtualFileSystemInfo vfsInfo;

   /**
    * Creates new instance of this presenter.
    */
   public ShowProjectsPresenter()
   {
      IDE.getInstance().addControl(new ShowProjectsControl());

      IDE.addHandler(ShowProjectsEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(AllFilesClosedEvent.TYPE, this);
   }

   /**
    * Handles {@link ShowProjectsEvent} and opens {@link ShowProjectsView}.
    * 
    * @see org.exoplatform.ide.client.project.list.ShowProjectsHandler#onShowProjects(org.exoplatform.ide.client.project.list.ShowProjectsEvent)
    */
   @Override
   public void onShowProjects(ShowProjectsEvent event)
   {
      if (display != null || vfsInfo == null)
      {
         return;
      }

      getProjectList();
   }

   /**
    * Creates and binds display.
    */
   private void createAndBindDisplay()
   {
      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());

      display.getOpenButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            openProject();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getProjectsListGrid().addDoubleClickHandler(new DoubleClickHandler()
      {
         @Override
         public void onDoubleClick(DoubleClickEvent event)
         {
            openProject();
         }
      });

      display.getProjectsListGrid().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            Scheduler.get().scheduleDeferred(openButtonEnablingChecker);
         }
      });

      display.setOpenButtonEnabled(false);
   }

   /**
    * Enables or disables Open button.
    */
   private ScheduledCommand openButtonEnablingChecker = new ScheduledCommand()
   {
      @Override
      public void execute()
      {
         if (display == null)
         {
            return;
         }

         if (display.getSelectedItems().size() != 1)
         {
            display.setOpenButtonEnabled(false);
            return;
         }

         ProjectModel selectedProject = display.getSelectedItems().get(0);
         if (selectedProject.getId().equals(currentOpenedProjectID))
         {
            display.setOpenButtonEnabled(false);
         }
         else
         {
            display.setOpenButtonEnabled(true);
         }
      }
   };

   /**
    * Refreshes the list of available projects and opens new {@link ShowProjectsView}.
    */
   private void getProjectList()
   {
      HashMap<String, String> query = new HashMap<String, String>();

      query.put("nodeType", "vfs:project");

      try
      {
         VirtualFileSystem.getInstance().search(
            query,
            -1,
            0,
            new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<List<Item>>(
               new ChildrenUnmarshaller(new ArrayList<Item>()))
            {
               @Override
               protected void onSuccess(List<Item> result)
               {
                  List<ProjectModel> projects = new ArrayList<ProjectModel>();
                  for (Item item : result)
                  {
                     if (item instanceof ProjectModel)
                     {
                        projects.add((ProjectModel)item);
                     }
                  }

                  createAndBindDisplay();
                  display.getProjectsListGrid().setValue(projects);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception, "Searching of projects failed."));
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         IDE.fireEvent(new ExceptionThrownEvent(e, "Searching of projects failed."));
      }
   }

   /**
    * Opens selected project.
    * First, close all opened files.  
    */
   private void openProject()
   {
      if (display.getSelectedItems().size() == 0)
      {
         return;
      }

      IDE.fireEvent(new CloseAllFilesEvent());
   }

   /**
    * Opens the project after all files are closed.
    * 
    * @see org.exoplatform.ide.client.framework.event.AllFilesClosedHandler#onAllFilesClosed(org.exoplatform.ide.client.framework.event.AllFilesClosedEvent)
    */
   @Override
   public void onAllFilesClosed(AllFilesClosedEvent event)
   {
      if (display == null)
      {
         return;
      }

      ProjectModel project = (ProjectModel)display.getSelectedItems().get(0);
      IDE.fireEvent(new OpenProjectEvent(project));
   }

   /**
    * Handle {@link ViewClosedEvent} and reset instance of {@link Display}.
    * 
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   /**
    * Receives the name of the currently opened project.
    * 
    * @see org.exoplatform.ide.client.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.project.ProjectOpenedEvent)
    */
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      currentOpenedProjectID = event.getProject().getId();

      if (display != null)
      {
         IDE.getInstance().closeView(display.asView().getId());
      }
   }

   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      vfsInfo = event.getVfsInfo();
   }

   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      if (event.getProject().getId().equals(currentOpenedProjectID))
      {
         currentOpenedProjectID = null;
      }
   }

}
