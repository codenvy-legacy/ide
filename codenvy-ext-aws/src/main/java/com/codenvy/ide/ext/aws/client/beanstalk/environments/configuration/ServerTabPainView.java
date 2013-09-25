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
