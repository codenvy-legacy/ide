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
    }

}
