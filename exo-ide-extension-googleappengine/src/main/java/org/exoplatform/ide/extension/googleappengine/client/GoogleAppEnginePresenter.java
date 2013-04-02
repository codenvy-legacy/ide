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
public abstract class GoogleAppEnginePresenter implements VfsChangedHandler, ItemsSelectedHandler
//, ProjectOpenedHandler, ProjectClosedHandler, ActiveProjectChangedHandler
{
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
//      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
//      IDE.addHandler(ProjectClosedEvent.TYPE, this);
//      IDE.addHandler(ActiveProjectChangedEvent.TYPE, this);
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
