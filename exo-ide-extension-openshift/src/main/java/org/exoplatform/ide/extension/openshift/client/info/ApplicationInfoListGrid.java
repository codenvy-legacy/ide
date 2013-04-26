/*
 * Copyright (C) 2011 eXo Platform SAS.
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
