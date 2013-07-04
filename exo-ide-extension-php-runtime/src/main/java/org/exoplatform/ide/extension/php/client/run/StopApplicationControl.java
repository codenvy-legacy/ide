/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
import org.exoplatform.ide.extension.php.client.run.event.StopApplicationEvent;

/**
 * Control for stopping PHP application.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: StopApplicationControl.java Apr 17, 2013 4:10:52 PM azatsarynnyy $
 *
 */
@RolesAllowed("developer")
public class StopApplicationControl extends SimpleControl implements IDEControl, ProjectClosedHandler,
                                                                     ProjectOpenedHandler, ApplicationStartedHandler,
                                                                     ApplicationStoppedHandler {
    public static final String ID = "Run/Stop PHP Application";

    private static final String TITLE = PhpRuntimeExtension.PHP_LOCALIZATION.stopApplicationControlTitle();

    private static final String PROMPT = PhpRuntimeExtension.PHP_LOCALIZATION.stopApplicationControlPrompt();

    public StopApplicationControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(PhpExtensionClientBundle.INSTANCE.stopApp(), PhpExtensionClientBundle.INSTANCE.stopAppDisabled());
        setEvent(new StopApplicationEvent());
        setGroupName(GroupNames.RUNDEBUG);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(false);
        setEnabled(false);

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
        boolean isPhpProject = ProjectType.PHP.value().equals(projectType);
        setVisible(isPhpProject);
        setEnabled(false);
        setShowInContextMenu(isPhpProject);
    }

    /** @see org.exoplatform.ide.extension.php.client.run.event.ApplicationStoppedHandler#onApplicationStopped(org.exoplatform.ide.extension.php.client.run.event.ApplicationStoppedEvent) */
    @Override
    public void onApplicationStopped(ApplicationStoppedEvent event) {
        setEnabled(false);
    }

    /** @see org.exoplatform.ide.extension.php.client.run.event.ApplicationStartedHandler#onApplicationStarted(org.exoplatform.ide.extension.php.client.run.event.ApplicationStartedEvent) */
    @Override
    public void onApplicationStarted(ApplicationStartedEvent event) {
        setEnabled(true);
    }
}
