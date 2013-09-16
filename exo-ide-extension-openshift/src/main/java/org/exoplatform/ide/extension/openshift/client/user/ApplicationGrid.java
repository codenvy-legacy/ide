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
package org.exoplatform.ide.extension.openshift.client.user;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HTML;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 14, 2011 11:11:34 AM anya $
 */
public class ApplicationGrid extends ListGrid<AppInfo> {
    private static final String ID = "ideApplicationGrid";

    /** Column for deleting user's applications. */
    private Column<AppInfo, String> deleteAppColumn;

    public ApplicationGrid() {
        setID(ID);
        initColumns();

        HTML emptyTable = new HTML(OpenShiftExtension.LOCALIZATION_CONSTANT.createAppForView());
        emptyTable.getElement().getStyle().setMarginBottom(2, Style.Unit.PX);
        getCellTable().setEmptyTableWidget(emptyTable);

        setValue(Collections.<AppInfo>emptyList());
    }

    /** Initialize columns. */
    private void initColumns() {
        Column<AppInfo, String> appColumn = new Column<AppInfo, String>(new TextCell()) {

            @Override
            public String getValue(AppInfo application) {
                return application.getName();
            }
        };

        deleteAppColumn = new Column<AppInfo, String>(new Link()) {
            @Override
            public String getValue(AppInfo object) {
                return "Delete";
            }
        };

        getCellTable().addColumn(appColumn, OpenShiftExtension.LOCALIZATION_CONSTANT.userInfoViewApplications());
        getCellTable().setColumnWidth(appColumn, "100%");
        getCellTable().addColumn(deleteAppColumn, "Delete");
        getCellTable().setColumnWidth(deleteAppColumn, "30");
    }

    /**
     * Handler for deleting applications.
     *
     * @param handler
     * @return
     */
    public HandlerRegistration addDeleteButtonSelectionHandler(final SelectionHandler<AppInfo> handler) {
        deleteAppColumn.setFieldUpdater(new FieldUpdater<AppInfo, String>() {

            @Override
            public void update(int index, AppInfo object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
        return null;
    }

    /** Implementation of {@link SelectionEvent} event. */
    private class SelectionEventImpl extends SelectionEvent<AppInfo> {
        /**
         * @param selectedItem
         *         selected application
         */
        protected SelectionEventImpl(AppInfo selectedItem) {
            super(selectedItem);
        }

    }

    /** @see org.exoplatform.gwtframework.ui.client.component.ListGrid#setValue(java.util.List) */
    @Override
    public void setValue(List<AppInfo> value) {
        super.setValue(value);
        if (value != null && value.size() > 0) {
            selectItem(getValue().get(0));
            updateGrid();
        }
    }

    /** Cell for clicking to delete application. */
    private class Link extends ClickableTextCell {
        /**
         * @see com.google.gwt.cell.client.ClickableTextCell#render(com.google.gwt.cell.client.Cell.Context,
         *      com.google.gwt.safehtml.shared.SafeHtml, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
         */
        @Override
        protected void render(com.google.gwt.cell.client.Cell.Context context, final SafeHtml value, SafeHtmlBuilder sb) {
            SafeHtml s = new SafeHtml() {
                private static final long serialVersionUID = 1L;

                @Override
                public String asString() {
                    return "<u style=\"cursor: pointer; color:##555555\">" + value.asString() + "</u>";
                }
            };
            sb.append(s);
        }
    }
}
