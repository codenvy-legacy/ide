/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
