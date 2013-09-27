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
package org.exoplatform.ide.client.operation.deleteitem;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.NavigatorDisplay;
import org.exoplatform.ide.client.framework.project.PackageExplorerDisplay;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.client.project.explorer.ProjectSelectedEvent;
import org.exoplatform.ide.client.project.explorer.ProjectSelectedHandler;
import org.exoplatform.ide.vfs.client.event.ItemDeletedEvent;
import org.exoplatform.ide.vfs.client.event.ItemDeletedHandler;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"developer"})
public class DeleteItemControl extends SimpleControl implements
                                                     IDEControl, ItemsSelectedHandler, VfsChangedHandler, ViewActivatedHandler,
                                                     ProjectSelectedHandler, ItemDeletedHandler,
                                                     ProjectOpenedHandler, ProjectClosedHandler {

    private static final String ID = "File/Delete...";

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.deleteItemsTitleControl();

    private static final String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.deleteItemsPromptControl();

    /** Current workspace's href. */
    private VirtualFileSystemInfo vfsInfo;

    /** Current active view. */
    private View activeView;

    private boolean navigatorSelected;

    private List<Item> selectedItems;

    private ProjectModel selectedProject;

    private ProjectModel openedProject;

    /**
     *
     */
    public DeleteItemControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(IDEImageBundle.INSTANCE.delete(), IDEImageBundle.INSTANCE.deleteDisabled());
        setEvent(new DeleteItemEvent());
    }

    /** @see org.exoplatform.ide.client.navigation.control.MultipleSelectionItemsControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ViewActivatedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(ProjectSelectedEvent.TYPE, this);
        IDE.addHandler(ItemDeletedEvent.TYPE, this);

        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
    }

    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfsInfo = event.getVfsInfo();
        updateState();
    }

    @Override
    public void onViewActivated(ViewActivatedEvent event) {
        activeView = event.getView();
        navigatorSelected = activeView instanceof NavigatorDisplay ||
                            activeView instanceof ProjectExplorerDisplay ||
                            activeView instanceof PackageExplorerDisplay;
        updateState();
    }

    /**
     * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     *      .framework.navigation.event.ItemsSelectedEvent)
     */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        this.selectedItems = event.getSelectedItems();
        updateState();
    }

    /** Update control's state. */
    protected void updateState() {
        if (vfsInfo == null) {
            setVisible(false);
            return;
        }
        setVisible(true);

        setShowInContextMenu(navigatorSelected);

        if (openedProject != null) {
            if (selectedItems != null && !selectedItems.isEmpty() &&
                !selectedItems.get(0).getId().equals(vfsInfo.getRoot().getId())) {

                setEnabled(navigatorSelected);
            } else {
                setEnabled(false);
            }
        } else {
            if (selectedProject != null) {
                setEnabled(navigatorSelected);
            } else {
                setEnabled(false);
            }
        }


//        if (selectedProject != null && (selectedItems == null || selectedItems.isEmpty())) {
//            setEnabled(navigatorSelected);
//            return;
//        }
//        
//        if ((selectedItems == null || selectedItems.size() != 1) && selectedProject == null) {
//            setEnabled(false);
//            return;
//        } else if (selectedItems != null && !selectedItems.isEmpty()
//                   && selectedItems.get(0).getId().equals(vfsInfo.getRoot().getId())) {
//            setEnabled(false);
//            return;
//        }
//        
//        setEnabled(navigatorSelected);
    }

    /**
     * @see org.exoplatform.ide.client.project.explorer.ProjectSelectedHandler#onProjectSelected(org.exoplatform.ide.client.project
     *      .explorer.ProjectSelectedEvent)
     */
    @Override
    public void onProjectSelected(ProjectSelectedEvent event) {
        this.selectedProject = event.getProject();
        updateState();
    }

    @Override
    public void onItemDeleted(ItemDeletedEvent event) {
        setEnabled(false);
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        openedProject = event.getProject();
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        openedProject = null;
    }

}
