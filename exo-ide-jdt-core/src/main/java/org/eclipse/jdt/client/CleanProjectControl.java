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
package org.eclipse.jdt.client;

import org.eclipse.jdt.client.event.CleanProjectEvent;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 9:57:22 AM Mar 5, 2012 evgen $
 */
@RolesAllowed({"workspace/developer"})
public class CleanProjectControl extends SimpleControl implements IDEControl, 
            ProjectOpenedHandler, ProjectClosedHandler {

    private boolean isJavaProject = false;

    public CleanProjectControl() {
        super(JdtExtension.LOCALIZATION_CONSTANT.updateDependencyControlId());
        setTitle(JdtExtension.LOCALIZATION_CONSTANT.updateDependencyControlTitle());
        setPrompt(JdtExtension.LOCALIZATION_CONSTANT.updateDependencyControlPrompt());
        setImages(JdtClientBundle.INSTANCE.clean(), JdtClientBundle.INSTANCE.cleanDisabled());
        setEvent(new CleanProjectEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        setVisible(false);
        setEnabled(false);
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     * .project.ProjectClosedEvent) */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        isJavaProject = false;
        updateEnabling();
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     * .project.ProjectOpenedEvent) */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        isJavaProject = JdtExtension.get().isProjectSupported(event.getProject().getProjectType());
        updateEnabling();
    }

    /**
     *
     */
    private void updateEnabling() {
        setVisible(isJavaProject);
        setEnabled(isJavaProject);
    }

}
