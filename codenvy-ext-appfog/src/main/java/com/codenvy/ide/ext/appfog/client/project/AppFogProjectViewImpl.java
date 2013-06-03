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
package com.codenvy.ide.ext.appfog.client.project;

import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.AppfogResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link AppFogProjectView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class AppFogProjectViewImpl extends DialogBox implements AppFogProjectView {
    interface AppFogProjectViewImplUiBinder extends UiBinder<Widget, AppFogProjectViewImpl> {
    }

    private static AppFogProjectViewImplUiBinder ourUiBinder = GWT.create(AppFogProjectViewImplUiBinder.class);

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
    Label                     infra;
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
    final   AppfogResources            res;
    @UiField(provided = true)
    final   AppfogLocalizationConstant locale;
    private ActionDelegate             delegate;
    private boolean                    isShown;

    /**
     * Create view.
     *
     * @param resources
     * @param constant
     */
    @Inject
    protected AppFogProjectViewImpl(AppfogResources resources, AppfogLocalizationConstant constant) {
        this.res = resources;
        this.locale = constant;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText("AppFog Project");
        this.setWidget(widget);
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
    public String getApplicationInfra() {
        return infra.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationInfra(String infra) {
        this.infra.setText(infra);
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

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
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