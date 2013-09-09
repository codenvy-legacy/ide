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
package org.exoplatform.ide.extension.heroku.client.info;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.client.marshaller.Property;

/**
 * Grid for displaying application information.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 1, 2011 3:02:57 PM anya $
 */
public class ApplicationInfoGrid extends ListGrid<Property> {
    private final String ID = "ideApplicationInfoGrid";

    private final String NAME = HerokuExtension.LOCALIZATION_CONSTANT.applicationInfoGridNameField();

    private final String VALUE = HerokuExtension.LOCALIZATION_CONSTANT.applicationInfoGridValueField();

    public ApplicationInfoGrid() {
        super();

        setID(ID);

        Column<Property, SafeHtml> nameColumn = new Column<Property, SafeHtml>(new SafeHtmlCell()) {

            @Override
            public SafeHtml getValue(final Property property) {
                SafeHtml html = new SafeHtml() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String asString() {
                        return "<b>" + property.getName() + "</b>";
                    }
                };
                return html;
            }
        };

        Column<Property, SafeHtml> valueColumn = new Column<Property, SafeHtml>(new SafeHtmlCell()) {

            @Override
            public SafeHtml getValue(final Property property) {
                SafeHtml html = new SafeHtml() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String asString() {
                        return property.getValue();
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
