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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.TextInput;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: LoadBalancerTabPain.java Oct 8, 2012 5:38:06 PM azatsarynnyy $
 */
public class LoadBalancerTabPain extends Composite {

    private static LoadBalancerTabPainUiBinder uiBinder = GWT.create(LoadBalancerTabPainUiBinder.class);

    interface LoadBalancerTabPainUiBinder extends UiBinder<Widget, LoadBalancerTabPain> {
    }

    private static final String APP_HEALTH_CHECK_URL_FIELD_ID = "ideLoadBalancerTabPainAppHealthCheckUrlField";

    private static final String HEALTH_CHECK_INTERVAL_FIELD_ID = "ideLoadBalancerTabPainHealthCheckIntervalField";

    private static final String HEALTH_CHECK_TIMEOUT_FIELD_ID = "ideLoadBalancerTabPainHealthCheckTimeoutField";

    private static final String HEALTHY_THRESHOLD_FIELD_ID = "ideLoadBalancerTabPainHealthyThresholdField";

    private static final String UNHEALTHY_THRESHOLD_FIELD_ID = "ideLoadBalancerTabPainUnhealthyThresholdField";

    @UiField
    TextInput appHealthCheckUrlField;

    @UiField
    TextInput healthCheckIntervalField;

    @UiField
    TextInput healthCheckTimeoutField;

    @UiField
    TextInput healthyThresholdField;

    @UiField
    TextInput unhealthyThresholdField;

    public LoadBalancerTabPain() {
        initWidget(uiBinder.createAndBindUi(this));

        appHealthCheckUrlField.setName(APP_HEALTH_CHECK_URL_FIELD_ID);
        healthCheckIntervalField.setName(HEALTH_CHECK_INTERVAL_FIELD_ID);
        healthCheckTimeoutField.setName(HEALTH_CHECK_TIMEOUT_FIELD_ID);
        healthyThresholdField.setName(HEALTHY_THRESHOLD_FIELD_ID);
        unhealthyThresholdField.setName(UNHEALTHY_THRESHOLD_FIELD_ID);
    }

    /** @return the appHealthCheckUrlField */
    public TextInput getAppHealthCheckUrlField() {
        return appHealthCheckUrlField;
    }

    /** @return the healthCheckIntervalField */
    public TextInput getHealthCheckIntervalField() {
        return healthCheckIntervalField;
    }

    /** @return the healthCheckTimeoutField */
    public TextInput getHealthCheckTimeoutField() {
        return healthCheckTimeoutField;
    }

    /** @return the healthyThresholdField */
    public TextInput getHealthyThresholdField() {
        return healthyThresholdField;
    }

    /** @return the unhealthyThresholdField */
    public TextInput getUnhealthyThresholdField() {
        return unhealthyThresholdField;
    }
}
