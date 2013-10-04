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
package org.exoplatform.ide.git.client.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedEvent;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedHandler;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

/**
 * The common control for working with Git.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 15, 2011 10:06:58 AM anya $
 */
public abstract class GitControl extends SimpleControl implements IDEControl, VfsChangedHandler,
                                                      ProjectOpenedHandler, ProjectClosedHandler, TreeRefreshedHandler {

    enum EnableState {
        BEFORE_INIT,
        AFTER_INIT;
    }

    /**
     * Current Virtual File System
     */
    protected VirtualFileSystemInfo vfsInfo;

    /**
     * Variable, which indicated, when control must be enabled: before initializing the git repository or after.
     * <p/>
     * IDE-1252
     */
    protected EnableState           enableState = EnableState.AFTER_INIT;

    protected ProjectModel          project;

    protected boolean               isProjectExplorerVisible;

    /**
     * @param id control's id
     */
    public GitControl(String id) {
        super(id);
    }

    /**
     * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
     */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfsInfo = event.getVfsInfo();
        updateControlState();
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
     */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        project = event.getProject();
        updateControlState();
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
     */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        project = null;
        updateControlState();
    }


    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(TreeRefreshedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        updateControlState();
    }


    /**
     * Set the state, where control must be enabled: before initializing repository or after.
     * <p/>
     * IDE-1252
     * 
     * @param enableState
     */
    public void setEnableState(EnableState enableState) {
        this.enableState = enableState;
    }

    @Override
    public void onTreeRefreshed(TreeRefreshedEvent event) {
        updateControlState();
    }

    protected void updateControlState() {
        if (vfsInfo == null || project == null) {
            setEnabled(false);
            setVisible(false);
            return;
        }

        setVisible(true);

        if (project.getProperty(GitExtension.GIT_REPOSITORY_PROP) == null) {
            if (enableState == EnableState.BEFORE_INIT) {
                setEnabled(true);
            } else {
                setEnabled(false);
            }
        } else {
            if (enableState == EnableState.BEFORE_INIT || (IDE.currentWorkspace.isTemporary())
                && super.getId().equals(GitExtension.MESSAGES.projectReadOnlyGitUrlId())) {
                setEnabled(false);
            } else {
                setEnabled(true);
            }
        }
    }

}
