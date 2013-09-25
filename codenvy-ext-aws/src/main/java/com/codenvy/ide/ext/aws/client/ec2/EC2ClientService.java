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
package com.codenvy.ide.ext.aws.client.ec2;

import com.codenvy.ide.ext.aws.shared.ec2.InstanceInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

/**
 * Client service for controlling EC2 Instances.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface EC2ClientService {
    /**
     * Returns the list of EC2 instances.
     *
     * @param callback
     * @throws com.google.gwt.http.client.RequestException
     */
    public void getInstances(AsyncRequestCallback<JsonArray<InstanceInfo>> callback) throws RequestException;

    /**
     * Terminate the specified EC2 instance.
     *
     * @param id
     *         EC2 instance identifier
     * @param callback
     * @throws RequestException
     */
    public void terminateInstance(String id, AsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Reboot the specified EC2 instance.
     *
     * @param id
     *         EC2 instance identifier
     * @param callback
     * @throws RequestException
     */
    public void rebootInstance(String id, AsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Stop the specified EC2 instance.
     *
     * @param id
     *         EC2 instance identifier
     * @param force
     *         forces the instance to stop
     * @param callback
     * @throws RequestException
     */
    public void stopInstance(String id, boolean force, AsyncRequestCallback<Object> callback)
            throws RequestException;

    /**
     * Start the specified EC2 instance.
     *
     * @param id
     *         EC2 instance identifier
     * @param callback
     * @throws RequestException
     */
    public void startInstance(String id, AsyncRequestCallback<Object> callback) throws RequestException;
}
