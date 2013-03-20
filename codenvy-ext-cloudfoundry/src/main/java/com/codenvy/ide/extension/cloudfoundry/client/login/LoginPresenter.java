/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.extension.cloudfoundry.client.login;

import com.codenvy.ide.api.ui.console.Console;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAutoBeanFactory;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.TargetsUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.shared.SystemInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.codenvy.ide.rest.HTTPStatus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for logging on CloudFoundry.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 25, 2011 3:56:55 PM anya $
 */
@Singleton
public class LoginPresenter implements LoginView.ActionDelegate
{
   private LoginView view;

   private Console console;

   /**
    * The last server, that user logged in.
    */
   private String server;

   private LoggedInHandler loggedIn;

   private LoginCanceledHandler loginCanceled;

   private EventBus eventBus;

   private CloudFoundryLocalizationConstant constant;

   private CloudFoundryAutoBeanFactory autoBeanFactory;

   /**
    * Create presenter.
    * 
    * @param view
    * @param eventBus
    * @param console
    * @param constant
    * @param autoBeanFactory
    */
   @Inject
   protected LoginPresenter(LoginView view, EventBus eventBus, Console console,
      CloudFoundryLocalizationConstant constant, CloudFoundryAutoBeanFactory autoBeanFactory)
   {
      this.view = view;
      this.view.setDelegate(this);
      this.eventBus = eventBus;
      this.console = console;
      this.constant = constant;
      this.autoBeanFactory = autoBeanFactory;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onLogInClicked()
   {
      doLogin();
   }

   /**
    * Perform log in CloudFoundry.
    */
   protected void doLogin()
   {
      final String enteredServer = view.getServer();
      final String email = view.getEmail();
      final String password = view.getPassword();

      try
      {
         CloudFoundryClientService.getInstance().login(enteredServer, email, password,
            new AsyncRequestCallback<String>()
            {

               /**
                * {@inheritDoc}
                */
               @Override
               protected void onSuccess(String result)
               {
                  server = enteredServer;
                  console.print(constant.loginSuccess());
                  if (loggedIn != null)
                  {
                     loggedIn.onLoggedIn();
                  }

                  view.close();
               }

               /**
                * {@inheritDoc}
                */
               @Override
               protected void onFailure(Throwable exception)
               {
                  if (exception instanceof ServerException)
                  {
                     ServerException serverException = (ServerException)exception;
                     if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus()
                        && serverException.getMessage() != null
                        && serverException.getMessage().contains("Can't access target."))
                     {
                        view.setError(constant.loginViewErrorUnknownTarget());
                        return;
                     }
                     else if (HTTPStatus.OK != serverException.getHTTPStatus() && serverException.getMessage() != null
                        && serverException.getMessage().contains("Operation not permitted"))
                     {
                        view.setError(constant.loginViewErrorInvalidUserOrPassword());
                        return;
                     }
                     // otherwise will be called method from superclass.
                  }
                  eventBus.fireEvent(new ExceptionThrownEvent(exception));
                  console.print(exception.getMessage());
               }
            });
      }
      catch (RequestException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onCancelClicked()
   {
      if (loginCanceled != null)
      {
         loginCanceled.onLoginCanceled();
      }

      view.close();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onValueChanged()
   {
      updateComponent();
   }

   /**
    * Updates component on the view.
    */
   private void updateComponent()
   {
      view.enableLoginButton(isFieldsFullFilled());
   }

   /**
    * Check whether necessary fields are fullfilled.
    * 
    * @return if <code>true</code> all necessary fields are fullfilled
    */
   private boolean isFieldsFullFilled()
   {
      return (view.getEmail() != null && !view.getEmail().isEmpty() && view.getPassword() != null && !view
         .getPassword().isEmpty());
   }

   /**
    * Shows dialog.
    */
   public void showDialog(LoggedInHandler loggedIn, LoginCanceledHandler loginCanceled, String loginUrl)
   {
      this.loggedIn = loggedIn;
      this.loginCanceled = loginCanceled;
      if (loginUrl != null)
      {
         server = loginUrl;
         if (server != null && !server.startsWith("http"))
         {
            server = "http://" + server;
         }
      }

      showDialog();
   }

   /**
    * Shows dialog.
    */
   public void showDialog()
   {
      fillViewFields();

      view.showDialog();
   }

   /**
    * Fills fields on the view.
    */
   private void fillViewFields()
   {
      view.enableLoginButton(false);
      view.focusInEmailField();
      view.setPassword("");
      view.setError("");

      getSystemInformation();
   }

   /**
    * Get Cloud Foundry system information to fill the login field, if user is logged in.
    */
   protected void getSystemInformation()
   {
      try
      {
         AutoBean<SystemInfo> systemInfo = autoBeanFactory.systemInfo();
         AutoBeanUnmarshaller<SystemInfo> unmarshaller = new AutoBeanUnmarshaller<SystemInfo>(systemInfo);
         CloudFoundryClientService.getInstance().getSystemInfo(server,
            new AsyncRequestCallback<SystemInfo>(unmarshaller)
            {
               @Override
               protected void onSuccess(SystemInfo result)
               {
                  view.setEmail(result.getUser());
                  getServers();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  if (exception instanceof UnmarshallerException)
                  {
                     Window.alert(exception.getMessage());
                  }
                  else
                  {
                     getServers();
                  }
               }
            });
      }
      catch (RequestException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }

   /**
    * Gets the list of available servers for user.
    */
   private void getServers()
   {
      try
      {
         CloudFoundryClientService.getInstance()
            .getTargets(
               new AsyncRequestCallback<JsonArray<String>>(new TargetsUnmarshaller(JsonCollections
                  .<String> createArray()))
               {
                  @Override
                  protected void onSuccess(JsonArray<String> result)
                  {
                     if (result.isEmpty())
                     {
                        JsonArray<String> servers = JsonCollections.createArray();
                        servers.add(CloudFoundryExtension.DEFAULT_SERVER);
                        view.setServerValues(servers);
                        if (server == null || server.isEmpty())
                        {
                           view.setServer(CloudFoundryExtension.DEFAULT_SERVER);
                        }
                        else
                        {
                           view.setServer(server);
                        }
                     }
                     else
                     {
                        view.setServerValues(result);
                        if (server == null || server.isEmpty())
                        {
                           view.setServer(result.get(0));
                        }
                        else
                        {
                           view.setServer(server);
                        }
                     }
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     eventBus.fireEvent(new ExceptionThrownEvent(exception));
                     console.print(exception.getMessage());
                  }
               });
      }
      catch (RequestException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }
}