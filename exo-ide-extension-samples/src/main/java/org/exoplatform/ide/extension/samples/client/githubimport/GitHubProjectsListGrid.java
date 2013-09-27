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
package org.exoplatform.ide.extension.samples.client.githubimport;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.extension.samples.client.github.load.ProjectData;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SamplesListGrid.java Aug 30, 2011 11:43:59 AM vereshchaka $
 */
public class GitHubProjectsListGrid extends ListGrid<ProjectData> {
    private static final String ID = "ideGithubProjectsGrid";

    private static final String REPOSITORY_HEADER = SamplesExtension.LOCALIZATION_CONSTANT.samplesListRepositoryColumn();

    private static final String DESCRIPTION_HEADER = SamplesExtension.LOCALIZATION_CONSTANT
                                                                     .samplesListDescriptionColumn();

    public GitHubProjectsListGrid() {
        super();

        setID(ID);

        // Image column
        Column<ProjectData, ImageResource> iconColumn = new Column<ProjectData, ImageResource>(new ImageResourceCell()) {
            @Override
            public ImageResource getValue(ProjectData item) {
                return ProjectResolver.getImageForProject(item.getType());
            }
        };

        Column<ProjectData, SafeHtml> repositoryColumn = new Column<ProjectData, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(final ProjectData item) {
                SafeHtml html = new SafeHtml() {
                    private static final long serialVersionUID = 1L;

                    public String asString() {
                        return item.getName();
                    }
                };
                return html;
            }
        };

        Column<ProjectData, SafeHtml> descriptionColumn = new Column<ProjectData, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(final ProjectData item) {
                SafeHtml html = new SafeHtml() {
                    private static final long serialVersionUID = 1L;

                    public String asString() {
                        return "<span>" + item.getDescription() + "</span>";
                    }
                };
                return html;
            }
        };

        getCellTable().addColumn(iconColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        getCellTable().setColumnWidth(iconColumn, 28, Unit.PX);

        getCellTable().addColumn(repositoryColumn, REPOSITORY_HEADER);
        getCellTable().addColumn(descriptionColumn, DESCRIPTION_HEADER);
    }

}
