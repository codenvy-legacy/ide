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
package com.codenvy.ide.ext.appfog.client.create;

import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.AppfogResources;
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

    private static CreateApplicationViewImplUiBinder ourUiBinder = GWT.create(CreateApplicationViewImplUiBinder.class);

    @UiField
    ListBox                   infra;
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
    TextBox                   server;
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
    protected CreateApplicationViewImpl(AppfogResources resources, AppfogLocalizationConstant constant) {
        this.res = resources;
        this.locale = constant;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText("Create Application");
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public String getInfra() {
        int selectedIndex = infra.getSelectedIndex();
        return selectedIndex != -1 ? infra.getItemText(selectedIndex) : "";
    }

    /** {@inheritDoc} */
    @Override
    public void setInfra(String infra) {
        int count = this.infra.getItemCount();
        boolean isItemFound = false;

        // Looks up entered server into available list of servers
        int i = 0;
        while (i < count && !isItemFound) {
            String item = this.infra.getItemText(i);
            isItemFound = item.equals(infra);

            i++;
        }

        // If item was found then it will be shown otherwise do nothing
        if (isItemFound) {
            this.infra.setSelectedIndex(i - 1);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setInfras(JsonArray<String> infras) {
        infra.clear();
        for (int i = 0; i < infras.size(); i++) {
            infra.addItem(infras.get(i));
        }
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
        return server.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setServer(String server) {
        this.server.setText(server);
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
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnCancel")
    void onBtnCancelClick(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("btnCreate")
    void onBtnCreateClick(ClickEvent event) {
        delegate.onCreateClicked();
    }

    @UiHandler("infra")
    void onServerChange(ChangeEvent event) {
        delegate.onInfraChanged();
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