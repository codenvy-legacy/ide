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
package org.exoplatform.ide.extension.heroku.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;

/**
 * Presenter for login view.
 * The view must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 25, 2011 3:56:55 PM anya $
 *
 */
public class LoginPresenter implements LoginHandler, ViewClosedHandler, SwitchAccountHandler
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
       * Get login as demo user button click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getLoginDemoButton();

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

   private HandlerManager eventBus;
   
   private boolean loggedIn;

   public LoginPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(LoginEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(SwitchAccountEvent.TYPE, this);
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
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getLoginButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            final String email = display.getEmailField().getValue();
            final String password = display.getPasswordField().getValue();
            if (loggedIn)
            {
               HerokuClientService.getInstance().logout(new AsyncRequestCallback<String>()
               {
                  @Override
                  protected void onSuccess(String result)
                  {
                     doLogin(email, password);
                  }

               });
            }
            else
            {
               doLogin(email, password);
            }
         }
      });
      
      display.getLoginDemoButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            final String demoLogin = HerokuExtension.CREDENTIALS_CONSTANT.loginDemoAccountEmail();
            final String demoPassword = HerokuExtension.CREDENTIALS_CONSTANT.loginDemoAccountPassword();
            if (loggedIn)
            {
               HerokuClientService.getInstance().logout(new AsyncRequestCallback<String>()
               {
                  @Override
                  protected void onSuccess(String result)
                  {
                     doLogin(demoLogin, demoPassword);
                  }

               });
            }
            else
            {
               doLogin(demoLogin, demoPassword);
            }
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
    * @see org.exoplatform.ide.extension.heroku.client.login.LoginHandler#onLogin(org.exoplatform.ide.extension.heroku.client.login.LoginEvent)
    */
   @Override
   public void onLogin(LoginEvent event)
   {
      if (display == null)
      {
         openLoginForm();
      }
   }

   /**
    * Perform log in Heroku.
    */
   protected void doLogin(String email, String password)
   {
      HerokuClientService.getInstance().login(email, password, new AsyncRequestCallback<String>()
      {
         @Override
         protected void onSuccess(String result)
         {
            IDE.getInstance().closeView(display.asView().getId());
            loggedIn = true;
            eventBus.fireEvent(new OutputEvent(HerokuExtension.LOCALIZATION_CONSTANT.loginSuccess(), Type.INFO));
            eventBus.fireEvent(new LoggedInEvent(false));
         }

         /**
          * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
          */
         @Override
         protected void onFailure(Throwable exception)
         {
            eventBus.fireEvent(new LoggedInEvent(true));
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
      }
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.login.SwitchAccountHandler#onSwitchAccount(org.exoplatform.ide.extension.heroku.client.login.SwitchAccountEvent)
    */
   @Override
   public void onSwitchAccount(SwitchAccountEvent event)
   {
      if (display == null)
      {
         openLoginForm();
      }
   }
   
   /**
    * Open form to login to Heroku
    */
   private void openLoginForm()
   {
      Display display = GWT.create(Display.class);
      bindDisplay(display);
      IDE.getInstance().openView(display.asView());
      display.enableLoginButton(false);
      display.focusInEmailField();
   }
}
