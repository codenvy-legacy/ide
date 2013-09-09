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
package org.exoplatform.ide.extension.googleappengine.client;

import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 17, 2012 11:49:18 AM anya $
 */
public abstract class GoogleAppEnginePresenter implements VfsChangedHandler, ItemsSelectedHandler {
    
    /** Current virtual file system. */
    protected VirtualFileSystemInfo currentVfs;

    /** Selected item in browser tree. */
    protected Item selectedItem;


    /** Current project. */
    protected ProjectModel currentProject;

    protected GoogleAppEnginePresenter() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     * .framework.navigation.event.ItemsSelectedEvent) */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        if (event.getSelectedItems().size() != 1) {
            selectedItem = null;
            return;
        } else {
            selectedItem = event.getSelectedItems().get(0);
        }
        if (selectedItem instanceof ProjectModel) {
            currentProject = (ProjectModel)selectedItem;
        } else {
            currentProject = ((ItemContext)selectedItem).getProject();
        }
    }


    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     * .application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        currentVfs = event.getVfsInfo();
    }

    /**
     * Returns, whether current project is Google App Engine application.
     *
     * @return {@link Boolean} <code>true</code> if project is App Engine application
     */
    protected boolean isAppEngineProject() {
        return GoogleAppEngineExtension.isAppEngineProject(currentProject);
    }

}
