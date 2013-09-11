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
package org.exoplatform.ide.client.project.explorer;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Grid for displaying projects list.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ProjectsListGrid.java Apr 23, 2012 4:52:55 PM azatsarynnyy $
 */

public class ProjectsListGrid extends ListGrid<ProjectModel> {

    public ProjectsListGrid() {
        Column<ProjectModel, ImageResource> iconColumn = new Column<ProjectModel, ImageResource>(new ImageResourceCell()) {
            @Override
            public ImageResource getValue(ProjectModel object) {
                return ProjectResolver.getImageForProject(object.getProjectType());
            }
        };

        Column<ProjectModel, String> nameColumn = new Column<ProjectModel, String>(new TextCell()) {
            @Override
            public String getValue(ProjectModel object) {
                return object.getName();
            }
        };

        getCellTable().addColumn(iconColumn);
        getCellTable().setColumnWidth(iconColumn, "20px");

        nameColumn.setCellStyleNames("default-cursor");
        getCellTable().addColumn(nameColumn);
        setListGridId("ideProjectExplorerProjectsListGrid");
    }

}
