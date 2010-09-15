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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.editor.custom.OpenFileWithForm;
import org.exoplatform.ide.client.event.edit.ItemsToPasteSelectedEvent;
import org.exoplatform.ide.client.framework.application.ApplicationConfiguration;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.application.event.RegisterEventHandlersEvent;
import org.exoplatform.ide.client.framework.application.event.RegisterEventHandlersHandler;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.module.IDEModule;
import org.exoplatform.ide.client.framework.ui.event.ClearFocusEvent;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.model.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.model.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.model.settings.ApplicationSettings;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.model.util.ImageUtil;
import org.exoplatform.ide.client.module.navigation.action.CreateFolderForm;
import org.exoplatform.ide.client.module.navigation.action.DeleteItemForm;
import org.exoplatform.ide.client.module.navigation.action.GetItemURLForm;
import org.exoplatform.ide.client.module.navigation.action.RenameItemForm;
import org.exoplatform.ide.client.module.navigation.control.CopyItemsCommand;
import org.exoplatform.ide.client.module.navigation.control.CutItemsCommand;
import org.exoplatform.ide.client.module.navigation.control.DeleteItemCommand;
import org.exoplatform.ide.client.module.navigation.control.GetFileURLControl;
import org.exoplatform.ide.client.module.navigation.control.GoToFolderControl;
import org.exoplatform.ide.client.module.navigation.control.OpenFileWithCommand;
import org.exoplatform.ide.client.module.navigation.control.PasteItemsCommand;
import org.exoplatform.ide.client.module.navigation.control.RefreshBrowserControl;
import org.exoplatform.ide.client.module.navigation.control.RenameItemCommand;
import org.exoplatform.ide.client.module.navigation.control.SaveAllFilesCommand;
import org.exoplatform.ide.client.module.navigation.control.SaveFileAsCommand;
import org.exoplatform.ide.client.module.navigation.control.SaveFileAsTemplateCommand;
import org.exoplatform.ide.client.module.navigation.control.SaveFileCommand;
import org.exoplatform.ide.client.module.navigation.control.SearchFilesCommand;
import org.exoplatform.ide.client.module.navigation.control.ViewItemPropertiesCommand;
import org.exoplatform.ide.client.module.navigation.control.download.DownloadFileCommand;
import org.exoplatform.ide.client.module.navigation.control.download.DownloadZippedFolderCommand;
import org.exoplatform.ide.client.module.navigation.control.newitem.CreateFileFromTemplateControl;
import org.exoplatform.ide.client.module.navigation.control.newitem.CreateFolderControl;
import org.exoplatform.ide.client.module.navigation.control.newitem.NewFileCommandMenuGroup;
import org.exoplatform.ide.client.module.navigation.control.newitem.NewFilePopupMenuControl;
import org.exoplatform.ide.client.module.navigation.control.upload.OpenLocalFileCommand;
import org.exoplatform.ide.client.module.navigation.control.upload.UploadFileCommand;
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
import org.exoplatform.ide.client.module.navigation.handler.FileClosedHandler;
import org.exoplatform.ide.client.module.navigation.handler.GoToFolderCommandThread;
import org.exoplatform.ide.client.module.navigation.handler.OpenFileCommandThread;
import org.exoplatform.ide.client.module.navigation.handler.PasteItemsCommandThread;
import org.exoplatform.ide.client.module.navigation.handler.SaveAllFilesCommandThread;
import org.exoplatform.ide.client.module.navigation.handler.SaveFileAsCommandThread;
import org.exoplatform.ide.client.module.navigation.handler.SaveFileCommandThread;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.Item;
import org.exoplatform.ide.client.module.vfs.api.LockToken;
import org.exoplatform.ide.client.module.vfs.api.event.ItemLockedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemLockedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.ItemUnlockedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemUnlockedHandler;
import org.exoplatform.ide.client.module.vfs.webdav.WebDavVirtualFileSystem;
import org.exoplatform.ide.client.search.file.SearchForm;
import org.exoplatform.ide.client.statusbar.NavigatorStatusControl;
import org.exoplatform.ide.client.template.SaveAsTemplateForm;
import org.exoplatform.ide.client.upload.UploadForm;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class NavigationModule implements IDEModule, OpenFileWithHandler, UploadFileHandler, SaveAsTemplateHandler,
   CreateFolderHandler, CopyItemsHandler, CutItemsHandler, RenameItemHander, DeleteItemHandler, SearchFileHandler,
   GetFileURLHandler, ApplicationSettingsReceivedHandler, ItemsSelectedHandler, RegisterEventHandlersHandler,
   EditorFileOpenedHandler, EditorFileClosedHandler, EntryPointChangedHandler,
   ConfigurationReceivedSuccessfullyHandler, EditorActiveFileChangedHandler, InitializeServicesHandler, ItemLockedHandler, ItemUnlockedHandler
{
   private HandlerManager eventBus;

   private ApplicationContext context;

   protected Handlers handlers;

   private ApplicationSettings applicationSettings;

   private ApplicationConfiguration applicationConfiguration;

   private List<Item> selectedItems = new ArrayList<Item>();

   private Map<String, File> openedFiles = new LinkedHashMap<String, File>();

   private String entryPoint;

   private File activeFile;
   
   private Map<String, LockToken> lockTokens = new HashMap<String, LockToken>();

   public NavigationModule(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);

      NewFilePopupMenuControl newFilePopupMenuControl = new NewFilePopupMenuControl();

      eventBus.fireEvent(new RegisterControlEvent(newFilePopupMenuControl, true));
      eventBus.fireEvent(new RegisterControlEvent(new NewFileCommandMenuGroup(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new CreateFileFromTemplateControl(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new CreateFolderControl(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New XML File", "XML File",
         "Create New XML File", Images.FileTypes.XML, MimeType.TEXT_XML)));
      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New HTML File", "HTML File",
         "Create New HTML File", Images.FileTypes.HTML, MimeType.TEXT_HTML)));
      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New TEXT File", "Text File",
         "Create New Text File", Images.FileTypes.TXT, MimeType.TEXT_PLAIN)));
      eventBus.fireEvent(new RegisterControlEvent(
         new NewItemControl("File/New/New Java Script File", "JavaScript File", "Create New Java Script File",
            Images.FileTypes.JAVASCRIPT, MimeType.APPLICATION_JAVASCRIPT)));
      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New CSS File", "CSS File",
         "Create New CSS File", Images.FileTypes.CSS, MimeType.TEXT_CSS)));
      eventBus.fireEvent(new RegisterControlEvent(new ViewItemPropertiesCommand(eventBus), true, true));
      eventBus.fireEvent(new RegisterControlEvent(new OpenFileWithCommand(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new UploadFileCommand(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new OpenLocalFileCommand(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new DownloadFileCommand(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new DownloadZippedFolderCommand(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new SaveFileCommand(eventBus), true));
      eventBus.fireEvent(new RegisterControlEvent(new SaveFileAsCommand(eventBus), true));
      eventBus.fireEvent(new RegisterControlEvent(new SaveAllFilesCommand(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new SaveFileAsTemplateCommand(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new CutItemsCommand(eventBus), true));
      eventBus.fireEvent(new RegisterControlEvent(new CopyItemsCommand(eventBus), true));
      eventBus.fireEvent(new RegisterControlEvent(new PasteItemsCommand(eventBus), true));
      eventBus.fireEvent(new RegisterControlEvent(new RenameItemCommand(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new DeleteItemCommand(eventBus), true));
      eventBus.fireEvent(new RegisterControlEvent(new SearchFilesCommand(eventBus), true));
      eventBus.fireEvent(new RegisterControlEvent(new RefreshBrowserControl(eventBus), true));
      eventBus.fireEvent(new RegisterControlEvent(new GoToFolderControl(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new GetFileURLControl(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new NavigatorStatusControl(eventBus)));

      handlers.addHandler(InitializeServicesEvent.TYPE, this);
      handlers.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      handlers.addHandler(RegisterEventHandlersEvent.TYPE, this);
      handlers.addHandler(EntryPointChangedEvent.TYPE, this);
      handlers.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      
      handlers.addHandler(ItemLockedEvent.TYPE, this);
      handlers.addHandler(ItemUnlockedEvent.TYPE, this);

      new CreateFileCommandThread(eventBus, context);
      new OpenFileCommandThread(eventBus, lockTokens, context);
      new SaveFileCommandThread(eventBus, lockTokens);
      new SaveFileAsCommandThread(eventBus, lockTokens);
      new SaveAllFilesCommandThread(eventBus, lockTokens);
      new GoToFolderCommandThread(eventBus);
      new PasteItemsCommandThread(eventBus, context);
      new FileClosedHandler(eventBus, lockTokens);
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();
   }

   public void onInitializeServices(InitializeServicesEvent event)
   {
      new WebDavVirtualFileSystem(eventBus, event.getLoader(), ImageUtil.getIcons(), event
         .getApplicationConfiguration().getContext());
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
      new OpenFileWithForm(eventBus, (File)selectedItems.get(0), openedFiles, applicationSettings);
   }

   public void onUploadFile(UploadFileEvent event)
   {
      String path = "";
     
      if (selectedItems == null || selectedItems.size() == 0)
      {
         if (! event.isOpenFile() )
         {
            Dialogs.getInstance().showInfo("Please, select target folder in the Workspace Panel before calling this command !");
            return;
         }
      }
      else
      {
         Item item = selectedItems.get(0);
   
         path = item.getHref();
         if (item instanceof File)
         {
            path = path.substring(path.lastIndexOf("/"));
         }
      }
      eventBus.fireEvent(new ClearFocusEvent());
      new UploadForm(eventBus, selectedItems, path, event.isOpenFile(), applicationConfiguration);
   }

   public void onSaveAsTemplate(SaveAsTemplateEvent event)
   {
      new SaveAsTemplateForm(eventBus, activeFile);
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
      new RenameItemForm(eventBus, selectedItems, openedFiles, lockTokens);
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

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
   }
   
   /**
    * @see org.exoplatform.ide.client.module.vfs.api.event.ItemUnlockedHandler#onItemUnlocked(org.exoplatform.ide.client.module.vfs.api.event.ItemUnlockedEvent)
    */
   public void onItemUnlocked(ItemUnlockedEvent event)
   {
      lockTokens.remove(event.getItem().getHref());
   }

   /**
    * @see org.exoplatform.ide.client.module.vfs.api.event.ItemLockedHandler#onItemLocked(org.exoplatform.ide.client.module.vfs.api.event.ItemLockedEvent)
    */
   public void onItemLocked(ItemLockedEvent event)
   {
      lockTokens.put(event.getItem().getHref(), event.getLockToken());
   }

}
