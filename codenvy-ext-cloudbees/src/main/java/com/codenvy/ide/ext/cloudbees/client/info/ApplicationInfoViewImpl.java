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
package com.codenvy.ide.ext.cloudbees.client.info;

import com.codenvy.ide.ext.cloudbees.client.CloudBeesLocalizationConstant;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@Singleton
public class ApplicationInfoViewImpl extends DialogBox implements ApplicationInfoView {
    interface ApplicationInfoViewImplUiBinder extends UiBinder<Widget, ApplicationInfoViewImpl> {
    }

    private static ApplicationInfoViewImplUiBinder ourUiBinder = GWT.create(ApplicationInfoViewImplUiBinder.class);

    @UiField
    Label                     id;
    @UiField
    Label                     title;
    @UiField
    Label                     serverpool;
    @UiField
    Label                     status;
    @UiField
    Label                     container;
    @UiField
    Label                     idletimeout;
    @UiField
    Label                     maxmemory;
    @UiField
    Label                     securitymode;
    @UiField
    Label                     clustersize;
    @UiField
    Label                     url;
    @UiField
    com.codenvy.ide.ui.Button btnOk;
    @UiField(provided = true)
    final   CloudBeesResources                 res;
    @UiField(provided = true)
    final   CloudBeesLocalizationConstant      locale;
    private ApplicationInfoView.ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param resources
     * @param constant
     */
    @Inject
    protected ApplicationInfoViewImpl(CloudBeesResources resources, CloudBeesLocalizationConstant constant) {
        this.res = resources;
        this.locale = constant;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText("Application Info");
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setAppId(String id) {
        this.id.setText(id);
    }

    /** {@inheritDoc} */
    @Override
    public void setAppTitle(String title) {
        this.title.setText(title);
    }

    /** {@inheritDoc} */
    @Override
    public void setServerPool(String serverPool) {
        this.serverpool.setText(serverPool);
    }

    /** {@inheritDoc} */
    @Override
    public void setAppStatus(String status) {
        this.status.setText(status);
    }

    /** {@inheritDoc} */
    @Override
    public void setAppContainer(String container) {
        this.container.setText(container);
    }

    /** {@inheritDoc} */
    @Override
    public void setIdleTimeout(String timeout) {
        this.idletimeout.setText(timeout);
    }

    /** {@inheritDoc} */
    @Override
    public void setMaxMemory(String maxMemory) {
        this.maxmemory.setText(maxMemory);
    }

    /** {@inheritDoc} */
    @Override
    public void setSecurityMode(String securityMode) {
        this.securitymode.setText(securityMode);
    }

    /** {@inheritDoc} */
    @Override
    public void setClusterSize(String size) {
        this.clustersize.setText(size);
    }

    /** {@inheritDoc} */
    @Override
    public void setUrl(String url) {
        this.url.setText(url);
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