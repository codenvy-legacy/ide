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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceFileEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.FileCallback;
import org.exoplatform.ide.client.framework.vfs.FileContentSaveCallback;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemPropertiesCallback;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.callback.MoveItemCallback;
import org.exoplatform.ide.client.model.util.IDEMimeTypes;
import org.exoplatform.ide.client.navigation.event.RenameItemEvent;
import org.exoplatform.ide.client.navigation.event.RenameItemHander;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Presenter for renaming file and changing mime-type of file.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class RenameFilePresenter implements RenameItemHander, ApplicationSettingsReceivedHandler, ItemsSelectedHandler,
   EditorFileOpenedHandler, EditorFileClosedHandler, ViewClosedHandler
{

   /**
    * Interface for display for renaming files and folders.
    */
   public interface Display extends IsView
   {

      HasValue<String> getItemNameField();

      HasClickHandlers getRenameButton();

      HasClickHandlers getCancelButton();

      HasKeyPressHandlers getNameFieldKeyPressHandler();

      HasValue<String> getMimeType();

      void setMimeTypes(String[] mimeTypes);

      void enableMimeTypeSelect();

      void disableMimeTypeSelect();

      void setDefaultMimeType(String mimeType);

      void enableRenameButton(boolean enable);

      void addLabel(String text);
      
      void focusInNameField();

   }

   private static final String CANT_CHANGE_MIME_TYPE_TO_OPENED_FILE = IDE.ERRORS_CONSTANT
      .renameItemCantRenameMimeTypeToOpenedFile();
   
   private static final String SELECT_ITEM_TO_RENAME = "";

   private HandlerManager eventBus;

   private Display display;

   private String itemBaseHref;

   private List<Item> selectedItems;

   private Map<String, File> openedFiles = new LinkedHashMap<String, File>();

   private Map<String, String> lockTokens;
   
   private Item renamedItem;

   private String sourceHref;
   
   public RenameFilePresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      
      eventBus.addHandler(RenameItemEvent.TYPE, this);
      eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      display = d;

      display.enableRenameButton(false);

      itemBaseHref = selectedItems.get(0).getWorkDir();

      display.getItemNameField().setValue(selectedItems.get(0).getName());
      display.getItemNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         public void onValueChange(ValueChangeEvent<String> event)
         {
            display.enableRenameButton(wasItemPropertiesChanged());
         }
      });

      display.getRenameButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            rename();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            closeView();
         }
      });

      display.getNameFieldKeyPressHandler().addKeyPressHandler(new KeyPressHandler()
      {

         public void onKeyPress(KeyPressEvent event)
         {
            if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER && wasItemPropertiesChanged())
            {
               rename();
            }
         }

      });

      File file = (File)selectedItems.get(0);

      List<String> mimeTypes = IDEMimeTypes.getSupportedMimeTypes();
      Collections.sort(mimeTypes);

      String[] valueMap = mimeTypes.toArray(new String[0]);

      display.setMimeTypes(valueMap);

      display.setDefaultMimeType(file.getContentType());

      display.getMimeType().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         public void onValueChange(ValueChangeEvent<String> event)
         {
            display.enableRenameButton(wasItemPropertiesChanged());
         }
      });
      if (openedFiles.containsKey(file.getHref()))
      {
         display.disableMimeTypeSelect();
         display.addLabel(CANT_CHANGE_MIME_TYPE_TO_OPENED_FILE);
      }
      
      display.focusInNameField();

   }

   private boolean wasItemPropertiesChanged()
   {
      File file = (File)selectedItems.get(0);

      //if name is not set
      final String newName = display.getItemNameField().getValue();

      if (newName == null || newName.length() == 0)
      {
         return false;
      }

      //if mime-type is not set
      final String newMimeType = display.getMimeType().getValue();

      if (newMimeType == null || newMimeType.length() == 0)
      {
         return false;
      }

      //if file name was changed or file mime-type was changed, than return true;
      if (!file.getName().equals(newName) || !file.getContentType().equals(newMimeType))
      {
         return true;
      }
      return false;
   }

   protected void rename()
   {
      File file = (File)selectedItems.get(0);
      final String destination = getDestination(file);

      String newMimeType = display.getMimeType().getValue();
      if (newMimeType != null && newMimeType.length() > 0)
      {
         file.setContentType(newMimeType);
         VirtualFileSystem.getInstance().getContent(file, new FileCallback()
         {
            @Override
            protected void onSuccess(File result)
            {
               fileContentReceived(result);
            }
         });
         return;
      }
      moveItem(file, destination);
   }

   private String getDestination(Item item)
   {
      return itemBaseHref + URL.encodePathSegment(display.getItemNameField().getValue());
   }

   private void completeMove()
   {
      String href = sourceHref;
      if (href.endsWith("/"))
      {
         href = href.substring(0, href.length() - 1);
      }

      href = href.substring(0, href.lastIndexOf("/") + 1);
      eventBus.fireEvent(new RefreshBrowserEvent(new Folder(href), renamedItem));

      closeView();
   }

   public void fileContentReceived(File file)
   {
      VirtualFileSystem.getInstance().saveContent(file, lockTokens.get(file.getHref()), new FileContentSaveCallback()
      {
         @Override
         protected void onSuccess(FileData result)
         {
            final Item item = selectedItems.get(0);

            final String destination = getDestination(item);

            if (!item.getHref().equals(destination))
            {
               moveItem(item, destination);
            }
            else
            {
               String href = item.getHref();
               if (href.endsWith("/"))
               {
                  href = href.substring(0, href.length() - 1);
               }

               href = href.substring(0, href.lastIndexOf("/") + 1);
               eventBus.fireEvent(new RefreshBrowserEvent(new Folder(href), item));
               closeView();
            }
         }
      });
   }

   /**
    * Mote item.
    * 
    * @param item - the item to move (with old properties: href and name)
    * @param destination - the location of new item (must be encoded)
    */
   private void moveItem(Item item, String destination)
   {
      MoveItemCallback moveItemCallback = new MoveItemCallback(eventBus)
      {
         @Override
         protected void onSuccess(MoveItemData result)
         {
            itemMoved(result.getItem(), result.getOldHref());
         }
      };
      VirtualFileSystem.getInstance().move(item, destination, lockTokens.get(item.getHref()), moveItemCallback);
   }

   /**
    * @param item - moved item
    * @param oldItemHref - href of old item
    */
   private void itemMoved(Item item, final String oldItemHref)
   {
      renamedItem = item;
      sourceHref = oldItemHref;

      VirtualFileSystem.getInstance().getProperties(item, new ItemPropertiesCallback()
      {
         @Override
         protected void onSuccess(Item result)
         {
            renamedItem = result;

            File file = (File)result;

            if (openedFiles.containsKey(oldItemHref))
            {
               File openedFile = openedFiles.get(oldItemHref);
               openedFile.setHref(file.getHref());
               openedFiles.remove(oldItemHref);
               openedFiles.put(openedFile.getHref(), file);

               eventBus.fireEvent(new EditorReplaceFileEvent(new File(oldItemHref), file));
            }

            if (result.getHref().equals(renamedItem.getHref()) && openedFiles.get(renamedItem.getHref()) != null)
            {
               openedFiles.get(renamedItem.getHref()).getProperties().clear();
               openedFiles.get(renamedItem.getHref()).getProperties().addAll(result.getProperties());
            }
            completeMove();
         }
      });

   }

   /**
    * @see org.exoplatform.ide.client.navigation.event.RenameItemHander#onRenameItem(org.exoplatform.ide.client.navigation.event.RenameItemEvent)
    */
   @Override
   public void onRenameItem(RenameItemEvent event)
   {
      if (selectedItems == null || selectedItems.isEmpty())
      {
         eventBus.fireEvent(new ExceptionThrownEvent(SELECT_ITEM_TO_RENAME));
         return;
      }
      if (selectedItems.get(0) instanceof File)
         openView();
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent)
    */
   @Override
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
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
    * @see org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent)
    */
   @Override
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      ApplicationSettings applicationSettings = event.getApplicationSettings();
      
      if (applicationSettings.getValueAsMap("lock-tokens") == null)
      {
         applicationSettings.setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }

      lockTokens = applicationSettings.getValueAsMap("lock-tokens");
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent)
    */
   @Override
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }
   
   private void openView()
   {
      if (display == null)
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView(d.asView());
         bindDisplay(d);
      }
      else
      {
         eventBus.fireEvent(new ExceptionThrownEvent("Display RenameFile must be null"));
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
