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
package org.exoplatform.ide.extension.cloudfoundry.client.services;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;

/**
 * Grid for displaying provisioned services.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 13, 2012 5:48:28 PM anya $
 */
public class BoundedServicesGrid extends ListGrid<String> implements HasUnbindServiceHandler {
    private Column<String, String> nameColumn;

    private Column<String, String> unbindColumn;

    public BoundedServicesGrid() {
        setID("eXoBoundedServicesGrid");

        nameColumn = new Column<String, String>(new TextCell()) {

            @Override
            public String getValue(String name) {
                return name;
            }
        };

        unbindColumn = new Column<String, String>(new ButtonCell()) {

            @Override
            public String getValue(String object) {
                return CloudFoundryExtension.LOCALIZATION_CONSTANT.unBindButton();
            }
        };

        getCellTable().addColumn(nameColumn);
        getCellTable().addColumn(unbindColumn);
        getCellTable().setColumnWidth(unbindColumn, "60px");
    }

    private class SelectionEventImpl extends SelectionEvent<String> {
        /** @param selectedItem */
        protected SelectionEventImpl(String selectedItem) {
            super(selectedItem);
        }
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.services.HasUnbindServiceHandler#addUnbindServiceHandler(com.google.gwt
     * .event.logical.shared.SelectionHandler) */
    @Override
    public void addUnbindServiceHandler(final SelectionHandler<String> handler) {
        unbindColumn.setFieldUpdater(new FieldUpdater<String, String>() {

            @Override
            public void update(int index, String object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }
}
