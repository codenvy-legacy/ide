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

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltHandler;
import org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationEvent;

import java.util.List;

/**
 * Presenter for update application operation.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: OperationsApplicationPresenter.java Jul 14, 2011 11:51:13 AM vereshchaka $
 */
public class UpdateApplicationPresenter implements ItemsSelectedHandler, UpdateApplicationHandler, ApplicationBuiltHandler
{
   /**
    * Events handler.
    */
   private HandlerManager eventBus;
   
   /**
    * Selected items in navigation tree.
    */
   private List<Item> selectedItems;
   
   /**
    * Location of war file (Java only).
    */
   private String warUrl;
   
   /**
    * Location of working copy of application.
    */
   private String workDir;
   
   public UpdateApplicationPresenter(HandlerManager eventbus)
   {
      this.eventBus = eventbus;
      
      eventBus.addHandler(UpdateApplicationEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
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
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
      this.selectedItems = event.getSelectedItems();
      if (selectedItems.size() == 0) {
         return;
      }
      
      workDir = selectedItems.get(0).getWorkDir();
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.update.UpdateApplicationHandler#onUpdateApplication(org.exoplatform.ide.extension.cloudfoundry.client.update.UpdateApplicationEvent)
    */
   @Override
   public void onUpdateApplication(UpdateApplicationEvent event)
   {
      validateData();
   }
   
   private void updateApplication()
   {
      CloudFoundryClientService.getInstance().updateApplication(workDir, null, warUrl,
         new CloudFoundryAsyncRequestCallback<String>(eventBus, loggedInHandler, null)
         {
            @Override
            protected void onSuccess(String result)
            {
               CloudFoundryClientService.getInstance().getApplicationInfo(workDir, null,
                  new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(eventBus, null, null)
                  {

                     @Override
                     protected void onSuccess(CloudfoundryApplication result)
                     {
                        eventBus.fireEvent(new OutputEvent(CloudFoundryExtension.LOCALIZATION_CONSTANT
                           .updateApplicationSuccess(result.getName()), Type.INFO));
                     }
                  });
            }
         });
   }

   /**
    * @see org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltHandler#onApplicationBuilt(org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent)
    */
   @Override
   public void onApplicationBuilt(ApplicationBuiltEvent event)
   {
      eventBus.removeHandler(event.getAssociatedType(), this);
      if (event.getJobStatus().getArtifactUrl() != null)
      {
         warUrl = event.getJobStatus().getArtifactUrl();
         updateApplication();
      }
      else
      {
         eventBus.fireEvent(new ExceptionThrownEvent(CloudFoundryExtension.LOCALIZATION_CONSTANT.createApplicationWarIsNull()));
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
      CloudFoundryClientService.getInstance().validateAction("update", null, null, null, workDir, 0, 0, false,
         new CloudFoundryAsyncRequestCallback<String>(eventBus, validateHandler, null)
         {
            @Override
            protected void onSuccess(String result)
            {
               isBuildApplication(workDir);
            }
         });
   }
   
   /**
    * Check, is work dir contains <code>pom.xml</code> file,
    * that starts build project.
    * <p/>
    * Otherwise, create Cloud Foundry application.
    * 
    * @param workDir
    */
   private void isBuildApplication(String workDir)
   {
      CloudFoundryClientService.getInstance().checkFileExists(workDir, "pom.xml", new AsyncRequestCallback<String>(eventBus)
      {
         @Override
         protected void onSuccess(String result)
         {
            buildApplication();
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            if (exception instanceof ServerException)
            {
               ServerException serverException = (ServerException)exception;
               if (HTTPStatus.NOT_FOUND == serverException.getHTTPStatus())
               {
                  updateApplication();
                  return;
               }
               else
               {
                  super.onFailure(exception);
               }
            }
            super.onFailure(exception);
         }
      });
   }
   
   private void buildApplication()
   {
      eventBus.addHandler(ApplicationBuiltEvent.TYPE, this);
      eventBus.fireEvent(new BuildApplicationEvent());
   }
}
