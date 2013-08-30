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

package org.exoplatform.ide.client.theme;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ThemesListGrid extends ListGrid<Theme> {

    public ThemesListGrid() {
        Column<Theme, SafeHtml> themeNameColumn = new Column<Theme, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(final Theme theme) {
                @SuppressWarnings("serial")
                SafeHtml html = new SafeHtml() {
                    @Override
                    public String asString() {
                        if (theme.isActive()) {
                            return "<span style=\"color:#3764A3;\">" + theme.getName() + "&nbsp;&nbsp;[Active]</span>";
                        } else {
                            return theme.getName();
                        }
                    }
                };
                return html;
            }

        };

        themeNameColumn.setCellStyleNames("default-cursor ide-table-row-text");
        getCellTable().addColumn(themeNameColumn, "Theme");
    }

}
