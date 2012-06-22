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
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.UnauthorizedException;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineAsyncRequestCallback;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;
import org.exoplatform.ide.extension.googleappengine.shared.User;

/**
 * Presenter for log in Google App Engine operation. The view must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 18, 2012 12:19:01 PM anya $
 * 
 */
public class LoginPresenter implements LoginHandler, ViewClosedHandler
{
   interface Display extends IsView
   {
      /**
       * Get Go button click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getGoButton();

      /**
       * Get Cancel button click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getCancelButton();
   }

   private Display display;

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
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getGoButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            doLogin();
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.login.LoginHandler#onLogin(org.exoplatform.ide.extension.googleappengine.client.login.LoginEvent)
    */
   @Override
   public void onLogin(LoginEvent event)
   {
      if (display == null)
      {
         Display display = GWT.create(Display.class);
         bindDisplay(display);
         IDE.getInstance().openView(display.asView());
      }
   }

   private void doLogin()
   {
      UrlBuilder builder = new UrlBuilder();
      builder.setProtocol(Window.Location.getProtocol()).setHost(Window.Location.getHost())
         .setPath(GoogleAppEngineClientService.getInstance().getAuthUrl());
      if (Window.Location.getPort() != null && !Window.Location.getPort().isEmpty())
      {
         builder.setPort(Integer.parseInt(Window.Location.getPort()));
      }

      final String url = builder.buildString();
      isUserLogged(url);
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

   private void isUserLogged(final String url)
   {
      AutoBean<User> user = GoogleAppEngineExtension.AUTO_BEAN_FACTORY.user();
      AutoBeanUnmarshaller<User> unmarshaller = new AutoBeanUnmarshaller<User>(user);
      try
      {
         GoogleAppEngineClientService.getInstance().getLoggedUser(
            new GoogleAppEngineAsyncRequestCallback<User>(unmarshaller)
            {

               @Override
               protected void onSuccess(User result)
               {
                  if (!result.isAuthenticated())
                  {
                     if (display != null)
                     {
                        IDE.getInstance().closeView(display.asView().getId());
                     }
                     Window.open(url, "_blank", null);
                  }
               }

               /**
                * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineAsyncRequestCallback#onFailure(java.lang.Throwable)
                */
               @Override
               protected void onFailure(Throwable exception)
               {
                  if (display != null)
                  {
                     IDE.getInstance().closeView(display.asView().getId());
                  }
                  if (exception instanceof UnauthorizedException)
                  {
                     IDE.fireEvent(new ExceptionThrownEvent(exception));
                     return;
                  }
                  Window.open(url, "_blank", null);
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

}
