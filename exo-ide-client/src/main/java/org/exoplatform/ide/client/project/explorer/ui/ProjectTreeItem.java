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
package org.exoplatform.ide.client.project.explorer.ui;

import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.ide.client.framework.project.api.IDEProject;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class ProjectTreeItem extends FolderTreeItem {

    public ProjectTreeItem(IDEProject project) {
        super(project);
    }
    
    @Override
    protected ImageResource getItemIcon() {
        IDEProject project = (IDEProject)getUserObject();
        return ProjectResolver.getImageForProject(project.getProjectType());
    }

    protected ProjectExplorerTreeItem createTreeItem(Item item) {
        if (item instanceof IDEProject) {
            return new ProjectTreeItem((IDEProject)item);
        }
        
        return super.createTreeItem(item);
    }
    
}
