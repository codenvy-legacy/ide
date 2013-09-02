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
package org.exoplatform.ide.client.operation.uploadfile;

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

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"developer"})
public class UploadFileControl extends SimpleControl implements IDEControl, ItemsSelectedHandler,
                                                                ViewVisibilityChangedHandler, VfsChangedHandler {

    private final static String ID = "File/Upload File...";

    private final static String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.uploadFileControl();

    private boolean browserPanelSelected = true;

    private List<Item> selectedItems = new ArrayList<Item>();

    /**
     *
     */
    public UploadFileControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(TITLE);
        setDelimiterBefore(true);
        setImages(IDEImageBundle.INSTANCE.upload(), IDEImageBundle.INSTANCE.uploadDisabled());
        setEvent(new UploadFileEvent(false));
        setGroupName(GroupNames.UPLOAD);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(ViewVisibilityChangedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
    }

    /**
     *
     */
    private void updateEnabling() {
        if (browserPanelSelected) {
            if (selectedItems.size() == 1) {
                setEnabled(true);
            } else {
                setEnabled(false);
            }
        } else {
            setEnabled(false);
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     *      .framework.navigation.event.ItemsSelectedEvent)
     */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        selectedItems = event.getSelectedItems();
        updateEnabling();
    }

    /**
     * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     *      .application.event.VfsChangedEvent)
     */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        if (event.getVfsInfo() != null) {
            setVisible(true);
        } else {
            setVisible(false);
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide
     *      .client.framework.ui.api.event.ViewVisibilityChangedEvent)
     */
    @Override
    public void onViewVisibilityChanged(ViewVisibilityChangedEvent event) {
        if (event.getView() instanceof NavigatorDisplay || event.getView() instanceof ProjectExplorerDisplay
            || event.getView() instanceof PackageExplorerDisplay) {
            browserPanelSelected = event.getView().isViewVisible();
            updateEnabling();
        }
    }

}
