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
import org.exoplatform.ideall.client.action.MoveItemForm;
import org.exoplatform.ideall.client.application.component.AbstractApplicationComponent;
import org.exoplatform.ideall.client.editor.custom.OpenFileWithForm;
import org.exoplatform.ideall.client.event.ClearFocusEvent;
import org.exoplatform.ideall.client.event.edit.CopyFileEvent;
import org.exoplatform.ideall.client.event.edit.CopyFileHandler;
import org.exoplatform.ideall.client.event.edit.CutFileEvent;
import org.exoplatform.ideall.client.event.edit.CutFileHandler;
import org.exoplatform.ideall.client.event.edit.HideLineNumbersEvent;
import org.exoplatform.ideall.client.event.edit.HideLineNumbersHandler;
import org.exoplatform.ideall.client.event.edit.ShowLineNumbersEvent;
import org.exoplatform.ideall.client.event.edit.ShowLineNumbersHandler;
import org.exoplatform.ideall.client.event.file.CreateFileFromTemplateEvent;
import org.exoplatform.ideall.client.event.file.CreateFileFromTemplateHandler;
import org.exoplatform.ideall.client.event.file.CreateFolderEvent;
import org.exoplatform.ideall.client.event.file.CreateFolderHandler;
import org.exoplatform.ideall.client.event.file.CreateNewFileEvent;
import org.exoplatform.ideall.client.event.file.CreateNewFileHandler;
import org.exoplatform.ideall.client.event.file.DeleteItemEvent;
import org.exoplatform.ideall.client.event.file.DeleteItemHandler;
import org.exoplatform.ideall.client.event.file.FileCreatedEvent;
import org.exoplatform.ideall.client.event.file.GetFileURLEvent;
import org.exoplatform.ideall.client.event.file.GetFileURLHandler;
import org.exoplatform.ideall.client.event.file.MoveItemEvent;
import org.exoplatform.ideall.client.event.file.MoveItemHander;
import org.exoplatform.ideall.client.event.file.OpenFileWithEvent;
import org.exoplatform.ideall.client.event.file.OpenFileWithHandler;
import org.exoplatform.ideall.client.event.file.SaveAsTemplateEvent;
import org.exoplatform.ideall.client.event.file.SaveAsTemplateHandler;
import org.exoplatform.ideall.client.event.file.SearchFileEvent;
import org.exoplatform.ideall.client.event.file.SearchFileHandler;
import org.exoplatform.ideall.client.event.file.UploadFileEvent;
import org.exoplatform.ideall.client.event.file.UploadFileHandler;
import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.model.Item;
import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.client.model.settings.SettingsService;
import org.exoplatform.ideall.client.model.template.FileTemplates;
import org.exoplatform.ideall.client.model.template.TemplateService;
import org.exoplatform.ideall.client.model.template.event.TemplateListReceivedEvent;
import org.exoplatform.ideall.client.model.template.event.TemplateListReceivedHandler;
import org.exoplatform.ideall.client.model.util.ImageUtil;
import org.exoplatform.ideall.client.model.util.MimeTypeResolver;
import org.exoplatform.ideall.client.model.util.NodeTypeUtil;
import org.exoplatform.ideall.client.search.AdvancedSearchForm;
import org.exoplatform.ideall.client.template.CreateFileFromTemplateForm;
import org.exoplatform.ideall.client.template.SaveAsTemplateForm;
import org.exoplatform.ideall.client.upload.UploadForm;

