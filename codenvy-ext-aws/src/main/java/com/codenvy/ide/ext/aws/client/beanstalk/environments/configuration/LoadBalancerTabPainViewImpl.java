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
package com.codenvy.ide.ext.aws.client.beanstalk.environments.configuration;

import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link LoadBalancerTabPainView}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class LoadBalancerTabPainViewImpl extends Composite implements LoadBalancerTabPainView {
    interface LoadBalancerTabPainViewImplUiBinder extends UiBinder<Widget, LoadBalancerTabPainViewImpl> {
    }

    private static LoadBalancerTabPainViewImplUiBinder uiBinder = GWT.create(LoadBalancerTabPainViewImplUiBinder.class);

    @UiField
    ModifiableTextBox appHealthCheckUrlField;

    @UiField
    ModifiableTextBox healthCheckIntervalField;

    @UiField
    ModifiableTextBox healthCheckTimeoutField;

    @UiField
    ModifiableTextBox healthyThresholdField;

    @UiField
    ModifiableTextBox unhealthyThresholdField;

    @UiField(provided = true)
    AWSLocalizationConstant constant;

    private ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param constant
     */
    @Inject
    protected LoadBalancerTabPainViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.initWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public void setHealthCheckUrl(String healthCheckUrl) {
        appHealthCheckUrlField.setText(healthCheckUrl);
    }

    /** {@inheritDoc} */
    @Override
    public String getHealthCheckUrl() {
        return appHealthCheckUrlField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setHealthCheckInterval(String healthCheckInterval) {
        healthCheckIntervalField.setText(healthCheckInterval);
    }

    /** {@inheritDoc} */
    @Override
    public String getHealthCheckInterval() {
        return healthCheckIntervalField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setHealthCheckTimeOut(String healthCheckTimeOut) {
        healthCheckTimeoutField.setText(healthCheckTimeOut);
    }

    /** {@inheritDoc} */
    @Override
    public String getHealthCheckTimeOut() {
        return healthCheckTimeoutField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setHealthCheckCountThreshold(String healthCheckCountThreshold) {
        healthyThresholdField.setText(healthCheckCountThreshold);
    }

    /** {@inheritDoc} */
    @Override
    public String getHealthCheckCountThreshold() {
        return healthyThresholdField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setUnhealthyCheckCountThreshold(String unhealthyCheckCountThreshold) {
        unhealthyThresholdField.setText(unhealthyCheckCountThreshold);
    }

    /** {@inheritDoc} */
    @Override
    public String getUnhealthyCheckCountThreshold() {
        return unhealthyThresholdField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void resetModifiedFields() {
        appHealthCheckUrlField.setModified(false);
        healthCheckIntervalField.setModified(false);
        healthCheckTimeoutField.setModified(false);
        healthyThresholdField.setModified(false);
        unhealthyThresholdField.setModified(false);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHealthCheckUrlModified() {
        return appHealthCheckUrlField.isModified();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHealthCheckIntervalModified() {
        return healthCheckIntervalField.isModified();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHealthCheckTimeOutModified() {
        return healthCheckTimeoutField.isModified();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHealthCheckCountThresholdModified() {
        return healthyThresholdField.isModified();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isUnhealthyCheckCountThresholdModified() {
        return unhealthyThresholdField.isModified();
    }

    @UiHandler("appHealthCheckUrlField")
    public void onAppHealthCheckUrlFieldChanged(KeyUpEvent event) {
        appHealthCheckUrlField.setModified(true);
    }

    @UiHandler("healthCheckIntervalField")
    public void onHealthCheckIntervalFieldChanged(KeyUpEvent event) {
        healthCheckIntervalField.setModified(true);
    }

    @UiHandler("healthCheckTimeoutField")
    public void onHealthCheckTimeoutFieldChanged(KeyUpEvent event) {
        healthCheckTimeoutField.setModified(true);
    }

    @UiHandler("healthyThresholdField")
    public void onHealthyThresholdFieldChanged(KeyUpEvent event) {
        healthyThresholdField.setModified(true);
    }

    @UiHandler("unhealthyThresholdField")
    public void onUnhealthyThresholdFieldChanged(KeyUpEvent event) {
        unhealthyThresholdField.setModified(true);
    }
}
