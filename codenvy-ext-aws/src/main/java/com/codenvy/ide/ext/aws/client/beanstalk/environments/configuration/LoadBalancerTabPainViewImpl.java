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
package com.codenvy.ide.ext.aws.client.beanstalk.environments.configuration;

import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
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

    @Inject
    protected LoadBalancerTabPainViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.initWidget(widget);
    }

    @Override
    public void setHealthCheckUrl(String healthCheckUrl) {
        appHealthCheckUrlField.setText(healthCheckUrl);
    }

    @Override
    public String getHealthCheckUrl() {
        return appHealthCheckUrlField.getText();
    }

    @Override
    public void setHealthCheckInterval(String healthCheckInterval) {
        healthCheckIntervalField.setText(healthCheckInterval);
    }

    @Override
    public String getHealthCheckInterval() {
        return healthCheckIntervalField.getText();
    }

    @Override
    public void setHealthCheckTimeOut(String healthCheckTimeOut) {
        healthCheckTimeoutField.setText(healthCheckTimeOut);
    }

    @Override
    public String getHealthCheckTimeOut() {
        return healthCheckTimeoutField.getText();
    }

    @Override
    public void setHealthCheckCountThreshold(String healthCheckCountThreshold) {
        healthyThresholdField.setText(healthCheckCountThreshold);
    }

    @Override
    public String getHealthCheckCountThreshold() {
        return healthyThresholdField.getText();
    }

    @Override
    public void setUnhealthyCheckCountThreshold(String unhealthyCheckCountThreshold) {
        unhealthyThresholdField.setText(unhealthyCheckCountThreshold);
    }

    @Override
    public String getUnhealthyCheckCountThreshold() {
        return unhealthyThresholdField.getText();
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void resetModifiedFields() {
        appHealthCheckUrlField.setModified(false);
        healthCheckIntervalField.setModified(false);
        healthCheckTimeoutField.setModified(false);
        healthyThresholdField.setModified(false);
        unhealthyThresholdField.setModified(false);
    }

    @Override
    public boolean isHealthCheckUrlModified() {
        return appHealthCheckUrlField.isModified();
    }

    @Override
    public boolean isHealthCheckIntervalModified() {
        return healthCheckIntervalField.isModified();
    }

    @Override
    public boolean isHealthCheckTimeOutModified() {
        return healthCheckTimeoutField.isModified();
    }

    @Override
    public boolean isHealthCheckCountThresholdModified() {
        return healthyThresholdField.isModified();
    }

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