import com.google.gwt.user.client.Window.Location;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CommonActionsComponent extends AbstractApplicationComponent implements CreateNewFileHandler,
   CreateFileFromTemplateHandler, UploadFileHandler, CreateFolderHandler, DeleteItemHandler, MoveItemHander,
   SearchFileHandler, SaveAsTemplateHandler, TemplateListReceivedHandler, ShowLineNumbersHandler,
   HideLineNumbersHandler, GetFileURLHandler, OpenFileWithHandler, CopyFileHandler, CutFileHandler
{

   private SaveFileCommandHandler saveFileCommandHandler;

   private SaveFileAsCommandHandler saveFileAsCommandHandler;

   private SaveAllFilesCommandHandler saveAllFilesCommandHandler;

   private GoToFolderCommandHandler goToFolderCommandHandler;

   public CommonActionsComponent()
   {
      super(new CommonActionsComponentInitializer());
   }

   @Override
   protected void registerHandlers()
   {
      addHandler(CreateNewFileEvent.TYPE, this);
      addHandler(CreateFileFromTemplateEvent.TYPE, this);
      addHandler(UploadFileEvent.TYPE, this);
      addHandler(CreateFolderEvent.TYPE, this);
      addHandler(DeleteItemEvent.TYPE, this);
      addHandler(MoveItemEvent.TYPE, this);
      addHandler(SearchFileEvent.TYPE, this);
      addHandler(SaveAsTemplateEvent.TYPE, this);
      addHandler(TemplateListReceivedEvent.TYPE, this);

      addHandler(ShowLineNumbersEvent.TYPE, this);
      addHandler(HideLineNumbersEvent.TYPE, this);

      addHandler(GetFileURLEvent.TYPE, this);

      addHandler(OpenFileWithEvent.TYPE, this);

      addHandler(CopyFileEvent.TYPE, this);
      addHandler(CutFileEvent.TYPE, this);

      /*
       * Initializing Save, Save As, Save All Command Handlers
       */
      saveFileCommandHandler = new SaveFileCommandHandler(eventBus, context);
      saveFileAsCommandHandler = new SaveFileAsCommandHandler(eventBus, context);
      saveAllFilesCommandHandler = new SaveAllFilesCommandHandler(eventBus, context);
      goToFolderCommandHandler = new GoToFolderCommandHandler(eventBus, context);
   }

   public void onCreateNewFile(CreateNewFileEvent event)
   {
      Item item = context.getSelectedItems().get(0);

      String extension = MimeTypeResolver.getExtensionsMap().get(event.getMimeType());

      String path = item.getPath();
      if (item instanceof File)
      {
         path = path.substring(0, path.lastIndexOf("/"));
      }

      String content = FileTemplates.getTemplateFor(event.getMimeType());

      String fileName = "Untitled file." + extension;

      File newFile = new File(path + "/" + fileName);
      newFile.setContentType(event.getMimeType());
      newFile.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(event.getMimeType()));
      newFile.setIcon(ImageUtil.getIcon(event.getMimeType()));
      newFile.setNewFile(true);
      newFile.setContent(content);
      newFile.setContentChanged(true);

      eventBus.fireEvent(new FileCreatedEvent(newFile));
   }

   public void onCreateFileFromTemplate(CreateFileFromTemplateEvent event)
   {
      TemplateService.getInstance().getTemplates();
   }

   public void onUploadFile(UploadFileEvent event)
   {

      Item item = context.getSelectedItems().get(0);

      String path = item.getPath();
      if (item instanceof File)
      {
         path = path.substring(path.lastIndexOf("/"));
      }
      eventBus.fireEvent(new ClearFocusEvent());
      new UploadForm(eventBus, path);
   }

   public void onCreateFolder(CreateFolderEvent event)
   {
      Item item = context.getSelectedItems().get(0);
      
      String path = item.getPath();
      if (item instanceof File)
      {
         path = path.substring(0, path.lastIndexOf("/"));
      }

      new CreateFolderForm(eventBus, path);
   }

   public void onDeleteItem(DeleteItemEvent event)
   {
      new DeleteItemForm(eventBus, context);
   }

   public void onMoveItem(MoveItemEvent event)
   {
      new MoveItemForm(eventBus, context);
   }

   public void onSearchFile(SearchFileEvent event)
   {
      new AdvancedSearchForm(eventBus, context);
   }

   public void onSaveAsTemplate(SaveAsTemplateEvent event)
   {
      File file = context.getActiveFile();
      new SaveAsTemplateForm(eventBus, file);
   }

   public void onTemplateListReceived(TemplateListReceivedEvent event)
   {
      context.setTemplateList(event.getTemplateList());
      new CreateFileFromTemplateForm(eventBus, context);
   }

   public void onShowLineNumbers(ShowLineNumbersEvent event)
   {
      context.setShowLineNumbers(true);
      SettingsService.getInstance().saveSetting(context);
   }

   public void onHideLineNumbers(HideLineNumbersEvent event)
   {
      context.setShowLineNumbers(false);
      SettingsService.getInstance().saveSetting(context);
   }

   public void onGetFileURL(GetFileURLEvent event)
   {
      new GetItemURLForm(eventBus, getURL());
   }

   private String getURL()
   {
      String url = Location.getProtocol() + "//" + Location.getHost() +
      //( "80".equals(Location.getPort()) ? "" : ":" + Location.getPort() ) +
         Configuration.getInstance().getContext() + "/jcr" + context.getSelectedItems().get(0).getPath();
      return url;
   }

   public void onOpenFileWith(OpenFileWithEvent event)
   {
      new OpenFileWithForm(eventBus, context);
   }

   public void onCopyFile(CopyFileEvent event)
   {
      // TODO Auto-generated method stub

   }

   public void onCutFile(CutFileEvent event)
   {
      // TODO Auto-generated method stub

   }

}
