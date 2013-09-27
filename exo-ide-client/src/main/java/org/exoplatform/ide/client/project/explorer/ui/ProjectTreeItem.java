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
