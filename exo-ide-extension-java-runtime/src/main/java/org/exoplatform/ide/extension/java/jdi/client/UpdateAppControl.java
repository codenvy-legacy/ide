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
package org.exoplatform.ide.extension.java.jdi.client;

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
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStartedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStartedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStoppedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStoppedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerActivityEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerActivityHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.UpdateAppEvent;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Property;

import java.util.List;

/**
 * Control for updating deployed application using JRebel.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: UpdateAppControl.java Oct 30, 2012 2:53:32 PM azatsarynnyy $
 */
@RolesAllowed("developer")
public class UpdateAppControl extends SimpleControl implements IDEControl, ProjectClosedHandler, ProjectOpenedHandler,
                                                   AppStartedHandler, AppStoppedHandler, DebuggerActivityHandler {
    public static final String  ID     = DebuggerExtension.LOCALIZATION_CONSTANT.updateAppControlId();

    private static final String TITLE  = DebuggerExtension.LOCALIZATION_CONSTANT.updateAppControlTitle();

    private static final String PROMPT = DebuggerExtension.LOCALIZATION_CONSTANT.updateAppControlPrompt();

    private static final String JREBEL = "jrebel";

    public UpdateAppControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(DebuggerClientBundle.INSTANCE.updateApp(), DebuggerClientBundle.INSTANCE.updateAppDisabled());
        setEvent(new UpdateAppEvent());
        setGroupName(GroupNames.RUNDEBUG);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(false);
        setEnabled(false);

        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(AppStartedEvent.TYPE, this);
        IDE.addHandler(AppStoppedEvent.TYPE, this);
        IDE.addHandler(DebuggerActivityEvent.TYPE, this);
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
        updateState(event.getProject());
    }


    /** @param project */
    private void updateState(ProjectModel project) {
        String projectType = project.getProjectType();
        boolean isJavaProject = ProjectResolver.SPRING.equals(projectType)
                                || ProjectResolver.SERVLET_JSP.equals(projectType)
                                || ProjectResolver.APP_ENGINE_JAVA.equals(projectType)
                                || ProjectType.JAVA.value().equals(projectType)
                                || ProjectType.WAR.value().equals(projectType)
                                || ProjectType.JSP.value().equals(projectType);

        boolean useJRebel = isUseJRebel(project);

        setVisible(isJavaProject && useJRebel);
        setEnabled(false);
        setShowInContextMenu(isJavaProject && useJRebel);
    }

    @Override
    public void onAppStopped(AppStoppedEvent appStopedEvent) {
        setEnabled(false);
    }

    @Override
    public void onAppStarted(AppStartedEvent event) {
        setEnabled(true);
    }

    /**
     * Read projects property 'jrebel'.
     * 
     * @return <code>true</code> if need to use JRebel
     */
    private boolean isUseJRebel(ProjectModel project) {
        Property property = project.getProperty(JREBEL);
        if (property != null) {
            List<String> value = property.getValue();
            if (value != null && !value.isEmpty()) {
                if (value.get(0) != null) {
                    return Boolean.parseBoolean(value.get(0));
                }
            }
        }
        return false;
    }

    /**
     * Set update button enable if in current state debugger is not stopped on breakpoint.
     * 
     * @param event
     */
    @Override
    public void onDebuggerActivityChanged(DebuggerActivityEvent event) {
        setEnabled(event.getState());
    }
}
