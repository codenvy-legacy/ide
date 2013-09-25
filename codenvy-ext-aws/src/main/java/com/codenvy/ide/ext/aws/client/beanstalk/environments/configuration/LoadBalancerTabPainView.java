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

import com.codenvy.ide.api.mvp.View;

/**
 * The view for {@link LoadBalancerTabPainPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface LoadBalancerTabPainView extends View<LoadBalancerTabPainView.ActionDelegate> {
    /** Interface which must implement presenter to process any actions. */
    interface ActionDelegate {
    }

    /** Reset modifiable state for user inputs. */
    void resetModifiedFields();

    /**
     * Set health check url.
     *
     * @param healthCheckUrl
     *         health check url.
     */
    void setHealthCheckUrl(String healthCheckUrl);

    /**
     * Get health check url.
     *
     * @return health check url,
     */
    String getHealthCheckUrl();

    /**
     * Is health check url modified.
     *
     * @return true if modified.
     */
    boolean isHealthCheckUrlModified();

    /**
     * Set health check interval.
     *
     * @param healthCheckInterval
     *         health check interval.
     */
    void setHealthCheckInterval(String healthCheckInterval);

    /**
     * Get health check interval.
     *
     * @return health check interval.
     */
    String getHealthCheckInterval();

    /**
     * Is health check interval modified.
     *
     * @return true if modified.
     */
    boolean isHealthCheckIntervalModified();

    /**
     * Set health check time out.
     *
     * @param healthCheckTimeOut
     *         health check time out.
     */
    void setHealthCheckTimeOut(String healthCheckTimeOut);

    /**
     * Get health check interval time out.
     *
     * @return health check interval time out.
     */
    String getHealthCheckTimeOut();

    /**
     * Is health check interval time out modified.
     *
     * @return true if modified.
     */
    boolean isHealthCheckTimeOutModified();

    /**
     * Set health check count threshold.
     *
     * @param healthCheckCountThreshold
     *         health check count threshold.
     */
    void setHealthCheckCountThreshold(String healthCheckCountThreshold);

    /**
     * Get health check count threshold.
     *
     * @return health check count threshold.
     */
    String getHealthCheckCountThreshold();

    /**
     * Is health check count threshold modified.
     *
     * @return true if modified.
     */
    boolean isHealthCheckCountThresholdModified();

    /**
     * Set unhealthy check count threshold.
     *
     * @param unhealthyCheckCountThreshold
     *         unhealthy check count threshold.
     */
    void setUnhealthyCheckCountThreshold(String unhealthyCheckCountThreshold);

    /**
     * Get unhealthy check count threshold.
     *
     * @return unhealthy check count threshold.
     */
    String getUnhealthyCheckCountThreshold();

    /**
     * Is unhealthy check count threshold modifies.
     *
     * @return true if modified.
     */
    boolean isUnhealthyCheckCountThresholdModified();
}
