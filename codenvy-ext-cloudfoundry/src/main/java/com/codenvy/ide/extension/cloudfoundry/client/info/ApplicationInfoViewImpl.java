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
package com.codenvy.ide.extension.cloudfoundry.client.info;

import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryResources;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;

/**
 * The implementation of {@link ApplicationInfoView}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ApplicationInfoViewImpl extends DialogBox implements ApplicationInfoView {
    interface ApplicationInfoViewImplUiBinder extends UiBinder<Widget, ApplicationInfoViewImpl> {
    }

    private static ApplicationInfoViewImplUiBinder uiBinder = GWT.create(ApplicationInfoViewImplUiBinder.class);

    @UiField
    com.codenvy.ide.ui.Button btnOk;
    @UiField
    Label                     name;
    @UiField
    Label                     state;
    @UiField
    Label                     instances;
    @UiField
    Label                     version;
    @UiField
    Label                     resourceDisk;
    @UiField
    Label                     memory;
    @UiField
    Label                     model;
    @UiField
    Label                     stack;
    @UiField(provided = true)
    CellTable<String> urisTable         = new CellTable<String>();
    @UiField(provided = true)
    CellTable<String> environmentsTable = new CellTable<String>();
    @UiField(provided = true)
    CellTable<String> servicesTable     = new CellTable<String>();
    @UiField(provided = true)
    final   CloudFoundryResources              res;
    @UiField(provided = true)
    final   CloudFoundryLocalizationConstant   locale;
    private ApplicationInfoView.ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param constant
     * @param resources
     */
    @Inject
    protected ApplicationInfoViewImpl(CloudFoundryLocalizationConstant constant, CloudFoundryResources resources) {
        this.res = resources;
        this.locale = constant;

        createCellTable(urisTable, "URIs");
        createCellTable(servicesTable, "Services");
        createCellTable(environmentsTable, "Environments");

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText("Application Info");
        this.setWidget(widget);
    }

    /**
     * Creates table.
     *
     * @param table
     * @param header
     */
    private void createCellTable(CellTable<String> table, String header) {
        Column<String, String> column = new Column<String, String>(new TextCell()) {
            @Override
            public String getValue(String object) {
                return object;
            }
        };

        table.addColumn(column, header);
        table.setColumnWidth(column, "100%");

        // don't show loading indicator
        table.setLoadingIndicator(null);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setName(String name) {
        this.name.setText(name);
    }

    /** {@inheritDoc} */
    @Override
    public void setState(String state) {
        this.state.setText(state);
    }

    /** {@inheritDoc} */
    @Override
    public void setInstances(String instances) {
        this.instances.setText(instances);
    }

    /** {@inheritDoc} */
    @Override
    public void setVersion(String version) {
        this.version.setText(version);
    }

    /** {@inheritDoc} */
    @Override
    public void setDisk(String disk) {
        this.resourceDisk.setText(disk);
    }

    /** {@inheritDoc} */
    @Override
    public void setMemory(String memory) {
        this.memory.setText(memory);
    }

    /** {@inheritDoc} */
    @Override
    public void setStack(String stack) {
        this.stack.setText(stack);
    }

    /** {@inheritDoc} */
    @Override
    public void setModel(String model) {
        this.model.setText(model);
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationUris(JsonArray<String> applications) {
        setItemsIntoCellTable(applications, urisTable);
    }

    /**
     * Sets items into selected table.
     *
     * @param items
     * @param table
     */
    private void setItemsIntoCellTable(JsonArray<String> items, CellTable<String> table) {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < items.size(); i++) {
            String item = items.get(i);
            list.add(item);
        }

        table.setRowData(list);
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationServices(JsonArray<String> services) {
        setItemsIntoCellTable(services, servicesTable);
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationEnvironments(JsonArray<String> environments) {
        setItemsIntoCellTable(environments, environmentsTable);
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    @UiHandler("btnOk")
    void onBtnOkClick(ClickEvent event) {
        delegate.onOKClicked();
    }
}