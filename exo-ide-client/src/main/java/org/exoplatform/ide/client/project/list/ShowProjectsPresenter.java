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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.client.project.explorer.OpenProjectEvent;
import org.exoplatform.ide.client.project.explorer.ProjectOpenedEvent;
import org.exoplatform.ide.client.project.explorer.ProjectOpenedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ShowProjectsPresenter implements ShowProjectsHandler, ViewClosedHandler,
   UserInfoReceivedHandler, ProjectOpenedHandler
{

   public interface Display extends IsView
   {

      ListGridItem<ProjectModel> getProjectsListGrid();

      List<ProjectModel> getSelectedItems();

      HasClickHandlers getOpenButton();

      HasClickHandlers getCancelButton();

      void setOpenButtonEnabled(boolean enabled);

   }

   private Display display;

   private String userName;

   public ShowProjectsPresenter()
   {
      IDE.getInstance().addControl(new ShowProjectsControl());

      IDE.addHandler(ShowProjectsEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(UserInfoReceivedEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
   }

   @Override
   public void onShowProjects(ShowProjectsEvent event)
   {
      if (display != null)
      {
         return;
      }

      getProjectList();
   }

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
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand()
            {
               @Override
               public void execute()
               {
                  if (display == null)
                  {
                     return;
                  }
                  display.setOpenButtonEnabled(display.getSelectedItems().size() > 0);
                  System.out.println("selected items > " + display.getSelectedItems().size());
               }
            });

         }
      });

      display.setOpenButtonEnabled(false);
   }

   private void getProjectList()
   {
      HashMap<String, String> query = new HashMap<String, String>();

      String path = (!"root".equals(userName)) ? "/" + userName : "";
      query.put("path", path);
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

   private void openProject()
   {
      if (display.getSelectedItems().size() <= 0)
      {
         return;
      }

      ProjectModel project = (ProjectModel)display.getSelectedItems().get(0);
      IDE.fireEvent(new OpenProjectEvent(project));
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   @Override
   public void onUserInfoReceived(UserInfoReceivedEvent event)
   {
      userName = event.getUserInfo().getName();
   }

   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      if (display != null)
      {
         IDE.getInstance().closeView(display.asView().getId());
      }
   }

}
