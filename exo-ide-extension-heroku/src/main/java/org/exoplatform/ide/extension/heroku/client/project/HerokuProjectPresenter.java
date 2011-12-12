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
package org.exoplatform.ide.extension.heroku.client.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.delete.ApplicationDeletedEvent;
import org.exoplatform.ide.extension.heroku.client.delete.ApplicationDeletedHandler;
import org.exoplatform.ide.extension.heroku.client.delete.DeleteApplicationEvent;
import org.exoplatform.ide.extension.heroku.client.info.ShowApplicationInfoEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.logs.ShowLogsEvent;
import org.exoplatform.ide.extension.heroku.client.marshaller.Property;
import org.exoplatform.ide.extension.heroku.client.rake.RakeCommandEvent;
import org.exoplatform.ide.extension.heroku.client.rename.ApplicationRenamedEvent;
import org.exoplatform.ide.extension.heroku.client.rename.ApplicationRenamedHandler;
import org.exoplatform.ide.extension.heroku.client.rename.RenameApplicationEvent;
import org.exoplatform.ide.extension.heroku.client.stack.ChangeApplicationStackEvent;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.List;

/**
 * Presenter for managing project, deployed on Heroku.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Dec 2, 2011 2:21:04 PM anya $
 *
 */
public class HerokuProjectPresenter extends GitPresenter implements ProjectOpenedHandler, ProjectClosedHandler,
   ManageHerokuProjectHandler, ViewClosedHandler, LoggedInHandler, ApplicationRenamedHandler, ApplicationDeletedHandler
{

   interface Display extends IsView
   {
      HasClickHandlers getCloseButton();

      HasClickHandlers getRenameButton();

      HasClickHandlers getDeleteButton();

      HasClickHandlers getInfoButton();

      HasClickHandlers getRakeButton();

      HasClickHandlers getLogsButton();

      HasClickHandlers getEditStackButton();

      HasValue<String> getApplicationName();

      void setApplicationURL(String URL);

      HasValue<String> getApplicationStack();
   }

   /**
    * Presenter's display.
    */
   private Display display;

   /**
    * Opened project in project explorer.
    */
   private ProjectModel openedProject;

   public HerokuProjectPresenter()
   {
      IDE.getInstance().addControl(new HerokuControl());

      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(ManageHerokuProjectEvent.TYPE, this);
      IDE.addHandler(ApplicationRenamedEvent.TYPE, this);
      IDE.addHandler(ApplicationDeletedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * Bind display with presenter.
    */
   public void bindDisplay()
   {
      display.getDeleteButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.eventBus().fireEvent(new DeleteApplicationEvent());
         }
      });

      display.getRenameButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.eventBus().fireEvent(new RenameApplicationEvent());
         }
      });

      display.getCloseButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getInfoButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.eventBus().fireEvent(new ShowApplicationInfoEvent());
         }
      });

      display.getEditStackButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.eventBus().fireEvent(new ChangeApplicationStackEvent());
         }
      });

      display.getLogsButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.eventBus().fireEvent(new ShowLogsEvent());
         }
      });

      display.getRakeButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.eventBus().fireEvent(new RakeCommandEvent());
         }
      });
   }

   /**
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
    * @see org.exoplatform.ide.extension.heroku.client.project.ManageHerokuProjectHandler#onManageHerokuProject(org.exoplatform.ide.extension.heroku.client.project.ManageHerokuProjectEvent)
    */
   @Override
   public void onManageHerokuProject(ManageHerokuProjectEvent event)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
         IDE.getInstance().openView(display.asView());
      }
      getApplicationInfo();
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
    */
   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      openedProject = null;
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
    */
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      openedProject = event.getProject();
   }

   /**
    * Get application's information.
    */
   protected void getApplicationInfo()
   {
      try
      {
         HerokuClientService.getInstance().getApplicationInfo(null, vfs.getId(), openedProject.getId(), false,
            new HerokuAsyncRequestCallback(this)
            {

               @Override
               protected void onSuccess(List<Property> properties)
               {
                  displayProperties(properties);
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent)
    */
   @Override
   public void onLoggedIn(LoggedInEvent event)
   {
      IDE.removeHandler(LoggedInEvent.TYPE, this);
      if (!event.isFailed())
      {
         getApplicationInfo();
      }
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.rename.ApplicationRenamedHandler#onApplicationRenamed(org.exoplatform.ide.extension.heroku.client.rename.ApplicationRenamedEvent)
    */
   @Override
   public void onApplicationRenamed(ApplicationRenamedEvent event)
   {
      if (openedProject != null && openedProject.getId() == event.getProjectId() && display != null)
      {
         displayProperties(event.getProperties());
      }
   }

   /**
    * Display application's properties.
    * 
    * @param properties application's properties
    */
   protected void displayProperties(List<Property> properties)
   {
      for (Property property : properties)
      {
         if ("name".equals(property.getName()))
         {
            display.getApplicationName().setValue(property.getValue());
         }
         else if ("webUrl".equals(property.getName()))
         {
            display.setApplicationURL(property.getValue());
         }
         else if ("stack".equals(property.getName()))
         {
            display.getApplicationStack().setValue((property.getValue()));
         }
      }
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.delete.ApplicationDeletedHandler#onApplicationDeleted(org.exoplatform.ide.extension.heroku.client.delete.ApplicationDeletedEvent)
    */
   @Override
   public void onApplicationDeleted(ApplicationDeletedEvent event)
   {
      if (display != null && vfs.getId().equals(event.getVfsId()) && openedProject != null
         && openedProject.getId().equals(event.getProjectId()))
      {
         IDE.getInstance().closeView(display.asView().getId());
      }
   }
}
