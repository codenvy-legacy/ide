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
package com.codenvy.ide.extension.html.client.logs;

import com.codenvy.ide.extension.html.client.HtmlRuntimeExtension;
import com.codenvy.ide.extension.html.client.HtmlExtensionClientBundle;
import com.codenvy.ide.extension.html.client.run.event.ApplicationStartedEvent;
import com.codenvy.ide.extension.html.client.run.event.ApplicationStartedHandler;
import com.codenvy.ide.extension.html.client.run.event.ApplicationStoppedEvent;
import com.codenvy.ide.extension.html.client.run.event.ApplicationStoppedHandler;

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

/**
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ShowLogsControl.java Jun 26, 2013 11:05:43 AM azatsarynnyy $
 *
 */
@RolesAllowed({"developer"})
public class ShowLogsControl extends SimpleControl implements IDEControl, ProjectClosedHandler, ProjectOpenedHandler,
                                                              ApplicationStartedHandler, ApplicationStoppedHandler {
    private static final String ID = "Run/HTML Logs";

    private static final String TITLE = HtmlRuntimeExtension.HTML_LOCALIZATION_CONSTANTS.showLogsControlTitle();

    private static final String PROMPT = HtmlRuntimeExtension.HTML_LOCALIZATION_CONSTANTS.showLogsControlPrompt();

    public ShowLogsControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(HtmlExtensionClientBundle.INSTANCE.logs(), HtmlExtensionClientBundle.INSTANCE.logsDisabled());
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
        boolean isHtmlProject = ProjectType.JAVASCRIPT.value().equals(projectType);
        setVisible(isHtmlProject);
        setEnabled(false);
        setShowInContextMenu(isHtmlProject);
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

    /** @see com.codenvy.ide.extension.html.client.run.event.ApplicationStoppedHandler#onApplicationStopped(com.codenvy.ide.extension.html.client.run.event.ApplicationStoppedEvent) */
    @Override
    public void onApplicationStopped(ApplicationStoppedEvent event) {
        setEnabled(false);
    }

    /** @see com.codenvy.ide.extension.html.client.run.event.ApplicationStartedHandler#onApplicationStarted(com.codenvy.ide.extension.html.client.run.event.ApplicationStartedEvent) */
    @Override
    public void onApplicationStarted(ApplicationStartedEvent event) {
        setEnabled(true);
    }
}
