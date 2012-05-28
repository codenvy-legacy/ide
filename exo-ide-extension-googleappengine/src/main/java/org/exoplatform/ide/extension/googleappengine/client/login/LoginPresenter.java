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
package org.exoplatform.ide.extension.googleappengine.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;

/**
 * Presenter for log in Google App Engine operation. The view must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 18, 2012 12:19:01 PM anya $
 * 
 */
public class LoginPresenter implements LoginHandler, ViewClosedHandler, LoginFailedHandler, GAEOperationFailedHandler
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
      TextFieldItem getEmailField();

      /**
       * Get password field.
       * 
       * @return {@link HasValue}
       */
      TextFieldItem getPasswordField();

      /**
       * Login result label.
       * 
       * @return {@link String}
       */
      HasValue<String> getLoginResult();

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

   }

   private Display display;

   private PerformOperationHandler performOperationHandler;

   private LoginCanceledHandler loginCanceledHandler;

   private LoggedInHandler loggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         IDE.getInstance().closeView(display.asView().getId());
      }
   };

   public LoginPresenter()
   {
      IDE.addHandler(LoginEvent.TYPE, this);
      IDE.addHandler(LoginFailedEvent.TYPE, this);
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
            if (loginCanceledHandler != null)
               loginCanceledHandler.onLoginCanceled();

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

      display.getEmailField().addKeyUpHandler(new KeyUpHandler()
      {

         @Override
         public void onKeyUp(KeyUpEvent event)
         {
            if (event.getNativeKeyCode() == 13 && isFieldsFullFilled())
            {
               doLogin();
            }
         }
      });

      display.getPasswordField().addKeyUpHandler(new KeyUpHandler()
      {

         @Override
         public void onKeyUp(KeyUpEvent event)
         {
            if (event.getNativeKeyCode() == 13 && isFieldsFullFilled())
            {
               doLogin();
            }
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
      performOperationHandler = event.getPerformOperationHandler();
      loginCanceledHandler = event.getLoginCanceled();
      if (display == null)
      {
         Display display = GWT.create(Display.class);
         bindDisplay(display);
         IDE.getInstance().openView(display.asView());

         IDE.addHandler(GAEOperationFailedEvent.TYPE, this);
         display.enableLoginButton(false);
         display.focusInEmailField();
         display.getLoginResult().setValue("");
      }
      else
      {
         display.getLoginResult().setValue(GoogleAppEngineExtension.GAE_LOCALIZATION.loginFailedMessage());
      }
   }

   /**
    * Perform log in Google App Engine account.
    */
   protected void doLogin()
   {
      final String email = display.getEmailField().getValue();
      final String password = display.getPasswordField().getValue();
      performOperationHandler.onPerformOperation(email, password, loggedInHandler);
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
         loginCanceledHandler = null;
         IDE.removeHandler(GAEOperationFailedEvent.TYPE, this);
      }
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.login.LoginFailedHandler#onLoginFailed(org.exoplatform.ide.extension.googleappengine.client.login.LoginFailedEvent)
    */
   @Override
   public void onLoginFailed(LoginFailedEvent event)
   {
      if (display != null)
      {
         display.getLoginResult().setValue(GoogleAppEngineExtension.GAE_LOCALIZATION.loginFailedMessage());
      }
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.login.GAEOperationFailedHandler#onGAEOperationFailed(org.exoplatform.ide.extension.googleappengine.client.login.GAEOperationFailedEvent)
    */
   @Override
   public void onGAEOperationFailed(GAEOperationFailedEvent event)
   {
      if (display != null)
      {
         IDE.getInstance().closeView(display.asView().getId());
      }
   }
}
