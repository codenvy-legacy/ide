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
