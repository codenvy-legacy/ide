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