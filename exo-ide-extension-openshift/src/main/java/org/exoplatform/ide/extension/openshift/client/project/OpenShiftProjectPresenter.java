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
package org.exoplatform.ide.extension.openshift.client.project;

import com.google.gwt.http.client.RequestException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.copy.ServerException;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientService;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExceptionThrownEvent;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.delete.ApplicationDeletedEvent;
import org.exoplatform.ide.extension.openshift.client.delete.ApplicationDeletedHandler;
import org.exoplatform.ide.extension.openshift.client.delete.DeleteApplicationEvent;
import org.exoplatform.ide.extension.openshift.client.info.ShowApplicationInfoEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoginEvent;
import org.exoplatform.ide.extension.openshift.client.marshaller.AppInfoUmarshaller;
import org.exoplatform.ide.extension.openshift.client.preview.PreviewApplicationEvent;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Presenter for managing project, deployed on OpenShift.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 8, 2011 9:39:29 AM anya $
 * 
 */
public class OpenShiftProjectPresenter extends GitPresenter implements ProjectOpenedHandler, ProjectClosedHandler,
   ViewClosedHandler, ManageOpenShiftProjectHandler, LoggedInHandler, ApplicationDeletedHandler
{

   interface Display extends IsView
   {

      HasClickHandlers getCloseButton();

      HasClickHandlers getPreviewButton();

      HasClickHandlers getDeleteButton();

      HasClickHandlers getInfoButton();

      HasValue<String> getApplicationName();

      void setApplicationURL(String URL);

      HasValue<String> getApplicationType();
   }

   private Display display;

   private ProjectModel openedProject;

   public OpenShiftProjectPresenter()
   {
      IDE.getInstance().addControl(new OpenShiftControl());

      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(ManageOpenShiftProjectEvent.TYPE, this);
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

      display.getPreviewButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.eventBus().fireEvent(new PreviewApplicationEvent());
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
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.project.ManageOpenShiftProjectHandler#onManageOpenShiftProject(org.exoplatform.ide.extension.openshift.client.project.ManageOpenShiftProjectEvent)
    */
   @Override
   public void onManageOpenShiftProject(ManageOpenShiftProjectEvent event)
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
   public void getApplicationInfo()
   {
      try
      {
         OpenShiftClientService.getInstance().getApplicationInfo(null, vfs.getId(), openedProject.getId(),
            new AsyncRequestCallback<AppInfo>(new AppInfoUmarshaller(new AppInfo()))
            {

               @Override
               protected void onSuccess(AppInfo result)
               {
                  display.getApplicationName().setValue(result.getName());
                  display.setApplicationURL(result.getPublicUrl());
                  display.getApplicationType().setValue(result.getType());
               }

               /**
                * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
                */
               @Override
               protected void onFailure(Throwable exception)
               {
                  if (exception instanceof ServerException)
                  {
                     ServerException serverException = (ServerException)exception;
                     if (HTTPStatus.OK == serverException.getHTTPStatus()
                        && "Authentication-required".equals(serverException.getHeader(HTTPHeader.JAXRS_BODY_PROVIDED)))
                     {
                        addLoggedInHandler();
                        IDE.fireEvent(new LoginEvent());
                        return;
                     }
                  }
                  IDE.fireEvent(new OpenShiftExceptionThrownEvent(exception, OpenShiftExtension.LOCALIZATION_CONSTANT
                     .getApplicationInfoFail()));
               }

            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new OpenShiftExceptionThrownEvent(e, OpenShiftExtension.LOCALIZATION_CONSTANT
            .getApplicationInfoFail()));
      }
   }

   /**
    * Register {@link LoggedInHandler} handler.
    */
   protected void addLoggedInHandler()
   {
      IDE.addHandler(LoggedInEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent)
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
    * @see org.exoplatform.ide.extension.openshift.client.delete.ApplicationDeletedHandler#onApplicationDeleted(org.exoplatform.ide.extension.openshift.client.delete.ApplicationDeletedEvent)
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
