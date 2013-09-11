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
package org.exoplatform.ide.extension.appfog.client.url;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;

/**
 * Grid for displaying registered URLs for application.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class RegisteredUrlsGrid extends ListGrid<String> implements HasUnmapClickHandler {
    private final String ID = "ideAppfogUnmapUrlGrid";

    private final String URL = AppfogExtension.LOCALIZATION_CONSTANT.applicationUnmapUrlGridUrlField();

    private final String UNMAP_BUTTON_TITLE = AppfogExtension.LOCALIZATION_CONSTANT.unmapButton();

    private final String UNMAP_COLUMN_HEADER = AppfogExtension.LOCALIZATION_CONSTANT.unmapUrlListGridColumnTitle();

    private Column<String, String> buttonColumn;

    public RegisteredUrlsGrid() {
        super();

        setID(ID);

        buttonColumn = new Column<String, String>(new ButtonCell()) {
            @Override
            public String getValue(String object) {
                return UNMAP_BUTTON_TITLE;
            }
        };

        Column<String, SafeHtml> valueColumn = new Column<String, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(final String url) {
                SafeHtml html = new SafeHtml() {
                    private static final long serialVersionUID = 1L;

                    public String asString() {
                        return "<a target=\"_blank\" href=\"http://" + url + "\">" + url + "</a>";
                    }
                };
                return html;
            }
        };

        getCellTable().addColumn(valueColumn, URL);
        getCellTable().setColumnWidth(valueColumn, "75%");
        getCellTable().addColumn(buttonColumn, UNMAP_COLUMN_HEADER);
        getCellTable().setColumnWidth(buttonColumn, "25%");
    }

    @Override
    public void addUnmapClickHandler(final UnmapHandler handler) {
        buttonColumn.setFieldUpdater(new FieldUpdater<String, String>() {
            @Override
            public void update(int index, String url, String value) {
                handler.onUnmapUrl(url);
            }
        });
    }
}
