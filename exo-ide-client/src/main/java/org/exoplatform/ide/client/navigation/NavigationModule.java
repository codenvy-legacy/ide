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

import org.exoplatform.gwtframework.commons.rest.copy.MimeType;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.download.DownloadHandler;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.navigation.control.CreateFileFromTemplateControl;
import org.exoplatform.ide.client.navigation.control.NewItemMenuGroup;
import org.exoplatform.ide.client.navigation.control.NewItemPopupToolbarControl;
import org.exoplatform.ide.client.navigation.control.RefreshBrowserControl;
import org.exoplatform.ide.client.navigation.handler.CreateFileCommandHandler;
import org.exoplatform.ide.client.navigation.handler.FileClosedHandler;
import org.exoplatform.ide.client.navigation.handler.OpenFileCommandHandler;
import org.exoplatform.ide.client.navigation.handler.SaveAllFilesCommandHandler;
import org.exoplatform.ide.client.navigation.handler.SaveFileAsCommandHandler;
import org.exoplatform.ide.client.navigation.handler.SaveFileCommandHandler;
import org.exoplatform.ide.client.navigation.template.CreateFileFromTemplatePresenter;
import org.exoplatform.ide.client.navigator.NavigatorPresenter;
import org.exoplatform.ide.client.operation.createfolder.CreateFolderPresenter;
import org.exoplatform.ide.client.operation.cutcopy.CutCopyPasteItemsCommandHandler;
import org.exoplatform.ide.client.operation.deleteitem.DeleteItemsPresenter;
import org.exoplatform.ide.client.operation.geturl.GetItemURLPresenter;
import org.exoplatform.ide.client.operation.gotofolder.GoToFolderCommandHandler;
import org.exoplatform.ide.client.operation.openbypath.OpenFileByPathPresenter;
import org.exoplatform.ide.client.operation.openbyurl.OpenFileByURLPresenter;
import org.exoplatform.ide.client.operation.openlocalfile.OpenLocalFilePresenter;
import org.exoplatform.ide.client.operation.rename.RenameFilePresenter;
import org.exoplatform.ide.client.operation.rename.RenameFolderPresenter;
import org.exoplatform.ide.client.operation.rename.RenameItemControl;
import org.exoplatform.ide.client.operation.search.SearchFilesPresenter;
import org.exoplatform.ide.client.operation.search.SearchResultsPresenter;
import org.exoplatform.ide.client.operation.uploadfile.UploadFilePresenter;
import org.exoplatform.ide.client.operation.uploadzip.UploadZipPresenter;
import org.exoplatform.ide.client.progress.ProgressPresenter;
import org.exoplatform.ide.client.project.create.CreateProjectFromTemplateControl;
import org.exoplatform.ide.client.statusbar.NavigatorStatusControl;
import org.exoplatform.ide.client.template.SaveAsTemplatePresenter;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 * 
 */
public class NavigationModule implements InitializeServicesHandler
{

   public NavigationModule()
   {
      IDE.addHandler(InitializeServicesEvent.TYPE, this);

      IDE.getInstance().addControl(new NewItemPopupToolbarControl(), Docking.TOOLBAR);
      IDE.getInstance().addControl(new NewItemMenuGroup());

      IDE.getInstance().addControl(new CreateFileFromTemplateControl());
      IDE.getInstance().addControl(new CreateProjectFromTemplateControl());

      new CreateFolderPresenter();

      IDE.getInstance().addControl(
         new NewItemControl("File/New/New TEXT", IDE.IDE_LOCALIZATION_CONSTANT.controlNewTextTitle(),
            IDE.IDE_LOCALIZATION_CONSTANT.controlNewTextPrompt(), Images.FileTypes.TXT, MimeType.TEXT_PLAIN, true));

      // TODO: need rework according with VFS
      // IDE.getInstance().addControl(new ViewVersionHistoryControl(), Docking.TOOLBAR_RIGHT);
      // IDE.getInstance().addControl(new ViewVersionListControl(), Docking.TOOLBAR_RIGHT);
      // IDE.getInstance().addControl(new ViewPreviousVersionControl(), Docking.TOOLBAR_RIGHT);
      // IDE.getInstance().addControl(new ViewNextVersionControl(), Docking.TOOLBAR_RIGHT);
      // IDE.getInstance().addControl(new RestoreToVersionControl(), Docking.TOOLBAR_RIGHT);
      // new VersionHistoryCommandHandler();
      // new RestoreToVersionCommandHandler();
      // new VersionsListPresenter();

      new UploadFilePresenter();
      new UploadZipPresenter();

      new OpenLocalFilePresenter();
      new OpenFileByPathPresenter();
      new OpenFileByURLPresenter();

      new DownloadHandler();

      new SaveFileCommandHandler();

      new SaveFileAsCommandHandler();

      new SaveAllFilesCommandHandler();

      new SaveAsTemplatePresenter();

      new CutCopyPasteItemsCommandHandler();

      IDE.getInstance().addControl(new RenameItemControl());
      new RenameFilePresenter();
      new RenameFolderPresenter();

      new DeleteItemsPresenter();

      new SearchFilesPresenter();

      new SearchResultsPresenter();

      IDE.getInstance().addControl(new RefreshBrowserControl(), Docking.TOOLBAR);

      new GoToFolderCommandHandler();

      new GetItemURLPresenter();

      IDE.getInstance().addControl(new NavigatorStatusControl(), Docking.STATUSBAR);

      new CreateFileCommandHandler();
      new CreateFileFromTemplatePresenter();
      new OpenFileCommandHandler();
      new FileClosedHandler();

      new NavigatorPresenter();
      new ProgressPresenter();
      new ShellLinkUpdater();
   }

   public void onInitializeServices(InitializeServicesEvent event)
   {
      String workspace =
         (event.getApplicationConfiguration().getVfsBaseUrl().endsWith("/")) ? event.getApplicationConfiguration()
            .getVfsBaseUrl() + event.getApplicationConfiguration().getVfsId() : event.getApplicationConfiguration()
            .getVfsBaseUrl() + "/" + event.getApplicationConfiguration().getVfsId();
      new VirtualFileSystem(workspace, new GWTLoader());
   }
}
