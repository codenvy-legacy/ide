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
package org.exoplatform.ide.extension.cloudbees.client.info;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;

import java.util.Map.Entry;

/**
 * Grid for displaying application information.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 1, 2011 3:02:57 PM anya $
 */
public class ApplicationInfoGrid extends ListGrid<Entry<String, String>> {
    private final String ID = "ideCloudBeesApplicationInfoGrid";

    private final String NAME = CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridNameField();

    private final String VALUE = CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridValueField();

    public ApplicationInfoGrid() {
        super();

        setID(ID);

        Column<Entry<String, String>, SafeHtml> nameColumn =
                new Column<Entry<String, String>, SafeHtml>(new SafeHtmlCell()) {

                    @Override
                    public SafeHtml getValue(final Entry<String, String> entry) {
                        SafeHtml html = new SafeHtml() {
                            private static final long serialVersionUID = 1L;

                            @Override
                            public String asString() {
                                return "<b>" + entry.getKey().toUpperCase() + "</b>";
                            }
                        };
                        return html;
                    }
                };

        Column<Entry<String, String>, SafeHtml> valueColumn =
                new Column<Entry<String, String>, SafeHtml>(new SafeHtmlCell()) {

                    @Override
                    public SafeHtml getValue(final Entry<String, String> entry) {
                        SafeHtml html = new SafeHtml() {
                            private static final long serialVersionUID = 1L;

                            @Override
                            public String asString() {
                                if ("url".equalsIgnoreCase(entry.getKey())) {
                                    return "<a href =\"" + entry.getValue() + "\" target=\"_blank\">" + entry.getValue() + "</a>";
                                }
                                return entry.getValue();
                            }
                        };
                        return html;
                    }
                };

        getCellTable().addColumn(nameColumn, NAME);
        getCellTable().setColumnWidth(nameColumn, "35%");
        getCellTable().addColumn(valueColumn, VALUE);
        getCellTable().setColumnWidth(valueColumn, "65%");
    }
}
