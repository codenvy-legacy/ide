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
package org.exoplatform.ide.client.module.navigation;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.module.IDEModule;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.model.util.ImageUtil;
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
import org.exoplatform.ide.client.module.vfs.webdav.WebDavVirtualFileSystem;
import org.exoplatform.ide.client.statusbar.NavigatorStatusControl;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class NavigationModule implements IDEModule, InitializeServicesHandler
{
   
   private HandlerManager eventBus;
   
   private ApplicationContext context;
   
   private Handlers handlers;

   public NavigationModule(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);
      handlers.addHandler(InitializeServicesEvent.TYPE, this);

      NewFilePopupMenuControl newFilePopupMenuControl = new NewFilePopupMenuControl();

      eventBus.fireEvent(new RegisterControlEvent(newFilePopupMenuControl, true));
      eventBus.fireEvent(new RegisterControlEvent(new NewFileCommandMenuGroup(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new CreateFileFromTemplateControl(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new CreateFolderControl(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New XML File", "XML File", "Create New XML File", Images.FileTypes.XML, MimeType.TEXT_XML)));
      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New HTML file", "HTML File", "Create New HTML File", Images.FileTypes.HTML, MimeType.TEXT_HTML)));
      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New TEXT file", "Text File", "Create New Text File", Images.FileTypes.TXT, MimeType.TEXT_PLAIN)));
      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New Java Script file", "JavaScript File", "Create New Java Script File", Images.FileTypes.JAVASCRIPT, MimeType.APPLICATION_JAVASCRIPT)));
      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New CSS file", "CSS file", "Create New CSS File", Images.FileTypes.CSS, MimeType.TEXT_CSS)));
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
      
      new NavigationModuleEventHandler(eventBus, context);
   }

   public void onInitializeServices(InitializeServicesEvent event)
   {
      new WebDavVirtualFileSystem(eventBus, event.getLoader(), ImageUtil.getIcons(), context
         .getApplicationConfiguration().getContext());
   }

}
