/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.ext.openshift.client.info;

import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionModel;
import com.google.inject.Inject;

import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ApplicationInfoViewImpl extends DialogBox implements ApplicationInfoView {

    interface ApplicationInfoViewImplUiBinder extends UiBinder<Widget, ApplicationInfoViewImpl> {
    }

    private static ApplicationInfoViewImplUiBinder uiBinder = GWT.create(ApplicationInfoViewImplUiBinder.class);

    @UiField
    CellTable<ApplicationProperty> properties;

    @UiField
    Button btnClose;

    @UiField(provided = true)
    final OpenShiftLocalizationConstant constant;

    private ActionDelegate delegate;

    private boolean isShown;

    @Inject
    protected ApplicationInfoViewImpl(OpenShiftLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setTitle(constant.applicationInfoViewTitle());
        this.setWidget(widget);

        initPropertiesTable();
    }

    private void initPropertiesTable() {
        properties = new CellTable<ApplicationProperty>();
        properties.setWidth("100%", true);
        properties.setAutoHeaderRefreshDisabled(true);
        properties.setAutoFooterRefreshDisabled(true);

        final SelectionModel<ApplicationProperty> selectionModel = new NoSelectionModel<ApplicationProperty>();

        properties.setSelectionModel(selectionModel);

        Column<ApplicationProperty, String> propertyKeyColumn = new TextColumn<ApplicationProperty>() {
            @Override
            public String getValue(ApplicationProperty object) {
                return object.getPropertyName();
            }
        };

        Column<ApplicationProperty, String> propertyValueColumn = new TextColumn<ApplicationProperty>() {
            @Override
            public String getValue(ApplicationProperty object) {
                return object.getPropertyValue();
            }
        };

        properties.addColumn(propertyKeyColumn, constant.applicationInfoViewPropertyNameColumn());
        properties.addColumn(propertyValueColumn, constant.applicationInfoViewPropertyValueColumn());
    }

    @Override
    public void setApplicationProperties(List<ApplicationProperty> properties) {
        this.properties.setRowData(properties);
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

    @Override
    public void close() {
        this.isShown = false;
        this.hide();
    }

    @Override
    public void showDialog() {
        this.isShown = true;
        this.center();
        this.show();
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnClose")
    public void onCloseButtonClick(ClickEvent event) {
        delegate.onCloseClicked();
    }
}
