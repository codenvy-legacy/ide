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
package org.exoplatform.ide.client.module.navigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.command.GoToFolderCommandThread;
import org.exoplatform.ide.client.command.OpenFileCommandThread;
import org.exoplatform.ide.client.command.PasteItemsCommandThread;
import org.exoplatform.ide.client.command.SaveAllFilesCommandThread;
import org.exoplatform.ide.client.command.SaveFileAsCommandThread;
import org.exoplatform.ide.client.command.SaveFileCommandThread;
import org.exoplatform.ide.client.editor.custom.OpenFileWithForm;
import org.exoplatform.ide.client.event.edit.ItemsToPasteSelectedEvent;
import org.exoplatform.ide.client.framework.application.ApplicationConfiguration;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.framework.application.event.RegisterEventHandlersEvent;
import org.exoplatform.ide.client.framework.application.event.RegisterEventHandlersHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.ui.event.ClearFocusEvent;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.model.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.model.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.model.settings.ApplicationSettings;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.module.navigation.action.CreateFolderForm;
import org.exoplatform.ide.client.module.navigation.action.DeleteItemForm;
import org.exoplatform.ide.client.module.navigation.action.GetItemURLForm;
import org.exoplatform.ide.client.module.navigation.action.RenameItemForm;
import org.exoplatform.ide.client.module.navigation.event.DeleteItemEvent;
import org.exoplatform.ide.client.module.navigation.event.DeleteItemHandler;
import org.exoplatform.ide.client.module.navigation.event.GetFileURLEvent;
import org.exoplatform.ide.client.module.navigation.event.GetFileURLHandler;
import org.exoplatform.ide.client.module.navigation.event.OpenFileWithEvent;
import org.exoplatform.ide.client.module.navigation.event.OpenFileWithHandler;
import org.exoplatform.ide.client.module.navigation.event.RenameItemEvent;
import org.exoplatform.ide.client.module.navigation.event.RenameItemHander;
import org.exoplatform.ide.client.module.navigation.event.SaveAsTemplateEvent;
import org.exoplatform.ide.client.module.navigation.event.SaveAsTemplateHandler;
import org.exoplatform.ide.client.module.navigation.event.SearchFileEvent;
import org.exoplatform.ide.client.module.navigation.event.SearchFileHandler;
import org.exoplatform.ide.client.module.navigation.event.edit.CopyItemsEvent;
import org.exoplatform.ide.client.module.navigation.event.edit.CopyItemsHandler;
import org.exoplatform.ide.client.module.navigation.event.edit.CutItemsEvent;
import org.exoplatform.ide.client.module.navigation.event.edit.CutItemsHandler;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateFolderEvent;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateFolderHandler;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler;
import org.exoplatform.ide.client.module.navigation.event.upload.UploadFileEvent;
import org.exoplatform.ide.client.module.navigation.event.upload.UploadFileHandler;
import org.exoplatform.ide.client.module.navigation.handler.CreateFileCommandThread;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.Item;
import org.exoplatform.ide.client.search.file.SearchForm;
import org.exoplatform.ide.client.template.SaveAsTemplateForm;
import org.exoplatform.ide.client.upload.UploadForm;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class NavigationModuleEventHandler implements OpenFileWithHandler, UploadFileHandler, SaveAsTemplateHandler,
   CreateFolderHandler, CopyItemsHandler, CutItemsHandler, RenameItemHander, DeleteItemHandler, SearchFileHandler,
   GetFileURLHandler, ApplicationSettingsReceivedHandler, ItemsSelectedHandler, RegisterEventHandlersHandler,
   EditorFileOpenedHandler, EditorFileClosedHandler, EntryPointChangedHandler, ConfigurationReceivedSuccessfullyHandler
{
   private SaveFileCommandThread saveFileCommandHandler;

   private SaveFileAsCommandThread saveFileAsCommandHandler;

   private SaveAllFilesCommandThread saveAllFilesCommandHandler;

   private GoToFolderCommandThread goToFolderCommandHandler;

   private PasteItemsCommandThread pasteItemsCommandHandler;

   private OpenFileCommandThread openFileCommandThread;

   private CreateFileCommandThread createFileCommandThread;

   private HandlerManager eventBus;

   private ApplicationContext context;

   protected Handlers handlers;

   private ApplicationSettings applicationSettings;
   
   private ApplicationConfiguration applicationConfiguration;

   private List<Item> selectedItems = new ArrayList<Item>();

   private HashMap<String, File> openedFiles = new HashMap<String, File>();
   
   private String entryPoint;

   public NavigationModuleEventHandler(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;

      handlers = new Handlers(eventBus);
      handlers.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      handlers.addHandler(RegisterEventHandlersEvent.TYPE, this);
      handlers.addHandler(EntryPointChangedEvent.TYPE, this);
      handlers.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
      
      createFileCommandThread = new CreateFileCommandThread(eventBus, context);
      openFileCommandThread = new OpenFileCommandThread(eventBus, context);
      saveFileCommandHandler = new SaveFileCommandThread(eventBus, context);
      saveFileAsCommandHandler = new SaveFileAsCommandThread(eventBus, context);
      saveAllFilesCommandHandler = new SaveAllFilesCommandThread(eventBus, context);
      goToFolderCommandHandler = new GoToFolderCommandThread(eventBus, context);
      pasteItemsCommandHandler = new PasteItemsCommandThread(eventBus, context);      
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();
   }

   public void onRegisterEventHandlers(RegisterEventHandlersEvent event)
   {
      handlers.addHandler(OpenFileWithEvent.TYPE, this);
      handlers.addHandler(UploadFileEvent.TYPE, this);
      handlers.addHandler(SaveAsTemplateEvent.TYPE, this);
      handlers.addHandler(CreateFolderEvent.TYPE, this);
      handlers.addHandler(CopyItemsEvent.TYPE, this);
      handlers.addHandler(CutItemsEvent.TYPE, this);

      handlers.addHandler(DeleteItemEvent.TYPE, this);
      handlers.addHandler(RenameItemEvent.TYPE, this);
      handlers.addHandler(SearchFileEvent.TYPE, this);
      handlers.addHandler(GetFileURLEvent.TYPE, this);

      handlers.addHandler(EditorFileOpenedEvent.TYPE, this);
      handlers.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   public void onOpenFileWith(OpenFileWithEvent event)
   {
      new OpenFileWithForm(eventBus, context.getActiveFile(), context.getOpenedFiles(), applicationSettings);
   }

   public void onUploadFile(UploadFileEvent event)
   {
      Item item = selectedItems.get(0);

      String path = item.getHref();
      if (item instanceof File)
      {
         path = path.substring(path.lastIndexOf("/"));
      }
      eventBus.fireEvent(new ClearFocusEvent());
      new UploadForm(eventBus, selectedItems, path, event.isOpenFile(), applicationConfiguration);
   }

   public void onSaveAsTemplate(SaveAsTemplateEvent event)
   {
      File file = context.getActiveFile();
      new SaveAsTemplateForm(eventBus, file);
   }

   public void onCreateFolder(CreateFolderEvent event)
   {
      Item item = selectedItems.get(0);

      String href = item.getHref();
      if (item instanceof File)
      {
         href = href.substring(0, href.lastIndexOf("/") + 1);
      }

      new CreateFolderForm(eventBus, selectedItems.get(0), href);
   }

   public void onCopyItems(CopyItemsEvent event)
   {
      context.getItemsToCopy().clear();
      context.getItemsToCut().clear();
      context.getItemsToCopy().addAll(selectedItems);
      eventBus.fireEvent(new ItemsToPasteSelectedEvent());
   }

   public void onCutItems(CutItemsEvent event)
   {
      context.getItemsToCut().clear();
      context.getItemsToCopy().clear();

      context.getItemsToCut().addAll(selectedItems);
      eventBus.fireEvent(new ItemsToPasteSelectedEvent());
   }

   public void onRenameItem(RenameItemEvent event)
   {
      new RenameItemForm(eventBus, selectedItems, openedFiles);
   }

   public void onDeleteItem(DeleteItemEvent event)
   {
      new DeleteItemForm(eventBus, selectedItems, openedFiles);
   }

   public void onSearchFile(SearchFileEvent event)
   {
      new SearchForm(eventBus, selectedItems, entryPoint);
   }

   public void onGetFileURL(GetFileURLEvent event)
   {
      String url = selectedItems.get(0).getHref();
      new GetItemURLForm(eventBus, url);
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      entryPoint = event.getEntryPoint();
   }

   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      applicationConfiguration = event.getConfiguration();
   }

}
