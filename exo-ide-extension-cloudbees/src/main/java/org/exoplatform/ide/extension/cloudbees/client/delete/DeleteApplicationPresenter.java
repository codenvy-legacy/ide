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
package org.exoplatform.ide.extension.cloudbees.client.delete;

import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.login.LoggedInHandler;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.Map;

/**
 * Presenter for deleting application from CloudBees. Performs following actions on delete: 1. Gets application id (application
 * info) by work dir (location on file system). 2. Asks user to confirm the deleting of the application. 3. When user confirms -
 * performs deleting the application.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeleteApplicationPresenter.java Jul 1, 2011 12:59:52 PM vereshchaka $
 * 
 */
public class DeleteApplicationPresenter extends GitPresenter implements DeleteApplicationHandler
{
   /**
    * @param eventBus
    */
   public DeleteApplicationPresenter()
   {
      IDE.addHandler(DeleteApplicationEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.delete.DeleteApplicationHandler#onDeleteApplication(org.exoplatform.ide.extension.heroku.client.delete.DeleteApplicationEvent)
    */
   @Override
   public void onDeleteApplication(DeleteApplicationEvent event)
   {
      // application id and application title can be received from event
      // e.g.when, delete from application manager form
      if (event.getAppId() != null && event.getAppTitle() != null)
      {
         String appId = event.getAppId();
         String appTitle = event.getAppTitle() != null ? event.getAppTitle() : appId;
         askForDelete(appId, appTitle);
      }
      else if (makeSelectionCheck())
      {
         getApplicationInfo();
      }
   }

   /**
    * Get information about application.
    */
   protected void getApplicationInfo()
   {
      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      CloudBeesClientService.getInstance().getApplicationInfo(null, vfs.getId(), projectId,
         new CloudBeesAsyncRequestCallback<Map<String, String>>(IDE.eventBus(), new LoggedInHandler()
         {
            @Override
            public void onLoggedIn()
            {
               getApplicationInfo();
            }
         }, null)
         {

            @Override
            protected void onSuccess(Map<String, String> result)
            {
               String appId = result.get("id");
               String appTitle = result.get("title");
               askForDelete(appId, appTitle);
            }
         });
   }

   /**
    * Show confirmation message before delete.
    * 
    * @param gitWorkDir
    */
   protected void askForDelete(final String appId, final String appTitle)
   {
      Dialogs.getInstance().ask(CloudBeesExtension.LOCALIZATION_CONSTANT.deleteApplicationTitle(),
         CloudBeesExtension.LOCALIZATION_CONSTANT.deleteApplicationQuestion(appTitle),
         new BooleanValueReceivedHandler()
         {

            @Override
            public void booleanValueReceived(Boolean value)
            {
               if (value != null && value)
               {
                  doDelete(appId, appTitle);
               }
            }
         });
   }

   protected void doDelete(final String appId, final String appTitle)
   {
      String projectId = null;
      if (selectedItems.size() > 0 && selectedItems.get(0) instanceof ItemContext)
      {
         ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
         if (project != null && project.getPropertyValue("cloudbees-application") != null
            && appId.equals((String)project.getPropertyValue("cloudbees-application")))
         {
            projectId = project.getId();
         }
      }

      CloudBeesClientService.getInstance().deleteApplication(appId, vfs.getId(), projectId,
         new CloudBeesAsyncRequestCallback<String>(IDE.eventBus(), new LoggedInHandler()
         {
            @Override
            public void onLoggedIn()
            {
               doDelete(appId, appTitle);
            }
         }, null)
         {
            @Override
            protected void onSuccess(String result)
            {
               IDE.fireEvent(new OutputEvent(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationDeletedMsg(appTitle),
                  Type.INFO));
               IDE.fireEvent(new ApplicationDeletedEvent(appId));
            }
         });
   }
}
