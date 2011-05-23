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
package org.exoplatform.ide.git.client.clone;

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
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.Messages;

import java.util.List;

/**
 * Presenter for Clone Repository View.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 22, 2011 4:31:12 PM anya $
 *
 */
public class CloneRepositoryPresenter implements ItemsSelectedHandler, CloneRepositoryHandler
{
   public interface Display extends IsView
   {
      /**
       * Returns working directory field.
       * 
       * @return {@link HasValue<{@link String}>}
       */
      HasValue<String> getWorkDirValue();

      /**
       * Returns remote URI field.
       * 
       * @return {@link HasValue<{@link String}>}
       */
      HasValue<String> getRemoteUriValue();

      /**
       * Returns remote name field.
       * 
       * @return {@link HasValue<{@link String}>}
       */
      HasValue<String> getRemoteNameValue();

      /**
       * Returns clone repository button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getCloneButton();

      /**
       * Returns cancel button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getCancelButton();

      /**
       * Changes the state of clone button.
       * 
       * @param enable
       */
      void enableCloneButton(boolean enable);
      
      
      void focusInRemoteUrlField();
   }

   /**
    * Presenter's display.
    */
   private Display display;

   private HandlerManager eventBus;
   
   private static final String DEFAULT_REPO_NAME = "origin";

   /**
    * Selected items in browser tree.
    */
   private List<Item> selectedItems;

   /**
    * @param eventBus
    */
   public CloneRepositoryPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(CloneRepositoryEvent.TYPE, this);
   }

   /**
    * @param d
    */
   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getCloneButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            cloneRepository();
         }
      });

      display.getRemoteUriValue().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean enable = (event.getValue() != null || event.getValue().length() > 0);
            display.enableCloneButton(enable);
         }
      });
   }

   /**
    * @see org.exoplatform.ide.git.client.clone.CloneRepositoryHandler#onCloneRepository(org.exoplatform.ide.git.client.clone.CloneRepositoryEvent)
    */
   @Override
   public void onCloneRepository(CloneRepositoryEvent event)
   {
      if (selectedItems == null || selectedItems.size() != 1 || !(selectedItems.get(0) instanceof Folder))
      {
         Dialogs.getInstance().showInfo(Messages.SELECTED_ITEMS_FAIL);
         return;
      }

      Display d = GWT.create(Display.class);
      IDE.getInstance().openView(d.asView());
      bindDisplay(d);
      display.focusInRemoteUrlField();
      display.getWorkDirValue().setValue(selectedItems.get(0).getHref(), true);
      display.getRemoteNameValue().setValue(DEFAULT_REPO_NAME);
      display.enableCloneButton(false);
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   /**
    * Get the necessary parameters values and call the clone repository method.
    */
   private void cloneRepository()
   {
      String workDir = display.getWorkDirValue().getValue();
      String remoteUri = display.getRemoteUriValue().getValue();
      String remoteName = display.getRemoteNameValue().getValue();

      GitClientService.getInstance().cloneRepository(workDir, remoteUri, remoteName, new AsyncRequestCallback<String>()
      {

         @Override
         protected void onSuccess(String result)
         {
            eventBus.fireEvent(new OutputEvent(Messages.CLONE_SUCCESS, Type.INFO));
            eventBus.fireEvent(new RefreshBrowserEvent());
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMessage =
               (exception.getMessage() != null && exception.getMessage().length() > 0) ? exception.getMessage()
                  : Messages.CLONE_FAILED;
            eventBus.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
         }
      });
      IDE.getInstance().closeView(display.asView().getId());
   }

}
