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
package org.exoplatform.ide.extension.cloudfoundry.client.delete;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;

import java.util.List;

/**
 * Presenter for delete application operation.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeleteApplicationPresenter.java Jul 14, 2011 11:51:13 AM vereshchaka $
 */
public class DeleteApplicationPresenter implements ItemsSelectedHandler, DeleteApplicationHandler, ViewClosedHandler
{
   interface Display extends IsView
   {
      /**
       * Get delete services checkbox field.
       * 
       * @return {@link TextFieldItem}
       */
      HasValue<Boolean> getDeleteServicesCheckbox();

      /**
       * Get delete button's click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getDeleteButton();

      /**
       * Get cancel button's click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getCancelButton();
      
      /**
       * Set the ask message to delete application.
       * @param message
       */
      void setAskMessage(String message);
      
      void setAskDeleteServices(String text);
   }
   
   private Display display;
   /**
    * Events handler.
    */
   private HandlerManager eventBus;
   
   /**
    * Selected items in navigation tree.
    */
   private List<Item> selectedItems;
   
   /**
    * Location of working copy of application.
    */
   private String workDir;
   
   /**
    * The name of application.
    */
   private String appName;
   
   public DeleteApplicationPresenter(HandlerManager eventbus)
   {
      this.eventBus = eventbus;
      
      eventBus.addHandler(DeleteApplicationEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
   }
   
   public void bindDisplay()
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            closeView();
         }
      });

      display.getDeleteButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            deleteApplication();
         }
      });
   }
   
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
    * @see org.exoplatform.ide.extension.cloudfoundry.client.delete.DeleteApplicationHandler#onDeleteApplication(org.exoplatform.ide.extension.cloudfoundry.client.delete.DeleteApplicationEvent)
    */
   @Override
   public void onDeleteApplication(DeleteApplicationEvent event)
   {
      getApplicationInfo();
   }
   
   private LoggedInHandler appInfoLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         getApplicationInfo();
      }
   };
   
   private void getApplicationInfo()
   {
      CloudFoundryClientService.getInstance().getApplicationInfo(workDir, null,
         new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(eventBus, appInfoLoggedInHandler, null)
         {
            @Override
            protected void onSuccess(CloudfoundryApplication result)
            {
               appName = result.getName();
               showDeleteDialog(appName);
            }
         });
   }
   
   private LoggedInHandler deleteAppLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         deleteApplication();
      }
   };
   
   private void deleteApplication()
   {
      boolean isDeleteServices = display.getDeleteServicesCheckbox().getValue();
      CloudFoundryClientService.getInstance().deleteApplication(workDir, null, isDeleteServices,
         new CloudFoundryAsyncRequestCallback<String>(eventBus, deleteAppLoggedInHandler, null)
         {
            @Override
            protected void onSuccess(String result)
            {
               closeView();
               eventBus.fireEvent(new OutputEvent(
                  CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationDeletedMsg(appName), Type.INFO));
            }
         });
   }
   
   private void showDeleteDialog(String appName)
   {
      System.out.println(">>>is display null? " + String.valueOf(display == null));
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
         IDE.getInstance().openView(display.asView());
         display.setAskMessage(CloudFoundryExtension.LOCALIZATION_CONSTANT.deleteApplicationQuestion(appName));
         display.setAskDeleteServices(CloudFoundryExtension.LOCALIZATION_CONSTANT.deleteApplicationAskDeleteServices());
      }
   }
   
   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

}
