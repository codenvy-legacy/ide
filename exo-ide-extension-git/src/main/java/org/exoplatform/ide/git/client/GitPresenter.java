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
package org.exoplatform.ide.git.client;

import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

/**
 * Used as base for the most presenters, which work with Git. Most of the presenters have to store selected item in browser tree and to get
 * the location the working directory. If the working directory not found - the reaction is common, the actions on success differs, so
 * {@link #onWorkDirReceived()} will have different implementations.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 20, 2011 2:08:46 PM anya $
 */
public abstract class GitPresenter implements ItemsSelectedHandler, VfsChangedHandler {

    /** Selected item in browser tree. */
    protected Item                  selectedItem;

    /** Current virtual file system. */
    protected VirtualFileSystemInfo vfs;

    /** @param eventBus */
    public GitPresenter() {
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
    }

    /**
     * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     *      .framework.navigation.event.ItemsSelectedEvent)
     */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        if (event.getSelectedItems().size() != 1) {
            selectedItem = null;
        } else {
            selectedItem = event.getSelectedItems().get(0);
        }
    }

    protected ProjectModel getSelectedProject() {
        if (selectedItem == null) {
            return null;
        }

        if (selectedItem instanceof ProjectModel) {
            return (ProjectModel)selectedItem;
        } else {
            return ((ItemContext)selectedItem).getProject();
        }
    }

    protected boolean makeSelectionCheck() {
        ProjectModel project = getSelectedProject();
        if (project == null) {
            Dialogs.getInstance().showInfo(GitExtension.MESSAGES.selectedItemsFail());
            return false;
        }

        // ProjectModel project = selectedItem instanceof ProjectModel ? (ProjectModel)selectedItem :
        // ((ItemContext)selectedItem).getProject();
        //
        // if (project == null)
        // {
        // // TODO change message:
        // Dialogs.getInstance().showInfo("Project is not selected.");
        // return false;
        // }

        // if (selectedItems.get(0).getPath().isEmpty() || selectedItems.get(0).getPath().equals("/"))
        // {
        // Dialogs.getInstance().showInfo(GitExtension.MESSAGES.selectedWorkace());
        // return false;
        // }

        return true;
    }

    /**
     * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     *      .application.event.VfsChangedEvent)
     */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        this.vfs = event.getVfsInfo();
    }


}
