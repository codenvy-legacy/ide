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
