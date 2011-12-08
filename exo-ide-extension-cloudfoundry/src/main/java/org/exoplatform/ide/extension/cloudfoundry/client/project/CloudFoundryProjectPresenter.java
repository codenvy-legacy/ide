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
package org.exoplatform.ide.extension.cloudfoundry.client.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.delete.ApplicationDeletedEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.delete.ApplicationDeletedHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.delete.DeleteApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.start.RestartApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.start.StartApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.start.StopApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.update.UpdateApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.update.UpdateInstancesEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.update.UpdateMemoryEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.url.UnmapUrlEvent;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Presenter for managing project, deployed on CloudFoundry.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Dec 2, 2011 5:54:50 PM anya $
 *
 */
public class CloudFoundryProjectPresenter extends GitPresenter implements ProjectOpenedHandler, ProjectClosedHandler,
   ManageCloudFoundryProjectHandler, ViewClosedHandler, ApplicationDeletedHandler, ApplicationInfoChangedHandler
{
   interface Display extends IsView
   {
      HasClickHandlers getCloseButton();

      HasClickHandlers getUpdateButton();

      HasClickHandlers getDeleteButton();

      HasClickHandlers getInfoButton();

      HasValue<String> getApplicationName();

      void setApplicationURL(String url);

      HasValue<String> getApplicationModel();

      HasValue<String> getApplicationStack();

      HasValue<String> getApplicationInstances();

      HasValue<String> getApplicationMemory();

      HasClickHandlers getStartButton();

      HasClickHandlers getStopButton();

      HasClickHandlers getRestartButton();

      HasClickHandlers getEditMemoryButton();

      HasClickHandlers getEditURLButton();

      HasClickHandlers getEditInstancesButton();

      void setStartButtonEnabled(boolean enabled);

      void setStopButtonEnabled(boolean enabled);

      void setRestartButtonEnabled(boolean enabled);
   }

   /**
    * Presenter's display.
    */
   private Display display;

   /**
    * Opened project in Project Explorer.
    */
   private ProjectModel openedProject;

   public CloudFoundryProjectPresenter()
   {
      IDE.getInstance().addControl(new CloudFoundryControl());

      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(ManageCloudFoundryProjectEvent.TYPE, this);
      IDE.addHandler(ApplicationDeletedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ApplicationInfoChangedEvent.TYPE, this);
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

      display.getUpdateButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.eventBus().fireEvent(new UpdateApplicationEvent());
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
            IDE.eventBus().fireEvent(new ApplicationInfoEvent());
         }
      });

      display.getStartButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.eventBus().fireEvent(new StartApplicationEvent());
         }
      });

      display.getStopButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.eventBus().fireEvent(new StopApplicationEvent());
         }
      });

      display.getRestartButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.eventBus().fireEvent(new RestartApplicationEvent());
         }
      });

      display.getEditInstancesButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.eventBus().fireEvent(new UpdateInstancesEvent());
         }
      });

      display.getEditMemoryButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.eventBus().fireEvent(new UpdateMemoryEvent());
         }
      });

      display.getEditURLButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.eventBus().fireEvent(new UnmapUrlEvent());
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
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.ManageCloudFoundryProjectHandler#onManageCloudFoundryProject(org.exoplatform.ide.extension.cloudfoundry.client.project.ManageCloudFoundryProjectEvent)
    */
   @Override
   public void onManageCloudFoundryProject(ManageCloudFoundryProjectEvent event)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
         IDE.getInstance().openView(display.asView());
      }

      getApplicationInfo(openedProject);
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
    * Get application properties.
    * 
    * @param project
    */
   protected void getApplicationInfo(final ProjectModel project)
   {
      CloudFoundryClientService.getInstance().getApplicationInfo(vfs.getId(), project.getId(), null, null,
         new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(IDE.eventBus(), new LoggedInHandler()
         {
            @Override
            public void onLoggedIn()
            {
               getApplicationInfo(project);
            }
         }, null)
         {
            @Override
            protected void onSuccess(CloudfoundryApplication result)
            {
               displayApplicationProperties(result);
            }
         });
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.delete.ApplicationDeletedHandler#onApplicationDeleted(org.exoplatform.ide.extension.cloudfoundry.client.delete.ApplicationDeletedEvent)
    */
   @Override
   public void onApplicationDeleted(ApplicationDeletedEvent event)
   {
      if (display != null && event.getProjectId() != null && vfs.getId().equals(event.getVfsId())
         && openedProject != null && openedProject.getId().equals(event.getProjectId()))
      {
         IDE.getInstance().closeView(display.asView().getId());
      }
   }

   protected void displayApplicationProperties(CloudfoundryApplication application)
   {
      display.getApplicationName().setValue(application.getName());
      display.getApplicationInstances().setValue(String.valueOf(application.getInstances()));
      display.getApplicationMemory().setValue(String.valueOf(application.getResources().getMemory()) + "MB");
      display.getApplicationModel().setValue(String.valueOf(application.getStaging().getModel()));
      display.getApplicationStack().setValue(String.valueOf(application.getStaging().getStack()));

      if (application.getUris() != null && application.getUris().size() > 0)
      {
         display.setApplicationURL(application.getUris().get(0));
      }
      boolean isStarted = ("STARTED".equals(application.getState()));
      display.setStartButtonEnabled(!isStarted);
      display.setStopButtonEnabled(isStarted);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.ApplicationInfoChangedHandler#onApplicationInfoChanged(org.exoplatform.ide.extension.cloudfoundry.client.project.ApplicationInfoChangedEvent)
    */
   @Override
   public void onApplicationInfoChanged(ApplicationInfoChangedEvent event)
   {
      if (display != null && event.getProjectId() != null && vfs.getId().equals(event.getVfsId())
         && openedProject != null && openedProject.getId().equals(event.getProjectId()))
      {
         getApplicationInfo(openedProject);
      }
   }
}
