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
package org.exoplatform.ide.extension.openshift.client.start;

import com.google.gwt.http.client.RequestException;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.extension.openshift.client.OpenShiftAsyncRequestCallback;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientService;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.openshift.client.project.ApplicationInfoChangedEvent;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class StartApplicationPresenter
   implements StartApplicationHandler, StopApplicationHandler, RestartApplicationHandler
{
   private String appName;

   public StartApplicationPresenter()
   {
      IDE.addHandler(StartApplicationEvent.TYPE, this);
      IDE.addHandler(StopApplicationEvent.TYPE, this);
      IDE.addHandler(RestartApplicationEvent.TYPE, this);
   }

   /**
    * If user is not logged in to OpenShift, this handler will be called, after user logged in.
    */
   private LoggedInHandler startLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn(LoggedInEvent event)
      {
         if (!event.isFailed())
         {
            startApplication(appName);
         }
      }
   };

   /**
    * If user is not logged in to OpenShift, this handler will be called, after user logged in.
    */
   private LoggedInHandler stopLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn(LoggedInEvent event)
      {
         if (!event.isFailed())
         {
            stopApplication(appName);
         }
      }
   };

   /**
    * If user is not logged in to OpenShift, this handler will be called, after user logged in.
    */
   private LoggedInHandler restartLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn(LoggedInEvent event)
      {
         if (!event.isFailed())
         {
            restartApplication(appName);
         }
      }
   };

   @Override
   public void onRestartApplication(RestartApplicationEvent event)
   {
      appName = event.getApplicationName();
      restartApplication(appName);
   }

   @Override
   public void onStartApplication(StartApplicationEvent event)
   {
      appName = event.getApplicationName();
      startApplication(appName);
   }

   @Override
   public void onStopApplication(StopApplicationEvent event)
   {
      appName = event.getApplicationName();
      stopApplication(appName);
   }

   private void startApplication(final String appName)
   {
      try
      {
         OpenShiftClientService.getInstance().startApplication(appName,
            new OpenShiftAsyncRequestCallback<Void>(null, startLoggedInHandler, null)
            {
               @Override
               protected void onSuccess(Void result)
               {
                  IDE.fireEvent(new OutputEvent("Application " + appName + " started"));
                  IDE.fireEvent(new ApplicationInfoChangedEvent(appName));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void stopApplication(final String appName)
   {
      try
      {
         OpenShiftClientService.getInstance().stopApplication(appName,
            new OpenShiftAsyncRequestCallback<Void>(null, stopLoggedInHandler, null)
            {
               @Override
               protected void onSuccess(Void result)
               {
                  IDE.fireEvent(new OutputEvent("Application " + appName + " stopped"));
                  IDE.fireEvent(new ApplicationInfoChangedEvent(appName));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void restartApplication(final String appName)
   {
      try
      {
         OpenShiftClientService.getInstance().restartApplication(appName,
            new OpenShiftAsyncRequestCallback<Void>(null, restartLoggedInHandler, null)
            {
               @Override
               protected void onSuccess(Void result)
               {
                  IDE.fireEvent(new OutputEvent("Application " + appName + " restarted"));
                  IDE.fireEvent(new ApplicationInfoChangedEvent(appName));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }
}
