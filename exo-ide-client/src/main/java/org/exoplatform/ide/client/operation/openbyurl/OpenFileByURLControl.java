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
package org.exoplatform.ide.client.operation.openbyurl;

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
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Control for opening file by URL.
 * <p/>
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@RolesAllowed({"developer"})
public class OpenFileByURLControl extends SimpleControl implements IDEControl, VfsChangedHandler, ItemsSelectedHandler,
                                                                   ViewVisibilityChangedHandler {

    private VirtualFileSystemInfo vfsInfo;

    private List<Item> selectedItems = new ArrayList<Item>();

    private boolean browserPanelSelected = true;

    /** Creates a new instance of this control. */
    public OpenFileByURLControl() {
        super(IDE.IDE_LOCALIZATION_CONSTANT.openFileByURLControlId());
        setTitle(IDE.IDE_LOCALIZATION_CONSTANT.openFileByURLControlTitle());
        setPrompt(IDE.IDE_LOCALIZATION_CONSTANT.openFileByURLControlPrompt());
        setImages(IDEImageBundle.INSTANCE.url(), IDEImageBundle.INSTANCE.urlDisabled());
        setEvent(new OpenFileByURLEvent());
        setGroupName(GroupNames.UPLOAD);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(ViewVisibilityChangedEvent.TYPE, this);

        setVisible(true);
        updateEnabling();
    }

    /**
     *
     */
    private void updateEnabling() {
        setEnabled(vfsInfo != null && browserPanelSelected && selectedItems.size() > 0);
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     * .application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfsInfo = event.getVfsInfo();
        updateEnabling();
    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     * .framework.navigation.event.ItemsSelectedEvent) */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        selectedItems = event.getSelectedItems();
        updateEnabling();
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide
     * .client.framework.ui.api.event.ViewVisibilityChangedEvent) */
    @Override
    public void onViewVisibilityChanged(ViewVisibilityChangedEvent event) {
        if (event.getView() instanceof NavigatorDisplay || event.getView() instanceof ProjectExplorerDisplay
            || event.getView() instanceof PackageExplorerDisplay) {
            browserPanelSelected = event.getView().isViewVisible();
            updateEnabling();
        }
    }

}
