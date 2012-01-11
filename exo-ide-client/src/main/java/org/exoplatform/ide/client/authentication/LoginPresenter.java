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
package org.exoplatform.ide.client.authentication;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.exception.UnauthorizedException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.util.Log;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

/**
 * @author <a href="mailto:foo@bar.org">Foo Bar</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z aheritier $
 *
 */
public class LoginPresenter implements ViewClosedHandler, ExceptionThrownHandler, InitializeServicesHandler,
   UserInfoReceivedHandler
{

   /**
    * LoginDialog's display.
    */
   public interface Display extends IsView
   {

      HasClickHandlers getLoginButton();

      HasClickHandlers getCancelButton();

      void setLoginButtonEnabled(boolean enabled);

      TextFieldItem getLoginField();

      TextFieldItem getPasswordField();

   }

   /**
    * Display's instance.
    */
   private Display display;

   /**
    * Login.
    */
   private String login;

   /**
    * Password.
    */
   private String password;

   /**
    * Creates a new instance of LoginDialog.
    * 
    * @param eventBus
    */
   public LoginPresenter()
   {
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ExceptionThrownEvent.TYPE, this);
      IDE.addHandler(InitializeServicesEvent.TYPE, this);
      IDE.addHandler(UserInfoReceivedEvent.TYPE, this);

      /*
      // Uncomment this to show Image at the top of IDE to see how the Login Window looks.
      Image showLoginImage = new Image(IDEImageBundle.INSTANCE.browser());
      showLoginImage.getElement().getStyle().setZIndex(Integer.MAX_VALUE);
      RootPanel.get().add(showLoginImage, 300, 0);
      showLoginImage.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            showLoginDialog(null);
         }
      });
      */

   }

   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      //Nothing todo
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
    * Creates and shows new Login View.
    * 
    * @param loginCompleteHandler
    */
   public void showLoginDialog(final AsyncRequest asyncRequest)
   {
      if (display != null)
      {
         Window.alert("Another Login Dialog is opened!");
         return;
      }

      display = GWT.create(Display.class);

      display.getLoginButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            try
            {
               doLogin(asyncRequest);
            }
            catch (Exception e)
            {
               Log.info("Exception > " + e.getMessage());
            }
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      KeyPressHandler textFieldsKeyPressHandler = new KeyPressHandler()
      {
         @Override
         public void onKeyPress(KeyPressEvent event)
         {
            if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
            {
               try
               {
                  doLogin(asyncRequest);
               }
               catch (Exception e)
               {
                  Log.info("Exception > " + e.getMessage());
               }
            }
         }
      };

      display.getLoginField().addValueChangeHandler(valueChangeHandler);
      display.getLoginField().addKeyPressHandler(textFieldsKeyPressHandler);
      display.getPasswordField().addValueChangeHandler(valueChangeHandler);
      display.getPasswordField().addKeyPressHandler(textFieldsKeyPressHandler);

      display.getLoginField().setValue(login);
      display.getPasswordField().setValue(password);
      checkForLoginButtonEnabled();

      IDE.getInstance().openView(display.asView());
   }

   /**
    * Handle changing of the text in text fields.
    */
   ValueChangeHandler<String> valueChangeHandler = new ValueChangeHandler<String>()
   {
      @Override
      public void onValueChange(ValueChangeEvent<String> event)
      {
         checkForLoginButtonEnabled();
      }
   };

   /**
    * Checks for text in the text fields and enables or disables Login button.
    */
   private void checkForLoginButtonEnabled()
   {
      if (display.getLoginField().getValue() == null || display.getLoginField().getValue().trim().isEmpty()
         || display.getPasswordField().getValue() == null || display.getPasswordField().getValue().trim().isEmpty())
      {
         display.setLoginButtonEnabled(false);
         return;
      }

      display.setLoginButtonEnabled(true);

   }

   /**
    * Do Login.
    */
   private void doLogin(AsyncRequest asyncRequest)
   {
      login = display.getLoginField().getValue().trim();
      password = display.getPasswordField().getValue().trim();
      display.getLoginField().setValue(login);
      display.getPasswordField().setValue(password);

      hiddenLoadAuthorizationPage(asyncRequest);
   }

   private void hiddenLoadAuthorizationPage(final AsyncRequest asyncRequest)
   {
      String authorizationPageURL = getAuthorizationPageURL();
      try
      {
         RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, authorizationPageURL);
         requestBuilder.setCallback(new RequestCallback()
         {
            @Override
            public void onResponseReceived(Request request, Response response)
            {
               sendLoginRequest(asyncRequest);
            }

            @Override
            public void onError(Request request, Throwable exception)
            {
               Dialogs.getInstance().showError("Can not log in!");
            }
         });
         requestBuilder.send();

      }
      catch (Exception e)
      {
         Dialogs.getInstance().showError("Can not log in!");

         Log.info("Exception > " + e.getMessage());
      }
   }

   private void sendLoginRequest(final AsyncRequest asyncRequest)
   {
      StringBuffer postBuilder = new StringBuffer();
      postBuilder.append("j_username=");
      postBuilder.append(URL.encodeQueryString(login));
      postBuilder.append("&j_password=");
      postBuilder.append(URL.encodeQueryString(password));

      try
      {
         String securityCheckURL = getSecurityCheckURL();
         RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, securityCheckURL);
         requestBuilder.setHeader("Content-type", "application/x-www-form-urlencoded");
         requestBuilder.sendRequest(postBuilder.toString(), new RequestCallback()
         {
            @Override
            public void onResponseReceived(Request request, Response response)
            {
               IDE.getInstance().closeView(display.asView().getId());
               if (asyncRequest != null)
               {
                  asyncRequest.sendRequest();
               }
            }

            @Override
            public void onError(Request request, Throwable exception)
            {
               Dialogs.getInstance().showError("Can not log in!");
            }
         });

      }
      catch (Exception e)
      {
         Dialogs.getInstance().showError("Can not log in!");
         Log.info("Exception > " + e.getMessage());
      }
   }

   @Override
   public void onError(ExceptionThrownEvent event)
   {
      Throwable exception = event.getException();

      if (exception instanceof UnauthorizedException)
      {
         UnauthorizedException unauthorizedException = (UnauthorizedException)exception;
         AsyncRequest asyncRequest = unauthorizedException.getAsyncRequest();
         showLoginDialog(asyncRequest);
         return;
      }
   }

   @Override
   public void onUserInfoReceived(UserInfoReceivedEvent event)
   {
      login = event.getUserInfo().getName();
   }

   private native String getAuthorizationPageURL() /*-{
                                                   return $wnd.authorizationPageURL;
                                                   }-*/;

   private native String getSecurityCheckURL() /*-{
                                               return $wnd.securityCheckURL;
                                               }-*/;

}
