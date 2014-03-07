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
package com.codenvy.ide.extension.html.client.stop;

import com.codenvy.ide.extension.html.client.HtmlExtensionClientBundle;
import com.codenvy.ide.extension.html.client.HtmlRuntimeExtension;
import com.codenvy.ide.extension.html.client.start.ApplicationStartedEvent;
import com.codenvy.ide.extension.html.client.start.ApplicationStartedHandler;

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
 * Control for stopping HTML application.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: StopApplicationControl.java Jun 26, 2013 11:17:18 AM azatsarynnyy $
 */
@RolesAllowed({"workspace/developer"})
public class StopApplicationControl extends SimpleControl implements IDEControl, ProjectClosedHandler,
                                                         ProjectOpenedHandler, ApplicationStartedHandler,
                                                         ApplicationStoppedHandler {
    public static final String  ID     = "Run/Stop HTML Application";

    private static final String TITLE  = HtmlRuntimeExtension.HTML_LOCALIZATION_CONSTANTS.stopApplicationControlTitle();

    private static final String PROMPT = HtmlRuntimeExtension.HTML_LOCALIZATION_CONSTANTS.stopApplicationControlPrompt();

    public StopApplicationControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(HtmlExtensionClientBundle.INSTANCE.stopApp(), HtmlExtensionClientBundle.INSTANCE.stopAppDisabled());
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
        String projectType = event.getProject().getProjectType();
        boolean isHtmlProject = ProjectType.JAVASCRIPT.value().equals(projectType);
        setVisible(isHtmlProject);
        setEnabled(false);
        setShowInContextMenu(isHtmlProject);
    }

    /** @see com.codenvy.ide.extension.html.client.stop.ApplicationStoppedHandler#onApplicationStopped(com.codenvy.ide.extension.html.client.stop.ApplicationStoppedEvent) */
    @Override
    public void onApplicationStopped(ApplicationStoppedEvent event) {
        setEnabled(false);
    }

    /** @see com.codenvy.ide.extension.html.client.start.ApplicationStartedHandler#onApplicationStarted(com.codenvy.ide.extension.html.client.start.ApplicationStartedEvent) */
    @Override
    public void onApplicationStarted(ApplicationStartedEvent event) {
        setEnabled(true);
    }
}
