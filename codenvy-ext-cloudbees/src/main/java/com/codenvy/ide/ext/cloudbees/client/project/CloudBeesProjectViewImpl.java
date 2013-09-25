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