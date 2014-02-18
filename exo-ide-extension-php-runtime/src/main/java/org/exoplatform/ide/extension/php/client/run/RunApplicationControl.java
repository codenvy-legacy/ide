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
package org.exoplatform.ide.extension.php.client.run;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.extension.php.client.PhpExtensionClientBundle;
import org.exoplatform.ide.extension.php.client.PhpRuntimeExtension;
import org.exoplatform.ide.extension.php.client.run.event.ApplicationStartedEvent;
import org.exoplatform.ide.extension.php.client.run.event.ApplicationStartedHandler;
import org.exoplatform.ide.extension.php.client.run.event.ApplicationStoppedEvent;
import org.exoplatform.ide.extension.php.client.run.event.ApplicationStoppedHandler;
import org.exoplatform.ide.extension.php.client.run.event.RunApplicationEvent;

/**
 * Control for running PHP application.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: RunApplicationControl.java Apr 17, 2013 4:10:36 PM azatsarynnyy $
 *
 */
@RolesAllowed({"workspace/developer"})
public class RunApplicationControl extends SimpleControl implements IDEControl,
        ProjectClosedHandler, ProjectOpenedHandler, ApplicationStartedHandler,
        ApplicationStoppedHandler {
    public static final String ID = "Run/Run PHP Application";

    private static final String TITLE = PhpRuntimeExtension.PHP_LOCALIZATION.runApplicationControlTitle();

    private static final String PROMPT = PhpRuntimeExtension.PHP_LOCALIZATION.runApplicationControlPrompt();

    public RunApplicationControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(PhpExtensionClientBundle.INSTANCE.runApp(), PhpExtensionClientBundle.INSTANCE.runAppDisabled());
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
        String projectType = event.getProject().getProjectType();
        updateStatus(projectType);
    }

    /** @param projectType */
    private void updateStatus(String projectType) {
        boolean isPhpProject = ProjectType.PHP.value().equals(projectType);
        setVisible(isPhpProject);
        setEnabled(isPhpProject);
        setShowInContextMenu(isPhpProject);
    }

    /** @see org.exoplatform.ide.extension.php.client.run.event.ApplicationStoppedHandler#onApplicationStopped(org.exoplatform.ide.extension.php.client.run.event.ApplicationStoppedEvent) */
    @Override
    public void onApplicationStopped(ApplicationStoppedEvent event) {
        setEnabled(true);
    }

    /** @see org.exoplatform.ide.extension.php.client.run.event.ApplicationStartedHandler#onApplicationStarted(org.exoplatform.ide.extension.php.client.run.event.ApplicationStartedEvent) */
    @Override
    public void onApplicationStarted(ApplicationStartedEvent event) {
        setEnabled(false);
    }
    
}
