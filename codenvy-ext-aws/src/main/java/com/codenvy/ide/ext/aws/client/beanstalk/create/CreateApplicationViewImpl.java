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
package com.codenvy.ide.ext.aws.client.beanstalk.create;

import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class CreateApplicationViewImpl extends DialogBox implements CreateApplicationView {
    interface CreateApplicationViewImplUiBinder extends UiBinder<Widget, CreateApplicationViewImpl> {}

    private static CreateApplicationViewImplUiBinder uiBinder = GWT.create(CreateApplicationViewImplUiBinder.class);

    @UiField
    TextBox appNameField;

    @UiField
    TextBox appDescField;

    @UiField
    TextBox s3BucketField;

    @UiField
    TextBox s3KeyField;

    @UiField
    TextBox envNameField;

    @UiField
    TextBox envDescField;

    @UiField
    CheckBox launchNewEnv;

    @UiField
    ListBox solutionStack;

    @UiField
    FlowPanel createApplicationStep;

    @UiField
    FlowPanel createEnvironmentStep;

    @UiField
    Button btnNext;

    @UiField
    Button btnBack;

    @UiField
    Button btnFinish;

    @UiField
    Button btnCancel;

    @UiField(provided = true)
    AWSLocalizationConstant constant;

    private boolean isShown;

    private ActionDelegate delegate;

    @Inject
    protected CreateApplicationViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(constant.createApplicationViewTitle());
        this.setWidget(widget);

        envNameField.setEnabled(false);
        envDescField.setEnabled(false);
    }

    @Override
    public String getApplicationName() {
        return appNameField.getText();
    }

    @Override
    public String getDescription() {
        return appDescField.getText();
    }

    @Override
    public String getS3Bucket() {
        return s3BucketField.getText();
    }

    @Override
    public String getS3Key() {
        return s3KeyField.getText();
    }

    @Override
    public String getEnvironmentName() {
        return envNameField.getText();
    }

    @Override
    public String getEnvironmentDescription() {
        return envDescField.getText();
    }

    @Override
    public String getSolutionStack() {
        return solutionStack.getItemText(solutionStack.getSelectedIndex());
    }

    @Override
    public void setSolutionStacks(JsonArray<String> stack) {
        for (int i = 0; i < stack.size(); i++) {
            solutionStack.addItem(stack.get(i));
        }
    }

    @Override
    public boolean launchNewEnvironment() {
        return launchNewEnv.getValue();
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

    @Override
    public void showDialog() {
        this.isShown = true;
        this.center();
        this.show();
    }

    @Override
    public void close() {
        this.isShown = false;
        this.hide();
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void enableCreateEnvironmentStep(boolean enabled) {
        envNameField.setEnabled(enabled);
        envDescField.setEnabled(enabled);
        solutionStack.setEnabled(enabled);
    }

    @Override
    public void showCreateApplicationStep() {
        createEnvironmentStep.setVisible(false);
        createApplicationStep.setVisible(true);
        btnBack.setVisible(false);
        btnFinish.setVisible(false);
        btnNext.setVisible(true);
    }

    @Override
    public void showCreateEnvironmentStep() {
        createApplicationStep.setVisible(false);
        createEnvironmentStep.setVisible(true);
        btnBack.setVisible(true);
        btnFinish.setVisible(true);
        btnNext.setVisible(false);
    }

    @UiHandler("btnNext")
    public void onNextButtonClicked(ClickEvent event) {
        delegate.onNextButtonClicked();
    }

    @UiHandler("btnBack")
    public void onBackButtonClicked(ClickEvent event) {
        delegate.onBackButtonClicked();
    }

    @UiHandler("btnFinish")
    public void onFinishButtonClicked(ClickEvent event) {
        delegate.onFinishButtonClicked();
    }

    @UiHandler("btnCancel")
    public void onCancelButtonClicked(ClickEvent event) {
        delegate.onCancelButtonClicked();
    }

    @UiHandler("launchNewEnv")
    public void onLaunchEnvClicked(ClickEvent event) {
        delegate.onLaunchEnvironmentClicked(launchNewEnv.getValue());
    }
}
