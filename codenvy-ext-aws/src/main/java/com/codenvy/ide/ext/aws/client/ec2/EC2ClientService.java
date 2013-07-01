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
package com.codenvy.ide.ext.aws.client.ec2;

import com.codenvy.ide.ext.aws.shared.ec2.InstanceInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

import java.util.List;

/**
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
