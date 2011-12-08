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

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ItemContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Presenter for delete application operation.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeleteApplicationPresenter.java Jul 14, 2011 11:51:13 AM vereshchaka $
 */
public class DeleteApplicationPresenter extends GitPresenter implements DeleteApplicationHandler, ViewClosedHandler
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
    * The name of application.
    */
   private String appName;

   public DeleteApplicationPresenter()
   {
      IDE.addHandler(DeleteApplicationEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
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
    * @see org.exoplatform.ide.extension.cloudfoundry.client.delete.DeleteApplicationHandler#onDeleteApplication(org.exoplatform.ide.extension.cloudfoundry.client.delete.DeleteApplicationEvent)
    */
   @Override
   public void onDeleteApplication(DeleteApplicationEvent event)
   {
      if (event.getApplicationName() == null && makeSelectionCheck())
         getApplicationInfo();
      else
      {
         appName = event.getApplicationName();
         showDeleteDialog(appName);
      }
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
      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      CloudFoundryClientService.getInstance().getApplicationInfo(vfs.getId(), projectId, null, null,
         new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(IDE.eventBus(), appInfoLoggedInHandler, null)
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
      final String projectId =
         (((ItemContext)selectedItems.get(0)).getProject() != null) ? ((ItemContext)selectedItems.get(0)).getProject()
            .getId() : null;
      CloudFoundryClientService.getInstance().deleteApplication(vfs.getId(), projectId, appName, null,
         isDeleteServices, new CloudFoundryAsyncRequestCallback<String>(IDE.eventBus(), deleteAppLoggedInHandler, null)
         {
            @Override
            protected void onSuccess(String result)
            {
               closeView();
               IDE.fireEvent(new OutputEvent(
                  CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationDeletedMsg(appName), Type.INFO));
               IDE.fireEvent(new ApplicationDeletedEvent(vfs.getId(), projectId));
            }
         });
   }

   private void showDeleteDialog(String appName)
   {
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
