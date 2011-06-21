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
package org.exoplatform.ide.extension.openshift.client.delete;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientService;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExceptionThrownEvent;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoginEvent;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.git.client.GitPresenter;

/**
 * Presenter for deleting application.
 * Following steps are done:<br>
 * <ol>
 * <li>Get location of the Git working directory.</li>
 * <li>Get application information by Git working directory.</li>
 * <li>Ask user to delete application (pointed by name).</li>
 * <li>Delete application by it's name.</li>
 * </ol>
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 9, 2011 12:27:01 PM anya $
 *
 */
public class DeleteApplicationPresenter extends GitPresenter implements DeleteApplicationHandler, LoggedInHandler
{

   /**
    * @param eventBus
    */
   public DeleteApplicationPresenter(HandlerManager eventBus)
   {
      super(eventBus);
      eventBus.addHandler(DeleteApplicationEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.delete.DeleteApplicationHandler#onDeleteApplication(org.exoplatform.ide.extension.openshift.client.delete.DeleteApplicationEvent)
    */
   @Override
   public void onDeleteApplication(DeleteApplicationEvent event)
   {
      getWorkDir();
   }

   /**
    * @see org.exoplatform.ide.git.client.GitPresenter#onWorkDirReceived()
    */
   @Override
   public void onWorkDirReceived()
   {
      getApplicationsInfo();
   }

   /**
    * Get applications information.
    */
   protected void getApplicationsInfo()
   {
      OpenShiftClientService.getInstance().getApplicationInfo(null, workDir, new AsyncRequestCallback<AppInfo>()
      {

         @Override
         protected void onSuccess(AppInfo result)
         {
            askDeleteApplication(result.getName());
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
               if (HTTPStatus.UNAUTHORIZED == serverException.getHTTPStatus())
               {
                  addLoggedInHandler();
                  eventBus.fireEvent(new LoginEvent());
                  return;
               }
            }
            eventBus.fireEvent(new OpenShiftExceptionThrownEvent(exception, OpenShiftExtension.LOCALIZATION_CONSTANT.getApplicationInfoFail()));
         }
      });
   }

   /**
    * Register {@link LoggedInHandler} handler.
    */
   protected void addLoggedInHandler()
   {
      eventBus.addHandler(LoggedInEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent)
    */
   @Override
   public void onLoggedIn(LoggedInEvent event)
   {
      eventBus.removeHandler(LoggedInEvent.TYPE, this);
      if (!event.isFailed())
      {
         getApplicationsInfo();
      }
   }

   /**
    * Confirm the deleting of the application on OpenShift.
    * 
    * @param name application's name
    */
   protected void askDeleteApplication(final String name)
   {
      Dialogs.getInstance().ask(OpenShiftExtension.LOCALIZATION_CONSTANT.deleteApplicationTitle(),
         OpenShiftExtension.LOCALIZATION_CONSTANT.deleteApplication(name), new BooleanValueReceivedHandler()
         {

            @Override
            public void booleanValueReceived(Boolean value)
            {
               if (value != null && value)
               {
                  doDeleteApplication(name);
               }
            }
         });
   }

   /**
    * Perform deleting application on OpenShift.
    * 
    * @param name application's name
    */
   protected void doDeleteApplication(final String name)
   {
      OpenShiftClientService.getInstance().destroyApplication(name, new AsyncRequestCallback<String>()
      {

         @Override
         protected void onSuccess(String result)
         {
            eventBus.fireEvent(new OutputEvent(OpenShiftExtension.LOCALIZATION_CONSTANT.deleteApplicationSuccess(name),
               Type.INFO));
         }
         
         /**
          * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
          */
         @Override
         protected void onFailure(Throwable exception)
         {
            eventBus.fireEvent(new OpenShiftExceptionThrownEvent(exception, OpenShiftExtension.LOCALIZATION_CONSTANT.deleteApplicationFail(name)));
         }
      });
   }
}
