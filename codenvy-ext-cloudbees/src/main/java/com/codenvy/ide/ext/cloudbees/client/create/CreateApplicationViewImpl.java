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
package com.codenvy.ide.ext.cloudbees.client.create;

import com.codenvy.ide.ext.cloudbees.client.CloudBeesLocalizationConstant;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesResources;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
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
 * The implementation of {@link CreateApplicationView}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class CreateApplicationViewImpl extends DialogBox implements CreateApplicationView {
    private static CreateApplicationViewImplUiBinder ourUiBinder = GWT.create(CreateApplicationViewImplUiBinder.class);

    interface CreateApplicationViewImplUiBinder extends UiBinder<Widget, CreateApplicationViewImpl> {
    }

    @UiField
    ListBox                   targetField;
    @UiField
    TextBox                   nameField;
    @UiField
    TextBox                   urlField;
    @UiField
    com.codenvy.ide.ui.Button btnCreate;
    @UiField
    com.codenvy.ide.ui.Button btnCancel;
    @UiField(provided = true)
    final   CloudBeesResources            res;
    @UiField(provided = true)
    final   CloudBeesLocalizationConstant locale;
    private ActionDelegate                delegate;

    /**
     * Create view.
     *
     * @param resources
     * @param constant
     */
    @Inject
    protected CreateApplicationViewImpl(CloudBeesResources resources, CloudBeesLocalizationConstant constant) {
        this.res = resources;
        this.locale = constant;

        Widget widget = ourUiBinder.createAndBindUi(this);

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
    public String getUrl() {
        return urlField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setUrl(String url) {
        urlField.setText(url);
    }

    /** {@inheritDoc} */
    @Override
    public String getDomain() {
        int selectedIndex = targetField.getSelectedIndex();
        return selectedIndex != -1 ? targetField.getItemText(selectedIndex) : "";
    }

    /** {@inheritDoc} */
    @Override
    public void setDomain(String domain) {
        int count = this.targetField.getItemCount();
        boolean isItemFound = false;

        // Looks up entered server into available list of servers
        int i = 0;
        while (i < count && !isItemFound) {
            String item = this.targetField.getItemText(i);
            isItemFound = item.equals(domain);

            i++;
        }

        // If item was found then it will be shown otherwise do nothing
        if (isItemFound) {
            this.targetField.setSelectedIndex(i - 1);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setDomainValues(JsonArray<String> servers) {
        targetField.clear();
        for (int i = 0; i < servers.size(); i++) {
            targetField.addItem(servers.get(i));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableCreateButton(boolean enable) {
        btnCreate.setEnabled(enable);
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

    @UiHandler("nameField")
    void onNameFieldKeyUp(KeyUpEvent event) {
        delegate.onValueChanged();
    }

    @UiHandler("targetField")
    void onTargetFieldChange(ChangeEvent event) {
        delegate.onValueChanged();
    }

    @UiHandler("btnCancel")
    void onBtnCancelClick(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("btnCreate")
    void onBtnCreateClick(ClickEvent event) {
        delegate.onCreateClicked();
    }
}