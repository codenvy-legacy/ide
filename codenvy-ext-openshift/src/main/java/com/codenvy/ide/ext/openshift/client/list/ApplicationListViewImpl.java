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
package com.codenvy.ide.ext.openshift.client.list;

import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ext.openshift.client.info.ApplicationProperty;
import com.codenvy.ide.ext.openshift.shared.AppInfo;
import com.codenvy.ide.ext.openshift.shared.OpenShiftEmbeddableCartridge;
import com.codenvy.ide.ui.Button;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;

import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ApplicationListViewImpl extends DialogBox implements ApplicationListView {

    interface ApplicationListViewImplUiBinder extends UiBinder<Widget, ApplicationListViewImpl> {
    }

    private static ApplicationListViewImplUiBinder uiBinder = GWT.create(ApplicationListViewImplUiBinder.class);

    @UiField
    CellTable<AppInfo> applicationList;

    @UiField
    CellTable<ApplicationProperty> applicationProperties;

    @UiField
    CellTable<OpenShiftEmbeddableCartridge> applicationCartridges;

    @UiField
    TextBox userLoginField;

    @UiField
    TextBox userDomainFiled;

    @UiField
    Button btnChangeAccount;

    @UiField
    Button btnChangeDomain;

    @UiField
    Button btnCreateCartridge;

    @UiField
    Button btnClose;

    @UiField(provided = true)
    final OpenShiftLocalizationConstant constant;

    private ActionDelegate delegate;

    private boolean isShown;

    private AppInfo selectedApplication;

    @Inject
    protected ApplicationListViewImpl(OpenShiftLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setTitle(constant.applicationListViewTitle());
        this.setWidget(widget);

        initApplicationListTable();
        initApplicationPropertiesTable();
        initCartridgesTable();
    }

    private void initApplicationListTable() {
        applicationList = new CellTable<AppInfo>();
        applicationList.setWidth("100%", true);
        applicationList.setAutoHeaderRefreshDisabled(true);
        applicationList.setAutoFooterRefreshDisabled(true);

        final SelectionModel<AppInfo> selectionModel = new SingleSelectionModel<AppInfo>();

        applicationList.setSelectionModel(selectionModel);

        Column<AppInfo, String> appNameColumn = new TextColumn<AppInfo>() {
            @Override
            public String getValue(AppInfo object) {
                return object.getName();
            }
        };

        Column<AppInfo, String> appDeleteColumn = new Column<AppInfo, String>(new ButtonCell()) {
            @Override
            public String getValue(AppInfo object) {
                return "Delete";
            }
        };

        this.applicationList.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                selectedApplication = ((SingleSelectionModel<AppInfo>)selectionModel).getSelectedObject();
            }
        });

