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
package org.exoplatform.ide.extension.samples.client.inviting.github;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.DisableInTempWorkspace;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedEvent;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.extension.samples.client.SamplesClientBundle;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
@RolesAllowed({"developer"})
@DisableInTempWorkspace
public class InviteGitHubCollaboratorsControl extends SimpleControl implements IDEControl, ProjectOpenedHandler,
                                                                               ProjectClosedHandler, FolderRefreshedHandler {

    private static final String ID = "Share/Invite GitHub Collaborators...";

    private static final String TITLE = "Invite GitHub Collaborators...";

    private static final String PROMPT = "Invite GitHub Collaborators...";

    private ProjectModel project;

    public InviteGitHubCollaboratorsControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(SamplesClientBundle.INSTANCE.invite(), SamplesClientBundle.INSTANCE.inviteDisable());
        setEvent(new InviteGitHubCollaboratorsEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(FolderRefreshedEvent.TYPE, this);
        setVisible(true);
        setEnabled(false);
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     * .project.ProjectOpenedEvent) */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        project = event.getProject();
        refresh();
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     * .project.ProjectClosedEvent) */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        project = null;
        setEnabled(false);
    }

    /** Refresh controls visibility */
    private void refresh() {
        if (project == null) {
            setEnabled(false);
            return;
        }

        setEnabled(project.hasProperty("isGitRepository"));
    }

    @Override
    public void onFolderRefreshed(FolderRefreshedEvent event) {
        refresh();
    }

}
