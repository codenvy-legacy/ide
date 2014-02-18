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
package org.exoplatform.ide.extension.cloudbees.client.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientBundle;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.info.ApplicationInfoEvent;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Control for showing information about application.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: InitializeApplicationControl.java Jun 23, 2011 12:00:53 PM vereshchaka $
 */
@RolesAllowed({"workspace/developer"})
public class ApplicationInfoControl extends SimpleControl implements IDEControl, VfsChangedHandler,
                                                                     ItemsSelectedHandler, ViewVisibilityChangedHandler {

    private static final String ID = CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoControlId();

    private static final String TITLE = CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoControlTitle();

    private static final String PROMPT = CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoControlPrompt();

    private VirtualFileSystemInfo vfsInfo;

    private List<Item> selectedItems = new ArrayList<Item>();

    private boolean isProjectExplorerVisible;

    public ApplicationInfoControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(CloudBeesClientBundle.INSTANCE.applicationInfo(),
                  CloudBeesClientBundle.INSTANCE.applicationInfoDisabled());
        setEvent(new ApplicationInfoEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(ViewVisibilityChangedEvent.TYPE, this);

        setVisible(true);
    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     * .framework.navigation.event.ItemsSelectedEvent) */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        selectedItems = event.getSelectedItems();
        refresh();
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     * .application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfsInfo = event.getVfsInfo();
        refresh();
    }

    /**
     *
     */
    private void refresh() {
        setEnabled(vfsInfo != null && selectedItems.size() > 0 && isProjectExplorerVisible);
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide
     * .client.framework.ui.api.event.ViewVisibilityChangedEvent) */
    @Override
    public void onViewVisibilityChanged(ViewVisibilityChangedEvent event) {
        if (event.getView() instanceof ProjectExplorerDisplay) {
            isProjectExplorerVisible = event.getView().isViewVisible();
            refresh();
        }
    }

}
