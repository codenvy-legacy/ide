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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

import org.exoplatform.gwtframework.ui.client.CellTableResource;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ListGrid} represents panel with cell table.
 * <p/>
 * Replaced default css style by custom.
 * <p/>
 * CellTable is decorated by FlowPanel, because it is need
 * to set size of table widget and add scrolling to view all table.
 *
 * @param <T>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ListGrid.java Mar 14, 2011 5:49:14 PM vereshchaka $
 */
public class ListGrid<T> extends FlowPanel implements ListGridItem<T> {

    private final static int WIDTH = 200;

    private final static Integer DEFAULT_PAGE_SIZE = 20;

    private CellTableResource resources = GWT.create(CellTableResource.class);

    CellTable<T> cellTable;

    protected List<T> items;

    private SelectionModel<T> selectionModel;

    private ListHandler<T> columnSortHandler;

    public ListGrid() {
        cellTable = new CellTable<T>(DEFAULT_PAGE_SIZE, resources);
        cellTable.setWidth("100%");
        selectionModel = new SingleSelectionModel<T>();
        cellTable.setSelectionModel(selectionModel);
        cellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
        super.setWidth(WIDTH + "px");
        //super.setHeight("auto");
        super.setStyleName(resources.cellTableStyle().cellTableBox(), true);
        super.add(cellTable);
    }

    //---------- API -----------------

    /**
     * Get the cell table element.
     *
     * @return {@link CellTable}
     */
    public CellTable<T> getCellTable() {
        return cellTable;
    }

    /**
     * Set the cell table id.
     *
     * @param id
     *         - new id
     */
    public void setID(String id) {
        DOM.setElementProperty(cellTable.getElement(), "id", id);
    }

    /**
     * Sets new ID of this ListGrid.
     *
     * @param id
     */
    public void setListGridId(String id) {
        setID(id);
    }

    /**
     * Set the width of table in pixels.
     *
     * @param width
     *         - the width
     */
    public void setWidth(int width) {
        super.setWidth(width + "px");
    }

    /**
     * Set the height of table in pixels.
     * <p/>
     * If size of table if more, than size of
     * panel, scrolling will appear.
     *
     * @param height
     *         - the height
     */
    public void setHeight(int height) {
        super.setHeight(height + "px");
    }

    /**
     * Get selected items.
     *
     * @return list of selected items
     */
    public List<T> getSelectedItems() {
        List<T> selectedItems = new ArrayList<T>();

        if (items != null) {
            for (T item : items) {
                if (getCellTable().getSelectionModel().isSelected(item)) {
                    selectedItems.add(item);
                }
            }            
        }

        return selectedItems;
    }

    /**
     * Select item in list grid.
     *
     * @param item
     *         - the item to select
     */
    public void selectItem(T item) {
        getCellTable().getSelectionModel().setSelected(item, true);
    }

    //---------- Has methods and handlers -----------------

    /** @see com.google.gwt.user.client.ui.HasValue#getValue() */
    public List<T> getValue() {
        return items;
    }

    /**
     * Set table data and initialize column sort handler
     *
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
     */
    public void setValue(List<T> value) {
        this.items = value;

        updateGrid();

        columnSortHandler = new ListHandler<T>(value) {
            @Override
            public void onColumnSort(ColumnSortEvent event) {
                super.onColumnSort(event);

                setValue(getValue());

                getCellTable().redraw();
            }
        };

        getCellTable().addColumnSortHandler(columnSortHandler);
    }

    /**
     * Add table data and initialize column sort handler
     *
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
     */
    public void addItems(List<T> value) {
        if (this.items == null) {
            this.items = value;
        } else {
            this.items.addAll(value);
        }
        updateGrid();

        columnSortHandler = new ListHandler<T>(value) {
            @Override
            public void onColumnSort(ColumnSortEvent event) {
                super.onColumnSort(event);

                setValue(getValue());

                getCellTable().redraw();
            }
        };

        getCellTable().addColumnSortHandler(columnSortHandler);
    }

    /** @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean) */
    public void setValue(List<T> value, boolean fireEvents) {
        setValue(value);
    }

    /**
     * Get handler of column sorting
     *
     * @return
     */
    protected ListHandler<T> getColumnSortHandler() {
        return columnSortHandler;
    }

    /** @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared
     * .ValueChangeHandler) */
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<T>> handler) {
        return cellTable.addHandler(handler, ValueChangeEvent.getType());
    }

    /** @see com.google.gwt.event.logical.shared.HasSelectionHandlers#addSelectionHandler(com.google.gwt.event.logical.shared
     * .SelectionHandler) */
    public HandlerRegistration addSelectionHandler(final SelectionHandler<T> handler) {
        return selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                handler.onSelection(new SelectedEventImpl<T>(((SingleSelectionModel<T>)selectionModel).getSelectedObject()));
            }
        });
    }

    /** @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler) */
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return cellTable.addDomHandler(handler, ClickEvent.getType());
    }

    /** @see com.google.gwt.event.dom.client.HasDoubleClickHandlers#addDoubleClickHandler(com.google.gwt.event.dom.client
     * .DoubleClickHandler) */
    public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
        return cellTable.addDomHandler(handler, DoubleClickEvent.getType());
    }

    //---------- Implementation ------------------

    /** Update value of <code>cellTable</code> from <code>items</code> list. */
    protected void updateGrid() {
        cellTable.setRowData(items);
    }

    /**
     * Remove header tag "thead" from cell table
     *
     * @param cellTable
     */
    protected void removeTableHeader() {
        if (cellTable.getElement().getElementsByTagName("thead") != null) {
            cellTable.getElement().getElementsByTagName("thead").getItem(0).removeFromParent();
        }
    }

}
