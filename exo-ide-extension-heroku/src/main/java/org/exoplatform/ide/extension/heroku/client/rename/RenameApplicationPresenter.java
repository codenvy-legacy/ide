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
package org.exoplatform.ide.extension.heroku.client.rename;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.marshaller.Property;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponse;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;

/**
 * Presenter for rename application on Heroku.
 * The view must be pointed in Views.gwt.xml.
 * Performs following actions on rename:
 * 1. Gets the Git working directory location.
 * 2. Gets application name (application info) by Git working directory location.
 * 3. Opens view for rename with pointed old name.
 * 4. When user clicks "Rename" button - performs rename application.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 2, 2011 11:54:59 AM anya $
 *
 */
public class RenameApplicationPresenter implements RenameApplicationHandler, ViewClosedHandler, ItemsSelectedHandler,
   LoggedInHandler
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
    * Selected items.
    */
   private List<Item> selectedItems;

   /**
    * Git working directory.
    */
   private String workDir;

   /**
    * Heroku application's name.
    */
   private String applicationName;
   
   private static final String NAME_PROPERTY = "name";

   /**
    * @param eventBus events handler
    */
   public RenameApplicationPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(RenameApplicationEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * Bind display with presenter.
    */
   public void bindDisplay()
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getRenameButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doRenameApplication();
         }
      });

      display.getRenameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean enable =
               (applicationName != null && !event.getValue().equals(applicationName) && event.getValue() != null && !event
                  .getValue().isEmpty());
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
               doRenameApplication();
            }
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.rename.RenameApplicationHandler#onRenameApplication(org.exoplatform.ide.extension.heroku.client.rename.RenameApplicationEvent)
    */
   @Override
   public void onRenameApplication(RenameApplicationEvent event)
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
    * Get the location of Git working directory.
    */
   protected void getWorkDir()
   {
      if (selectedItems == null || selectedItems.size() <= 0)
      {
         Dialogs.getInstance().showInfo(GitExtension.MESSAGES.selectedItemsFail());
         return;
      }

      //First get the working directory of the repository if exists:
      GitClientService.getInstance().getWorkDir(selectedItems.get(0).getId(),
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
               Dialogs.getInstance().showError(GitExtension.MESSAGES.notGitRepository());
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
               for (Property property : result)
               {
                  if (NAME_PROPERTY.equals(property.getName()))
                  {
                     applicationName = property.getValue();
                     break;
                  }
               }
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

   /**
    * Perform renaming the application.
    */
   public void doRenameApplication()
   {
      final String newName = display.getRenameField().getValue();
      HerokuClientService.getInstance().renameApplication(workDir, null, newName,
         new HerokuAsyncRequestCallback(eventBus, this)
         {

            @Override
            protected void onSuccess(List<Property> result)
            {
               eventBus.fireEvent(new OutputEvent(HerokuExtension.LOCALIZATION_CONSTANT.renameApplicationSuccess(applicationName, newName), Type.INFO));
               IDE.getInstance().closeView(display.asView().getId());
            }
         });
   }
}
