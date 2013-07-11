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
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStartedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStartedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStoppedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.AppStoppedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.DebugAppEvent;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

@RolesAllowed("developer")
public class DebugAppControl extends SimpleControl implements IDEControl,
                                                  // ProjectClosedHandler, ProjectOpenedHandler,
                                                  AppStartedHandler, AppStoppedHandler, ItemsSelectedHandler {
    public static final String  ID     = DebuggerExtension.LOCALIZATION_CONSTANT.debugAppControlId();

    private static final String TITLE  = "Debug Application";

    private static final String PROMPT = "Launch Debug";

    public DebugAppControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(DebuggerClientBundle.INSTANCE.debugApp(), DebuggerClientBundle.INSTANCE.debugAppDisabled());
        setEvent(new DebugAppEvent());
        setGroupName(GroupNames.RUNDEBUG);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(false);
        setEnabled(false);

        // IDE.addHandler(ProjectClosedEvent.TYPE, this);
        // IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(AppStartedEvent.TYPE, this);
        IDE.addHandler(AppStoppedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
    }

    // /**
    // * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
    // .project.ProjectClosedEvent)
    // */
    // @Override
    // public void onProjectClosed(ProjectClosedEvent event)
    // {
    // setEnabled(false);
    // setVisible(false);
    // }
    //
    // /**
    // * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
    // .project.ProjectOpenedEvent)
    // */
    // @Override
    // public void onProjectOpened(ProjectOpenedEvent event)
    // {
    // String projectType = event.getProject().getProjectType();
    // updateState(projectType);
    // }

    /** @param event */
    private void updateState(String projectType) {
        boolean isJavaProject = ProjectResolver.SPRING.equals(projectType)
                                || ProjectResolver.SERVLET_JSP.equals(projectType)
                                || ProjectResolver.APP_ENGINE_JAVA.equals(projectType)
                                || ProjectType.JAVA.value().equals(projectType)
                                || ProjectType.WAR.value().equals(projectType)
                                || ProjectType.JSP.value().equals(projectType);
        // setVisible(isJavaProject);
        setEnabled(isJavaProject);
        setShowInContextMenu(isJavaProject);
    }

    @Override
    public void onAppStopped(AppStoppedEvent appStopedEvent) {
        setEnabled(true);
    }

    @Override
    public void onAppStarted(AppStartedEvent event) {
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
            if (ProjectType.MultiModule.value().equals(project.getProjectType())
                || ProjectType.JAR.value().equals(project.getProjectType())
                || ProjectType.JAVASCRIPT.value().equals(project.getProjectType())
                || ProjectType.RUBY_ON_RAILS.value().equals(project.getProjectType())
                || ProjectType.PYTHON.value().equals(project.getProjectType())
                || ProjectType.PHP.value().equals(project.getProjectType())
                || ProjectType.NODE_JS.value().equals(project.getProjectType())) {
                setVisible(false);
            }
            updateState(project.getProjectType());
        }
    }

}
