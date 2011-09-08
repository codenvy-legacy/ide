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

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.ClearFocusForm;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.navigation.control.CopyItemsCommand;
import org.exoplatform.ide.client.navigation.control.CutItemsCommand;
import org.exoplatform.ide.client.navigation.control.DeleteItemCommand;
import org.exoplatform.ide.client.navigation.control.DownloadFileCommand;
import org.exoplatform.ide.client.navigation.control.DownloadZippedFolderCommand;
import org.exoplatform.ide.client.navigation.control.GoToFolderControl;
import org.exoplatform.ide.client.navigation.control.PasteItemsCommand;
import org.exoplatform.ide.client.navigation.control.RefreshBrowserControl;
import org.exoplatform.ide.client.navigation.control.RenameItemCommand;
import org.exoplatform.ide.client.navigation.control.SaveAllFilesCommand;
import org.exoplatform.ide.client.navigation.control.SaveFileAsCommand;
import org.exoplatform.ide.client.navigation.control.SaveFileAsTemplateCommand;
import org.exoplatform.ide.client.navigation.control.SaveFileCommand;
import org.exoplatform.ide.client.navigation.control.SearchFilesCommand;
import org.exoplatform.ide.client.navigation.control.newitem.CreateFileFromTemplateControl;
import org.exoplatform.ide.client.navigation.control.newitem.CreateFolderControl;
import org.exoplatform.ide.client.navigation.control.newitem.NewFileCommandMenuGroup;
import org.exoplatform.ide.client.navigation.control.newitem.NewFilePopupMenuControl;
import org.exoplatform.ide.client.navigation.event.CopyItemsEvent;
import org.exoplatform.ide.client.navigation.event.CopyItemsHandler;
import org.exoplatform.ide.client.navigation.event.CutItemsEvent;
import org.exoplatform.ide.client.navigation.event.CutItemsHandler;
import org.exoplatform.ide.client.navigation.event.ItemsToPasteSelectedEvent;
import org.exoplatform.ide.client.navigation.handler.CreateFileCommandHandler;
import org.exoplatform.ide.client.navigation.handler.FileClosedHandler;
import org.exoplatform.ide.client.navigation.handler.GoToFolderCommandHandler;
import org.exoplatform.ide.client.navigation.handler.OpenFileCommandHandler;
import org.exoplatform.ide.client.navigation.handler.PasteItemsCommandHandler;
import org.exoplatform.ide.client.navigation.handler.SaveAllFilesCommandHandler;
import org.exoplatform.ide.client.navigation.handler.SaveFileAsCommandHandler;
import org.exoplatform.ide.client.navigation.handler.SaveFileCommandHandler;
import org.exoplatform.ide.client.navigation.template.CreateFileFromTemplatePresenter;
import org.exoplatform.ide.client.operation.geturl.GetItemURLPresenter;
import org.exoplatform.ide.client.operation.openbypath.OpenFileByPathPresenter;
import org.exoplatform.ide.client.operation.uploadzip.UploadZipPresenter;
import org.exoplatform.ide.client.remote.OpenFileByURLPresenter;
import org.exoplatform.ide.client.statusbar.NavigatorStatusControl;
import org.exoplatform.ide.client.template.SaveAsTemplatePresenter;
import org.exoplatform.ide.client.upload.OpenLocalFileCommand;
import org.exoplatform.ide.client.upload.OpenLocalFileForm;
import org.exoplatform.ide.client.upload.UploadFileCommand;
import org.exoplatform.ide.client.upload.UploadFileEvent;
import org.exoplatform.ide.client.upload.UploadFileForm;
import org.exoplatform.ide.client.upload.UploadFileHandler;
import org.exoplatform.ide.client.upload.UploadForm;
import org.exoplatform.ide.client.versioning.VersionsListPresenter;
import org.exoplatform.ide.client.versioning.control.RestoreToVersionControl;
import org.exoplatform.ide.client.versioning.control.ViewNextVersionControl;
import org.exoplatform.ide.client.versioning.control.ViewPreviousVersionControl;
import org.exoplatform.ide.client.versioning.control.ViewVersionHistoryControl;
import org.exoplatform.ide.client.versioning.control.ViewVersionListControl;
import org.exoplatform.ide.client.versioning.handler.RestoreToVersionCommandHandler;
import org.exoplatform.ide.client.versioning.handler.VersionHistoryCommandHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class NavigationModule implements UploadFileHandler, CopyItemsHandler, CutItemsHandler, ItemsSelectedHandler,
   VfsChangedHandler, ConfigurationReceivedSuccessfullyHandler, InitializeServicesHandler,
   EditorFileClosedHandler, EditorFileOpenedHandler
{
   private HandlerManager eventBus;

   private ApplicationContext context;

   private IDEConfiguration applicationConfiguration;

   private List<Item> selectedItems = new ArrayList<Item>();

   private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

   private String entryPoint;

   public NavigationModule(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;

      NewFilePopupMenuControl newFilePopupMenuControl = new NewFilePopupMenuControl();

      eventBus.fireEvent(new RegisterControlEvent(newFilePopupMenuControl, DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new NewFileCommandMenuGroup()));
      //eventBus.fireEvent(new RegisterControlEvent(new CreateProjectFromTemplateControl()));
      eventBus.fireEvent(new RegisterControlEvent(new CreateFileFromTemplateControl()));
      eventBus.fireEvent(new RegisterControlEvent(new CreateFolderControl()));

      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New TEXT", IDE.IDE_LOCALIZATION_CONSTANT
         .controlNewTextTitle(), IDE.IDE_LOCALIZATION_CONSTANT.controlNewTextPrompt(), Images.FileTypes.TXT,
         MimeType.TEXT_PLAIN).setGroup(1)));

      /*      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New JSON File", "JSON File",
               "Create New JSON File", Images.FileTypes.JSON, MimeType.APPLICATION_JSON))); */

      eventBus.fireEvent(new RegisterControlEvent(new ViewVersionHistoryControl(), DockTarget.TOOLBAR, true));
      eventBus.fireEvent(new RegisterControlEvent(new ViewVersionListControl(), DockTarget.TOOLBAR, true));
      eventBus.fireEvent(new RegisterControlEvent(new ViewPreviousVersionControl(), DockTarget.TOOLBAR, true));
      eventBus.fireEvent(new RegisterControlEvent(new ViewNextVersionControl(), DockTarget.TOOLBAR, true));
      eventBus.fireEvent(new RegisterControlEvent(new RestoreToVersionControl(), DockTarget.TOOLBAR, true));
      eventBus.fireEvent(new RegisterControlEvent(new UploadFileCommand()));

      new UploadZipPresenter();

      eventBus.fireEvent(new RegisterControlEvent(new OpenLocalFileCommand()));

      new OpenFileByPathPresenter();
      new OpenFileByURLPresenter();

      eventBus.fireEvent(new RegisterControlEvent(new DownloadFileCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new DownloadZippedFolderCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new SaveFileCommand(), DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new SaveFileAsCommand(), DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new SaveAllFilesCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new SaveFileAsTemplateCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new CutItemsCommand(), DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new CopyItemsCommand(), DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new PasteItemsCommand(), DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new RenameItemCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new DeleteItemCommand(), DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new SearchFilesCommand(), DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new RefreshBrowserControl(), DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new GoToFolderControl()));

      new GetItemURLPresenter();

      eventBus.fireEvent(new RegisterControlEvent(new NavigatorStatusControl(), DockTarget.STATUSBAR));
      //eventBus.fireEvent(new RegisterControlEvent(new CreateProjectTemplateControl()));

      eventBus.addHandler(InitializeServicesEvent.TYPE, this);
      eventBus.addHandler(VfsChangedEvent.TYPE, this);
      eventBus.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);

      eventBus.addHandler(UploadFileEvent.TYPE, this);
      eventBus.addHandler(CopyItemsEvent.TYPE, this);
      eventBus.addHandler(CutItemsEvent.TYPE, this);
      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
      eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);

      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);

      new CreateFileCommandHandler(eventBus);
      new CreateFileFromTemplatePresenter(eventBus);
      new OpenFileCommandHandler(eventBus);
      new SaveFileCommandHandler(eventBus);
      new SaveFileAsCommandHandler(eventBus);
      new SaveAllFilesCommandHandler(eventBus);
      new GoToFolderCommandHandler(eventBus);
      new PasteItemsCommandHandler(eventBus, context);
      new FileClosedHandler(eventBus);

      new VersionHistoryCommandHandler(eventBus);
      new RestoreToVersionCommandHandler(eventBus);

      new WorkspacePresenter(eventBus);
      new SearchFilesPresenter(eventBus, selectedItems, entryPoint);
      new SearchResultsPresenter(eventBus);
      new DeleteItemsPresenter(eventBus);

      new CreateFolderPresenter(eventBus);
      new SaveAsTemplatePresenter(eventBus);
      new VersionsListPresenter(eventBus);
      new RenameFilePresenter(eventBus);
      new RenameFolderPresenter(eventBus);

      new ShellLinkUpdater(eventBus);
   }

   public void onInitializeServices(InitializeServicesEvent event)
   {
      //      new WebDavVirtualFileSystem(eventBus, event.getLoader(), ImageUtil.getIcons(), event
      //         .getApplicationConfiguration().getContext());
      new VirtualFileSystem(event.getApplicationConfiguration().getDefaultEntryPoint());

   }

   public void onUploadFile(UploadFileEvent event)
   {
      FolderModel folder = null;

      if (selectedItems == null || selectedItems.size() == 0)
      {
         if (UploadFileEvent.UploadType.FILE.equals(event.getUploadType())
            || UploadFileEvent.UploadType.FOLDER.equals(event.getUploadType()))
         {
            Dialogs.getInstance().showInfo(IDE.ERRORS_CONSTANT.navigationUploadNoTargetSelected());
            return;
         }
      }
      else
      {
         Item item = selectedItems.get(0);

         if (item instanceof FileModel)
         {
            folder = ((FileModel)item).getParent();
         }
         else
         {
            folder = (FolderModel)item;
         }
      }

      //      eventBus.fireEvent(new ClearFocusEvent());
      ClearFocusForm.getInstance().clearFocus();

      if (UploadFileEvent.UploadType.OPEN_FILE.equals(event.getUploadType()))
      {
         new OpenLocalFileForm(eventBus, selectedItems, folder, applicationConfiguration);
      }
      else if (UploadFileEvent.UploadType.FILE.equals(event.getUploadType()))
      {
         new UploadFileForm(eventBus, selectedItems, folder, applicationConfiguration);
      }
      else if (UploadFileEvent.UploadType.FOLDER.equals(event.getUploadType()))
      {
         new UploadForm(eventBus, selectedItems, folder, applicationConfiguration);
      }
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

      for (FileModel f : openedFiles.values())
      {
         for (Item i : selectedItems)
         {
            if (f.getPath().startsWith(i.getPath()))
            {
               Dialogs.getInstance().showError(IDE.NAVIGATION_CONSTANT.cutFolderHasOpenFile(i.getName(), f.getName()));
               return;
            }
         }
      }
      context.getItemsToCut().addAll(selectedItems);
      eventBus.fireEvent(new ItemsToPasteSelectedEvent());
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   public void onVfsChanged(VfsChangedEvent event)
   {
      entryPoint = event.getEntryPoint().getHref();
   }

   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      applicationConfiguration = event.getConfiguration();
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
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent)
    */
   @Override
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

}
