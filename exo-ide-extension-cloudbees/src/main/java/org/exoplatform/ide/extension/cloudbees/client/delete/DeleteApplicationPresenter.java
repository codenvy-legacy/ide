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

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.login.LoggedInHandler;

import java.util.List;
import java.util.Map;

/**
 * Presenter for deleting application from CloudBees.
 * Performs following actions on delete:
 * 1. Gets application id (application info) by work dir (location on file system).
 * 2. Asks user to confirm the deleting of the application.
 * 3. When user confirms - performs deleting the application.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeleteApplicationPresenter.java Jul 1, 2011 12:59:52 PM vereshchaka $
 *
 */
public class DeleteApplicationPresenter implements ItemsSelectedHandler, DeleteApplicationHandler
{
   /**
    * Events handler.
    */
   private HandlerManager eventBus;

   /**
    * Selected items.
    */
   private List<Item> selectedItems;

   /**
    * Location of application on file system.
    */
   private String workDir;

   private String appId;
   
   private String appTitle;

   /**
    * @param eventBus
    */
   public DeleteApplicationPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(DeleteApplicationEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.delete.DeleteApplicationHandler#onDeleteApplication(org.exoplatform.ide.extension.heroku.client.delete.DeleteApplicationEvent)
    */
   @Override
   public void onDeleteApplication(DeleteApplicationEvent event)
   {
      getApplicationInfo();
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      this.selectedItems = event.getSelectedItems();
      if (selectedItems.size() == 0) {
         return;
      }
      
      workDir = selectedItems.get(0).getHref();
   }

   /**
    * Get information about application.
    */
   protected void getApplicationInfo()
   {
      CloudBeesClientService.getInstance().getApplicationInfo(workDir, null,
         new CloudBeesAsyncRequestCallback<Map<String, String>>(eventBus, new LoggedInHandler()
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
               askForDelete(appTitle);
            }
         });
   }

   /**
    * Show confirmation message before delete.
    * 
    * @param gitWorkDir
    */
   protected void askForDelete(final String applicationTitle)
   {
      final String title = (applicationTitle != null) ? applicationTitle : workDir;

      Dialogs.getInstance().ask(CloudBeesExtension.LOCALIZATION_CONSTANT.deleteApplicationTitle(),
        CloudBeesExtension.LOCALIZATION_CONSTANT.deleteApplicationQuestion(title),
         new BooleanValueReceivedHandler()
         {

            @Override
            public void booleanValueReceived(Boolean value)
            {
               if (value != null && value)
               {
                  doDelete();
               }
            }
         });
   }

   /**
    * Perform deleting the application on Heroku.
    *
    */
   protected void doDelete()
   {
      CloudBeesClientService.getInstance().deleteApplication(workDir, appId,
         new CloudBeesAsyncRequestCallback<String>(eventBus, new LoggedInHandler()
         {
            @Override
            public void onLoggedIn()
            {
               doDelete();
            }
         }, null)
         {
            @Override
            protected void onSuccess(String result)
            {
               eventBus.fireEvent(new OutputEvent(
                  CloudBeesExtension.LOCALIZATION_CONSTANT.applicationDeletedMsg(appTitle), Type.INFO));
            }
         });
   }
}
