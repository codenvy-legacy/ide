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
package org.exoplatform.ide.extension.appfog.client.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ActiveProjectChangedEvent;
import org.exoplatform.ide.client.framework.project.ActiveProjectChangedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.appfog.client.AppfogAsyncRequestCallback;
import org.exoplatform.ide.extension.appfog.client.AppfogClientService;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;
import org.exoplatform.ide.extension.appfog.client.delete.ApplicationDeletedEvent;
import org.exoplatform.ide.extension.appfog.client.delete.ApplicationDeletedHandler;
import org.exoplatform.ide.extension.appfog.client.delete.DeleteApplicationEvent;
import org.exoplatform.ide.extension.appfog.client.info.ApplicationInfoEvent;
import org.exoplatform.ide.extension.appfog.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.appfog.client.marshaller.StringUnmarshaller;
import org.exoplatform.ide.extension.appfog.client.services.ManageServicesEvent;
import org.exoplatform.ide.extension.appfog.client.start.RestartApplicationEvent;
import org.exoplatform.ide.extension.appfog.client.start.StartApplicationEvent;
import org.exoplatform.ide.extension.appfog.client.start.StopApplicationEvent;
import org.exoplatform.ide.extension.appfog.client.update.UpdateApplicationEvent;
import org.exoplatform.ide.extension.appfog.client.update.UpdateInstancesEvent;
import org.exoplatform.ide.extension.appfog.client.update.UpdateMemoryEvent;
import org.exoplatform.ide.extension.appfog.client.url.UnmapUrlEvent;
import org.exoplatform.ide.extension.appfog.shared.AppfogApplication;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Presenter for managing project, deployed on Appfog.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class AppfogProjectPresenter extends GitPresenter implements /*ProjectOpenedHandler, ProjectClosedHandler,*/
   ManageAppfogProjectHandler, ViewClosedHandler, ApplicationDeletedHandler, ApplicationInfoChangedHandler
   /*, ActiveProjectChangedHandler*/
{
   interface Display extends IsView
   {
      HasClickHandlers getCloseButton();

      HasClickHandlers getUpdateButton();

      HasClickHandlers getLogsButton();

      HasClickHandlers getServicesButton();

      HasClickHandlers getDeleteButton();

      HasClickHandlers getInfoButton();

      HasValue<String> getApplicationName();

      void setApplicationURL(String url);

      HasValue<String> getApplicationModel();

      HasValue<String> getApplicationInfra();

      HasValue<String> getApplicationStack();

      HasValue<String> getApplicationInstances();

      HasValue<String> getApplicationMemory();

      HasValue<String> getApplicationStatus();

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

//   /**
//    * Opened project in Project Explorer.
//    */
//   private ProjectModel openedProject;

   private AppfogApplication application;

   public AppfogProjectPresenter()
   {
      IDE.getInstance().addControl(new AppfogControl());

//      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
//      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(ManageAppfogProjectEvent.TYPE, this);
      IDE.addHandler(ApplicationDeletedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ApplicationInfoChangedEvent.TYPE, this);
//      IDE.addHandler(ActiveProjectChangedEvent.TYPE, this);
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

      display.getLogsButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            getLogs();
         }
      });

      display.getServicesButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.fireEvent(new ManageServicesEvent(application));
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

   protected void getLogs()
   {
      try
      {
         ProjectModel project = getSelectedProject();
         
         AppfogClientService.getInstance().getLogs(vfs.getId(), project.getId(),
            new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder()))
            {

               @Override
               protected void onSuccess(StringBuilder result)
               {
                  IDE.fireEvent(new OutputEvent("<pre>" + result.toString() + "</pre>", Type.OUTPUT));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception.getMessage()));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e.getMessage()));
         e.printStackTrace();
      }
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
   public void onManageAppfogProject(ManageAppfogProjectEvent event)
   {
//      getApplicationInfo(openedProject);
      getApplicationInfo(getSelectedProject());
   }

//   /**
//    * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
//    */
//   @Override
//   public void onProjectClosed(ProjectClosedEvent event)
//   {
//      openedProject = null;
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
//    */
//   @Override
//   public void onProjectOpened(ProjectOpenedEvent event)
//   {
//      openedProject = event.getProject();
//   }
//
//   @Override
//   public void onActiveProjectChanged(ActiveProjectChangedEvent event)
//   {
//      openedProject = event.getProject();
//   }

   /**
    * Get application properties.
    *
    * @param project
    */
   protected void getApplicationInfo(final ProjectModel project)
   {
      try
      {
         AutoBean<AppfogApplication> appfogApplication =
            AppfogExtension.AUTO_BEAN_FACTORY.appfogApplication();

         AutoBeanUnmarshaller<AppfogApplication> unmarshaller =
            new AutoBeanUnmarshaller<AppfogApplication>(appfogApplication);

         AppfogClientService.getInstance().getApplicationInfo(vfs.getId(), project.getId(), null, null,
            new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, new LoggedInHandler()
            {
               @Override
               public void onLoggedIn()
               {
                  getApplicationInfo(project);
               }
            }, null)
            {
               @Override
               protected void onSuccess(AppfogApplication result)
               {
                  if (display == null)
                  {
                     display = GWT.create(Display.class);
                     bindDisplay();
                     IDE.getInstance().openView(display.asView());
                  }
                  application = result;
                  displayApplicationProperties(result);
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.delete.ApplicationDeletedHandler#onApplicationDeleted(org.exoplatform.ide.extension.cloudfoundry.client.delete.ApplicationDeletedEvent)
    */
   @Override
   public void onApplicationDeleted(ApplicationDeletedEvent event)
   {
//      if (event.getApplicationName() != null && openedProject != null
//         && event.getApplicationName().equals((String)openedProject.getPropertyValue("appfog-application")))
      if (event.getApplicationName() != null && getSelectedProject() != null
               && event.getApplicationName().equals((String)getSelectedProject().getPropertyValue("appfog-application")))
      {
         if (display != null)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
         //IDE.fireEvent(new RefreshBrowserEvent(openedProject));
         IDE.fireEvent(new RefreshBrowserEvent(getSelectedProject()));
      }
   }

   protected void displayApplicationProperties(AppfogApplication application)
   {
      display.getApplicationName().setValue(application.getName());
      display.getApplicationInstances().setValue(String.valueOf(application.getInstances()));
      display.getApplicationMemory().setValue(String.valueOf(application.getResources().getMemory()) + "MB");
      display.getApplicationModel().setValue(String.valueOf(application.getStaging().getModel()));
      display.getApplicationStack().setValue(String.valueOf(application.getStaging().getStack()));
      display.getApplicationStatus().setValue(String.valueOf(application.getState()));
      display.getApplicationInfra().setValue(String.valueOf(application.getInfra().getProvider()));

      if (application.getUris() != null && application.getUris().size() > 0)
      {
         display.setApplicationURL(application.getUris().get(0));
      }
      else
      {
         //Set empty field if we specialy unmap all urls and closed url controller window, if whe don't do this, in
         //info window will be appear old url, that is not good
         display.setApplicationURL(null);
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
//      if (display != null && event.getProjectId() != null && vfs.getId().equals(event.getVfsId())
//         && openedProject != null && openedProject.getId().equals(event.getProjectId()))
//      {
//         getApplicationInfo(openedProject);
//      }
      if (display != null && event.getProjectId() != null && vfs.getId().equals(event.getVfsId())
               && getSelectedProject() != null && getSelectedProject().getId().equals(event.getProjectId()))
            {
               getApplicationInfo(getSelectedProject());
            }
   }

}
