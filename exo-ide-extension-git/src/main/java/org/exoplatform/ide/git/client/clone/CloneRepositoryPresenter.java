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
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewDisplay;
import org.exoplatform.ide.client.framework.ui.gwt.ViewEx;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.git.client.clone.event.CloneRepositoryEvent;
import org.exoplatform.ide.git.client.clone.event.CloneRepositoryHandler;
import org.exoplatform.ide.git.client.service.GitClientService;

import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 22, 2011 4:31:12 PM anya $
 *
 */
public class CloneRepositoryPresenter implements ItemsSelectedHandler, CloneRepositoryHandler
{
   public interface Display extends ViewDisplay
   {
      HasValue<String> getWorkDirValue();

      HasValue<String> getRemoteUriValue();

      HasValue<String> getRemoteNameValue();

      HasClickHandlers getCloneButton();

      HasClickHandlers getCancelButton();

      void enableCloneButton(boolean enable);

   }

   private Display display;

   private HandlerManager eventBus;

   private List<Item> selectedItems;

   /**
    * 
    */
   public CloneRepositoryPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(CloneRepositoryEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.getView().getId());
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
    * @see org.exoplatform.ide.git.client.clone.event.CloneRepositoryHandler#onCloneRepository(org.exoplatform.ide.git.client.clone.event.CloneRepositoryEvent)
    */
   @Override
   public void onCloneRepository(CloneRepositoryEvent event)
   {
      if (selectedItems == null || selectedItems.size() != 1 || !(selectedItems.get(0) instanceof Folder))
      {
         Dialogs.getInstance().showInfo("Please, select one folder in browser tree.");
         return;
      }

      Display d = GWT.create(Display.class);
      IDE.getInstance().openView((ViewEx)d);
      bindDisplay(d);
      display.getWorkDirValue().setValue(selectedItems.get(0).getHref(), true);
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
            Dialogs.getInstance().showInfo("Clone remote repository", "Repository was successfully cloned.");
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            Dialogs.getInstance().showError("Clone remote repository", "Repository was not cloned.");
         }
      });
      IDE.getInstance().closeView(display.getView().getId());
   }

}
