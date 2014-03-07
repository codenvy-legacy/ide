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
package org.exoplatform.ide.client.operation.createfolder;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.NavigatorDisplay;
import org.exoplatform.ide.client.framework.project.PackageExplorerDisplay;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"workspace/developer"})
public class CreateFolderControl extends SimpleControl implements IDEControl, ItemsSelectedHandler,
                                                                  ViewActivatedHandler, VfsChangedHandler {

    private boolean browserPanelSelected = true;

    public final static String ID = "File/New/Create Folder...";

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.createFolderTitleControl();

    private static final String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.createFolderPromptControl();

    private List<Item> selectedItems = new ArrayList<Item>();

    /** Current workspace's href. */
    private VirtualFileSystemInfo vfsInfo;

    /**
     *
     */
    public CreateFolderControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(IDEImageBundle.INSTANCE.newFolder(), IDEImageBundle.INSTANCE.newFolderDisabled());
        setEvent(new CreateFolderEvent());
        setGroupName(GroupNames.NEW_COLLECTION);

        setShowInContextMenu(true);
        updateState();
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(ViewActivatedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
    }

    /**
     *
     */
    private void updateState() {
        if (vfsInfo == null) {
            setVisible(false);
            return;
        }

        setVisible(true);

        if (!browserPanelSelected || selectedItems.size() != 1) {
            setEnabled(false);
            return;
        }

        setEnabled(true);
    }

    /**
     * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     *      .framework.navigation.event.ItemsSelectedEvent)
     */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        selectedItems = event.getSelectedItems();
        browserPanelSelected = event.getView() instanceof NavigatorDisplay ||
                               event.getView() instanceof ProjectExplorerDisplay ||
                               event.getView() instanceof PackageExplorerDisplay;
        updateState();
    }

    @Override
    public void onViewActivated(ViewActivatedEvent event) {
        browserPanelSelected = event.getView() instanceof NavigatorDisplay ||
                               event.getView() instanceof ProjectExplorerDisplay ||
                               event.getView() instanceof PackageExplorerDisplay;
        updateState();
    }

    /**
     * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     *      .application.event.VfsChangedEvent)
     */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfsInfo = event.getVfsInfo();
        updateState();
    }

}
