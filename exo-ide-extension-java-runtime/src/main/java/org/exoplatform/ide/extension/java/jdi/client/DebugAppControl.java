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

@RolesAllowed({"workspace/developer"})
public class DebugAppControl extends SimpleControl implements IDEControl, AppStartedHandler, AppStoppedHandler, ItemsSelectedHandler {
    public static final String  ID                = DebuggerExtension.LOCALIZATION_CONSTANT.debugAppControlId();

    private static final String TITLE             = "Debug Application";

    private static final String PROMPT            = "Launch Debug";

    private ProjectModel        currentProject    = null;

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
    private void updateState() {
        String projectType = (currentProject != null) ? currentProject.getProjectType() : null;
        boolean isJavaProject = projectType != null && (ProjectResolver.SPRING.equals(projectType)
                                                        || ProjectResolver.SERVLET_JSP.equals(projectType)
                                                        || ProjectResolver.APP_ENGINE_JAVA.equals(projectType)
                                                        || ProjectType.JAVA.value().equals(projectType)
                                                        || ProjectType.WAR.value().equals(projectType)
                                                        || ProjectType.JSP.value().equals(projectType));
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

            currentProject = selectedItem instanceof ProjectModel ? (ProjectModel)selectedItem
                                                                  : ((ItemContext)selectedItem).getProject();
            if (currentProject == null || currentProject.getProjectType() == null
                || ProjectType.MultiModule.value().equals(currentProject.getProjectType())
                || ProjectType.JAR.value().equals(currentProject.getProjectType())
                || ProjectType.JAVASCRIPT.value().equals(currentProject.getProjectType())
                || ProjectType.RUBY_ON_RAILS.value().equals(currentProject.getProjectType())
                || ProjectType.PYTHON.value().equals(currentProject.getProjectType())
                || ProjectType.PHP.value().equals(currentProject.getProjectType())
                || ProjectType.NODE_JS.value().equals(currentProject.getProjectType())) {
                setVisible(false);
            }
            updateState();
        }
    }

}
