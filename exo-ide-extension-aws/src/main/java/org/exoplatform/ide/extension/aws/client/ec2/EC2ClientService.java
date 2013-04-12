/*
 * Copyright (C) 2011 eXo Platform SAS.
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