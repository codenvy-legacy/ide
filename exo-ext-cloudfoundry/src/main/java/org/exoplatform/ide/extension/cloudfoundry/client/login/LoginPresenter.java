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
package org.exoplatform.ide.extension.cloudfoundry.client.login;

import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.api.ui.console.Console;
import org.exoplatform.ide.commons.exception.ServerException;
import org.exoplatform.ide.commons.exception.UnmarshallerException;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import org.exoplatform.ide.extension.cloudfoundry.client.marshaller.TargetsUnmarshaller;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;
import org.exoplatform.ide.output.event.OutputEvent;
import org.exoplatform.ide.output.event.OutputMessage.Type;
import org.exoplatform.ide.rest.AsyncRequestCallback;
import org.exoplatform.ide.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.rest.HTTPStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class LoginPresenter implements LoginView.ActionDelegate, LoginHandler
{
   private LoginView view;

   private Console console;

   /**
    * The last server, that user logged in.
    */
   private String server;

   private static final CloudFoundryLocalizationConstant lb = CloudFoundryExtension.LOCALIZATION_CONSTANT;

   private LoggedInHandler loggedIn;

   private LoginCanceledHandler loginCanceled;

   private EventBus eventBus;

   @Inject
   protected LoginPresenter(LoginView view, EventBus eventBus, Console console)
   {
      this.view = view;
      this.view.setDelegate(this);
      this.eventBus = eventBus;
      this.console = console;

      eventBus.addHandler(LoginEvent.TYPE, this);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void doLogIn()
   {
      doLogin();
   }

   /**
    * Perform log in OpenShift.
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
                  eventBus.fireEvent(new OutputEvent(lb.loginSuccess(), Type.INFO));
                  if (loggedIn != null)
                  {
                     loggedIn.onLoggedIn();
                  }

                  console.print(lb.loginSuccess());

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
                        view.setError(lb.loginViewErrorUnknownTarget());
                        return;
                     }
                     else if (HTTPStatus.OK != serverException.getHTTPStatus() && serverException.getMessage() != null
                        && serverException.getMessage().contains("Operation not permitted"))
                     {
                        view.setError(lb.loginViewErrorInvalidUserOrPassword());
                        return;
                     }
                     // otherwise will be called method from superclass.
                  }
                  //                  eventBus.fireEvent(new ExceptionThrownEvent(exception));
                  //                  //TODO
                  console.print(exception.getMessage());
               }
            });
      }
      catch (RequestException e)
      {
         //         eventBus.fireEvent(new ExceptionThrownEvent(e));
         //TODO
         console.print(e.getMessage());
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void doCancel()
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
    * {@inheritDoc}
    */
   @Override
   public void onLogin(LoginEvent event)
   {
      loggedIn = event.getLoggedIn();
      loginCanceled = event.getLoginCanceled();
      if (event.getLoginUrl() != null)
      {
         server = event.getLoginUrl();
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
         AutoBean<SystemInfo> systemInfo = CloudFoundryExtension.AUTO_BEAN_FACTORY.systemInfo();
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
                     // TODO
                     //                     Dialogs.getInstance().showError(exception.getMessage());
                     console.print(exception.getMessage());
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
         // TODO
         //         eventBus.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }

   private void getServers()
   {
      try
      {
         CloudFoundryClientService.getInstance().getTargets(
            new AsyncRequestCallback<List<String>>(new TargetsUnmarshaller(new ArrayList<String>()))
            {
               @Override
               protected void onSuccess(List<String> result)
               {
                  if (result.isEmpty())
                  {
                     view.setServerValues(new String[]{CloudFoundryExtension.DEFAULT_SERVER});
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
                     String[] targets = new String[result.size()];
                     targets = result.toArray(targets);
                     view.setServerValues(targets);
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
                  // TODO
                  //                  eventBus.fireEvent(new ExceptionThrownEvent(exception));
                  console.print(exception.getMessage());
               }
            });
      }
      catch (RequestException e)
      {
         // TODO
         //         eventBus.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }
}