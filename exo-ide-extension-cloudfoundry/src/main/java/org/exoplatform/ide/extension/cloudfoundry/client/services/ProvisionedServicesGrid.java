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
import org.exoplatform.ide.extension.cloudfoundry.shared.ProvisionedService;

/**
 * Grid for displaying provisioned services.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 13, 2012 5:48:28 PM anya $
 */
public class ProvisionedServicesGrid extends ListGrid<ProvisionedService> implements HasBindServiceHandler {
    private Column<ProvisionedService, String> nameColumn;

    private Column<ProvisionedService, String> bindColumn;

    public ProvisionedServicesGrid() {
        setID("eXoProvisionedServicesGrid");

        nameColumn = new Column<ProvisionedService, String>(new TextCell()) {

            @Override
            public String getValue(ProvisionedService object) {
                StringBuilder title = new StringBuilder(object.getName());
                title.append(" (").append(object.getVendor()).append(" ").append(object.getVersion()).append(")");

                return title.toString();
            }
        };

        bindColumn = new Column<ProvisionedService, String>(new ButtonCell()) {

            @Override
            public String getValue(ProvisionedService object) {
                return CloudFoundryExtension.LOCALIZATION_CONSTANT.bindButton();
            }
        };

        getCellTable().addColumn(nameColumn);
        getCellTable().addColumn(bindColumn);
        getCellTable().setColumnWidth(bindColumn, "60px");
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.services.HasBindServiceHandler#addBindServiceHandler(com.google.gwt.event
     * .logical.shared.SelectionHandler) */
    @Override
    public void addBindServiceHandler(final SelectionHandler<ProvisionedService> handler) {
        bindColumn.setFieldUpdater(new FieldUpdater<ProvisionedService, String>() {

            @Override
            public void update(int index, ProvisionedService object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }

    private class SelectionEventImpl extends SelectionEvent<ProvisionedService> {
        /** @param selectedItem */
        protected SelectionEventImpl(ProvisionedService selectedItem) {
            super(selectedItem);
        }
    }
}
