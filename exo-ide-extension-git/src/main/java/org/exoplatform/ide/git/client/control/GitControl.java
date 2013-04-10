/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.git.client.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
<<<<<<< Updated upstream
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
=======
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedEvent;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.PackageExplorerDisplay;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
>>>>>>> Stashed changes
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedEvent;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedHandler;
import org.exoplatform.ide.git.client.GitExtension;
<<<<<<< Updated upstream
import org.exoplatform.ide.vfs.client.model.ProjectModel;
=======
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
>>>>>>> Stashed changes
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
        BEFORE_INIT, AFTER_INIT;
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
    protected EnableState enableState = EnableState.AFTER_INIT;

    protected ProjectModel project;

    protected boolean isProjectExplorerVisible;

    /**
     * @param id
     *         control's id
     */
    public GitControl(String id) {
        super(id);
    }
    
    /**
     * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
     */
    @Override
<<<<<<< Updated upstream
    public void onVfsChanged(VfsChangedEvent event) {
        vfsInfo = event.getVfsInfo();
        updateControlState();
=======
    public void onItemsSelected(ItemsSelectedEvent event) {
        if (event.getSelectedItems().size() != 1) {
            selectedItem = null;
            updateControlState();
        } else {
            selectedItem = event.getSelectedItems().get(0);
            updateControlState();
        }
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
=======
    /** @see org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler#onFolderRefreshed(org.exoplatform.ide.client
     * .framework.navigation.event.FolderRefreshedEvent) */
    @Override
    public void onFolderRefreshed(FolderRefreshedEvent event) {
        updateControlState();
    }
    
>>>>>>> Stashed changes
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
            if (enableState == EnableState.BEFORE_INIT) {
                setEnabled(false);
            } else {
                setEnabled(true);
            }
        }
    }
<<<<<<< Updated upstream
 
=======

>>>>>>> Stashed changes
}
