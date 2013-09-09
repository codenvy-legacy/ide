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
package org.exoplatform.ide.client.download;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.NavigatorDisplay;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class DownloadItemControl extends SimpleControl implements IDEControl, ItemsSelectedHandler,
                                                                  ViewVisibilityChangedHandler {

    enum Type {
        FILE, ZIP
    }

    private static final String ID_FILE = "File/Download File...";

    private static final String TITLE_FILE = IDE.IDE_LOCALIZATION_CONSTANT.downloadTitleControl();

    private static final String PROMPT_FILE = IDE.IDE_LOCALIZATION_CONSTANT.downloadPromptControl();

    private static final String ID_ZIP = "File/Download Zipped Folder...";

    private static final String TITLE_ZIP = IDE.IDE_LOCALIZATION_CONSTANT.downloadZippedFolderControl();

    private static final String PROMPT_ZIP = IDE.IDE_LOCALIZATION_CONSTANT.downloadZippedFolderControl();

    private boolean downloadZip;

    private boolean browserPanelSelected = true;

    /**
     *
     */
    public DownloadItemControl(boolean downloadZip) {
        super(downloadZip ? ID_ZIP : ID_FILE);

        this.downloadZip = downloadZip;

        if (downloadZip) {
            setTitle(TITLE_ZIP);
            setPrompt(PROMPT_ZIP);
            setImages(IDEImageBundle.INSTANCE.downloadFolder(), IDEImageBundle.INSTANCE.downloadFolderDisabled());
        } else {
            setTitle(TITLE_FILE);
            setPrompt(PROMPT_FILE);
            setImages(IDEImageBundle.INSTANCE.downloadFile(), IDEImageBundle.INSTANCE.downloadFileDisabled());
        }
        setEvent(new DownloadItemEvent());
        setGroupName(GroupNames.DOWNLOAD);
    }

    /** @see org.exoplatform.ide.client.navigation.control.MultipleSelectionItemsControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
    }

    private boolean isApplicableFor(Item item) {
        if (downloadZip && (item instanceof FolderModel || item instanceof ProjectModel)) {
            return true;
        } else if (!downloadZip && item instanceof FileModel) {
            return true;
        }

        return false;
    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     * .framework.navigation.event.ItemsSelectedEvent) */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        if (event.getSelectedItems().size() == 1 && isApplicableFor(event.getSelectedItems().get(0))) {
            setEnabled(true);
        } else {
            setEnabled(false);
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide
     * .client.framework.ui.api.event.ViewVisibilityChangedEvent) */
    @Override
    public void onViewVisibilityChanged(ViewVisibilityChangedEvent event) {
        if (event.getView() instanceof NavigatorDisplay || event.getView() instanceof ProjectExplorerDisplay) {
            setEnabled(event.getView().isViewVisible());
        }
    }

}
