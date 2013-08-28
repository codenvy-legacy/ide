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

package org.exoplatform.ide.extension.java.client.datasource;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.java.shared.DataSourceOptions;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class DatasourceListGrid extends ListGrid<DataSourceOptions> {

    public DatasourceListGrid() {
        Column<DataSourceOptions, String> nameColumn = new Column<DataSourceOptions, String>(new TextCell()) {
            @Override
            public String getValue(DataSourceOptions datasource) {
                if (datasource.getName() == null) {
                    return "";
                } else {
                    return datasource.getName();                    
                }
            }
        };

        nameColumn.setCellStyleNames("default-cursor");
        getCellTable().addColumn(nameColumn);        
        getCellTable().setColumnWidth(0, "100%");
    }
    
    public void redraw() {
        getCellTable().redraw();
    }

}
