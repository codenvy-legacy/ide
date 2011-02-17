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
package org.exoplatform.ide.client.module.navigation.action;

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
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceFileEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.FileCallback;
import org.exoplatform.ide.client.framework.vfs.FileContentSaveCallback;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemPropertiesCallback;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.callback.MoveItemCallback;
import org.exoplatform.ide.client.framework.vfs.event.FileContentReceivedEvent;
import org.exoplatform.ide.client.framework.vfs.event.FileContentReceivedHandler;
import org.exoplatform.ide.client.model.util.IDEMimeTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Presenter for renaming folders and files form.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class RenameItemPresenter implements FileContentReceivedHandler, ExceptionThrownHandler
{

   /**
    * Interface for display for renaming files and folders.
    */
   public interface Display
   {

      HasValue<String> getItemNameField();

      HasClickHandlers getRenameButton();

      HasClickHandlers getCancelButton();

      HasKeyPressHandlers getItemNameFieldKeyPressHandler();
      
      HasValue<String> getMimeType();

      void setMimeTypes(String[] mimeTypes);

      void enableMimeTypeSelect();

      void disableMimeTypeSelect();

      void setDefaultMimeType(String mimeType);
      
      void enableRenameButton();
      
      void disableRenameButton();
      
      void addLabel(String style, String text);
      
      void closeForm();

   }

   private HandlerManager eventBus;

   private Display display;

   private Handlers handlers;

   //private ApplicationContext context;

   private String itemBaseHref;

   private List<Item> selectedItems;

   private Map<String, File> openedFiles;

   private Map<String, String> lockTokens;

   public RenameItemPresenter(HandlerManager eventBus, List<Item> selectedItems, Map<String, File> openedFiles,
      Map<String, String> lockTokens)
   {
      this.eventBus = eventBus;
      this.selectedItems = selectedItems;
      this.openedFiles = openedFiles;
      this.lockTokens = lockTokens;
      handlers = new Handlers(eventBus);
   }

   public void bindDisplay(Display d)
   {
      display = d;
      
      display.disableRenameButton();

      itemBaseHref = selectedItems.get(0).getHref();
      if (selectedItems.get(0) instanceof Folder)
      {
         itemBaseHref = itemBaseHref.substring(0, itemBaseHref.lastIndexOf("/"));
      }
      itemBaseHref = itemBaseHref.substring(0, itemBaseHref.lastIndexOf("/") + 1);

      display.getItemNameField().setValue(selectedItems.get(0).getName());
      
      display.getItemNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (wasItemPropertiesChanged())
            {
               display.enableRenameButton();
            }
            else
            {
               display.disableRenameButton();
            }
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
            display.closeForm();
         }
      });

      display.getItemNameFieldKeyPressHandler().addKeyPressHandler(new KeyPressHandler()
      {

         public void onKeyPress(KeyPressEvent event)
         {
            if (event.getCharCode() == KeyCodes.KEY_ENTER)
            {
               rename();
            }
         }

      });
      
      if (selectedItems.get(0) instanceof File)
      {
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
               if (wasItemPropertiesChanged())
               {
                  display.enableRenameButton();
               }
               else
               {
                  display.disableRenameButton();
               }
            }
         });
         if (openedFiles.containsKey(file.getHref()))
         {
            display.disableMimeTypeSelect();
            display.addLabel("", "Can't change mime-type to opened file");
         }
         
      }
      
   }
   
   private boolean wasItemPropertiesChanged()
   {
      if (selectedItems.get(0) instanceof File)
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
      else
      {
         final String newName = display.getItemNameField().getValue();
         if (newName == null || newName.length() == 0)
         {
            return false;
         }
         final String oldName = selectedItems.get(0).getName();
         if (newName.equals(oldName))
         {
            return false;
         }
         return true;
      }
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   protected void rename()
   {
      final Item item = selectedItems.get(0);

      final String destination = getDestination(item);

      if (item instanceof File)
      {
         File file = (File)item;
         String newMimeType = display.getMimeType().getValue();
         if (newMimeType != null && newMimeType.length() > 0)
         {
            file.setContentType(newMimeType);
            handlers.addHandler(FileContentReceivedEvent.TYPE, this);
            handlers.addHandler(ExceptionThrownEvent.TYPE, this);
            VirtualFileSystem.getInstance().getContent(file, new FileCallback(eventBus)
            {
               
               @Override
               public void onResponseReceived(Request request, Response response)
               {
                  eventBus.fireEvent(new FileContentReceivedEvent(this.getFile()));
               }
            });
            return;
         }
         moveItem(item, destination);
      }

      moveItem(item, destination);
   }
   
   private String getDestination(Item item)
   {
      String href = display.getItemNameField().getValue();

      href = itemBaseHref + href;

      if (item instanceof Folder)
      {
         href += "/";
      }
      return href;
   }

   private void updateOpenedFiles(String href, String sourceHref)
   {
      List<String> keys = new ArrayList<String>();
      for (String key : openedFiles.keySet())
      {
         keys.add(key);
      }

      for (String key : keys)
      {
         if (key.startsWith(sourceHref))
         {
            File file = openedFiles.get(key);
            String fileHref = file.getHref().replace(sourceHref, href);
            file.setHref(fileHref);

            openedFiles.remove(key);
            openedFiles.put(fileHref, file);
            eventBus.fireEvent(new EditorReplaceFileEvent(file, null));
         }
      }
   }

   private Item renamedItem;

   private String sourceHref;

   private void completeMove()
   {
      String href = sourceHref;
      if (href.endsWith("/"))
      {
         href = href.substring(0, href.length() - 1);
      }

      href = href.substring(0, href.lastIndexOf("/") + 1);
      eventBus.fireEvent(new RefreshBrowserEvent(new Folder(href), renamedItem));

      handlers.removeHandlers();
      display.closeForm();
   }


   /**
    * @see org.exoplatform.ide.client.framework.vfs.event.FileContentReceivedHandler#onFileContentReceived(org.exoplatform.ide.client.framework.vfs.event.FileContentReceivedEvent)
    */
   public void onFileContentReceived(FileContentReceivedEvent event)
   {
      handlers.removeHandlers();
      File file = event.getFile();
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
      VirtualFileSystem.getInstance().saveContent(file, lockTokens.get(file.getHref()), new FileContentSaveCallback(eventBus)
      {
         public void onResponseReceived(Request request, Response response)
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

               handlers.removeHandlers();
               display.closeForm();
            }
         }
      });
   }
   
   private void moveItem(Item item, String destination)
   {
      VirtualFileSystem.getInstance().move(item, destination, lockTokens.get(item.getHref()), new MoveItemCallback(eventBus)
      {
         public void onResponseReceived(Request request, Response response)
         {
            itemMoved(this.getItem(), this.getSourceHref());
         }
      });
   }
   
   private void itemMoved(Item item, String oldItemHref)
   {
      renamedItem = item;
      sourceHref = oldItemHref;

      if (item instanceof File)
      {
         File file = (File)item;

         if (openedFiles.containsKey(oldItemHref))
         {
            File openedFile = openedFiles.get(oldItemHref);
            openedFile.setHref(file.getHref());
            openedFiles.remove(oldItemHref);
            openedFiles.put(openedFile.getHref(), openedFile);

            eventBus.fireEvent(new EditorReplaceFileEvent(file, null));
         }

         VirtualFileSystem.getInstance().getPropertiesCallback(item, new ItemPropertiesCallback()
         {
            
            public void onResponseReceived(Request request, Response response)
            {
               if (this.getItem().getHref().equals(renamedItem.getHref()) && openedFiles.get(renamedItem.getHref()) != null)
               {
                  openedFiles.get(renamedItem.getHref()).getProperties().clear();
                  openedFiles.get(renamedItem.getHref()).getProperties().addAll(this.getItem().getProperties());
               }
               completeMove();
            }
            
            @Override
            public void fireErrorEvent()
            {
               eventBus.fireEvent(new ExceptionThrownEvent("Service is not deployed.<br>Resource not found."));
               handlers.removeHandlers();
            }
         });
      }
      else
      {
         updateOpenedFiles(item.getHref(), oldItemHref);
         completeMove();
      }
   }

   /**
    * @see org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler#onError(org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent)
    */
   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
   }

}
