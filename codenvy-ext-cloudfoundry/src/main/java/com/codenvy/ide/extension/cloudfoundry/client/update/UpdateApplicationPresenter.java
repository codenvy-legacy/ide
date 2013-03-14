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
package com.codenvy.ide.extension.cloudfoundry.client.update;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.console.Console;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.extension.maven.client.event.BuildProjectEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltHandler;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Presenter for update application operation.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: OperationsApplicationPresenter.java Jul 14, 2011 11:51:13 AM vereshchaka $
 */
@Singleton
public class UpdateApplicationPresenter implements UpdateApplicationHandler, ProjectBuiltHandler
{
   /**
    * Location of war file (Java only).
    */
   private String warUrl;

   private EventBus eventBus;

   private ResourceProvider resourceProvider;

   private Console console;

   private CloudFoundryLocalizationConstant constant;

   private HandlerRegistration projectBuildHandler;

   @Inject
   public UpdateApplicationPresenter(EventBus eventBus, ResourceProvider resourceProvider, Console console,
      CloudFoundryLocalizationConstant constant)
   {
      this.eventBus = eventBus;
      this.resourceProvider = resourceProvider;
      this.console = console;
      this.constant = constant;

      this.eventBus.addHandler(UpdateApplicationEvent.TYPE, this);
   }

   LoggedInHandler loggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         updateApplication();
      }
   };

   /**
    * {@inheritDoc}
    */
   @Override
   public void onUpdateApplication(UpdateApplicationEvent event)
   {
      validateData();
   }

   private void updateApplication()
   {
      final String projectId = resourceProvider.getActiveProject().getId();

      try
      {
         CloudFoundryClientService.getInstance().updateApplication(resourceProvider.getVfsId(), projectId, null, null,
            warUrl,
            new CloudFoundryAsyncRequestCallback<String>(null, loggedInHandler, null, eventBus, console, constant)
            {
               @Override
               protected void onSuccess(String result)
               {
                  try
                  {
                     AutoBean<CloudFoundryApplication> cloudFoundryApplication =
                        CloudFoundryExtension.AUTO_BEAN_FACTORY.cloudFoundryApplication();

                     AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
                        new AutoBeanUnmarshaller<CloudFoundryApplication>(cloudFoundryApplication);

                     CloudFoundryClientService.getInstance().getApplicationInfo(
                        resourceProvider.getVfsId(),
                        projectId,
                        null,
                        null,
                        new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, null, null,
                           eventBus, console, constant)
                        {
                           @Override
                           protected void onSuccess(CloudFoundryApplication result)
                           {
                              console.print(constant.updateApplicationSuccess(result.getName()));
                           }
                        });
                  }
                  catch (RequestException e)
                  {
                     eventBus.fireEvent(new ExceptionThrownEvent(e));
                     console.print(e.getMessage());
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
    * {@inheritDoc}
    */
   @Override
   public void onProjectBuilt(ProjectBuiltEvent event)
   {
      projectBuildHandler.removeHandler();
      if (event.getBuildStatus().getDownloadUrl() != null)
      {
         warUrl = event.getBuildStatus().getDownloadUrl();
         updateApplication();
      }
   }

   private LoggedInHandler validateHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         validateData();
      }
   };

   private void validateData()
   {
      final String projectId = resourceProvider.getActiveProject().getId();

      try
      {
         CloudFoundryClientService.getInstance().validateAction("update", null, null, null, null,
            resourceProvider.getVfsId(), projectId, 0, 0, false,
            new CloudFoundryAsyncRequestCallback<String>(null, validateHandler, null, eventBus, console, constant)
            {
               @Override
               protected void onSuccess(String result)
               {
                  isBuildApplication();
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
    * Check, is work directory contains <code>pom.xml</code> file.
    */
   private void isBuildApplication()
   {
      final Project project = resourceProvider.getActiveProject();

      JsonArray<Resource> children = project.getChildren();

      for (int i = 0; i < children.size(); i++)
      {
         Resource child = children.get(i);
         if (child.isFile() && "pom.xml".equals(child.getName()))
         {
            buildApplication();
            return;
         }
      }
      warUrl = null;
      updateApplication();
   }

   private void buildApplication()
   {
      projectBuildHandler = eventBus.addHandler(ProjectBuiltEvent.TYPE, this);
      eventBus.fireEvent(new BuildProjectEvent());
   }
}