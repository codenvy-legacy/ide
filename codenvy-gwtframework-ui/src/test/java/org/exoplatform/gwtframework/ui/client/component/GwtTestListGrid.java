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
package org.exoplatform.gwtframework.ui.client.component;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class GwtTestListGrid extends GwtComponentTest {

    private static final String ID_LIST_GRID = "CELL_TABLE";

    private String innerHtml;

    private ListGrid<Employee> listGrid;

    private List<Employee> values;

    private boolean dbClick;

    private boolean fieldUpdated;

    private Column<Employee, Boolean> checkColumn;

    private Column<Employee, String> firstNameColumn;

    class Employee {
        String name;

        boolean isMale;

        public Employee(String name, boolean isMale) {
            this.name = name;
            this.isMale = isMale;
        }
    }

    protected void gwtSetUp() throws Exception {
        //create data for list grid
        values = new ArrayList<Employee>();
        values.add(new Employee("Nina", false));
        values.add(new Employee("John", true));
        values.add(new Employee("Liza", false));

        createListGrid();
    }

    /** Test, that creates new ListGrid element and add it to root panel. */
    public void testCreateListGrid() {
        createSimpleColumns();

        //ass list grid to panel
        RootPanel.get().add(listGrid);

        innerHtml = Document.get().getBody().getInnerHTML();
        assertTrue(innerHtml.contains("table"));
        assertTrue(innerHtml.contains(ID_LIST_GRID));
        assertEquals(values.size(), listGrid.getCellTable().getRowCount());
        assertEquals(2, listGrid.getCellTable().getColumnCount());

        final Element tableElement = Document.get().getBody().getElementsByTagName("table").getItem(0);

        assertEquals(ID_LIST_GRID, tableElement.getId());

    }

    /** Test, that click on row to select one item. */
    public void testListGridSelection() {
        assertTrue(listGrid != null);

        createSimpleColumns();

        //ass list grid to panel
        RootPanel.get().add(listGrid);

        listGrid.addSelectionHandler(new SelectionHandler<Employee>() {
            @Override
            public void onSelection(SelectionEvent<Employee> event) {
                assertEquals(1, listGrid.getSelectedItems().size());
                assertEquals("Nina", listGrid.getSelectedItems().get(0).name);
            }
        });

        //click on row
        listGrid.getCellTable().getRowElement(0).getCells().getItem(0)
                .dispatchEvent(Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false));

        Timer timer = new Timer() {
            public void run() {
                finishTest();
            }
        };
        delayTestFinish(510);
        timer.schedule(500);
    }

    /** Test, that does double click on row. */
    public void testListGridDoubleClick() {
        assertTrue(listGrid != null);

        createSimpleColumns();

        //ass list grid to panel
        RootPanel.get().add(listGrid);

        dbClick = false;

        listGrid.addDoubleClickHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                dbClick = true;
            }
        });

        //click on row
        listGrid.getCellTable().getRowElement(0).getCells().getItem(0)
                .dispatchEvent(Document.get().createDblClickEvent(0, 0, 0, 0, 0, false, false, false, false));

        Timer timer = new Timer() {
            public void run() {
                assertTrue(dbClick);
                finishTest();
            }
        };
        delayTestFinish(510);
        timer.schedule(500);
    }

    /**
     * Test, that creates list grid with editable column,
     * and change value in this column.
     */
    public void testListGridEditing() {
        assertTrue(listGrid != null);

        //create editable text column
        //and add field updater.
        createEditableColumns();

        //ass list grid to panel
        RootPanel.get().add(listGrid);

        //click on text cell to call text edit field
        listGrid.getCellTable().getRowElement(0).getCells().getItem(1)
                .dispatchEvent(Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false));

        //change value
        Element input =
                listGrid.getCellTable().getRowElement(0).getCells().getItem(1).getElementsByTagName("input").getItem(0);
        input.setAttribute("value", "Sasha");

        //press enter to finish editing of cell
        input.dispatchEvent(Document.get().createKeyCodeEvent("keydown", false, false, false, false, KeyCodes.KEY_ENTER));
        input.dispatchEvent(Document.get().createKeyCodeEvent("keyup", false, false, false, false, KeyCodes.KEY_ENTER));

        Timer timer = new Timer() {
            public void run() {
                assertTrue(fieldUpdated);
                assertEquals("Sasha", values.get(0).name);
                finishTest();
            }
        };
        delayTestFinish(510);
        timer.schedule(500);
    }

    /** Test, that creates list grid with sorting column. */
    public void testListGridSorting() {
        assertTrue(listGrid != null);

        TextHeader boolHeader = new TextHeader("boolean");
        TextHeader nameHeader = new TextHeader("first name");

        listGrid.getCellTable().redraw();

        // No header.
        assertEquals(0, getHeaderCount(listGrid.getCellTable()));

        TextColumn<Employee> firstColumn = new TextColumn<Employee>() {
            @Override
            public String getValue(Employee object) {
                return null;
            }
        };

        listGrid.getCellTable().addColumn(firstColumn, boolHeader);

        TextColumn<Employee> secondColumn = new TextColumn<Employee>() {
            @Override
            public String getValue(Employee object) {
                return null;
            }
        };

        listGrid.getCellTable().addColumn(secondColumn, nameHeader);

        listGrid.getCellTable().getColumn(0).setSortable(true);
        listGrid.getCellTable().getColumn(1).setSortable(true);

        ListHandler<Employee> columnSortHandler = listGrid.getColumnSortHandler();

        final ColumnSortList sortList = listGrid.getCellTable().getColumnSortList();
        assertEquals(0, sortList.size());

        columnSortHandler.setComparator(secondColumn, new Comparator<Employee>() {
            public int compare(Employee item1, Employee item2) {
                return item1.name.compareTo(item2.name);
            }
        });

        listGrid.getCellTable().redraw();
        RootPanel.get().add(listGrid);

        final Timer timer2 = new Timer() {

            @Override
            public void run() {
                assertEquals(1, sortList.size());
                assertEquals(listGrid.getCellTable().getColumn(1), sortList.get(0).getColumn());
                assertTrue(sortList.get(0).isAscending());
                finishTest();
            }
        };

        Timer timer = new Timer() {
            public void run() {
                NativeEvent click = Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false);
                getHeaderElement(listGrid.getCellTable(), 1).dispatchEvent(click);
                timer2.schedule(500);
            }
        };
        delayTestFinish(2750);
        timer.schedule(500);
    }

    //----- Implementation ----------

    /**
     * Get a column header from the table.
     *
     * @param table
     *         the {@link CellTable}
     * @param column
     *         the column index
     * @return the column header
     */
    private TableCellElement getHeaderElement(CellTable<?> table, int column) {
        TableElement tableElem = table.getElement().cast();
        TableSectionElement thead = tableElem.getTHead();
        TableRowElement tr = thead.getRows().getItem(0);
        return tr.getCells().getItem(column);
    }

    private int getHeaderCount(CellTable<?> table) {
        TableElement tableElem = table.getElement().cast();
        TableSectionElement thead = tableElem.getTHead();
        TableRowElement tr = thead.getRows().getItem(0);
        return tr.getCells().getLength();
    }

    private void createListGrid() {
        //create list grid and set data
        listGrid = new ListGrid<Employee>();
        listGrid.setValue(values);
        listGrid.setID(ID_LIST_GRID);
    }

    private void createSimpleColumns() {
        //create two columns: boolean and editable text
        checkColumn = new Column<Employee, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(Employee object) {
                return object.isMale;
            }
        };

        firstNameColumn = new Column<Employee, String>(new TextCell()) {
            @Override
            public String getValue(Employee object) {
                return object.name;
            }
        };

        TextHeader firstHeader = new TextHeader("abc");
        TextHeader secondHeader = new TextHeader("first name");

        listGrid.getCellTable().addColumn(checkColumn, firstHeader);
        listGrid.getCellTable().addColumn(firstNameColumn, secondHeader);
    }

    private void createEditableColumns() {
        //create two columns: boolean and editable text
        checkColumn = new Column<Employee, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(Employee object) {
                return object.isMale;
            }
        };

        firstNameColumn = new Column<Employee, String>(new EditTextCell()) {
            @Override
            public String getValue(Employee object) {
                return object.name;
            }
        };

        listGrid.getCellTable().addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        listGrid.getCellTable().addColumn(firstNameColumn, "first name");

        //add field updater
        firstNameColumn.setFieldUpdater(new FieldUpdater<Employee, String>() {

            public void update(int index, Employee object, String value) {
                object.name = value;
                fieldUpdated = true;
            }
        });
    }
}
