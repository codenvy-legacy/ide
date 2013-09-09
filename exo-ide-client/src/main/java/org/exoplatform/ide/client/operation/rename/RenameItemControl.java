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
package org.exoplatform.ide.client.operation.rename;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.NavigatorDisplay;
import org.exoplatform.ide.client.framework.project.PackageExplorerDisplay;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"developer"})
public class RenameItemControl extends SimpleControl implements IDEControl, ItemsSelectedHandler, VfsChangedHandler,
                                                                ViewActivatedHandler, EditorFileOpenedHandler, EditorFileClosedHandler {

    private static final String ID = "File/Rename...";

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.renameTitleControl();

    private static final String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.renamePromptControl();

    private List<Item> selectedItems = new ArrayList<Item>();

    /** Current workspace's href. */
    private VirtualFileSystemInfo vfsInfo = null;

    private boolean navigationViewSelected = false;
    
    private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

    /**
     *
     */
    public RenameItemControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setShowInContextMenu(true);
        setImages(IDEImageBundle.INSTANCE.rename(), IDEImageBundle.INSTANCE.renameDisabled());
        setEvent(new RenameItemEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(ViewActivatedEvent.TYPE, this);
        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
    }

    /** Update control's state. */
    private void updateState() {
        if (vfsInfo == null) {
            setVisible(false);
            setShowInContextMenu(false);
            return;
        }
        setVisible(true);

        if (!navigationViewSelected) {
            setEnabled(false);
            setShowInContextMenu(false);
            return;
        }

        setShowInContextMenu(navigationViewSelected);

        if (selectedItems.size() != 1 || vfsInfo.getRoot().getId().equals(selectedItems.get(0).getId())) {
            setEnabled(false);
            return;
        }
        
        Item selectedItem = selectedItems.get(0);
        if (selectedItem instanceof FileModel && !openedFiles.containsKey(selectedItem.getId())) {
            setEnabled(true);
            return;
        }
        
        if (selectedItem instanceof FolderModel) {
            String folderPath = selectedItem.getPath();
            for (FileModel file : openedFiles.values()) {
                if (file.getPath().startsWith(folderPath)) {
                    setEnabled(false);
                    return;
                }
            }
            
            setEnabled(true);
        } else {            
            setEnabled(false);
        }

    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     * .framework.navigation.event.ItemsSelectedEvent) */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        navigationViewSelected = event.getView() instanceof NavigatorDisplay ||
                                 event.getView() instanceof ProjectExplorerDisplay ||
                                 event.getView() instanceof PackageExplorerDisplay;
        selectedItems = event.getSelectedItems();

        updateState();
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     * .application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfsInfo = event.getVfsInfo();
        updateState();
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler#onViewActivated(org.exoplatform.ide.client.framework
     * .ui.api.event.ViewActivatedEvent) */
    @Override
    public void onViewActivated(ViewActivatedEvent event) {
        navigationViewSelected = event.getView() instanceof NavigatorDisplay ||
                                 event.getView() instanceof ProjectExplorerDisplay ||
                                 event.getView() instanceof PackageExplorerDisplay;

        updateState();
    }

    @Override
    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        openedFiles = event.getOpenedFiles();
        updateState();
    }

    @Override
    public void onEditorFileClosed(EditorFileClosedEvent event) {
        openedFiles = event.getOpenedFiles();
        updateState();
    }

}
