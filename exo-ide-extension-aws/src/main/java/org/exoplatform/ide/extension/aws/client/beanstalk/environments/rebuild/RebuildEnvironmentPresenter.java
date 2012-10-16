/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.rebuild;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService;
import org.exoplatform.ide.extension.aws.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentStatus;

/**
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: RebuildEnvironmentPresenter.java Sep 28, 2012 3:56:46 PM azatsarynnyy $
 *
 */
public class RebuildEnvironmentPresenter implements RebuildEnvironmentHandler, ViewClosedHandler
{
   interface Display extends IsView
   {
      HasClickHandlers getRebuildButton();

      HasClickHandlers getCancelButton();

      HasValue<String> getRebuildQuestion();
   }

   private Display display;

   private EnvironmentInfo environment;

   private RebuildEnvironmentStartedHandler rebuildEnvironmentStartedHandler;

   public RebuildEnvironmentPresenter()
   {
      IDE.addHandler(RebuildEnvironmentEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   public void bindDisplay()
   {
      display.getRebuildButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            rebuildEnvironment();
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
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.rebuild.RebuildEnvironmentHandler#onRebuildEnvironment(org.exoplatform.ide.extension.aws.client.beanstalk.environments.rebuild.RebuildEnvironmentEvent)
    */
   @Override
   public void onRebuildEnvironment(RebuildEnvironmentEvent event)
   {
      this.environment = event.getEnvironmentInfo();
      this.rebuildEnvironmentStartedHandler = event.getRebuildEnvironmentStartedHandler();
      if (!environment.getStatus().equals(EnvironmentStatus.Ready))
      {
         Dialogs.getInstance().showError("Environment is in an invalid state for this operation. Must be Ready");
         return;
      }

      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();
      }
      display.getRebuildQuestion().setValue(
         AWSExtension.LOCALIZATION_CONSTANT.rebuildEnvironmentQuestion(environment.getName()));
   }

   /**
    * Rebuilds specified environment.
    */
   private void rebuildEnvironment()
   {
      try
      {
         BeanstalkClientService.getInstance().rebuildEnvironment(environment.getId(),
            new AwsAsyncRequestCallback<Object>(new LoggedInHandler()
            {

               @Override
               public void onLoggedIn()
               {
                  rebuildEnvironment();
               }
            })
            {

               @Override
               protected void processFail(Throwable exception)
               {
                  String message = AWSExtension.LOCALIZATION_CONSTANT.rebuildEnvironmentFailed(environment.getId());
                  if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null)
                  {
                     message += "<br>" + ((ServerException)exception).getMessage();
                  }
                  Dialogs.getInstance().showError(message);
               }

               @Override
               protected void onSuccess(Object result)
               {
                  IDE.getInstance().closeView(display.asView().getId());

                  if (rebuildEnvironmentStartedHandler != null)
                  {
                     rebuildEnvironmentStartedHandler.onRebuildEnvironmentStarted(environment);
                  }
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }
}
