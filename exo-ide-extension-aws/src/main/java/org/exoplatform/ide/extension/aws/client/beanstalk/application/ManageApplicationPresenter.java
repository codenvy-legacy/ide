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
package org.exoplatform.ide.extension.aws.client.beanstalk.application;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService;
import org.exoplatform.ide.extension.aws.client.beanstalk.login.LoggedInHandler;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 19, 2012 11:34:35 AM anya $
 * 
 */
public class ManageApplicationPresenter implements ProjectOpenedHandler, ProjectClosedHandler,
   ManageApplicationHandler, VfsChangedHandler, ViewClosedHandler
{

   interface Display extends IsView
   {
      // GeneralInfo

      HasValue<String> getApplicationNameField();

      HasValue<String> getDescriptionField();

      HasValue<String> getCreateDateField();

      HasValue<String> getUpdatedDateField();

      HasClickHandlers getDeleteButton();

      HasClickHandlers getUpdateDescriptionButton();

      HasClickHandlers getCloseButton();

   }

   private Display display;

   private ProjectModel openedProject;

   private VirtualFileSystemInfo currentVfs;

   public ManageApplicationPresenter()
   {
      IDE.getInstance().addControl(new ManageApplicationControl());

      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(ManageApplicationEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationHandler#onManageApplication(org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationEvent)
    */
   @Override
   public void onManageApplication(ManageApplicationEvent event)
   {
      /*
       * TODO if (openedProject == null || !AWSExtension.isAWSApplication(openedProject)) {
       * Dialogs.getInstance().showInfo(AWSExtension.LOCALIZATION_CONSTANT.notAWSApplictaionMessage()); return; }
       */
/*TODO
      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();
      }*/
   }

   public void bindDisplay()
   {
      display.getCloseButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getDeleteButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            askForDelete();
         }
      });
      
      display.getUpdateDescriptionButton().addClickHandler(new ClickHandler()
      {
         
         @Override
         public void onClick(ClickEvent event)
         {
            //TODO
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
    */
   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      this.openedProject = null;
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
    */
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      this.openedProject = event.getProject();
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.currentVfs = event.getVfsInfo();
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

   private void askForDelete()
   {
      // TODO insert application name:
      final String applicationName = "test";
      Dialogs.getInstance().ask(AWSExtension.LOCALIZATION_CONSTANT.deleteApplicationTitle(),
         AWSExtension.LOCALIZATION_CONSTANT.deleteApplicationQuestion(applicationName),
         new BooleanValueReceivedHandler()
         {

            @Override
            public void booleanValueReceived(Boolean value)
            {
               if (value != null && value)
               {
                  deleteApplication(applicationName);
               }
            }
         });
   }

   public void deleteApplication(final String applicationName)
   {
      try
      {
         BeanstalkClientService.getInstance().deleteApplication(currentVfs.getId(), openedProject.getId(),
            new BeanstalkAsyncRequestCallback<Object>(new LoggedInHandler()
            {

               @Override
               public void onLoggedIn()
               {
                  deleteApplication(applicationName);
               }
            })
            {
               @Override
               protected void processFail(Throwable exception)
               {
                  String message = AWSExtension.LOCALIZATION_CONSTANT.deleteApplicationFailed(applicationName);
                  if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null)
                  {
                     message += "<br>" + ((ServerException)exception).getMessage();
                  }
                  IDE.fireEvent(new OutputEvent(message, Type.ERROR));
               }

               @Override
               protected void onSuccess(Object result)
               {
                  IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT
                     .deleteApplicationSuccess(applicationName), Type.INFO));
                  if (display != null)
                  {
                     IDE.getInstance().closeView(display.asView().getId());
                  }
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }

   }
}
