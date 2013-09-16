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
package org.exoplatform.ide.client.project.create;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.template.ProjectTemplate;
import org.exoplatform.ide.client.framework.util.ProjectResolver;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Oct 25, 2011 evgen $
 */
public class ProjectTemplateGrid extends ListGrid<ProjectTemplate> {
    private Column<ProjectTemplate, String> descriptionColumn;

    private Column<ProjectTemplate, ImageResource> imageColumn;

    public ProjectTemplateGrid() {
        descriptionColumn = new Column<ProjectTemplate, String>(new TextCell()) {

            @Override
            public String getValue(ProjectTemplate projectTemplate) {
                return projectTemplate.getDescription();
            }
        };

        imageColumn = new Column<ProjectTemplate, ImageResource>(new ImageResourceCell()) {

            @Override
            public ImageResource getValue(ProjectTemplate projectTemplate) {
                return ProjectResolver.getImageForProject(ProjectType.fromValue(projectTemplate.getType()));
            }
        };

        getCellTable().addColumn(imageColumn);
        getCellTable().setColumnWidth(imageColumn, 16, Unit.PX);
        getCellTable().addColumn(descriptionColumn);
    }
}
