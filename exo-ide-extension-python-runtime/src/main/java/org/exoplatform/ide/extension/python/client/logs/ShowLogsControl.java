/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.python.client.logs;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.python.client.PythonExtensionClientBundle;
import org.exoplatform.ide.extension.python.client.PythonRuntimeExtension;
import org.exoplatform.ide.extension.python.client.run.event.ApplicationStartedEvent;
import org.exoplatform.ide.extension.python.client.run.event.ApplicationStartedHandler;
import org.exoplatform.ide.extension.python.client.run.event.ApplicationStoppedEvent;
import org.exoplatform.ide.extension.python.client.run.event.ApplicationStoppedHandler;

@RolesAllowed({"developer"})
public class ShowLogsControl extends SimpleControl implements IDEControl, ProjectClosedHandler, ProjectOpenedHandler,
                                                              ApplicationStartedHandler, ApplicationStoppedHandler {
    private static final String ID = "Run/Python Logs";

    private static final String TITLE = PythonRuntimeExtension.PYTHON_LOCALIZATION.showLogsControlTitle();

    private static final String PROMPT = PythonRuntimeExtension.PYTHON_LOCALIZATION.showLogsControlPrompt();

    public ShowLogsControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(PythonExtensionClientBundle.INSTANCE.logs(), PythonExtensionClientBundle.INSTANCE.logsDisabled());
        setEvent(new ShowLogsEvent());
        setGroupName(GroupNames.RUNDEBUG);
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     * .project.ProjectClosedEvent) */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        setEnabled(false);
        setVisible(false);
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     * .project.ProjectOpenedEvent) */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        String projectType = event.getProject().getProjectType();
        boolean isPythonProject = ProjectResolver.APP_ENGINE_PYTHON.equals(projectType);
        setVisible(isPythonProject);
        setEnabled(false);
        setShowInContextMenu(isPythonProject);
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

    /** @see org.exoplatform.ide.extension.python.client.run.event.ApplicationStoppedHandler#onApplicationStopped(org.exoplatform.ide
     * .extension.python.client.run.event.ApplicationStoppedEvent) */
    @Override
    public void onApplicationStopped(ApplicationStoppedEvent event) {
        setEnabled(false);
    }

    /** @see org.exoplatform.ide.extension.python.client.run.event.ApplicationStartedHandler#onApplicationStarted(org.exoplatform.ide
     * .extension.python.client.run.event.ApplicationStartedEvent) */
    @Override
    public void onApplicationStarted(ApplicationStartedEvent event) {
        setEnabled(true);
    }
}
