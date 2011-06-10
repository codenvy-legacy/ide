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
package org.exoplatform.ide.extension.openshift.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientService;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoginEvent;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;

import java.util.List;

/**
 * Presenter for creating new OpenShift application.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 7, 2011 5:50:34 PM anya $
 *
 */
public class CreateApplicationPresenter implements ItemsSelectedHandler, CreateApplicationHandler, ViewClosedHandler,
   LoggedInHandler
{
   interface Display extends IsView
   {
      /**
       * Get create button's click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getCreateButton();

      /**
       * Get cancel button's click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getCancelButton();

      /**
       * Get application name field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getApplicationNameField();

      /**
       * Get application's directory location field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getWorkDirLocationField();

      /**
       * Get application's type field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getTypeField();

      /**
       * Change the enable state of the create button.
       * 
       * @param enable
       */
      void enableCreateButton(boolean enable);

      /**
       * Give focus to application name field.
       */
      void focusInApplicationNameField();

      void setApplicationTypeValues(String[] values);
   }

   /**
    * Types of the application.
    */
   private static String[] APP_TYPES = new String[]{"php-5.3.2", //
      "wsgi-3.2.1", //
      "jbossas-7.0.0", //
      "perl-5.10.1", //
      "rack-1.1.0"}; //

   private Display display;

   /**
    * Events handler manager.
    */
   private HandlerManager eventBus;

   /**
    * Selected items.
    */
   private List<Item> selectedItems;

   /**
    * Application's location.
    */
   private String workDir;

   /**
    * @param eventBus
    */
   public CreateApplicationPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(CreateApplicationEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this); 
   }

   /**
    * Bind display with presenter.
    */
   public void bindDisplay()
   {
      display.getCreateButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doCreateApplication();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getApplicationNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean isNotEmpty = (event.getValue() != null && event.getValue().trim().length() > 0);
            display.enableCreateButton(isNotEmpty);
         }
      });
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

   /**
    * @see org.exoplatform.ide.extension.openshift.client.create.CreateApplicationHandler#onCreateApplication(org.exoplatform.ide.extension.openshift.client.create.CreateApplicationEvent)
    */
   @Override
   public void onCreateApplication(CreateApplicationEvent event)
   {
      if (selectedItems == null || selectedItems.size() == 0 || !(selectedItems.get(0) instanceof Folder))
      {
         Dialogs.getInstance().showInfo(OpenShiftExtension.LOCALIZATION_CONSTANT.selectFolder());
         return;
      }

      workDir = selectedItems.get(0).getHref();
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
         IDE.getInstance().openView(display.asView());
         display.setApplicationTypeValues(APP_TYPES);
         display.focusInApplicationNameField();
         display.getWorkDirLocationField().setValue(workDir);
         display.enableCreateButton(false);
      }
   }

   /**
    * Perform creation of application on OpenShift.
    */
   protected void doCreateApplication()
   {
      String applicationName = display.getApplicationNameField().getValue();
      String type = display.getTypeField().getValue();
      OpenShiftClientService.getInstance().createApplication(applicationName, type, workDir,
         new AsyncRequestCallback<AppInfo>()
         {

            @Override
            protected void onSuccess(AppInfo result)
            {
               IDE.getInstance().closeView(display.asView().getId());
               eventBus.fireEvent(new OutputEvent(formApplicationCreatedMessage(result), Type.INFO));
            }

            /**
             * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
             */
            @Override
            protected void onFailure(Throwable exception)
            {
               if (exception instanceof ServerException)
               {
                  ServerException serverException = (ServerException)exception;
                  if (401 == serverException.getHTTPStatus())
                  {
                     addLoggedInHandler();
                     eventBus.fireEvent(new LoginEvent());
                     return;
                  }
               }
               super.onFailure(exception);
            }
         });
   }

   /**
    * Register {@link LoggedInHandler} handler.
    */
   protected void addLoggedInHandler()
   {
      eventBus.addHandler(LoggedInEvent.TYPE, this);
   }

   /**
    * Forms the message to be shown, when application is created.
    * 
    * @param appInfo application information
    * @return {@link String} message
    */
   protected String formApplicationCreatedMessage(AppInfo appInfo)
   {
      String applicationStr = "<br> [";
      applicationStr += "<b>Name</b>" + " : " + appInfo.getName() + "<br>";
      applicationStr += "<b>Git URL</b>" + " : " + appInfo.getGitUrl() + "<br>";
      applicationStr += "<b>Public URL</b>" + " : " + appInfo.getPublicUrl() + "<br>";
      applicationStr += "<b>Type</b>" + " : " + appInfo.getType() + "<br>";
      applicationStr += "] ";

      return OpenShiftExtension.LOCALIZATION_CONSTANT.createApplicationSuccess(applicationStr);
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent)
    */
   @Override
   public void onLoggedIn(LoggedInEvent event)
   {
      eventBus.removeHandler(LoggedInEvent.TYPE, this);
      if (!event.isFailed())
      {
         doCreateApplication();
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      this.selectedItems = event.getSelectedItems();
   }
}
