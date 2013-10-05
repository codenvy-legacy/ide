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
package org.exoplatform.ide.extension.nodejs.client.run;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.extension.nodejs.client.NodeJsExtensionClientBundle;
import org.exoplatform.ide.extension.nodejs.client.NodeJsRuntimeExtension;
import org.exoplatform.ide.extension.nodejs.client.run.event.ApplicationStartedEvent;
import org.exoplatform.ide.extension.nodejs.client.run.event.ApplicationStartedHandler;
import org.exoplatform.ide.extension.nodejs.client.run.event.ApplicationStoppedEvent;
import org.exoplatform.ide.extension.nodejs.client.run.event.ApplicationStoppedHandler;
import org.exoplatform.ide.extension.nodejs.client.run.event.RunApplicationEvent;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * Control for running Node.js application.
 * 
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: RunApplicationControl.java Apr 18, 2013 4:36:23 PM vsvydenko $
 */
@RolesAllowed("developer")
public class RunApplicationControl extends SimpleControl implements IDEControl,
                                                        ProjectClosedHandler, ProjectOpenedHandler, ApplicationStartedHandler,
                                                        ApplicationStoppedHandler, ItemsSelectedHandler {

    public static final String  ID     = "Run/Run Node.js Application";

    private static final String TITLE  = NodeJsRuntimeExtension.NODEJS_LOCALIZATION.runApplicationControlTitle();

    private static final String PROMPT = NodeJsRuntimeExtension.NODEJS_LOCALIZATION.runApplicationControlPrompt();

    public RunApplicationControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(NodeJsExtensionClientBundle.INSTANCE.runApp(), NodeJsExtensionClientBundle.INSTANCE.runAppDisabled());
        setEvent(new RunApplicationEvent());
        setGroupName(GroupNames.RUNDEBUG);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ApplicationStartedEvent.TYPE, this);
        IDE.addHandler(ApplicationStoppedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     *      .project.ProjectClosedEvent)
     */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        setVisible(false);
        setEnabled(false);
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     *      .project.ProjectOpenedEvent)
     */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        updateStatus(event.getProject());
    }

    /** @param projectType */
    private void updateStatus(ProjectModel project) {
        String projectType = (project != null) ? project.getProjectType() : null;
        boolean isNodeProject = projectType != null && ProjectType.NODE_JS.value().equals(projectType);
        setVisible(isNodeProject);
        setEnabled(isNodeProject);
        setShowInContextMenu(isNodeProject);
    }

    /** @see org.exoplatform.ide.extension.nodejs.client.run.event.ApplicationStoppedHandler#onApplicationStopped(org.exoplatform.ide.extension.nodejs.client.run.event.ApplicationStoppedEvent) */
    @Override
    public void onApplicationStopped(ApplicationStoppedEvent event) {
        setEnabled(true);
    }

    /** @see org.exoplatform.ide.extension.nodejs.client.run.event.ApplicationStartedHandler#onApplicationStarted(org.exoplatform.ide.extension.nodejs.client.run.event.ApplicationStartedEvent) */
    @Override
    public void onApplicationStarted(ApplicationStartedEvent event) {
        setEnabled(false);
    }

    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        if (event.getSelectedItems().size() != 1) {
            setEnabled(false);
            setVisible(false);
        } else {
            setVisible(true);
            Item selectedItem = event.getSelectedItems().get(0);

            ProjectModel project = selectedItem instanceof ProjectModel ? (ProjectModel)selectedItem
                : ((ItemContext)selectedItem).getProject();
            updateStatus(project);
        }
    }
}
