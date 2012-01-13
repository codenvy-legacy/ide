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
package org.exoplatform.ide.extension.openshift.client.key;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientService;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExceptionThrownEvent;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoginEvent;
import org.exoplatform.ide.extension.openshift.shared.RHUserInfo;

/**
 * Presenter for updating public key on OpenShift. First - get user's information to retrieve domain name, then update public SSH
 * key.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 21, 2011 12:36:44 PM anya $
 * 
 */
public class UpdatePublicKeyCommandHandler implements UpdatePublicKeyHandler, LoggedInHandler
{

   /**
    *
    */
   public UpdatePublicKeyCommandHandler()
   {
      IDE.addHandler(UpdatePublicKeyEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.key.UpdatePublicKeyHandler#onUpdatePublicKey(org.exoplatform.ide.extension.openshift.client.key.UpdatePublicKeyEvent)
    */
   @Override
   public void onUpdatePublicKey(UpdatePublicKeyEvent event)
   {
      getUserInfo();
   }

   /**
    * Get user's information.
    */
   public void getUserInfo()
   {
      OpenShiftClientService.getInstance().getUserInfo(false, new AsyncRequestCallback<RHUserInfo>()
      {

         @Override
         protected void onSuccess(RHUserInfo result)
         {
            doUpdatePublicKey(result.getNamespace());
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
               .getUserInfoFail()));
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
         getUserInfo();
      }
   }

   /**
    * Perform updating SSH public key on OpenShift.
    * 
    * @param namespace domain name
    */
   public void doUpdatePublicKey(String namespace)
   {
      OpenShiftClientService.getInstance().createDomain(namespace, true, new AsyncRequestCallback<String>()
      {

         @Override
         protected void onSuccess(String result)
         {
            IDE.fireEvent(new OutputEvent(OpenShiftExtension.LOCALIZATION_CONSTANT.updatePublicKeySuccess(), Type.INFO));
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
               .updatePublicKeyFailed()));
         }
      });
   }
}
