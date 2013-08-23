/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.client.navigation;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.download.DownloadHandler;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.navigation.control.RefreshBrowserControl;
import org.exoplatform.ide.client.navigation.handler.FileClosedHandler;
import org.exoplatform.ide.client.navigation.handler.OpenFileCommandHandler;
import org.exoplatform.ide.client.navigation.handler.SaveAllFilesCommandHandler;
import org.exoplatform.ide.client.navigation.handler.SaveFileAsCommandHandler;
import org.exoplatform.ide.client.navigation.handler.SaveFileCommandHandler;
import org.exoplatform.ide.client.navigation.handler.ShowHideHiddenFilesCommandHandler;
import org.exoplatform.ide.client.operation.createfile.CreateFilePresenter;
import org.exoplatform.ide.client.operation.createfile.NewItemMenuGroup;
import org.exoplatform.ide.client.operation.createfile.NewItemPopupToolbarControl;
import org.exoplatform.ide.client.operation.createfolder.CreateFolderPresenter;
import org.exoplatform.ide.client.operation.cutcopy.CutCopyPasteItemsCommandHandler;
import org.exoplatform.ide.client.operation.deleteitem.DeleteItemsPresenter;
import org.exoplatform.ide.client.operation.gotofolder.GoToFolderCommandHandler;
import org.exoplatform.ide.client.operation.openbypath.OpenFileByPathPresenter;
import org.exoplatform.ide.client.operation.openbyurl.OpenFileByURLPresenter;
import org.exoplatform.ide.client.operation.openlocalfile.OpenLocalFileCommand;
import org.exoplatform.ide.client.operation.rename.RenameFilePresenter;
import org.exoplatform.ide.client.operation.rename.RenameFolderPresenter;
import org.exoplatform.ide.client.operation.rename.RenameItemControl;
import org.exoplatform.ide.client.operation.search.SearchFilesPresenter;
import org.exoplatform.ide.client.operation.search.SearchResultsPresenter;
import org.exoplatform.ide.client.operation.uploadfile.UploadFilePresenter;
import org.exoplatform.ide.client.operation.uploadzip.UploadZipPresenter;
import org.exoplatform.ide.client.progress.ProgressPresenter;
import org.exoplatform.ide.client.statusbar.NavigatorStatusControl;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class NavigationModule implements InitializeServicesHandler {

    public NavigationModule() {
        IDE.addHandler(InitializeServicesEvent.TYPE, this);

        IDE.getInstance().addControl(new NewItemPopupToolbarControl(), Docking.TOOLBAR);
        IDE.getInstance().addControl(new NewItemMenuGroup());


        new CreateFolderPresenter();

        IDE.getInstance().addControl(
                new NewItemControl("File/New/New TEXT", IDE.IDE_LOCALIZATION_CONSTANT.controlNewTextTitle(),
                                   IDE.IDE_LOCALIZATION_CONSTANT.controlNewTextPrompt(), Images.FileTypes.TXT, MimeType.TEXT_PLAIN, true)
                        .setGroupName(GroupNames.NEW_FILE));

        new UploadFilePresenter();
        new UploadZipPresenter();

        //need to place this control here to save positioning, cause open local file presenter is now deleted, opening local file now
        //work by uploader
        IDE.getInstance().addControl(new OpenLocalFileCommand());

        new OpenFileByPathPresenter();
        new OpenFileByURLPresenter();
        new DownloadHandler();
        new SaveFileCommandHandler();
        new SaveFileAsCommandHandler();
        new SaveAllFilesCommandHandler();
        new CutCopyPasteItemsCommandHandler();
        new DeleteItemsPresenter();
        IDE.getInstance().addControl(new RenameItemControl());
        new RenameFilePresenter();
        new RenameFolderPresenter();
        new SearchFilesPresenter();
        new SearchResultsPresenter();
        IDE.getInstance().addControl(new RefreshBrowserControl(), Docking.TOOLBAR);
        new ShowHideHiddenFilesCommandHandler();
        new GoToFolderCommandHandler();
        IDE.getInstance().addControl(new NavigatorStatusControl(), Docking.STATUSBAR);
        new CreateFilePresenter();
        new OpenFileCommandHandler();
        new FileClosedHandler();
        IDE.getInstance().addControl(new ShowViewMenuGroup());
        IDE.getInstance().addControl(new NavigationMenuGroup());
        new ProgressPresenter();
        new ShellLinkUpdater();
    }

    public void onInitializeServices(InitializeServicesEvent event) {
        String workspace = event.getApplicationConfiguration().getVfsBaseUrl();
        new VirtualFileSystem(workspace, new GWTLoader());
    }
}
