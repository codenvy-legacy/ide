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
package org.exoplatform.ide.client.restdiscovery.ui;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.restdiscovery.ParamExt;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class RestServiceParameterListGrid extends ListGrid<ParamExt> {

    private static final String NAME = IDE.PREFERENCES_CONSTANT.restServiceListGridNameColumn();

    private static final String TYPE = IDE.PREFERENCES_CONSTANT.restServiceListGridTypeColumn();

    private static final String DEFAULT = IDE.PREFERENCES_CONSTANT.restServiceListGridDefaultColumn();

    public RestServiceParameterListGrid() {
        initColumns();
    }

    private void initColumns() {
        // name column
        Column<ParamExt, SafeHtml> nameColumn = new Column<ParamExt, SafeHtml>(new SafeHtmlCell()) {

            @Override
            public SafeHtml getValue(final ParamExt item) {
                SafeHtml html = new SafeHtml() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String asString() {
                        if (item.getParam() == null) {
                            String title = item.getTitle();
                            title = getDivider(title);
                            return title;
                        }
                        return item.getParam().getName();
                    }
                };
                return html;
            }
        };
        getCellTable().addColumn(nameColumn, NAME);

        // type column
        Column<ParamExt, SafeHtml> typeColumn = new Column<ParamExt, SafeHtml>(new SafeHtmlCell()) {

            @Override
            public SafeHtml getValue(final ParamExt item) {
                SafeHtml html = new SafeHtml() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String asString() {
                        if (item.getParam() == null)
                            return "";
                        return item.getParam().getType().getLocalName();
                    }
                };
                return html;
            }

        };
        getCellTable().addColumn(typeColumn, TYPE);

        // column By default
        Column<ParamExt, SafeHtml> defaultColumn = new Column<ParamExt, SafeHtml>(new SafeHtmlCell()) {

            @Override
            public SafeHtml getValue(final ParamExt item) {
                SafeHtml html = new SafeHtml() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String asString() {
                        if (item.getParam() == null)
                            return "";
                        return item.getParam().getDefault();
                    }
                };
                return html;
            }

        };
        getCellTable().addColumn(defaultColumn, DEFAULT);

    }

    private String getDivider(String title) {
        return "<b><font color=\"#3764A3\">" + title + "</font></b>";
    }

}
