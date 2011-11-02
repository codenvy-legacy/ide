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
package org.exoplatform.ide.extension.cloudbees.client.update;

import java.util.Map;

import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.gwtframework.ui.client.dialog.StringValueReceivedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesLocalizationConstant;
import org.exoplatform.ide.extension.cloudbees.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltHandler;
import org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationEvent;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ItemContext;

/**
 * Presenter for updating application on CloudBees.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: UpdateApplicationPresenter.java Oct 10, 2011 5:07:40 PM vereshchaka $
 */
public class UpdateApplicationPresenter extends GitPresenter implements UpdateApplicationHandler,
   ApplicationBuiltHandler
{
   private CloudBeesLocalizationConstant lb = CloudBeesExtension.LOCALIZATION_CONSTANT;

   private String appId;

   private String appTitle;

   /**
    * Message for git commit.
    */
   private String updateMessage;

   /**
    * Location of war file (Java only).
    */
   private String warUrl;

   /**
    * @param eventBus
    */
   public UpdateApplicationPresenter()
   {
      IDE.addHandler(UpdateApplicationEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.update.UpdateApplicationHandler#onUpdateApplication(org.exoplatform.ide.extension.cloudbees.client.update.UpdateApplicationEvent)
    */
   @Override
   public void onUpdateApplication(UpdateApplicationEvent event)
   {

      if (event.getAppId() != null && event.getAppTitle() != null)
      {
         appId = event.getAppId();
         appTitle = event.getAppTitle();
         askForMessage();
      }
      else if (makeSelectionCheck())
      {
         getApplicationInfo();
      }
   }

   private void askForMessage()
   {
      Dialogs.getInstance().askForValue(lb.updateAppAskForMsgTitle(), lb.updateAppAskForMsgText(), "",
         new StringValueReceivedHandler()
         {
            @Override
            public void stringValueReceived(String value)
            {
               if (value == null)
               {
                  updateMessage = null;
                  return;
               }
               else if (value.isEmpty())
               {
                  updateMessage = null;
               }
               else
               {
                  updateMessage = value;
               }
               buildApplication();
            }
         });
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
               appId = result.get("id");
               appTitle = result.get("title");
               askForMessage();
            }
         });
   }

   private void doUpdate()
   {
      String projectId = null;
      if (((ItemContext)selectedItems.get(0)).getProject() != null)
      {
         projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      }

      CloudBeesClientService.getInstance().updateApplication(appId, vfs.getId(), projectId, warUrl, updateMessage,
         new CloudBeesAsyncRequestCallback<Map<String, String>>(IDE.eventBus(), new LoggedInHandler()
         {

            @Override
            public void onLoggedIn()
            {
               doUpdate();
            }
         }, null)
         {

            @Override
            protected void onSuccess(Map<String, String> result)
            {
               IDE.fireEvent(new OutputEvent(CloudBeesExtension.LOCALIZATION_CONSTANT
                  .applicationUpdatedMsg(appTitle), Type.INFO));
            }
         });
   }

   /**
    * @see org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltHandler#onApplicationBuilt(org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent)
    */
   @Override
   public void onApplicationBuilt(ApplicationBuiltEvent event)
   {
      IDE.removeHandler(event.getAssociatedType(), this);
      if (event.getJobStatus().getArtifactUrl() != null)
      {
         warUrl = event.getJobStatus().getArtifactUrl();
         doUpdate();
      }
   }

   private void buildApplication()
   {
      IDE.addHandler(ApplicationBuiltEvent.TYPE, this);
      IDE.fireEvent(new BuildApplicationEvent());
   }

}
