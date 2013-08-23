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

package org.exoplatform.ide.client.project.properties;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.vfs.shared.Property;

import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class PropertiesListGrid extends ListGrid<Property> {

    public PropertiesListGrid() {

        Column<Property, String> nameColumn = new Column<Property, String>(new TextCell()) {
            @Override
            public String getValue(Property object) {
                return PropertyUtil.getHumanReadableName(object.getName());
            }
        };

        Column<Property, String> valueColumn = new Column<Property, String>(new TextCell()) {
            @Override
            public String getValue(Property object) {
                String value = "";
                List values = object.getValue();
                for (Object v : values) {
                    if (!value.isEmpty()) {
                        value += "<br>";
                    }

                    value += v;
                }

                return value;
            }
        };

        nameColumn.setCellStyleNames("default-cursor");
        valueColumn.setCellStyleNames("default-cursor");

        getCellTable().addColumn(nameColumn, "Name");
        getCellTable().addColumn(valueColumn, "Value");
    }

}
