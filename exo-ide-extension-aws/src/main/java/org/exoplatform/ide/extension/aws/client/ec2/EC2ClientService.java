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
package org.exoplatform.ide.extension.aws.client.ec2;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.extension.aws.shared.ec2.InstanceInfo;

import java.util.List;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: EC2ClientService.java Sep 21, 2012 12:24:54 PM azatsarynnyy $
 */
public abstract class EC2ClientService {
    private static EC2ClientService instance;

    public static EC2ClientService getInstance() {
        return instance;
    }

    protected EC2ClientService() {
        instance = this;
    }

    /**
     * Returns the list of EC2 instances.
     *
     * @param callback
     * @throws RequestException
     */
    public abstract void getInstances(AsyncRequestCallback<List<InstanceInfo>> callback) throws RequestException;

    /**
     * Terminate the specified EC2 instance.
     *
     * @param id
     *         EC2 instance identifier
     * @param callback
     * @throws RequestException
     */
    public abstract void terminateInstance(String id, AsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Reboot the specified EC2 instance.
     *
     * @param id
     *         EC2 instance identifier
     * @param callback
     * @throws RequestException
     */
    public abstract void rebootInstance(String id, AsyncRequestCallback<Object> callback) throws RequestException;

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
    public abstract void stopInstance(String id, boolean force, AsyncRequestCallback<Object> callback)
            throws RequestException;

    /**
     * Start the specified EC2 instance.
     *
     * @param id
     *         EC2 instance identifier
     * @param callback
     * @throws RequestException
     */
    public abstract void startInstance(String id, AsyncRequestCallback<Object> callback) throws RequestException;
}