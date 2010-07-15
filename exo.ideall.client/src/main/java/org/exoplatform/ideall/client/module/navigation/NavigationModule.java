/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.module.navigation;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.framework.control.NewItemControl;
import org.exoplatform.ideall.client.framework.model.AbstractApplicationContext;
import org.exoplatform.ideall.client.framework.plugin.AbstractIDEModule;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.module.navigation.control.CopyItemsCommand;
import org.exoplatform.ideall.client.module.navigation.control.CutItemsCommand;
import org.exoplatform.ideall.client.module.navigation.control.DeleteItemCommand;
import org.exoplatform.ideall.client.module.navigation.control.GetFileURLControl;
import org.exoplatform.ideall.client.module.navigation.control.GoToFolderControl;
import org.exoplatform.ideall.client.module.navigation.control.OpenFileWithCommand;
import org.exoplatform.ideall.client.module.navigation.control.PasteItemsCommand;
import org.exoplatform.ideall.client.module.navigation.control.RefreshBrowserControl;
import org.exoplatform.ideall.client.module.navigation.control.RenameItemCommand;
import org.exoplatform.ideall.client.module.navigation.control.SaveAllFilesCommand;
import org.exoplatform.ideall.client.module.navigation.control.SaveFileAsCommand;
import org.exoplatform.ideall.client.module.navigation.control.SaveFileAsTemplateCommand;
import org.exoplatform.ideall.client.module.navigation.control.SaveFileCommand;
import org.exoplatform.ideall.client.module.navigation.control.SearchFilesCommand;
import org.exoplatform.ideall.client.module.navigation.control.ViewItemPropertiesCommand;
import org.exoplatform.ideall.client.module.navigation.control.download.DownloadFileCommand;
import org.exoplatform.ideall.client.module.navigation.control.download.DownloadZippedFolderCommand;
import org.exoplatform.ideall.client.module.navigation.control.newitem.NewFileCommandMenuGroup;
import org.exoplatform.ideall.client.module.navigation.control.newitem.NewFilePopupMenuControl;
import org.exoplatform.ideall.client.module.navigation.control.upload.OpenLocalFileCommand;
import org.exoplatform.ideall.client.module.navigation.control.upload.UploadFileCommand;
import org.exoplatform.ideall.client.module.navigation.event.newitem.CreateNewFileEvent;
import org.exoplatform.ideall.client.statusbar.NavigatorStatusControl;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class NavigationModule extends AbstractIDEModule
{

   public NavigationModule(HandlerManager eventBus, ApplicationContext context)
   {
      super(eventBus, context);
      new NavigationModuleEventHandler(eventBus, context);
   }

   public void initializePlugin(HandlerManager eventBus, AbstractApplicationContext context)
   {
      
      NewFilePopupMenuControl newFilePopupMenuControl = new NewFilePopupMenuControl();
      addControl(newFilePopupMenuControl, true);
      
      addControl(new NewFileCommandMenuGroup());

      addControl(new NewItemControl("File/New/New XML File", "XML File", "Create New XML File", Images.FileTypes.XML,
         new CreateNewFileEvent(MimeType.TEXT_XML)));

      addControl(new NewItemControl("File/New/New HTML file", "HTML File", "Create New HTML File",
         Images.FileTypes.HTML, new CreateNewFileEvent(MimeType.TEXT_HTML)));

      addControl(new NewItemControl("File/New/New TEXT file", "Text File", "Create New Text File",
         Images.FileTypes.TXT, new CreateNewFileEvent(MimeType.TEXT_PLAIN)));

      addControl(new NewItemControl("File/New/New Java Script file", "JavaScript File", "Create New Java Script File",
         Images.FileTypes.JAVASCRIPT, new CreateNewFileEvent(MimeType.APPLICATION_JAVASCRIPT)));

      addControl(new NewItemControl("File/New/New CSS file", "CSS file", "Create New CSS File", Images.FileTypes.CSS,
         new CreateNewFileEvent(MimeType.TEXT_CSS)));

      addControl(new ViewItemPropertiesCommand(), true, true);
      addControl(new OpenFileWithCommand());
      
      addControl(new UploadFileCommand());
      addControl(new OpenLocalFileCommand());
      addControl(new DownloadFileCommand());
      addControl(new DownloadZippedFolderCommand());
      addControl(new SaveFileCommand(), true);
      addControl(new SaveFileAsCommand(), true);
      addControl(new SaveAllFilesCommand());
      addControl(new SaveFileAsTemplateCommand());

      addControl(new CutItemsCommand(), true);
      addControl(new CopyItemsCommand(), true);
      addControl(new PasteItemsCommand(), true);

      addControl(new RenameItemCommand());
      addControl(new DeleteItemCommand(), true);
      addControl(new SearchFilesCommand(), true);
      addControl(new RefreshBrowserControl(), true);
      addControl(new GoToFolderControl());
      addControl(new GetFileURLControl());
      
      NavigatorStatusControl navigatorStatusControl = new NavigatorStatusControl(eventBus, (ApplicationContext)context);
      addControl(navigatorStatusControl);
      context.getStatusBarItems().add(navigatorStatusControl.getId());
      
   }
}
