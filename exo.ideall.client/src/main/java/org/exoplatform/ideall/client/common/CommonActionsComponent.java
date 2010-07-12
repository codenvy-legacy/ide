/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.common;

import org.exoplatform.ideall.client.action.CreateFolderForm;
import org.exoplatform.ideall.client.action.DeleteItemForm;
import org.exoplatform.ideall.client.action.GetItemURLForm;
import org.exoplatform.ideall.client.action.GoToLineForm;
import org.exoplatform.ideall.client.action.RenameItemForm;
import org.exoplatform.ideall.client.application.component.AbstractApplicationComponent;
import org.exoplatform.ideall.client.autocompletion.AutoCompletionManager;
import org.exoplatform.ideall.client.command.CreateFileCommandThread;
import org.exoplatform.ideall.client.command.GoToFolderCommandThread;
import org.exoplatform.ideall.client.command.OpenFileCommandThread;
import org.exoplatform.ideall.client.command.PasteItemsCommandThread;
import org.exoplatform.ideall.client.command.SaveAllFilesCommandThread;
import org.exoplatform.ideall.client.command.SaveFileAsCommandThread;
import org.exoplatform.ideall.client.command.SaveFileCommandThread;
import org.exoplatform.ideall.client.cookie.CookieManager;
import org.exoplatform.ideall.client.editor.custom.OpenFileWithForm;
import org.exoplatform.ideall.client.event.edit.CopyItemsEvent;
import org.exoplatform.ideall.client.event.edit.CopyItemsHandler;
import org.exoplatform.ideall.client.event.edit.CutItemsEvent;
import org.exoplatform.ideall.client.event.edit.CutItemsHandler;
import org.exoplatform.ideall.client.event.edit.FindTextEvent;
import org.exoplatform.ideall.client.event.edit.FindTextHandler;
import org.exoplatform.ideall.client.event.edit.GoToLineEvent;
import org.exoplatform.ideall.client.event.edit.GoToLineHandler;
import org.exoplatform.ideall.client.event.edit.ItemsToPasteSelectedEvent;
import org.exoplatform.ideall.client.event.edit.ShowLineNumbersEvent;
import org.exoplatform.ideall.client.event.edit.ShowLineNumbersHandler;
import org.exoplatform.ideall.client.event.file.CreateFolderEvent;
import org.exoplatform.ideall.client.event.file.CreateFolderHandler;
import org.exoplatform.ideall.client.event.file.DeleteItemEvent;
import org.exoplatform.ideall.client.event.file.DeleteItemHandler;
import org.exoplatform.ideall.client.event.file.GetFileURLEvent;
import org.exoplatform.ideall.client.event.file.GetFileURLHandler;
import org.exoplatform.ideall.client.event.file.OpenFileWithEvent;
import org.exoplatform.ideall.client.event.file.OpenFileWithHandler;
import org.exoplatform.ideall.client.event.file.RenameItemEvent;
import org.exoplatform.ideall.client.event.file.RenameItemHander;
import org.exoplatform.ideall.client.event.file.SaveAsTemplateEvent;
import org.exoplatform.ideall.client.event.file.SaveAsTemplateHandler;
import org.exoplatform.ideall.client.event.file.SearchFileEvent;
import org.exoplatform.ideall.client.event.file.SearchFileHandler;
import org.exoplatform.ideall.client.event.file.UploadFileEvent;
import org.exoplatform.ideall.client.event.file.UploadFileHandler;
import org.exoplatform.ideall.client.framework.ui.event.ClearFocusEvent;
import org.exoplatform.ideall.client.model.discovery.DiscoveryService;
import org.exoplatform.ideall.client.model.discovery.event.EntryPointsReceivedEvent;
import org.exoplatform.ideall.client.model.discovery.event.EntryPointsReceivedHandler;
import org.exoplatform.ideall.client.outline.event.ShowOutlineEvent;
import org.exoplatform.ideall.client.outline.event.ShowOutlineHandler;
import org.exoplatform.ideall.client.search.file.SearchForm;
import org.exoplatform.ideall.client.search.text.FindTextForm;
import org.exoplatform.ideall.client.template.SaveAsTemplateForm;
import org.exoplatform.ideall.client.upload.UploadForm;
import org.exoplatform.ideall.client.workspace.EntryPointListForm;
import org.exoplatform.ideall.client.workspace.event.SelectWorkspaceEvent;
import org.exoplatform.ideall.client.workspace.event.SelectWorkspaceHandler;
import org.exoplatform.ideall.vfs.api.File;
import org.exoplatform.ideall.vfs.api.Item;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CommonActionsComponent extends AbstractApplicationComponent implements UploadFileHandler,
   CreateFolderHandler, DeleteItemHandler, RenameItemHander, SearchFileHandler, SaveAsTemplateHandler,
   ShowLineNumbersHandler, GetFileURLHandler, OpenFileWithHandler, CopyItemsHandler, CutItemsHandler,
   SelectWorkspaceHandler, EntryPointsReceivedHandler, GoToLineHandler, FindTextHandler, ShowOutlineHandler
{

   private SaveFileCommandThread saveFileCommandHandler;

   private SaveFileAsCommandThread saveFileAsCommandHandler;

   private SaveAllFilesCommandThread saveAllFilesCommandHandler;

   private GoToFolderCommandThread goToFolderCommandHandler;

   private PasteItemsCommandThread pasteItemsCommandHandler;

   private OpenFileCommandThread openFileCommandThread;

   private CreateFileCommandThread createFileCommandThread;

   public CommonActionsComponent()
   {
      super(new CommonActionsComponentInitializer());
   }

   @Override
   protected void onInitializeComponent()
   {
      openFileCommandThread = new OpenFileCommandThread(eventBus, context);
      createFileCommandThread = new CreateFileCommandThread(eventBus, context);
      //new JavaScriptAutoCompletionHandler(eventBus, context);
      new AutoCompletionManager(eventBus, context);
   }

   @Override
   protected void registerHandlers()
   {
      addHandler(UploadFileEvent.TYPE, this);
      addHandler(CreateFolderEvent.TYPE, this);
      addHandler(DeleteItemEvent.TYPE, this);
      addHandler(RenameItemEvent.TYPE, this);
      addHandler(SearchFileEvent.TYPE, this);
      addHandler(SaveAsTemplateEvent.TYPE, this);

      addHandler(ShowLineNumbersEvent.TYPE, this);
      addHandler(ShowOutlineEvent.TYPE, this);

      addHandler(GetFileURLEvent.TYPE, this);

      addHandler(OpenFileWithEvent.TYPE, this);

      addHandler(CopyItemsEvent.TYPE, this);
      addHandler(CutItemsEvent.TYPE, this);

      addHandler(SelectWorkspaceEvent.TYPE, this);
      addHandler(EntryPointsReceivedEvent.TYPE, this);

      addHandler(GoToLineEvent.TYPE, this);
      addHandler(FindTextEvent.TYPE, this);

      /*
       * Initializing Save, Save As, Save All Command Handlers
       */
      saveFileCommandHandler = new SaveFileCommandThread(eventBus, context);
      saveFileAsCommandHandler = new SaveFileAsCommandThread(eventBus, context);
      saveAllFilesCommandHandler = new SaveAllFilesCommandThread(eventBus, context);
      goToFolderCommandHandler = new GoToFolderCommandThread(eventBus, context);
      pasteItemsCommandHandler = new PasteItemsCommandThread(eventBus, context);
   }

   public void onUploadFile(UploadFileEvent event)
   {
      Item item = context.getSelectedItems(context.getSelectedNavigationPanel()).get(0);

      String path = item.getHref();
      if (item instanceof File)
      {
         path = path.substring(path.lastIndexOf("/"));
      }
      eventBus.fireEvent(new ClearFocusEvent());
      new UploadForm(eventBus, context, path, event.isOpenFile());
   }

   public void onCreateFolder(CreateFolderEvent event)
   {
      Item item = context.getSelectedItems(context.getSelectedNavigationPanel()).get(0);

      String href = item.getHref();
      if (item instanceof File)
      {
         href = href.substring(0, href.lastIndexOf("/") + 1);
      }

      new CreateFolderForm(eventBus, context, href);
   }

   public void onDeleteItem(DeleteItemEvent event)
   {
      new DeleteItemForm(eventBus, context);
   }

   public void onRenameItem(RenameItemEvent event)
   {
      new RenameItemForm(eventBus, context);
   }

   public void onSearchFile(SearchFileEvent event)
   {
      new SearchForm(eventBus, context);
   }

   public void onSaveAsTemplate(SaveAsTemplateEvent event)
   {
      File file = context.getActiveFile();
      new SaveAsTemplateForm(eventBus, file);
   }

   public void onShowLineNumbers(ShowLineNumbersEvent event)
   {
      context.setShowLineNumbers(event.isShowLineNumber());
      CookieManager.getInstance().storeLineNumbers(context);
   }

   public void onGetFileURL(GetFileURLEvent event)
   {
      String url = context.getSelectedItems(context.getSelectedNavigationPanel()).get(0).getHref();
      new GetItemURLForm(eventBus, url);
   }

   public void onOpenFileWith(OpenFileWithEvent event)
   {
      new OpenFileWithForm(eventBus, context);
   }

   public void onCopyItems(CopyItemsEvent event)
   {
      context.getItemsToCopy().clear();
      context.getItemsToCut().clear();
      context.getItemsToCopy().addAll(context.getSelectedItems(context.getSelectedNavigationPanel()));
      eventBus.fireEvent(new ItemsToPasteSelectedEvent());
   }

   public void onCutItems(CutItemsEvent event)
   {
      context.getItemsToCut().clear();
      context.getItemsToCopy().clear();

      context.getItemsToCut().addAll(context.getSelectedItems(context.getSelectedNavigationPanel()));
      eventBus.fireEvent(new ItemsToPasteSelectedEvent());
   }

   public void onSelectWorkspace(SelectWorkspaceEvent event)
   {
      DiscoveryService.getInstance().getEntryPoints();
   }

   public void onEntryPointsReceived(EntryPointsReceivedEvent event)
   {
      new EntryPointListForm(eventBus, context, event.getEntryPointList());
   }

   /**
    * @see org.exoplatform.ideall.client.event.edit.GoToLineHandler#onGoToLine(org.exoplatform.ideall.client.event.edit.GoToLineEvent)
    */
   public void onGoToLine(GoToLineEvent event)
   {
      if (context.getActiveFile() != null)
      {
         new GoToLineForm(eventBus, context);
      }
   }

   /**
    * @see org.exoplatform.ideall.client.event.edit.FindTextHandler#onFindText(org.exoplatform.ideall.client.event.edit.FindTextEvent)
    */
   public void onFindText(FindTextEvent event)
   {
      new FindTextForm(eventBus, context);
   }

   /**
    * @see org.exoplatform.ideall.client.outline.event.ShowOutlineHandler#onShowOutline(org.exoplatform.ideall.client.outline.event.ShowOutlineEvent)
    */
   public void onShowOutline(ShowOutlineEvent event)
   {
      context.setShowOutline(event.isShow());
      CookieManager.getInstance().storeOutline(context);
   }

}
