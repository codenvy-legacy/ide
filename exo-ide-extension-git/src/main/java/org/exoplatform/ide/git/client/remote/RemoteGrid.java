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
package org.exoplatform.ide.git.client.remote;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.shared.Remote;

import java.util.List;

/**
 * Grid to display remote repositories info.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 18, 2011 12:09:09 PM anya $
 */
public class RemoteGrid extends ListGrid<Remote> {
    /** Grid's ID. */
    private static final String ID = "ideRemoteGrid";

    /** Name column. */
    Column<Remote, String>      nameColumn;

    /** Location column. */
    Column<Remote, String>      urlColumn;

    public RemoteGrid() {
        super();
        setID(ID);
        initColumns();
    }

    /** Initialize the columns of the grid. */
    private void initColumns() {
        CellTable<Remote> cellTable = getCellTable();

        nameColumn = new Column<Remote, String>(new TextCell()) {

            @Override
            public String getValue(Remote remote) {
                return remote.getName();
            }

        };

        urlColumn = new Column<Remote, String>(new TextCell()) {

            @Override
            public String getValue(Remote remote) {
                return remote.getUrl();
            }

        };

        cellTable.addColumn(nameColumn, GitExtension.MESSAGES.remoteGridNameField());
        cellTable.setColumnWidth(nameColumn, "20%");
        cellTable.addColumn(urlColumn, GitExtension.MESSAGES.remoteGridLocationField());
        cellTable.setColumnWidth(urlColumn, "80%");
    }

    /** @see org.exoplatform.gwtframework.ui.client.component.ListGrid#setValue(java.util.List) */
    @Override
    public void setValue(List<Remote> value) {
        super.setValue(value);
        if (value != null && value.size() > 0) {
            selectItem(value.get(0));
            updateGrid();
        }
    }

    /**
     * Returns selected remote repository in grid.
     * 
     * @return {@link Remote} selected remote repository
     */
    public Remote getSelectedRemote() {
        return super.getSelectedItems().get(0);
    }
}
