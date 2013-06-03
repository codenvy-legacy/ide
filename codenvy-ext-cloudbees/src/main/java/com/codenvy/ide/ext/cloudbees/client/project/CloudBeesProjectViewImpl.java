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
package com.codenvy.ide.ext.cloudbees.client.project;

import com.codenvy.ide.ext.cloudbees.client.CloudBeesLocalizationConstant;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link CloudBeesProjectView}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class CloudBeesProjectViewImpl extends DialogBox implements CloudBeesProjectView {
    private static CloudBeesProjectViewImplUiBinder ourUiBinder = GWT.create(CloudBeesProjectViewImplUiBinder.class);

    interface CloudBeesProjectViewImplUiBinder extends UiBinder<Widget, CloudBeesProjectViewImpl> {
    }

    @UiField
    TextBox                   applicationName;
    @UiField
    com.codenvy.ide.ui.Button btnInfo;
    @UiField
    Anchor                    url;
    @UiField
    Label                     instances;
    @UiField
    Label                     status;
    @UiField
    com.codenvy.ide.ui.Button btnUpdate;
    @UiField
    com.codenvy.ide.ui.Button btnDelete;
    @UiField
    com.codenvy.ide.ui.Button btnClose;
    @UiField(provided = true)
    final   CloudBeesResources                  res;
    @UiField(provided = true)
    final   CloudBeesLocalizationConstant       locale;
    private CloudBeesProjectView.ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param resources
     * @param constant
     */
    @Inject
    protected CloudBeesProjectViewImpl(CloudBeesResources resources, CloudBeesLocalizationConstant constant) {
        this.res = resources;
        this.locale = constant;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText("CloudBees Project");
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public String getApplicationName() {
        return applicationName.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationName(String name) {
        applicationName.setText(name);
    }

    /** {@inheritDoc} */
    @Override
    public String getApplicationInstances() {
        return instances.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationInstances(String instances) {
        this.instances.setText(instances);
    }

    /** {@inheritDoc} */
    @Override
    public String getApplicationStatus() {
        return status.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationStatus(String status) {
        this.status.setText(status);
    }

    /** {@inheritDoc} */
    @Override
    public String getApplicationUrl() {
        return url.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationUrl(String url) {
        this.url.setText(url);
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

    @UiHandler("btnInfo")
    void onBtnInfoClick(ClickEvent event) {
        delegate.onInfoClicked();
    }

    @UiHandler("btnUpdate")
    void onBtnUpdateClick(ClickEvent event) {
        delegate.onUpdateClicked();
    }

    @UiHandler("btnDelete")
    void onBtnDeleteClick(ClickEvent event) {
        delegate.onDeleteClicked();
    }

    @UiHandler("btnClose")
    void onBtnCloseClick(ClickEvent event) {
        delegate.onCloseClicked();
    }
}