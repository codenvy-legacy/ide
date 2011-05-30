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
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.Messages;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponse;

import java.util.HashMap;
import java.util.List;

/**
 * Presenter for deleting application from Heroku.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 26, 2011 5:24:52 PM anya $
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
    * Git working directory.
    */
   private String workDir;

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

               askForDelete(workDir);
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               Dialogs.getInstance().showError(Messages.NOT_GIT_REPOSITORY);
            }
         });
   }

   /**
    * Show confirmation message before delete.
    * 
    * @param gitWorkDir
    */
   protected void askForDelete(final String gitWorkDir)
   {
      Dialogs.getInstance().ask("Delete application from Heroku",
         "Are you sure you want to delete application " + "<b>" + gitWorkDir + "</b>" + " from Heroku?",
         new BooleanValueReceivedHandler()
         {
            @Override
            public void booleanValueReceived(Boolean value)
            {
               if (value != null && value)
               {
                  doDelete(gitWorkDir);
               }
            }
         });
   }

   /**
    * Perform deleting the application on Heroku
    * 
    * @param gitWorkDir
    */
   protected void doDelete(String gitWorkDir)
   {
      HerokuClientService.getInstance().deleteApplication(gitWorkDir, null, new HerokuAsyncRequestCallback(eventBus)
      {
         
         @Override
         protected void onSuccess(HashMap<String, String> result)
         {
            eventBus.fireEvent(new OutputEvent(
               org.exoplatform.ide.extension.heroku.client.Messages.DESTROY_APPLICATION_SUCCESS, Type.INFO));
         }
      });
   }
}
