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
package com.codenvy.ide.ext.openshift.client.project;

import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ext.openshift.shared.AppInfo;
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;

import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ProjectViewImpl extends DialogBox implements ProjectView {

    interface ProjectViewImplUiBinder extends UiBinder<Widget, ProjectViewImpl> {
    }

    private static ProjectViewImplUiBinder uiBinder = GWT.create(ProjectViewImplUiBinder.class);

    @UiField
    TextBox appName;

    @UiField
    Anchor appUrl;

    @UiField
    Label appType;

    @UiField
    Label appHealth;

    @UiField
    Button btnStop;

    @UiField
    Button btnStart;

    @UiField
    Button btnRestart;

    @UiField
    Button btnShowProperties;

    @UiField
    Button btnClose;

    @UiField(provided = true)
    final OpenShiftLocalizationConstant constant;

    private ActionDelegate delegate;

    private boolean isShown;

    private AppInfo application;

    @Inject
    protected ProjectViewImpl(OpenShiftLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setTitle("title");
        this.setWidget(widget);
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

    @Override
    public void close() {
        application = null;
        this.hide();
    }

    @Override
    public void showDialog(AppInfo application) {
        this.isShown = true;
        this.application = application;

        this.appName.setText(application.getName());
        this.appType.setText(application.getType());
        this.appUrl.setText(application.getPublicUrl());

        this.center();
        this.show();
    }

    @Override
    public void setApplicationHealth(String health) {
        appHealth.setText(health);

        if ("STARTED".equals(health)) {
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            btnRestart.setEnabled(true);
        } else {
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            btnRestart.setEnabled(false);
        }
    }

    @UiHandler("btnRestart")
    public void onRestartButtonClicked(ClickEvent event) {
        delegate.onRestartApplicationClicked(application);
    }

    @UiHandler("btnStart")
    public void onStartButtonClicked(ClickEvent event) {
        delegate.onStartApplicationClicked(application);
    }

    @UiHandler("btnStop")
    public void onStopButtonClicked(ClickEvent event) {
        delegate.onStopApplicationClicked(application);
    }

    @UiHandler("btnShowProperties")
    public void onShowApplicationPropertiesClicked(ClickEvent event) {
        delegate.onShowApplicationPropertiesClicked(application);
    }

    @UiHandler("btnClose")
    public void onCloseButtonClicked(ClickEvent event) {
        delegate.onCloseClicked();
    }
}
