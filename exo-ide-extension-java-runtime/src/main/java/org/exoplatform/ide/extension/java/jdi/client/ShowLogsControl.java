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
import org.exoplatform.ide.client.framework.project.*;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.java.jdi.client.events.*;

@RolesAllowed("developer")
public class ShowLogsControl extends SimpleControl implements IDEControl, ProjectClosedHandler, ProjectOpenedHandler,
                                                              AppStartedHandler, AppStoppedHandler {
    private static final String ID = "Run/Java Logs";

    private static final String TITLE = DebuggerExtension.LOCALIZATION_CONSTANT.showLogsControlTitle();

    private static final String PROMPT = DebuggerExtension.LOCALIZATION_CONSTANT.showLogsControlPrompt();

    public ShowLogsControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(DebuggerClientBundle.INSTANCE.logs(), DebuggerClientBundle.INSTANCE.logsDisabled());
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
        updateState(projectType);
    }

    /** @param projectType */
    private void updateState(String projectType) {
        boolean isJavaProject =
                ProjectResolver.SPRING.equals(projectType) || ProjectResolver.SERVLET_JSP.equals(projectType)
                || ProjectResolver.APP_ENGINE_JAVA.equals(projectType) || ProjectType.JAVA.value().equals(projectType)
                || ProjectType.WAR.value().equals(projectType)
                || ProjectType.JSP.value().equals(projectType);
        setVisible(isJavaProject);
        setEnabled(false);
        setShowInContextMenu(isJavaProject);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(false);
        setEnabled(false);

        IDE.addHandler(AppStartedEvent.TYPE, this);
        IDE.addHandler(AppStoppedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
    }

    @Override
    public void onAppStarted(AppStartedEvent event) {
        setEnabled(true);
    }

    @Override
    public void onAppStopped(AppStoppedEvent appStopedEvent) {
        setEnabled(false);
    }
}
