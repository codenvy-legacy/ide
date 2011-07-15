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
package org.exoplatform.ide.extension.cloudfoundry.client.rename;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
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
 * Presenter for rename operation with application.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: RenameApplicationPresenter.java Jul 15, 2011 11:32:02 AM vereshchaka $
 *
 */
public class RenameApplicationPresenter implements ItemsSelectedHandler, RenameApplicationHandler, ViewClosedHandler
{
   interface Display extends IsView
   {
      /**
       * Get rename text field.
       * 
       * @return {@link TextFieldItem}
       */
      TextFieldItem getRenameField();

      /**
       * Get rename button's click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getRenameButton();

      /**
       * Get cancel button's click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getCancelButton();

      /**
       * Select value in rename field.
       */
      void selectValueInRenameField();

      /**
       * Change the enable state of the rename button.
       * 
       * @param isEnabled
       */
      void enableRenameButton(boolean isEnabled);
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
   private String applicationName;
   
   /**
    * The new name of application.
    */
   public RenameApplicationPresenter(HandlerManager eventbus)
   {
      this.eventBus = eventbus;
      
      eventBus.addHandler(RenameApplicationEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
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

      display.getRenameButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            renameApplication();
         }
      });

      display.getRenameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            String newName = event.getValue();
            boolean enable = !applicationName.equals(newName) && newName != null && !newName.isEmpty();
            display.enableRenameButton(enable);
         }
      });

      display.getRenameField().addKeyUpHandler(new KeyUpHandler()
      {

         @Override
         public void onKeyUp(KeyUpEvent event)
         {
            if (event.getNativeKeyCode() == 13)
            {
               renameApplication();
            }
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
               applicationName = result.getName();
               showRenameDialog();
            }
         });
   }
   
   private void showRenameDialog()
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
         IDE.getInstance().openView(display.asView());
         display.getRenameField().setValue(applicationName);
         display.selectValueInRenameField();
         display.enableRenameButton(false);
      }
   }
   
   private LoggedInHandler renameAppLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         renameApplication();
      }
   };
   
   private void renameApplication()
   {
      final String newName = display.getRenameField().getValue();
      CloudFoundryClientService.getInstance().renameApplication(workDir, applicationName, newName,
         new CloudFoundryAsyncRequestCallback<String>(eventBus, renameAppLoggedInHandler, null)
         {
            @Override
            protected void onSuccess(String result)
            {
               closeView();
               eventBus.fireEvent(new OutputEvent(CloudFoundryExtension.LOCALIZATION_CONSTANT.renameApplicationSuccess(
                  applicationName, newName)));
            }
         });
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.rename.RenameApplicationHandler#onRenameApplication(org.exoplatform.ide.extension.cloudfoundry.client.rename.RenameApplicationEvent)
    */
   @Override
   public void onRenameApplication(RenameApplicationEvent event)
   {
      getApplicationInfo();
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
