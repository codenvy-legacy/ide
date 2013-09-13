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
package org.exoplatform.ide.extension.openshift.client.info;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HTML;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;

import java.util.List;

/**
 * Grid for displaying application information.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 1, 2011 3:02:57 PM anya $
 */
public class ApplicationInfoListGrid extends ListGrid<Property> {
    private final String ID = "ide.OpenShift.ApplicationInfo.ListGrid";

    private final String NAME = OpenShiftExtension.LOCALIZATION_CONSTANT.applicationInfoGridNameField();

    private final String VALUE = OpenShiftExtension.LOCALIZATION_CONSTANT.applicationInfoGridValueField();

    public ApplicationInfoListGrid() {
        super();

        setID(ID);

        HTML emptyTable = new HTML(OpenShiftExtension.LOCALIZATION_CONSTANT.createAppForPropertiesView());
        emptyTable.getElement().getStyle().setMarginBottom(2, Style.Unit.PX);
        getCellTable().setEmptyTableWidget(emptyTable);

        Column<Property, SafeHtml> nameColumn = new Column<Property, SafeHtml>(new SafeHtmlCell()) {

            @Override
            public SafeHtml getValue(final Property property) {
                SafeHtml html = new SafeHtml() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String asString() {
                        return property.getName();
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

    @Override
    public void setValue(List<Property> value) {
        super.setValue(value);

        if (value.size() == 0) {
            setHeight(45);
        } else {
            setHeight(140);
        }
    }
}
