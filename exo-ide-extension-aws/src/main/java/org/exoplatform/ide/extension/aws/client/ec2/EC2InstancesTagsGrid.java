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
