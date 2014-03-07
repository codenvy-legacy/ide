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
package org.exoplatform.ide.extension.appfog.client.project;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedEvent;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.extension.appfog.client.AppfogClientBundle;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Control for managing project, deployed on Appfog.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@RolesAllowed({"workspace/developer"})
public class AppfogControl extends SimpleControl implements IDEControl, ProjectOpenedHandler,
        ProjectClosedHandler, FolderRefreshedHandler {
    
    private static final String ID = "Project/PaaS/Appfog";

    private static final String TITLE = AppfogExtension.LOCALIZATION_CONSTANT.appfogControlTitle();

    private static final String PROMPT = AppfogExtension.LOCALIZATION_CONSTANT.appfogControlPrompt();

    public AppfogControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(AppfogClientBundle.INSTANCE.appfog(), AppfogClientBundle.INSTANCE.appfogDisabled());
        setEvent(new ManageAppfogProjectEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(FolderRefreshedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     * .project.ProjectClosedEvent) */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        setVisible(false);
        setEnabled(false);
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     * .project.ProjectOpenedEvent) */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        update(event.getProject());
    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler#onFolderRefreshed(org.exoplatform.ide.client
     * .framework.navigation.event.FolderRefreshedEvent) */
    @Override
    public void onFolderRefreshed(FolderRefreshedEvent event) {
        if (event.getFolder() instanceof ProjectModel) {
            update((ProjectModel)event.getFolder());
        }
    }

    private void update(ProjectModel project) {
        boolean isAppfog = project.getPropertyValue("appfog-application") != null;
        setVisible(isAppfog);
        setEnabled(isAppfog);
    }
}
