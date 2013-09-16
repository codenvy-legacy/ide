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
package org.exoplatform.ide.client.operation.createfile;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
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

/* 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"developer"})
public class NewFileControl extends SimpleControl implements IDEControl, ViewVisibilityChangedHandler,
                                                             VfsChangedHandler, ItemsSelectedHandler {

    private VirtualFileSystemInfo vfsInfo;

    private boolean browserPanelSelected = true;

    private List<Item> selectedItems = new ArrayList<Item>();

    /**
     * @param id
     * @param title
     * @param prompt
     * @param icon
     * @param event
     */
    public NewFileControl(String id, String title, String prompt, String icon, GwtEvent<?> event) {
        super(id);
        setTitle(title);
        setPrompt(prompt);
        setIcon(icon);
        setEvent(event);
        setEnabled(true);

        setShowInContextMenu(true);
    }

    /**
     * @param id
     * @param title
     * @param prompt
     * @param normalIcon
     * @param disabledIcon
     * @param event
     */
    public NewFileControl(String id, String title, String prompt, ImageResource normalIcon, ImageResource disabledIcon,
                          GwtEvent<?> event) {
        super(id);
        setTitle(title);
        setPrompt(prompt);
        setImages(normalIcon, disabledIcon);
        setEvent(event);
        setEnabled(true);

        setShowInContextMenu(true);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(ViewVisibilityChangedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);

        setVisible(true);
        updateEnabling();
    }

    /**
     *
     */
    protected void updateEnabling() {
        if (vfsInfo == null) {
            setEnabled(false);
            return;
        }

        if (!browserPanelSelected || selectedItems.size() == 0) {
            setEnabled(false);
            return;
        }

        setEnabled(true);
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     * .application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfsInfo = event.getVfsInfo();
        updateEnabling();
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide
     * .client.framework.ui.api.event.ViewVisibilityChangedEvent) */
    @Override
    public void onViewVisibilityChanged(ViewVisibilityChangedEvent event) {
        if (event.getView() instanceof NavigatorDisplay ||
            event.getView() instanceof ProjectExplorerDisplay ||
            event.getView() instanceof PackageExplorerDisplay) {
            browserPanelSelected = event.getView().isViewVisible();
            updateEnabling();
        }
    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     * .framework.navigation.event.ItemsSelectedEvent) */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        selectedItems = event.getSelectedItems();
        updateEnabling();
    }

}
