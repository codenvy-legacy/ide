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
package org.exoplatform.ide.extension.cloudfoundry.client.operations;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.gwtframework.ui.client.dialog.StringValueReceivedHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;

import java.util.List;

/**
 * Presenter for operations with application: update, delete.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: OperationsApplicationPresenter.java Jul 14, 2011 11:51:13 AM vereshchaka $
 */
public class OperationsApplicationPresenter implements ItemsSelectedHandler, UpdateApplicationHandler
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
   private String war;
   
   /**
    * Location of working copy of application.
    */
   private String workDir;
   
   public OperationsApplicationPresenter(HandlerManager eventbus)
   {
      this.eventBus = eventbus;
      
      eventBus.addHandler(UpdateApplicationEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
   }
   
   public void bindDisplay(List<Framework> frameworks)
   {
   }
   
   LoggedInHandler loggedInHandler = new LoggedInHandler()
   {
      
      @Override
      public void onLoggedIn()
      {
         updateApplication(war);
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
      
      workDir = selectedItems.get(0).getHref();
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.operations.UpdateApplicationHandler#onUpdateApplication(org.exoplatform.ide.extension.cloudfoundry.client.operations.UpdateApplicationEvent)
    */
   @Override
   public void onUpdateApplication(UpdateApplicationEvent event)
   {
      askForWarLocation();
   }
   
   private void askForWarLocation()
   {
      Dialogs.getInstance().askForValue(CloudFoundryExtension.LOCALIZATION_CONSTANT.updateAskWarTitle(), 
         CloudFoundryExtension.LOCALIZATION_CONSTANT.updateAskWarMessage(), null, new StringValueReceivedHandler()
      {
         @Override
         public void stringValueReceived(String value)
         {
            if (value == null || value.isEmpty())
            {
               war = null;
            }
            else
            {
               war = value;
            }
            updateApplication(war);
         }
      });
   }
   
   private void updateApplication(String war)
   {
      CloudFoundryClientService.getInstance().updateApplication(workDir, null, war,
         new CloudFoundryAsyncRequestCallback<String>(eventBus, loggedInHandler, null)
         {
            @Override
            protected void onSuccess(String result)
            {
               eventBus.fireEvent(new OutputEvent(CloudFoundryExtension.LOCALIZATION_CONSTANT
                  .updateApplicationSuccess(result), Type.INFO));
            }
         });
   }

}
