/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.aws.client.ec2;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;

import java.util.List;
import java.util.Map.Entry;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: EC2InstancesTagsGrid.java Sep 21, 2012 3:07:04 PM azatsarynnyy $
 */
public class EC2InstancesTagsGrid extends ListGrid<Entry<String, String>> {
    private static final String ID = "ideEC2IntancesTagsGrid";

    public EC2InstancesTagsGrid() {
        setID(ID);
        initColumns();
    }

    /** Initialize columns. */
    private void initColumns() {
        Column<Entry<String, String>, String> keyCol = new Column<Entry<String, String>, String>(new TextCell()) {
            @Override
            public String getValue(Entry<String, String> tag) {
                return tag.getKey();
            }
        };

        Column<Entry<String, String>, String> valueCol = new Column<Entry<String, String>, String>(new TextCell()) {
            @Override
            public String getValue(Entry<String, String> tag) {
                return tag.getValue();
            }
        };

        getCellTable().addColumn(keyCol, "Key");
        getCellTable().setColumnWidth(keyCol, 300, Unit.PX);
        getCellTable().addColumn(valueCol, "Value");
    }

    /** @see org.exoplatform.gwtframework.ui.client.component.ListGrid#setValue(java.util.List) */
    @Override
    public void setValue(List<Entry<String, String>> value) {
        super.setValue(value);
        if (value != null && value.size() > 0) {
            selectItem(value.get(0));
        }
        getCellTable().redraw();
    }

}
