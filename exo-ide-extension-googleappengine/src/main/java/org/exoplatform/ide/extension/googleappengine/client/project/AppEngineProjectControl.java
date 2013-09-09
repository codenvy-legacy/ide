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
package org.exoplatform.ide.extension.googleappengine.client.project;

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
import org.exoplatform.ide.extension.googleappengine.client.GAEClientBundle;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 22, 2012 5:15:05 PM anya $
 */
@RolesAllowed("developer")
public class AppEngineProjectControl extends SimpleControl implements IDEControl,
            ProjectOpenedHandler, ProjectClosedHandler, ItemsSelectedHandler {
    
    private static final String ID = "Project/PaaS/Google App Engine";

    private static final String TITLE = GoogleAppEngineExtension.GAE_LOCALIZATION.googleAppEngineControl();

    private static final String PROMPT = GoogleAppEngineExtension.GAE_LOCALIZATION.googleAppEngineControl();

    public AppEngineProjectControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setEvent(new ManageAppEngineProjectEvent());
        setImages(GAEClientBundle.INSTANCE.googleAppEngine(), GAEClientBundle.INSTANCE.googleAppEngineDisabled());
        setGroupName(GroupNames.PAAS);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     * .project.ProjectOpenedEvent) */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        boolean isAppEngine = isDeployed(event.getProject());
        setVisible(isAppEngine);
        setEnabled(isAppEngine);
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     * .project.ProjectClosedEvent) */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        setVisible(false);
        setEnabled(false);
    }

    private boolean isDeployed(ProjectModel project) {
        return project != null
               && GoogleAppEngineExtension.isAppEngineProject(project)
               && project.getPropertyValue("gae-application") != null;
    }

    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        if (event.getSelectedItems().size() != 1) {
            setEnabled(false);
            return;
        }

        Item item = event.getSelectedItems().get(0);
        ProjectModel project = null;
        if (item instanceof ProjectModel) {
            project = (ProjectModel)item;
        } else {
            project = ((ItemContext)item).getProject();
        }

        setEnabled(isDeployed(project));
        setVisible(isDeployed(project));
    }

}
