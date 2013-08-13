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
package com.codenvy.ide.ext.appfog.client.services;

import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.AppfogResources;
import com.codenvy.ide.ext.appfog.shared.AppfogSystemService;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link CreateServiceView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class CreateServiceViewImpl extends DialogBox implements CreateServiceView {
    interface CreateServiceViewImplUiBinder extends UiBinder<Widget, CreateServiceViewImpl> {
    }

    private static CreateServiceViewImplUiBinder ourUiBinder = GWT.create(CreateServiceViewImplUiBinder.class);

    @UiField
    ListBox                   servicesField;
    @UiField
    TextBox                   nameField;
    @UiField
    com.codenvy.ide.ui.Button btnCreate;
    @UiField
    com.codenvy.ide.ui.Button btnCancel;
    @UiField(provided = true)
    final   AppfogResources            res;
    @UiField(provided = true)
    final   AppfogLocalizationConstant locale;
    private ActionDelegate             delegate;

    /**
     * Create view.
     *
     * @param resources
     * @param constant
     */
    @Inject
    protected CreateServiceViewImpl(AppfogResources resources, AppfogLocalizationConstant constant) {
        this.res = resources;
        this.locale = constant;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setWidget(widget);
        this.setText("Create service");
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public String getSystemServices() {
        int selectedIndex = servicesField.getSelectedIndex();
        return selectedIndex != -1 ? servicesField.getValue(selectedIndex) : "";
    }

    /** {@inheritDoc} */
    @Override
    public void setServices(JsonArray<AppfogSystemService> services) {
        servicesField.clear();
        for (int i = 0; i < services.size(); i++) {
            AppfogSystemService service = services.get(i);
            servicesField.addItem(service.getVendor(), service.getDescription());
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return nameField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setName(String name) {
        nameField.setText(name);
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

    @UiHandler("btnCreate")
    void onBtnCreateClick(ClickEvent event) {
        delegate.onCreateClicked();
    }

    @UiHandler("btnCancel")
    void onBtnCancelClick(ClickEvent event) {
        delegate.onCancelClicked();
    }
}