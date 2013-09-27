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
package org.exoplatform.ide.git.client.reset;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;

/**
 * Grid for displaying git files.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 12, 2011 5:05:49 PM anya $
 */
public class IndexFilesGrid extends ListGrid<IndexFile> {
    /** Grid's ID. */
    private static final String ID    = "ideIndexFilesGrid";

    private final String        FILES = "Files for commit";

    /** Files column. */
    Column<IndexFile, String>   filesColumn;

    /** Column with checkboxes. */
    Column<IndexFile, Boolean>  checkColumn;

    public IndexFilesGrid() {
        super();
        setID(ID);
        this.setHeight("auto");
        initColumns();
    }

    /** Initialize the columns of the grid. */
    private void initColumns() {
        CellTable<IndexFile> cellTable = getCellTable();

        // Create files column:
        filesColumn = new Column<IndexFile, String>(new TextCell()) {
            @Override
            public String getValue(IndexFile file) {
                return file.getPath();
            }
        };

        // Create column with checkboxes:
        checkColumn = new Column<IndexFile, Boolean>(new CheckboxCell(false, true)) {

            @Override
            public Boolean getValue(IndexFile file) {
                return !file.isIndexed();
            }

        };

        // Create bean value updater:
        FieldUpdater<IndexFile, Boolean> checkFieldUpdater = new FieldUpdater<IndexFile, Boolean>() {

            @Override
            public void update(int index, IndexFile file, Boolean value) {
                file.setIndexed(!value);
            }
        };

        checkColumn.setFieldUpdater(checkFieldUpdater);

        filesColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

        cellTable.addColumn(checkColumn, new SafeHtml() {
            private static final long serialVersionUID = 1L;

            @Override
            public String asString() {
                return "&nbsp;";
            }
        });
        cellTable.setColumnWidth(checkColumn, 1, Unit.PCT);

        cellTable.addColumn(filesColumn, FILES);
        cellTable.setColumnWidth(filesColumn, 35, Unit.PCT);
    }
}
