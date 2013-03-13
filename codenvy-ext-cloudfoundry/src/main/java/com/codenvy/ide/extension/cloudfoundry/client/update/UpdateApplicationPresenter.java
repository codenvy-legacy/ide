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
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension;
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

   private HandlerRegistration projectBuildHandler;

   @Inject
   public UpdateApplicationPresenter(EventBus eventBus, ResourceProvider resourceProvider, Console console)
   {
      this.eventBus = eventBus;
      this.resourceProvider = resourceProvider;
      this.console = console;

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
      //      if (makeSelectionCheck())
      //      {
      validateData();
      //      }
   }

   private void updateApplication()
   {
      // TODO
      //      final String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      final String projectId = resourceProvider.getActiveProject().getId();

      try
      {
         CloudFoundryClientService.getInstance().updateApplication(resourceProvider.getVfsId(), projectId, null, null,
            warUrl, new CloudFoundryAsyncRequestCallback<String>(null, loggedInHandler, null, eventBus)
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
                           eventBus)
                        {
                           @Override
                           protected void onSuccess(CloudFoundryApplication result)
                           {
                              // TODO
                              //                              IDE.fireEvent(new OutputEvent(CloudFoundryExtension.LOCALIZATION_CONSTANT
                              //                                 .updateApplicationSuccess(result.getName()), Type.INFO));
                              console.print(CloudFoundryExtension.LOCALIZATION_CONSTANT.updateApplicationSuccess(result
                                 .getName()));
                           }
                        });
                  }
                  catch (RequestException e)
                  {
                     // TODO
                     //                     IDE.fireEvent(new ExceptionThrownEvent(e));
                     console.print(e.getMessage());
                  }
               }
            });
      }
      catch (RequestException e)
      {
         // TODO
         //         IDE.fireEvent(new ExceptionThrownEvent(e));
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
      // TODO
      //      final String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      final String projectId = resourceProvider.getActiveProject().getId();

      try
      {
         CloudFoundryClientService.getInstance().validateAction("update", null, null, null, null,
            resourceProvider.getVfsId(), projectId, 0, 0, false,
            new CloudFoundryAsyncRequestCallback<String>(null, validateHandler, null, eventBus)
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
         // TODO
         //         IDE.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }

   /**
    * Check, is work directory contains <code>pom.xml</code> file.
    */
   private void isBuildApplication()
   {
      // TODO
      //      final ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
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

      // TODO
      //         VirtualFileSystem.getInstance().getChildren(project,
      //            new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
      //            {
      //
      //               @Override
      //               protected void onSuccess(List<Item> result)
      //               {
      //                  for (Item item : result)
      //                  {
      //                     if ("pom.xml".equals(item.getName()))
      //                     {
      //                        buildApplication();
      //                        return;
      //                     }
      //                  }
      //                  warUrl = null;
      //                  updateApplication();
      //               }
      //
      //               @Override
      //               protected void onFailure(Throwable exception)
      //               {
      //                  String msg =
      //                     CloudFoundryExtension.LOCALIZATION_CONSTANT.updateApplicationForbidden(project.getName());
      //                  IDE.fireEvent(new ExceptionThrownEvent(msg));
      //               }
      //            });

   }

   private void buildApplication()
   {
      projectBuildHandler = eventBus.addHandler(ProjectBuiltEvent.TYPE, this);
      eventBus.fireEvent(new BuildProjectEvent());
   }
}