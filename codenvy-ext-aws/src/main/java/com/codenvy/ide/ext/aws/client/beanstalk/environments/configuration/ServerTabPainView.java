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
import com.codenvy.ide.json.JsonArray;

/**
 * The view for {@link ServerTabPainPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface ServerTabPainView extends View<ServerTabPainView.ActionDelegate> {
    /** Interface which must implement presenter to process any actions. */
    interface ActionDelegate {
    }

    /** Reset modifiable state for user inputs. */
    void resetModifiedFields();

    /**
     * Set EC2 instances types.
     *
     * @param instanceTypes
     *         available instance types.
     * @param valueToSelect
     *         instance to select.
     */
    void setEc2InstanceTypes(JsonArray<String> instanceTypes, String valueToSelect);

    /**
     * Get selected EC2 instance type.
     *
     * @return EC2 instance.
     */
    String getEc2InstanceType();

    /**
     * Is EC2 instance type modified.
     *
     * @return true if modified.
     */
    boolean isEc2InstanceTypeModified();

    /**
     * Set EC2 security group.
     *
     * @param securityGroup
     *         security group.
     */
    void setEc2SecurityGroup(String securityGroup);

    /**
     * Get EC2 security group.
     *
     * @return security group.
     */
    String getEc2SecurityGroup();

    /**
     * Is EC2 security group modified.
     *
     * @return true if modified.
     */
    boolean isEc2SecurityGroupModified();

    /**
     * Set key pair name for instance.
     *
     * @param keyPair
     *         key pair name.
     */
    void setKeyPair(String keyPair);

    /**
     * Get key pair name for instance.
     *
     * @return key pair name.
     */
    String getKeyPair();

    /**
     * Is key pair modified.
     *
     * @return true if modified.
     */
    boolean isHeyPairModified();

    /**
     * Set monitoring interval.
     *
     * @param interval
     *         array of available monitoring interval.
     * @param valueForSelect
     *         interval to be selected.
     */
    void setMonitoringInterval(JsonArray<String> interval, String valueForSelect);

    /**
     * Get monitoring interval.
     *
     * @return monitoring interval.
     */
    String getMonitoringInterval();

    /**
     * Is monitoring interval modified.
     *
     * @return true if modified.
     */
    boolean isMonitoringIntervalModified();

    /**
     * Set image id.
     *
     * @param amiId
     *         image id.
     */
    void setAmiId(String amiId);

    /**
     * Get image id.
     *
     * @return image id.
     */
    String getAmiId();

    /**
     * Is image id modified.
     *
     * @return true if modified.
     */
    boolean isAmiIdModified();
}
