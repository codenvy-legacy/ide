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
package org.exoplatform.ide.extension.python.client.run;

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
import org.exoplatform.ide.extension.python.client.PythonExtensionClientBundle;
import org.exoplatform.ide.extension.python.client.PythonRuntimeExtension;
import org.exoplatform.ide.extension.python.client.run.event.ApplicationStartedEvent;
import org.exoplatform.ide.extension.python.client.run.event.ApplicationStartedHandler;
import org.exoplatform.ide.extension.python.client.run.event.ApplicationStoppedEvent;
import org.exoplatform.ide.extension.python.client.run.event.ApplicationStoppedHandler;
import org.exoplatform.ide.extension.python.client.run.event.RunApplicationEvent;

/**
 * Control for running Python application.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jun 20, 2012 2:58:43 PM anya $
 */
@RolesAllowed({"workspace/developer"})
public class RunApplicationControl extends SimpleControl implements IDEControl,
        ProjectClosedHandler, ProjectOpenedHandler, ApplicationStartedHandler, ApplicationStoppedHandler {
    
    public static final String ID = "Run/Run Python Application";

    private static final String TITLE = PythonRuntimeExtension.PYTHON_LOCALIZATION.runApplicationControlTitle();

    private static final String PROMPT = PythonRuntimeExtension.PYTHON_LOCALIZATION.runApplicationControlPrompt();

    public RunApplicationControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(PythonExtensionClientBundle.INSTANCE.runApp(), PythonExtensionClientBundle.INSTANCE.runAppDisabled());
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
        boolean isPythonProject = ProjectResolver.APP_ENGINE_PYTHON.equals(projectType) || ProjectType.PYTHON.value().equals(projectType);
        setVisible(isPythonProject);
        setEnabled(isPythonProject);
        setShowInContextMenu(isPythonProject);
    }

    /** @see org.exoplatform.ide.extension.python.client.run.event.ApplicationStoppedHandler#onApplicationStopped(org.exoplatform.ide
     * .extension.python.client.run.event.ApplicationStoppedEvent) */
    @Override
    public void onApplicationStopped(ApplicationStoppedEvent event) {
        setEnabled(true);
    }

    /** @see org.exoplatform.ide.extension.python.client.run.event.ApplicationStartedHandler#onApplicationStarted(org.exoplatform.ide
     * .extension.python.client.run.event.ApplicationStartedEvent) */
    @Override
    public void onApplicationStarted(ApplicationStartedEvent event) {
        setEnabled(false);
    }
    
}
