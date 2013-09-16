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
package org.exoplatform.ide.extension.appfog.client.services;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;
import org.exoplatform.ide.extension.appfog.shared.AppfogProvisionedService;

/**
 * Grid for displaying provisioned services.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ProvisionedServicesGrid extends ListGrid<AppfogProvisionedService> implements HasBindServiceHandler {
    private Column<AppfogProvisionedService, String> nameColumn;

    private Column<AppfogProvisionedService, String> bindColumn;

    public ProvisionedServicesGrid() {
        setID("eXoProvisionedServicesGrid");

        nameColumn = new Column<AppfogProvisionedService, String>(new TextCell()) {

            @Override
            public String getValue(AppfogProvisionedService object) {
                StringBuilder title = new StringBuilder(object.getName());
                title.append(" (").append(object.getVendor()).append(" ").append(object.getVersion()).append(")");

                return title.toString();
            }
        };

        bindColumn = new Column<AppfogProvisionedService, String>(new ButtonCell()) {

            @Override
            public String getValue(AppfogProvisionedService object) {
                return AppfogExtension.LOCALIZATION_CONSTANT.bindButton();
            }
        };

        getCellTable().addColumn(nameColumn);
        getCellTable().addColumn(bindColumn);
        getCellTable().setColumnWidth(bindColumn, "60px");
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.services.HasBindServiceHandler#addBindServiceHandler(com.google.gwt.event
     * .logical.shared.SelectionHandler) */
    @Override
    public void addBindServiceHandler(final SelectionHandler<AppfogProvisionedService> handler) {
        bindColumn.setFieldUpdater(new FieldUpdater<AppfogProvisionedService, String>() {

            @Override
            public void update(int index, AppfogProvisionedService object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
    }

    private class SelectionEventImpl extends SelectionEvent<AppfogProvisionedService> {
        /** @param selectedItem */
        protected SelectionEventImpl(AppfogProvisionedService selectedItem) {
            super(selectedItem);
        }
    }
}
