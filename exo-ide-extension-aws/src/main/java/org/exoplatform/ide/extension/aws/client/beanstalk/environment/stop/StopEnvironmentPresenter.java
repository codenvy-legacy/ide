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
package org.exoplatform.ide.extension.aws.client.beanstalk.environment.stop;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService;
import org.exoplatform.ide.extension.aws.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentStatus;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 20, 2012 5:01:04 PM anya $
 * 
 */
public class StopEnvironmentPresenter implements StopEnvironmentHandler, ViewClosedHandler
{
   interface Display extends IsView
   {
      HasClickHandlers getDeleteButton();

      HasClickHandlers getCancelButton();

      HasValue<String> getDeleteQuestion();

//      HasValue<Boolean> getDeleteS3Bundle();
   }

   private Display display;

   private String vfsId;

   private String projectId;

   private EnvironmentInfo environment;

//   private VersionDeletedHandler versionDeletedHandler;

   public StopEnvironmentPresenter()
   {
      IDE.addHandler(StopEnvironmentEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   public void bindDisplay()
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
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
            if (environment.getStatus().equals(EnvironmentStatus.Ready))
              stopEnvironment();
            else
               Dialogs.getInstance().showError("Environment alredy terminated"); 
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
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.versions.delete.DeleteVersionHandler#onDeleteVersion(org.exoplatform.ide.extension.aws.client.beanstalk.application.versions.DeleteVersionEvent)
    */
   @Override
   public void onStopEnvironment(StopEnvironmentEvent event)
   {
      this.environment = event.getEnvironmentInfo();
      if (!environment.getStatus().equals(EnvironmentStatus.Ready))
      {
        Dialogs.getInstance().showError("Environment alredy terminated");
        return;
      }

      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();
      }
      display.getDeleteQuestion().setValue(
         AWSExtension.LOCALIZATION_CONSTANT.stopEnvironmentQuestion(environment.getId()));
   }

   private void stopEnvironment()
   {
      try
      {
         AutoBean<EnvironmentInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.environmentInfo();
         AutoBeanUnmarshaller<EnvironmentInfo> unmarshaller = new AutoBeanUnmarshaller<EnvironmentInfo>(autoBean);
         BeanstalkClientService.getInstance().stopEnvironment(environment.getId(),
            new AwsAsyncRequestCallback<EnvironmentInfo>(unmarshaller, new LoggedInHandler()
            {

               @Override
               public void onLoggedIn()
               {
                  stopEnvironment();
               }
            })
            {

               @Override
               protected void processFail(Throwable exception)
               {
                  String message = AWSExtension.LOCALIZATION_CONSTANT.deleteVersionFailed(environment.getId());
                  if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null)
                  {
                     message += "<br>" + ((ServerException)exception).getMessage();
                  }
                  Dialogs.getInstance().showError(message);
               }

               @Override
               protected void onSuccess(EnvironmentInfo result)
               {
                  IDE.getInstance().closeView(display.asView().getId());
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }
}
