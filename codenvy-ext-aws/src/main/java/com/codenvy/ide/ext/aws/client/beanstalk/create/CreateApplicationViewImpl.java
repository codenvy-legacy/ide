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
 * The implementation of {@link CreateApplicationView}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class CreateApplicationViewImpl extends DialogBox implements CreateApplicationView {
    interface CreateApplicationViewImplUiBinder extends UiBinder<Widget, CreateApplicationViewImpl> {
    }

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

    /**
     * Create view.
     *
     * @param constant
     */
    @Inject
    protected CreateApplicationViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(constant.createApplicationViewTitle());
        this.setWidget(widget);

        envNameField.setEnabled(false);
        envDescField.setEnabled(false);
    }

    /** {@inheritDoc} */
    @Override
    public String getApplicationName() {
        return appNameField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return appDescField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public String getS3Bucket() {
        return s3BucketField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public String getS3Key() {
        return s3KeyField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public String getEnvironmentName() {
        return envNameField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public String getEnvironmentDescription() {
        return envDescField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public String getSolutionStack() {
        return solutionStack.getItemText(solutionStack.getSelectedIndex());
    }

    /** {@inheritDoc} */
    @Override
    public void setSolutionStacks(JsonArray<String> stack) {
        for (int i = 0; i < stack.size(); i++) {
            solutionStack.addItem(stack.get(i));
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean launchNewEnvironment() {
        return launchNewEnv.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isShown() {
        return isShown;
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
    public void close() {
        this.isShown = false;
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void enableCreateEnvironmentStep(boolean enabled) {
        envNameField.setEnabled(enabled);
        envDescField.setEnabled(enabled);
        solutionStack.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void showCreateApplicationStep() {
        createEnvironmentStep.setVisible(false);
        createApplicationStep.setVisible(true);
        btnBack.setVisible(false);
        btnFinish.setVisible(false);
        btnNext.setVisible(true);
    }

    /** {@inheritDoc} */
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
