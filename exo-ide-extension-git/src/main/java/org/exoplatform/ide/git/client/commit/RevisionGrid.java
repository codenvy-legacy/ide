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
package org.exoplatform.ide.git.client.commit;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.shared.Revision;

import java.util.Date;
import java.util.List;

/**
 * Grid for displaying revisions' info (date, commiter, comment.)
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 15, 2011 11:26:49 AM anya $
 */
public class RevisionGrid extends ListGrid<Revision> {
    /** Grid's ID. */
    private static final String ID       = "ideRevisionGrid";

    /** Date column's name. */
    private final String        DATE     = GitExtension.MESSAGES.commitGridDate();

    /** Commiter column's name. */
    private final String        COMMITER = GitExtension.MESSAGES.commitGridCommiter();

    /** Comment column's name. */
    private final String        COMMENT  = GitExtension.MESSAGES.commitGridComment();

    /** Date column. */
    Column<Revision, String>    dateColumn;

    /** Commiter column. */
    Column<Revision, String>    commiterColumn;

    /** Comment column. */
    Column<Revision, String>    commentColumn;

    public RevisionGrid() {
        super();
        setID(ID);
        initColumns();
    }

    /** Initialize the columns of the grid. */
    private void initColumns() {
        CellTable<Revision> cellTable = getCellTable();

        dateColumn = new Column<Revision, String>(new TextCell()) {

            @Override
            public String getValue(Revision revision) {
                return DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(
                                                                                          new Date(revision.getCommitTime()));
            }
        };

        commiterColumn = new Column<Revision, String>(new TextCell()) {

            @Override
            public String getValue(Revision revision) {
                if (revision.getCommitter() == null) {
                    return "";
                }
                return revision.getCommitter().getName();
            }

        };

        commentColumn = new Column<Revision, String>(new TextCell()) {

            @Override
            public String getValue(Revision revision) {
                return revision.getMessage();
            }

        };

        cellTable.addColumn(dateColumn, DATE);
        cellTable.setColumnWidth(dateColumn, "20%");
        cellTable.addColumn(commiterColumn, COMMITER);
        cellTable.setColumnWidth(commiterColumn, "30%");
        cellTable.addColumn(commentColumn, COMMENT);
        cellTable.setColumnWidth(commentColumn, "50%");
    }

    /** @see org.exoplatform.gwtframework.ui.client.component.ListGrid#setValue(java.util.List) */
    @Override
    public void setValue(List<Revision> value) {
        super.setValue(value);
        if (value != null && value.size() > 0) {
            selectItem(value.get(0));
            updateGrid();
        }
    }

    /**
     * Returns selected revision in grid.
     * 
     * @return {@link Revision} selected revision
     */
    public Revision getSelectedRevision() {
        return super.getSelectedItems().get(0);
    }
}
