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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * The implementation for {@link ProjectView}.
 *
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

    @UiField
    Button btnDelete;

    @UiField(provided = true)
    final OpenShiftLocalizationConstant constant;

    private ActionDelegate delegate;

    private boolean isShown;

    private AppInfo application;

    /**
     * Create view.
     *
     * @param constant
     *         locale constants
     */
    @Inject
    protected ProjectViewImpl(OpenShiftLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(constant.projectViewTitle());
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isShown() {
        return isShown;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        application = null;
        this.hide();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
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

    /**
     * Handler for Restart application button.
     *
     * @param event
     */
    @UiHandler("btnRestart")
    public void onRestartButtonClicked(ClickEvent event) {
        delegate.onRestartApplicationClicked(application);
    }

    /**
     * Handler for Start application button.
     *
     * @param event
     */
    @UiHandler("btnStart")
    public void onStartButtonClicked(ClickEvent event) {
        delegate.onStartApplicationClicked(application);
    }

    /**
     * Handler for Stop application button.
     *
     * @param event
     */
    @UiHandler("btnStop")
    public void onStopButtonClicked(ClickEvent event) {
        delegate.onStopApplicationClicked(application);
    }

    /**
     * Handler for Show application properties button.
     *
     * @param event
     */
    @UiHandler("btnShowProperties")
    public void onShowApplicationPropertiesClicked(ClickEvent event) {
        delegate.onShowApplicationPropertiesClicked(application);
    }

    /**
     * Handler for Close button.
     *
     * @param event
     */
    @UiHandler("btnClose")
    public void onCloseButtonClicked(ClickEvent event) {
        delegate.onCloseClicked();
    }

    @UiHandler("btnDelete")
    public void onDeleteApplicationClicked(ClickEvent event) {
        delegate.onDeleteApplicationDeleted(application);
    }
}
