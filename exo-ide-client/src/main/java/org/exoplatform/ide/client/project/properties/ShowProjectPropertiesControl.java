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

package org.exoplatform.ide.client.project.properties;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.*;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ShowProjectPropertiesControl extends SimpleControl implements IDEControl, ProjectOpenedHandler,
                                                                           ProjectClosedHandler, ItemsSelectedHandler,
                                                                           ViewActivatedHandler {

    public static final String ID = "Project/Properties...";

    private static final String TITLE = "Properties...";

    private static final String PROMPT = "Show Project Properties...";

    private boolean isNavigatorSelected;

    public ShowProjectPropertiesControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(IDEImageBundle.INSTANCE.projectProperties(), IDEImageBundle.INSTANCE.projectPropertiesDisabled());
        setEvent(new ShowProjectPropertiesEvent());
        setGroupName(GroupNames.PROPERTIES);
    }

    @Override
    public void initialize() {
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(ViewActivatedEvent.TYPE, this);
        setVisible(true);
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        setEnabled(true);
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        setEnabled(false);
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler#onViewActivated(org.exoplatform.ide.client.framework
     *      .ui.api.event.ViewActivatedEvent)
     */
    @Override
    public void onViewActivated(ViewActivatedEvent event) {
        isNavigatorSelected =
                event.getView() instanceof ProjectExplorerDisplay || event.getView() instanceof NavigatorDisplay;
    }

    /**
     * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     *      .framework.navigation.event.ItemsSelectedEvent)
     */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {

        if (event.getSelectedItems() == null || event.getSelectedItems().isEmpty()) {
            setShowInContextMenu(false);
            return;
        }
        setShowInContextMenu(isNavigatorSelected && event.getSelectedItems().get(0) instanceof ProjectModel);
    }

}
