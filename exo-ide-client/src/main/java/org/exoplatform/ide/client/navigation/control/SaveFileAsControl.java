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
package org.exoplatform.ide.client.navigation.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.SaveFileAsEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.NavigatorDisplay;
import org.exoplatform.ide.client.framework.project.PackageExplorerDisplay;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"developer"})
public class SaveFileAsControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler,
                                                                ItemsSelectedHandler, VfsChangedHandler, ViewVisibilityChangedHandler {

    private static final String ID = "File/Save As...";

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.saveFileAsControl();

    /** Current workspace's href. */
    private VirtualFileSystemInfo vfsInfo;

    private List<Item> selectedItems = new ArrayList<Item>();

    private FileModel activeFile;

    private boolean browserPanelSelected = true;

    /**
     *
     */
    public SaveFileAsControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(TITLE);
        setImages(IDEImageBundle.INSTANCE.saveAs(), IDEImageBundle.INSTANCE.saveAsDisabled());
        setEvent(new SaveFileAsEvent(SaveFileAsEvent.SaveDialogType.YES_CANCEL, null, null));
        setGroupName(GroupNames.SAVE);
    }

    /** @see org.exoplatform.ide.client.navigation.control.MultipleSelectionItemsControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ViewVisibilityChangedEvent.TYPE, this);
    }

    /** Update control's state. */
    private void updateState() {
        if (vfsInfo == null) {
            setVisible(false);
            return;
        }
        setVisible(true);

        if (browserPanelSelected && selectedItems.size() == 1 && activeFile != null) {
            setEnabled(true);
        } else {
            setEnabled(false);
        }
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        activeFile = event.getFile();
        updateState();
    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     * .framework.navigation.event.ItemsSelectedEvent) */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        selectedItems = event.getSelectedItems();
        updateState();
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     * .application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfsInfo = event.getVfsInfo();
        updateState();
        setEnabled(false);
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide
     * .client.framework.ui.api.event.ViewVisibilityChangedEvent) */
    @Override
    public void onViewVisibilityChanged(ViewVisibilityChangedEvent event) {
        if (event.getView() instanceof NavigatorDisplay ||
            event.getView() instanceof ProjectExplorerDisplay ||
            event.getView() instanceof PackageExplorerDisplay) {
            browserPanelSelected = event.getView().isViewVisible();
            updateState();
        }
    }

}
