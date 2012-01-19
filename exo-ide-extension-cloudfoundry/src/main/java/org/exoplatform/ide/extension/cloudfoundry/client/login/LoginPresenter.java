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
package org.exoplatform.ide.extension.cloudfoundry.client.login;

import java.util.List;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Presenter for login view.
 * The view must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 25, 2011 3:56:55 PM anya $
 *
 */
public class LoginPresenter implements LoginHandler, ViewClosedHandler
{
   interface Display extends IsView
   {
      /**
       * Get login button click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getLoginButton();

      /**
       * Get cancel button click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getCancelButton();

      /**
       * Get email field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getEmailField();

      /**
       * Get password field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getPasswordField();
      
      /**
       * Get target select item.
       * @return
       */
      HasValue<String> getTargetSelectField();
      
      /**
       * Get the label, where error message will be displayed.
       * @return
       */
      HasValue<String> getErrorLabelField();

      /**
       * Change the enable state of the login button.
       * 
       * @param enabled
       */
      void enableLoginButton(boolean enabled);

      /**
       * Give focus to login field.
       */
      void focusInEmailField();
      
      /**
       * Set the list of available targets.
       * @param targets
       */
      void setTargetValues(String[] targets);
   }
   
   private static final CloudFoundryLocalizationConstant lb = CloudFoundryExtension.LOCALIZATION_CONSTANT;

   private Display display;

   private LoggedInHandler loggedIn;

   private LoginCanceledHandler loginCanceled;
   
   /**
    * The last server, that user logged in.
    */
   private String server;

   public LoginPresenter()
   {
      IDE.addHandler(LoginEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * Bind display with presenter.
    * 
    * @param d
    */
   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            if (loginCanceled != null)
               loginCanceled.onLoginCanceled();

            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getLoginButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            doLogin();
         }
      });

      display.getEmailField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            display.enableLoginButton(isFieldsFullFilled());
         }
      });

      display.getPasswordField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            display.enableLoginButton(isFieldsFullFilled());
         }
      });

   }

   /**
    * Check whether necessary fields are fullfilled.
    * 
    * @return if <code>true</code> all necessary fields are fullfilled
    */
   private boolean isFieldsFullFilled()
   {
      return (display.getEmailField().getValue() != null && !display.getEmailField().getValue().isEmpty()
         && display.getPasswordField().getValue() != null && !display.getPasswordField().getValue().isEmpty());
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.login.LoginHandler#onLogin(org.exoplatform.ide.extension.openshift.client.login.LoginEvent)
    */
   @Override
   public void onLogin(LoginEvent event)
   {
      loggedIn = event.getLoggedIn();
      loginCanceled = event.getLoginCanceled();
      if (event.getLoginUrl() != null)
      {
         server = event.getLoginUrl();
      }
      if (display == null)
      {
         Display display = GWT.create(Display.class);
         bindDisplay(display);
         IDE.getInstance().openView(display.asView());
         display.enableLoginButton(false);
         display.focusInEmailField();
         getSystemInformation();
      }

   }

   /**
    * Get Cloud Foundry system information to fill the login field,
    *  if user is logged in.
    */
   protected void getSystemInformation()
   {
      CloudFoundryClientService.getInstance().getSystemInfo(server, new AsyncRequestCallback<SystemInfo>()
      {
         @Override
         protected void onSuccess(SystemInfo result)
         {
            display.getEmailField().setValue(result.getUser());
            getTargets();
         }

         /**
          * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
          */
         @Override
         protected void onFailure(Throwable exception)
         {
            if (exception instanceof UnmarshallerException)
            {
               Dialogs.getInstance().showError(exception.getMessage());
            }
            else
            {
               getTargets();
            }
         }
      });
   }
   
   private void getTargets()
   {
      CloudFoundryClientService.getInstance().getTargets(new AsyncRequestCallback<List<String>>()
      {
         @Override
         protected void onSuccess(List<String> result)
         {
            if (result.isEmpty())
            {
               display.setTargetValues(new String[]{CloudFoundryExtension.DEFAULT_SERVER});
               if (server == null || server.isEmpty())
               {
                  display.getTargetSelectField().setValue(CloudFoundryExtension.DEFAULT_SERVER);
               }
               else
                  display.getTargetSelectField().setValue(server);
            }
            else
            {
               String[] targets = new String[result.size()];
               targets = result.toArray(targets);
               display.setTargetValues(targets);
               if (server == null || server.isEmpty())
               {
                  display.getTargetSelectField().setValue(result.get(0));
               }
               else
                  display.getTargetSelectField().setValue(server);
            }
         }
      });
   }
   
   /**
    * Perform log in OpenShift.
    */
   protected void doLogin()
   {
      final String enteredServer = display.getTargetSelectField().getValue();
      final String email = display.getEmailField().getValue();
      final String password = display.getPasswordField().getValue();

      CloudFoundryClientService.getInstance().login(enteredServer, email, password, new AsyncRequestCallback<String>()
      {
         /**
          * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onSuccess(java.lang.Object)
          */
         @Override
         protected void onSuccess(String result)
         {
            server = enteredServer;
            IDE.fireEvent(new OutputEvent(lb.loginSuccess(), Type.INFO));
            if (loggedIn != null)
            {
               loggedIn.onLoggedIn();
            }
            IDE.getInstance().closeView(display.asView().getId());
         }

         /**
          * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
          */
         @Override
         protected void onFailure(Throwable exception)
         {
            IDE.fireEvent(new OutputEvent(lb.loginFailed(), Type.INFO));
            if (exception instanceof ServerException)
            {
               ServerException serverException = (ServerException)exception;
               if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus() && serverException.getMessage() != null
                        && serverException.getMessage().contains("Can't access target."))
               {
                  display.getErrorLabelField().setValue(lb.loginViewErrorUnknownTarget());
                  return;
               }
               else if (HTTPStatus.OK != serverException.getHTTPStatus() && serverException.getMessage() != null
                        && serverException.getMessage().contains("Operation not permitted"))
               {
                  display.getErrorLabelField().setValue(lb.loginViewErrorInvalidUserOrPassword());
                  return;
               }
               //otherwise will be called method from superclass.
            }
            super.onFailure(exception);
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
         loggedIn = null;
         loginCanceled = null;
      }
   }
}
