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

package org.exoplatform.ide.client.project.list;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ShowProjectsControl extends SimpleControl implements IDEControl, VfsChangedHandler, ViewActivatedHandler,
                                                                  ItemsSelectedHandler {

    public static final String ID = "Project/Open...";

    private static final String TITLE = "Open...";

    private static final String PROMPT = "Open Project...";

    private VirtualFileSystemInfo vfsInfo;

    private boolean projectExplorerSelected = false;

    /**
     *
     */
    public ShowProjectsControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(IDEImageBundle.INSTANCE.projectOpened(), IDEImageBundle.INSTANCE.projectOpenedDisabled());
        setEvent(new ShowProjectsEvent());
        // setDelimiterBefore(true);
        setGroupName(GroupNames.ACTIONS);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ViewActivatedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        update();
    }

    /**
     *
     */
    private void update() {
        if (vfsInfo == null) {
            setVisible(false);
            setEnabled(false);
            return;
        }
        setEnabled(true);
        setVisible(true);
    }

    /**
     * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     *      .application.event.VfsChangedEvent)
     */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfsInfo = event.getVfsInfo();
        update();
    }

    /**
     * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     *      .framework.navigation.event.ItemsSelectedEvent)
     */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {

        if (projectExplorerSelected && !event.getSelectedItems().isEmpty()) {
            setShowInContextMenu(event.getSelectedItems().get(0) instanceof ProjectModel);
        } else {
            setShowInContextMenu(false);
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler#onViewActivated(org.exoplatform.ide.client.framework
     *      .ui.api.event.ViewActivatedEvent)
     */
    @Override
    public void onViewActivated(ViewActivatedEvent event) {
        projectExplorerSelected = event.getView() instanceof ProjectExplorerDisplay;
    }

}
