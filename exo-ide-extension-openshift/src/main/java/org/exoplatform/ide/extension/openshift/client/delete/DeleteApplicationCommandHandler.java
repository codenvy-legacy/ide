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

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
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
import org.exoplatform.ide.vfs.client.model.ItemContext;

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
public class DeleteApplicationCommandHandler extends GitPresenter implements DeleteApplicationHandler, LoggedInHandler
{

   /**
    *
    */
   public DeleteApplicationCommandHandler()
   {
      IDE.addHandler(DeleteApplicationEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.delete.DeleteApplicationHandler#onDeleteApplication(org.exoplatform.ide.extension.openshift.client.delete.DeleteApplicationEvent)
    */
   @Override
   public void onDeleteApplication(DeleteApplicationEvent event)
   {
      if (makeSelectionCheck())
      {
         getApplicationsInfo();
      }
   }

   /**
    * Get applications information.
    */
   protected void getApplicationsInfo()
   {
      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      OpenShiftClientService.getInstance().getApplicationInfo(null, vfs.getId(), projectId,
         new AsyncRequestCallback<AppInfo>()
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
      final String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      OpenShiftClientService.getInstance().destroyApplication(name, vfs.getId(), projectId,
         new AsyncRequestCallback<String>()
         {

            @Override
            protected void onSuccess(String result)
            {
               IDE.fireEvent(new OutputEvent(OpenShiftExtension.LOCALIZATION_CONSTANT.deleteApplicationSuccess(name),
                  Type.INFO));
               IDE.fireEvent(new ApplicationDeletedEvent(vfs.getId(), projectId));
               IDE.fireEvent(new RefreshBrowserEvent(((ItemContext)selectedItems.get(0)).getProject()));
            }

            /**
             * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
             */
            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new OpenShiftExceptionThrownEvent(exception, OpenShiftExtension.LOCALIZATION_CONSTANT
                  .deleteApplicationFail(name)));
            }
         });
   }
}
