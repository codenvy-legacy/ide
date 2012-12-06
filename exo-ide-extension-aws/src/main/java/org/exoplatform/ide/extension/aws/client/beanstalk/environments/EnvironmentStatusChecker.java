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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments;

import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.autobean.shared.AutoBean;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentHealth;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentStatus;
import org.exoplatform.ide.extension.aws.shared.beanstalk.Event;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EventsList;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EventsSeverity;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ListEventsRequest;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * Class used to check environment status periodically. Status will be checked as long as it is in a follow state:
 * {@link EnvironmentStatus#Launching}, {@link EnvironmentStatus#Updating}, {@link EnvironmentStatus#Terminating}.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: EnvironmentStatusChecker.java Oct 1, 2012 2:16:56 PM azatsarynnyy $
 *
 */
public class EnvironmentStatusChecker
{
   /**
    * Need to check application name on server-side.
    */
   private VirtualFileSystemInfo vfs;

   /**
    * Need to check application name on server-side.
    */
   private ProjectModel project;

   /**
    * AWS Elastic Beanstalk application's environment to check status.
    */
   private EnvironmentInfo environmentToCheck;

   /**
    * Show events to output view.
    */
   private boolean showEvents;

   /**
    * Delay in millisecond between environment status checking.
    */
   private static final int delay = 2000;

   /**
    * Handler for checking the status of AWS Elastic Beanstalk application's environment.
    */
   private RequestStatusHandler statusHandler;

   /**
    * Time of last received event.
    */
   private long lastReceivedEventTime;

   /**
    * Previous environment status.
    */
   private EnvironmentStatus previousStatus;

   /**
    * Constructs a new instance of {@link EnvironmentStatusChecker}.
    * 
    * @param vfs {@link org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo}
    * @param project {@link ProjectModel}
    * @param environment {@link EnvironmentInfo} to check status
    * @param showEvents if <code>true</code> - events will be printed to output view
    * @param statusHandler {@link RequestStatusHandler}
    */
   public EnvironmentStatusChecker(VirtualFileSystemInfo vfs, ProjectModel project, EnvironmentInfo environment,
      boolean showEvents, RequestStatusHandler statusHandler)
   {
      this.vfs = vfs;
      this.project = project;
      this.environmentToCheck = environment;
      this.showEvents = showEvents;
      this.statusHandler = statusHandler;
   }

   /**
    * Start checking environment status.
    */
   public void startChecking()
   {
      previousStatus = environmentToCheck.getStatus();
      statusHandler.requestInProgress(environmentToCheck.getId());
      checkEnvironmentStatusTimer.schedule(delay);
   }

   /**
    * A timer for periodically sending request of environment status.
    */
   private Timer checkEnvironmentStatusTimer = new Timer()
   {
      @Override
      public void run()
      {
    	 if (showEvents)
         {
            outputEvents();
         }

         AutoBean<EnvironmentInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.environmentInfo();
         AutoBeanUnmarshaller<EnvironmentInfo> unmarshaller = new AutoBeanUnmarshaller<EnvironmentInfo>(autoBean);
         try
         {
            BeanstalkClientService.getInstance().getEnvironmentInfo(environmentToCheck.getId(),
               new AsyncRequestCallback<EnvironmentInfo>(unmarshaller)
               {
                  @Override
                  protected void onSuccess(EnvironmentInfo result)
                  {
                     updateEnvironmentStatus(result);
                     if (result.getStatus() == EnvironmentStatus.Launching
                        || result.getStatus() == EnvironmentStatus.Updating
                        || result.getStatus() == EnvironmentStatus.Terminating)
                     {
                        schedule(delay);
                     }
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     String message = AWSExtension.LOCALIZATION_CONSTANT.unableToGetEnvironmentInfo(environmentToCheck.getName());
                     if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null)
                     {
                        message += "<br>" + ((ServerException)exception).getMessage();
                     }
                     IDE.fireEvent(new OutputEvent(message, Type.ERROR));
                     statusHandler.requestError(environmentToCheck.getId(), exception);
                  }
               });
         }
         catch (RequestException e)
         {
            IDE.fireEvent(new ExceptionThrownEvent(e));
         }
      }
   };

   /**
    * Output events for environment.
    */
   private void outputEvents()
   {
      ListEventsRequest listEventsRequest = AWSExtension.AUTO_BEAN_FACTORY.listEventsRequest().as();
      // application name will be detected by vfs and projectId
      //listEventsRequest.setApplicationName(application.getName());

      // Only first received event has versionLabel property.
      // All subsequent events do not have versionLabel property.
      //listEventsRequest.setVersionLabel(environmentToCheck.getVersionLabel());

      listEventsRequest.setEnvironmentId(environmentToCheck.getId());
      listEventsRequest.setStartTime(lastReceivedEventTime);
      AutoBean<EventsList> eventsListAutoBean = AWSExtension.AUTO_BEAN_FACTORY.eventList();
      AutoBeanUnmarshaller<EventsList> eventsListUnmarshaller =
         new AutoBeanUnmarshaller<EventsList>(eventsListAutoBean);
      try
      {
         BeanstalkClientService.getInstance().getApplicationEvents(vfs.getId(), project.getId(), listEventsRequest,
            new AsyncRequestCallback<EventsList>(eventsListUnmarshaller)
            {
               @Override
               protected void onSuccess(EventsList result)
               {
                  List<Event> eventsList = result.getEvents();
                  if (eventsList.size() > 0)
                  {
                     // shows events in chronological order
                     for (int i = eventsList.size() - 1; i >= 0; i--)
                     {
                        Event event = eventsList.get(i);
                        OutputMessage.Type outputType = OutputMessage.Type.INFO;
                        EventsSeverity severity = event.getSeverity();
                        if (severity == EventsSeverity.ERROR || severity == EventsSeverity.FATAL)
                        {
                           outputType = OutputMessage.Type.ERROR;
                        }
                        else if (severity == EventsSeverity.TRACE || severity == EventsSeverity.DEBUG)
                        {
                           outputType = OutputMessage.Type.OUTPUT;
                        }
                        else if (severity == EventsSeverity.WARN)
                        {
                           outputType = OutputMessage.Type.WARNING;
                        }
                        IDE.fireEvent(new OutputEvent(event.getMessage(), outputType));
                     }
                     lastReceivedEventTime = eventsList.get(0).getEventDate() + 1;
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  // nothing to do
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Checks the environment status and if it {@link EnvironmentStatus#Ready} or
    * {@link EnvironmentStatus#Terminated} and then print appropriate message to the output view.
    * 
    * @param env {@link EnvironmentInfo}
    */
   private void updateEnvironmentStatus(EnvironmentInfo env)
   {
      if (env.getStatus() != previousStatus)
      {
         previousStatus = env.getStatus();
         IDE.fireEvent(new EnvironmentInfoChangedEvent(env));
      }

      StringBuffer message = new StringBuffer();
      if (env.getStatus() == EnvironmentStatus.Ready)
      {
         statusHandler.requestFinished(env.getId());

         message.append(AWSExtension.LOCALIZATION_CONSTANT.createApplicationStartedOnUrl(
            env.getApplicationName(), getAppUrl(env)));

         if (env.getHealth() != EnvironmentHealth.Green)
         {
            message.append(", but health status of the application's environment is " + env.getHealth().name());
         }
         IDE.fireEvent(new OutputEvent(message.toString(), Type.INFO));
      }
      else if (env.getStatus() == EnvironmentStatus.Terminated)
      {
         statusHandler.requestFinished(env.getId());

         message.append(AWSExtension.LOCALIZATION_CONSTANT.terminateEnvironmentSuccess(env.getName()));
         IDE.fireEvent(new OutputEvent(message.toString(), Type.INFO));
      }
   }

   /**
    * Returns formatted environment's URL.
    * 
    * @param env environment
    * @return environment URL
    */
   private String getAppUrl(EnvironmentInfo env)
   {
	  String appUrl = env.getCname();
	  if (appUrl == null)
	  {
		  appUrl = env.getEndpointUrl();
	  }
      if (!appUrl.startsWith("http"))
      {
         appUrl = "http://" + appUrl;
      }
      appUrl = "<a href=\"" + appUrl + "\" target=\"_blank\">" + appUrl + "</a>";
      return appUrl;
   }

}
