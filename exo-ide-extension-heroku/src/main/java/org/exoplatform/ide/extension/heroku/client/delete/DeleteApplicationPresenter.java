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
package org.exoplatform.ide.extension.heroku.client.delete;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.marshaller.Property;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.Messages;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponse;

import java.util.List;

/**
 * Presenter for deleting application from Heroku.
 *  Performs following actions on delete:
 * 1. Gets the Git working directory location.
 * 2. Gets application name (application info) by Git working directory location.
 * 3. Asks user to confirm the deleting of the application.
 * 4. When user confirms - performs deleting the application.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 26, 2011 5:24:52 PM anya $
 *
 */
public class DeleteApplicationPresenter implements ItemsSelectedHandler, DeleteApplicationHandler, LoggedInHandler
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
    * Git working directory.
    */
   private String workDir;

   private static final String NAME_PROPERTY = "name";

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
      getWorkDir();
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      this.selectedItems = event.getSelectedItems();
   }

   /**
    * Get the location of Git working directory.
    */
   protected void getWorkDir()
   {
      if (selectedItems == null || selectedItems.size() <= 0)
      {
         Dialogs.getInstance().showInfo(Messages.SELECTED_ITEMS_FAIL);
         return;
      }

      //First get the working directory of the repository if exists:
      GitClientService.getInstance().getWorkDir(selectedItems.get(0).getHref(),
         new AsyncRequestCallback<WorkDirResponse>()
         {
            @Override
            protected void onSuccess(WorkDirResponse result)
            {
               workDir = result.getWorkDir();
               workDir = (workDir.endsWith("/.git")) ? workDir.substring(0, workDir.lastIndexOf("/.git")) : workDir;

               getApplicationInfo();
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               Dialogs.getInstance().showError(Messages.NOT_GIT_REPOSITORY);
            }
         });
   }

   /**
    * Get information about application.
    */
   protected void getApplicationInfo()
   {
      HerokuClientService.getInstance().getApplicationInfo(workDir, null, false,
         new HerokuAsyncRequestCallback(eventBus, this)
         {
            @Override
            protected void onSuccess(List<Property> result)
            {
               String name = null;
               for (Property property : result)
               {
                  if (NAME_PROPERTY.equals(property.getName()))
                  {
                     name = property.getValue();
                     break;
                  }
               }
               askForDelete(name);
            }
         });
   }

   /**
    * Show confirmation message before delete.
    * 
    * @param gitWorkDir
    */
   protected void askForDelete(final String deleteName)
   {
      final boolean isName = (deleteName != null);
      String deletion = (isName) ? deleteName : workDir;

      Dialogs.getInstance().ask("Delete application from Heroku",
         "Are you sure you want to delete application " + "<b>" + deletion + "</b>" + " from Heroku?",
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
      HerokuClientService.getInstance().deleteApplication(workDir, null, new HerokuAsyncRequestCallback(eventBus, this)
      {
         @Override
         protected void onSuccess(List<Property> result)
         {
            eventBus.fireEvent(new OutputEvent(HerokuExtension.LOCALIZATION_CONSTANT.deleteApplicationSuccess(),
               Type.INFO));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent)
    */
   @Override
   public void onLoggedIn(LoggedInEvent event)
   {
      eventBus.removeHandler(LoggedInEvent.TYPE, this);
      if (!event.isFailed())
      {
         getApplicationInfo();
      }
   }
}
