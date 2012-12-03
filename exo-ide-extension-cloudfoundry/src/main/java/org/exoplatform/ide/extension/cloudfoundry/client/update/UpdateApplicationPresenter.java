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
package org.exoplatform.ide.extension.cloudfoundry.client.update;

import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for update application operation.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: OperationsApplicationPresenter.java Jul 14, 2011 11:51:13 AM vereshchaka $
 */
public class UpdateApplicationPresenter extends GitPresenter implements UpdateApplicationHandler, ProjectBuiltHandler
{
   /**
    * Location of war file (Java only).
    */
   private String warUrl;

   public UpdateApplicationPresenter()
   {
      IDE.addHandler(UpdateApplicationEvent.TYPE, this);
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
    * @see org.exoplatform.ide.extension.cloudfoundry.client.update.UpdateApplicationHandler#onUpdateApplication(org.exoplatform.ide.extension.cloudfoundry.client.update.UpdateApplicationEvent)
    */
   @Override
   public void onUpdateApplication(UpdateApplicationEvent event)
   {
      if (makeSelectionCheck())
      {
         validateData();
      }
   }

   private void updateApplication()
   {
      final String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      try
      {
         CloudFoundryClientService.getInstance().updateApplication(vfs.getId(), projectId, null, null, warUrl,
            new CloudFoundryAsyncRequestCallback<String>(null, loggedInHandler, null)
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

                     CloudFoundryClientService.getInstance().getApplicationInfo(vfs.getId(), projectId, null, null,
                        new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, null, null)
                        {

                           @Override
                           protected void onSuccess(CloudFoundryApplication result)
                           {
                              IDE.fireEvent(new OutputEvent(CloudFoundryExtension.LOCALIZATION_CONSTANT
                                 .updateApplicationSuccess(result.getName()), Type.INFO));
                           }
                        });
                  }
                  catch (RequestException e)
                  {
                     IDE.fireEvent(new ExceptionThrownEvent(e));
                  }
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * @see org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler#onProjectBuilt(org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent)
    */
   @Override
   public void onProjectBuilt(ProjectBuiltEvent event)
   {
      IDE.removeHandler(event.getAssociatedType(), this);
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
      final String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();

      try
      {
         CloudFoundryClientService.getInstance().validateAction("update", null, null, null, null, vfs.getId(),
            projectId, 0, 0, false, new CloudFoundryAsyncRequestCallback<String>(null, validateHandler, null)
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
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Check, is work directory contains <code>pom.xml</code> file.
    */
   private void isBuildApplication()
   {
      final ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();

      try
      {
         VirtualFileSystem.getInstance().getChildren(project,
            new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
            {

               @Override
               protected void onSuccess(List<Item> result)
               {
                  for (Item item : result)
                  {
                     if ("pom.xml".equals(item.getName()))
                     {
                        buildApplication();
                        return;
                     }
                  }
                  warUrl = null;
                  updateApplication();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  String msg =
                     CloudFoundryExtension.LOCALIZATION_CONSTANT.updateApplicationForbidden(project.getName());
                  IDE.fireEvent(new ExceptionThrownEvent(msg));
               }
            });
      }
      catch (RequestException ignored)
      {
      }
   }

   private void buildApplication()
   {
      IDE.addHandler(ProjectBuiltEvent.TYPE, this);
      IDE.fireEvent(new BuildProjectEvent());
   }
}
