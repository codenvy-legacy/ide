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
package com.codenvy.ide.extension.cloudfoundry.client.project;

import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link CloudFoundryProjectView}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class CloudFoundryProjectViewImpl extends DialogBox implements CloudFoundryProjectView {
    interface CloudFoundryProjectViewImplUiBinder extends UiBinder<Widget, CloudFoundryProjectViewImpl> {
    }

    private static CloudFoundryProjectViewImplUiBinder uiBinder = GWT.create(CloudFoundryProjectViewImplUiBinder.class);

    @UiField
    TextBox                   applicationName;
    @UiField
    com.codenvy.ide.ui.Button btnInfo;
    @UiField
    Anchor                    url;
    @UiField
    com.codenvy.ide.ui.Button btnEditUrl;
    @UiField
    TextBox                   memory;
    @UiField
    com.codenvy.ide.ui.Button btnEditMemory;
    @UiField
    TextBox                   instances;
    @UiField
    com.codenvy.ide.ui.Button btnEditInstances;
    @UiField
    Label                     stack;
    @UiField
    Label                     model;
    @UiField
    Label                     status;
    @UiField
    com.codenvy.ide.ui.Button btnStart;
    @UiField
    com.codenvy.ide.ui.Button btnStop;
    @UiField
    com.codenvy.ide.ui.Button btnRestart;
    @UiField
    com.codenvy.ide.ui.Button btnUpdate;
    @UiField
    com.codenvy.ide.ui.Button btnDelete;
    @UiField
    com.codenvy.ide.ui.Button btnServices;
    @UiField
    com.codenvy.ide.ui.Button btnLogs;
    @UiField
    com.codenvy.ide.ui.Button btnClose;
    @UiField(provided = true)
    final   CloudFoundryResources                  res;
    @UiField(provided = true)
    final   CloudFoundryLocalizationConstant       locale;
    private CloudFoundryProjectView.ActionDelegate delegate;
    private boolean                                isShown;

    /**
     * Create view.
     *
     * @param resources
     * @param constant
     */
    @Inject
    protected CloudFoundryProjectViewImpl(CloudFoundryResources resources, CloudFoundryLocalizationConstant constant) {
        this.res = resources;
        this.locale = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText("CloudFoundry Project");
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
    public String getApplicationModel() {
        return model.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationModel(String model) {
        this.model.setText(model);
    }

    /** {@inheritDoc} */
    @Override
    public String getApplicationStack() {
        return stack.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationStack(String stack) {
        this.stack.setText(stack);
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
    public String getApplicationMemory() {
        return memory.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationMemory(String memory) {
        this.memory.setText(memory);
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
    public void setEnabledStartButton(boolean enabled) {
        btnStart.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnabledStopButton(boolean enabled) {
        btnStop.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnabledRestartButton(boolean enabled) {
        btnRestart.setEnabled(enabled);
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
        this.isShown = false;
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.isShown = true;
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isShown() {
        return isShown;
    }

    @UiHandler("btnInfo")
    void onBtnInfoClick(ClickEvent event) {
        delegate.onInfoClicked();
    }

    @UiHandler("btnEditUrl")
    void onBtnEditUrlClick(ClickEvent event) {
        delegate.onEditUrlClicked();
    }

    @UiHandler("btnEditMemory")
    void onBtnEditMemoryClick(ClickEvent event) {
        delegate.onEditMemoryClicked();
    }

    @UiHandler("btnEditInstances")
    void onBtnEditInstancesClick(ClickEvent event) {
        delegate.onEditInstancesClicked();
    }

    @UiHandler("btnStart")
    void onBtnStartClick(ClickEvent event) {
        delegate.onStartClicked();
    }

    @UiHandler("btnStop")
    void onBtnStopClick(ClickEvent event) {
        delegate.onStopClicked();
    }

    @UiHandler("btnRestart")
    void onBtnRestartClick(ClickEvent event) {
        delegate.onRestartClicked();
    }

    @UiHandler("btnUpdate")
    void onBtnUpdateClick(ClickEvent event) {
        delegate.onUpdateClicked();
    }

    @UiHandler("btnDelete")
    void onBtnDeleteClick(ClickEvent event) {
        delegate.onDeleteClicked();
    }

    @UiHandler("btnServices")
    void onBtnServicesClick(ClickEvent event) {
        delegate.onServicesClicked();
    }

    @UiHandler("btnLogs")
    void onBtnLogsClick(ClickEvent event) {
        delegate.onLogsClicked();
    }

    @UiHandler("btnClose")
    void onBtnCloseClick(ClickEvent event) {
        delegate.onCloseClicked();
    }
}