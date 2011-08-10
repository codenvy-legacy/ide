/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.navigation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.FolderCreateCallback;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.navigation.event.CreateFolderEvent;
import org.exoplatform.ide.client.navigation.event.CreateFolderHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CreateFolderPresenter implements CreateFolderHandler, ItemsSelectedHandler, ViewClosedHandler
{
   
   public interface Display extends IsView
   {
      public static final String ID = "ideCreateFolderForm";
      
      HasValue<String> getFolderNameField();

      HasClickHandlers getCreateButton();

      HasClickHandlers getCancelButton();

      HasKeyPressHandlers getFolderNameFiledKeyPressed();

      void setFocusInNameField();
   }
   
   private static final String NEW_FOLDER_NAME = IDE.IDE_LOCALIZATION_CONSTANT.newFolderName();

   private Display display;
   
   private List<Item> selectedItems = new ArrayList<Item>();

   private HandlerManager eventBus;

   public CreateFolderPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      
      eventBus.addHandler(CreateFolderEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      display = d;

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            closeView();
         }
      });

      display.getCreateButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            createFolder();
         }
      });

      display.getFolderNameField().setValue(NEW_FOLDER_NAME);
      
      display.setFocusInNameField();

      display.getFolderNameFiledKeyPressed().addKeyPressHandler(new KeyPressHandler()
      {
         public void onKeyPress(KeyPressEvent event)
         {
            if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
            {
               createFolder();
            }
         }
      });

   }
   
   private void closeView()
   {
      IDE.getInstance().closeView(Display.ID);
   }

   protected void createFolder()
   {
      final String newFolderName = display.getFolderNameField().getValue();
      String newFolderHref = selectedItems.get(0).getWorkDir() + URL.encodePathSegment(newFolderName) + "/";
      Folder newFolder = new Folder(newFolderHref);
      VirtualFileSystem.getInstance().createFolder(newFolder, new FolderCreateCallback()
      {
         @Override
         protected void onSuccess(Folder result)
         {
            String folder = selectedItems.get(0).getWorkDir();
            eventBus.fireEvent(new RefreshBrowserEvent(new Folder(folder), result));
            closeView();
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.navigation.event.CreateFolderHandler#onCreateFolder(org.exoplatform.ide.client.navigation.event.CreateFolderEvent)
    */
   @Override
   public void onCreateFolder(CreateFolderEvent event)
   {
      if (selectedItems == null || selectedItems.isEmpty())
      {
         eventBus.fireEvent(new ExceptionThrownEvent(IDE.ERRORS_CONSTANT.createFolderSelectParentFolder()));
         return;
      }
      if (display == null)
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView(d.asView());
         bindDisplay(d);
         display.setFocusInNameField();
      }
      else
      {
         eventBus.fireEvent(new ExceptionThrownEvent("Display Go To Line must be null"));
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
