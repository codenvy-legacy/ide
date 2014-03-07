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
package org.exoplatform.ide.client.operation.cutcopy;

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
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
@RolesAllowed({"workspace/developer"})
public class CutItemsCommand extends SimpleControl implements IDEControl, VfsChangedHandler, ItemsSelectedHandler,
                                                              ViewActivatedHandler {

    private static final String ID = "Edit/Cut Item(s)";

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.cutItemsTitleControl();

    private static final String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.cutItemsPromptControl();

    private VirtualFileSystemInfo vfsInfo;

    private boolean browserPanelSelected = false;

    private List<Item> selectedItems;

    /**
     *
     */
    public CutItemsCommand() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setDelimiterBefore(true);
        setImages(IDEImageBundle.INSTANCE.cut(), IDEImageBundle.INSTANCE.cutDisabled());
        setEvent(new CutItemsEvent());
        setGroupName(GroupNames.CUT_COPY);
    }

    /** @see org.exoplatform.ide.client.navigation.control.MultipleSelectionItemsControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(ViewActivatedEvent.TYPE, this);
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

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler#onViewActivated(org.exoplatform.ide.client.framework
     *      .ui.api.event.ViewActivatedEvent)
     */
    @Override
    public void onViewActivated(ViewActivatedEvent event) {
        browserPanelSelected = event.getView() instanceof NavigatorDisplay ||
                               event.getView() instanceof ProjectExplorerDisplay ||
                               event.getView() instanceof PackageExplorerDisplay;
        updateState();
    }

    protected void updateState() {
        setShowInContextMenu(browserPanelSelected);

        if (vfsInfo == null || selectedItems == null || selectedItems.size() != 1) {
            setEnabled(false);
            return;
        }

        if (selectedItems.get(0) instanceof ProjectModel
            || selectedItems.get(0).getId().equals(vfsInfo.getRoot().getId())) {
            setEnabled(false);
            setShowInContextMenu(false);
            return;
        }

        setEnabled(browserPanelSelected);
    }

    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfsInfo = event.getVfsInfo();
        setVisible(vfsInfo != null);
        updateState();
    }

}
