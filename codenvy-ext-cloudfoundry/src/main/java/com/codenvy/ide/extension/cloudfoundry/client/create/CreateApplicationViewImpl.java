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
package com.codenvy.ide.extension.cloudfoundry.client.create;

import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryResources;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link CreateApplicationView}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class CreateApplicationViewImpl extends DialogBox implements CreateApplicationView {
    interface CreateApplicationViewImplUiBinder extends UiBinder<Widget, CreateApplicationViewImpl> {
    }

    private static CreateApplicationViewImplUiBinder uiBinder = GWT.create(CreateApplicationViewImplUiBinder.class);

    @UiField
    TextBox                   name;
    @UiField
    TextBox                   url;
    @UiField
    CheckBox                  customUrl;
    @UiField
    TextBox                   instances;
    @UiField
    TextBox                   memory;
    @UiField
    ListBox                   server;
    @UiField
    ListBox                   type;
    @UiField
    SimpleCheckBox            startAfterCreation;
    @UiField
    CheckBox                  autodetectType;
    @UiField
    com.codenvy.ide.ui.Button btnCreate;
    @UiField
    com.codenvy.ide.ui.Button btnCancel;
    @UiField(provided = true)
    final   CloudFoundryResources            res;
    @UiField(provided = true)
    final   CloudFoundryLocalizationConstant locale;
    private ActionDelegate                   delegate;

    /** Create view. */
    @Inject
    protected CreateApplicationViewImpl(CloudFoundryResources resources, CloudFoundryLocalizationConstant constant) {
        this.res = resources;
        this.locale = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText("Create Application");
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        int selectedItem = type.getSelectedIndex();
        return selectedItem != -1 ? type.getItemText(selectedItem) : "";
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAutodetectType() {
        return autodetectType.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setAutodetectType(boolean autodetected) {
        autodetectType.setValue(autodetected);
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setName(String name) {
        this.name.setText(name);
    }

    /** {@inheritDoc} */
    @Override
    public String getUrl() {
        return url.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setUrl(String url) {
        this.url.setText(url);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCustomUrl() {
        return customUrl.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public String getInstances() {
        return instances.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setInstances(String instances) {
        this.instances.setText(instances);
    }

    /** {@inheritDoc} */
    @Override
    public String getMemory() {
        return memory.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setMemory(String memory) {
        this.memory.setText(memory);
    }

    /** {@inheritDoc} */
    @Override
    public String getServer() {
        int serverIndex = server.getSelectedIndex();
        return serverIndex != -1 ? server.getItemText(serverIndex) : "";
    }

    /** {@inheritDoc} */
    @Override
    public void setServer(String server) {
        int count = this.server.getItemCount();
        boolean isItemFound = false;

        // Looks up entered server into available list of servers
        int i = 0;
        while (i < count && !isItemFound) {
            String item = this.server.getItemText(i);
            isItemFound = item.equals(server);

            i++;
        }

        // If item was found then it will be shown otherwise do nothing
        if (isItemFound) {
            this.server.setSelectedIndex(i - 1);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isStartAfterCreation() {
        return startAfterCreation.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setStartAfterCreation(boolean start) {
        this.startAfterCreation.setValue(start);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableCreateButton(boolean enable) {
        btnCreate.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void focusInNameField() {
        name.setFocus(true);
    }

    /** {@inheritDoc} */
    @Override
    public void setTypeValues(JsonArray<String> types) {
        type.clear();
        for (int i = 0; i < types.size(); i++) {
            type.addItem(types.get(i));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableTypeField(boolean enable) {
        type.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableUrlField(boolean enable) {
        url.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableMemoryField(boolean enable) {
        memory.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void setSelectedIndexForTypeSelectItem(int index) {
        type.setSelectedIndex(index);
    }

    /** {@inheritDoc} */
    @Override
    public void focusInUrlField() {
        url.setFocus(true);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableAutodetectTypeCheckItem(boolean enable) {
        autodetectType.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void setServerValues(JsonArray<String> servers) {
        server.clear();
        for (int i = 0; i < servers.size(); i++) {
            server.addItem(servers.get(i));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.center();
        this.show();
    }

    @UiHandler("btnCancel")
    void onBtnCancelClick(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("btnCreate")
    void onBtnCreateClick(ClickEvent event) {
        delegate.onCreateClicked();
    }

    @UiHandler("server")
    void onServerChange(ChangeEvent event) {
        delegate.onServerChanged();
    }

    @UiHandler("autodetectType")
    void onAutodetectTypeClick(ClickEvent event) {
        delegate.onAutoDetectTypeChanged();
    }

    @UiHandler("customUrl")
    void onCustomUrlClick(ClickEvent event) {
        delegate.onCustomUrlChanged();
    }

    @UiHandler("name")
    void onNameKeyUp(KeyUpEvent event) {
        delegate.onApplicationNameChanged();
    }

    @UiHandler("type")
    void onTypeChange(ChangeEvent event) {
        delegate.onTypeChanged();
    }
}