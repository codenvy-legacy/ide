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
package org.exoplatform.ide.extension.heroku.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.Messages;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponse;

import java.util.HashMap;
import java.util.List;

/**
 * Presenter for created application view.
 * The view must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 26, 2011 2:37:21 PM anya $
 *
 */
public class CreateApplicationPresenter implements ViewClosedHandler, ItemsSelectedHandler, CreateApplicationHandler,
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
       * Get remote repository name field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getRemoteNameField();

      /**
       * Get Git work directory location field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getWorkDirLocationField();

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
   }

   private Display display;

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
    * @param eventBus events handler
    */
   public CreateApplicationPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(CreateApplicationEvent.TYPE, this);
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
            boolean enabled = (event.getValue() != null && !event.getValue().isEmpty());
            display.enableCreateButton(enabled);
         }
      });

   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.create.CreateApplicationHandler#onCreateApplication(org.exoplatform.ide.extension.heroku.client.create.CreateApplicationEvent)
    */
   @Override
   public void onCreateApplication(CreateApplicationEvent event)
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
    * Get the location of Git work directory.
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
               if (display == null)
               {
                  display = GWT.create(Display.class);
                  bindDisplay();
                  IDE.getInstance().openView(display.asView());
                  display.enableCreateButton(false);
                  display.focusInApplicationNameField();
                  display.getWorkDirLocationField().setValue(workDir);
               }
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               Dialogs.getInstance().showError(Messages.NOT_GIT_REPOSITORY);
            }
         });
   }

   /**
    * Perform creation of application on Heroku.
    */
   protected void doCreateApplication()
   {
      String applicationName = display.getApplicationNameField().getValue();
      String remoteName = display.getRemoteNameField().getValue();
      HerokuClientService.getInstance().createApplication(applicationName, workDir, remoteName,
         new HerokuAsyncRequestCallback(eventBus, this)
         {

            @Override
            protected void onSuccess(HashMap<String, String> result)
            {
               IDE.getInstance().closeView(display.asView().getId());
               eventBus.fireEvent(new OutputEvent(formApplicationCreatedMessage(result), Type.INFO));
            }

            /**
             * @see org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback#onFailure(java.lang.Throwable)
             */
            @Override
            protected void onFailure(Throwable exception)
            {
               super.onFailure(exception);
            }
         });
   }

   /**
    * Form the message about application creation to display in output.
    * 
    * @param properties application's properties
    * @return {@link String}
    */
   public String formApplicationCreatedMessage(HashMap<String, String> properties)
   {
      if (properties == null)
      {
         return "Application is successfully created.";
      }
      String message = "Application <br> [";
      for (String key : properties.keySet())
      {
         message += "<b>" + key + "</b>" + " : " + properties.get(key) + "<br>";
      }
      message += "] ";
      message += "is successfully created.";
      return message;
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
         doCreateApplication();
      }
   }
}