//        appNameColumn.setFieldUpdater(new FieldUpdater<AppInfo, String>() {
//            @Override
//            public void update(int index, AppInfo object, String value) {
//                delegate.onApplicationSelectClicked(object);
//            }
//        });

        appDeleteColumn.setFieldUpdater(new FieldUpdater<AppInfo, String>() {
            @Override
            public void update(int index, AppInfo object, String value) {
                delegate.onApplicationDeleteClicked(object);
            }
        });

        applicationList.addColumn(appNameColumn, "Application");
        applicationList.addColumn(appDeleteColumn, "Delete");
    }

    private void initApplicationPropertiesTable() {
        applicationProperties = new CellTable<ApplicationProperty>();
        applicationProperties.setWidth("100%", true);
        applicationProperties.setAutoHeaderRefreshDisabled(true);
        applicationProperties.setAutoFooterRefreshDisabled(true);

        final SelectionModel<ApplicationProperty> selectionModel = new NoSelectionModel<ApplicationProperty>();

        applicationProperties.setSelectionModel(selectionModel);

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

        applicationProperties.addColumn(propertyKeyColumn, "Property");
        applicationProperties.addColumn(propertyValueColumn, "Value");
    }

    private void initCartridgesTable() {
        applicationCartridges = new CellTable<OpenShiftEmbeddableCartridge>();
        applicationCartridges.setWidth("100%", true);
        applicationCartridges.setAutoHeaderRefreshDisabled(true);
        applicationCartridges.setAutoFooterRefreshDisabled(true);

        final SelectionModel<OpenShiftEmbeddableCartridge> selectionModel = new SingleSelectionModel<OpenShiftEmbeddableCartridge>();

        applicationCartridges.setSelectionModel(selectionModel);

        Column<OpenShiftEmbeddableCartridge, String> cartridgeNameColumn = new TextColumn<OpenShiftEmbeddableCartridge>() {
            @Override
            public String getValue(OpenShiftEmbeddableCartridge object) {
                return object.getName();
            }
        };

        Column<OpenShiftEmbeddableCartridge, String> cartridgeStartColumn =
                new Column<OpenShiftEmbeddableCartridge, String>(new ButtonCell()) {
                    @Override
                    public String getValue(OpenShiftEmbeddableCartridge object) {
                        return "Start";
                    }
                };

        Column<OpenShiftEmbeddableCartridge, String> cartridgeStopColumn =
                new Column<OpenShiftEmbeddableCartridge, String>(new ButtonCell()) {
                    @Override
                    public String getValue(OpenShiftEmbeddableCartridge object) {
                        return "Stop";
                    }
                };

        Column<OpenShiftEmbeddableCartridge, String> cartridgeRestartColumn =
                new Column<OpenShiftEmbeddableCartridge, String>(new ButtonCell()) {
                    @Override
                    public String getValue(OpenShiftEmbeddableCartridge object) {
                        return "Restart";
                    }
                };

        Column<OpenShiftEmbeddableCartridge, String> cartridgeReloadColumn =
                new Column<OpenShiftEmbeddableCartridge, String>(new ButtonCell()) {
                    @Override
                    public String getValue(OpenShiftEmbeddableCartridge object) {
                        return "Reload";
                    }
                };

        Column<OpenShiftEmbeddableCartridge, String> cartridgeDeleteColumn =
                new Column<OpenShiftEmbeddableCartridge, String>(new ButtonCell()) {
                    @Override
                    public String getValue(OpenShiftEmbeddableCartridge object) {
                        return "Delete";
                    }
                };

        cartridgeStartColumn.setFieldUpdater(new FieldUpdater<OpenShiftEmbeddableCartridge, String>() {
            @Override
            public void update(int index, OpenShiftEmbeddableCartridge object, String value) {
                delegate.onCartridgeStartClicked(object);
            }
        });

        cartridgeStopColumn.setFieldUpdater(new FieldUpdater<OpenShiftEmbeddableCartridge, String>() {
            @Override
            public void update(int index, OpenShiftEmbeddableCartridge object, String value) {
                delegate.onCartridgeStopClicked(object);
            }
        });

        cartridgeRestartColumn.setFieldUpdater(new FieldUpdater<OpenShiftEmbeddableCartridge, String>() {
            @Override
            public void update(int index, OpenShiftEmbeddableCartridge object, String value) {
                delegate.onCartridgeRestartClicked(object);
            }
        });

        cartridgeReloadColumn.setFieldUpdater(new FieldUpdater<OpenShiftEmbeddableCartridge, String>() {
            @Override
            public void update(int index, OpenShiftEmbeddableCartridge object, String value) {
                delegate.onCartridgeReloadClicked(object);
            }
        });

        cartridgeDeleteColumn.setFieldUpdater(new FieldUpdater<OpenShiftEmbeddableCartridge, String>() {
            @Override
            public void update(int index, OpenShiftEmbeddableCartridge object, String value) {
                delegate.onCartridgeDeleteClicked(object);
            }
        });

        applicationCartridges.addColumn(cartridgeNameColumn, "Cartridge");
        applicationCartridges.addColumn(cartridgeStartColumn, "Start");
        applicationCartridges.addColumn(cartridgeStopColumn, "Stop");
        applicationCartridges.addColumn(cartridgeRestartColumn, "Restart");
        applicationCartridges.addColumn(cartridgeReloadColumn, "Reload");
        applicationCartridges.addColumn(cartridgeDeleteColumn, "Delete");
    }

    @Override
    public void setApplications(List<AppInfo> applications) {
        this.applicationList.setRowData(applications);
        this.applicationList.getSelectionModel().setSelected(applications.get(0), true);
    }

    @Override
    public void setCartridges(List<OpenShiftEmbeddableCartridge> cartridges) {
        this.applicationCartridges.setRowData(cartridges);
    }

    @Override
    public void setApplicationInfo(List<ApplicationProperty> properties) {
        this.applicationProperties.setRowData(properties);
    }

    @Override
    public void setUserLogin(String userLogin) {
        this.userLoginField.setText(userLogin);
    }

    @Override
    public void setUserDomain(String userDomain) {
        this.userDomainFiled.setText(userDomain);
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
    public AppInfo getSelectedApplication() {
        return selectedApplication;
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnClose")
    public void onCloseButtonClick(ClickEvent event) {
        delegate.onCloseClicked();
    }

    @UiHandler("btnCreateCartridge")
    public void onCreateCartridgeClicked(ClickEvent event) {
        delegate.onCreateCartridgeClicked();
    }

    @UiHandler("btnChangeAccount")
    public void onChangeAccountClicked(ClickEvent event) {
        delegate.onChangeAccountClicked();
    }

    @UiHandler("btnChangeDomain")
    public void onChangeDomainClicked(ClickEvent event) {
        delegate.onChangeDomainNameClicked();
    }
}
