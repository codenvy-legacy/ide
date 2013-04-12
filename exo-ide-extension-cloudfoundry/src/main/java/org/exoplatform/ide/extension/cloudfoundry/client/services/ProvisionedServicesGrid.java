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
