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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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
public class LoadBalancerTabPain extends Composite {
    interface LoadBalancerTabPainUiBinder extends UiBinder<Widget, LoadBalancerTabPain> {}

    private static LoadBalancerTabPainUiBinder uiBinder = GWT.create(LoadBalancerTabPainUiBinder.class);

    @UiField
    TextBox appHealthCheckUrlField;

    @UiField
    TextBox healthCheckIntervalField;

    @UiField
    TextBox healthCheckTimeoutField;

    @UiField
    TextBox healthyThresholdField;

    @UiField
    TextBox unhealthyThresholdField;

    @Inject
    protected LoadBalancerTabPain() {
        Widget widget = uiBinder.createAndBindUi(this);

        this.initWidget(widget);
    }

    public void setAppHealthCheckUrl(String value) {
        appHealthCheckUrlField.setText(value);
    }

    public void setHealthCheckInterval(String value) {
        healthCheckIntervalField.setText(value);
    }

    public void setHealthCheckTimeout(String value) {
        healthCheckTimeoutField.setText(value);
    }

    public void setHealthyThreshold(String value) {
        healthyThresholdField.setText(value);
    }

    public void setUnhealthyThreshold(String value) {
        unhealthyThresholdField.setText(value);
    }
}
