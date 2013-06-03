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
package com.codenvy.ide.ext.jenkins.client;

import com.codenvy.ide.ext.jenkins.shared.Job;
import com.codenvy.ide.ext.jenkins.shared.JobStatus;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

/**
 * Client service for Jenkins Extension
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
public interface JenkinsService {
    /**
     * Create new Jenkins job
     *
     * @param name
     *         of job
     * @param user
     *         User name
     * @param mail
     *         User e-mail
     * @param callback
     * @throws RequestException
     */
    void createJenkinsJob(String name, String user, String mail, String vfsId, String projectId, AsyncRequestCallback<Job> callback)
            throws RequestException;

    /**
     * Start building job
     *
     * @param vfsId
     *         identifier of the virtual file system
     * @param projectId
     *         identifier of the project we want to send for build
     * @param jobName
     *         Name of Job
     * @param callback
     * @throws RequestException
     */
    void buildJob(String vfsId, String projectId, String jobName, AsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Get Job status
     *
     * @param jobName
     *         Name of Job
     * @param callback
     * @throws RequestException
     */
    void jobStatus(String vfsId, String projectId, String jobName, AsyncRequestCallback<JobStatus> callback) throws RequestException;

    /**
     * Get Jenkins output.
     *
     * @param vfsId
     *         identifier of the virtual file system
     * @param projectId
     *         identifier of the project we want to send for build
     * @param jobName
     *         Name of Job
     * @param callback
     * @throws RequestException
     */
    void getJenkinsOutput(String vfsId, String projectId, String jobName, AsyncRequestCallback<StringBuilder> callback)
            throws RequestException;
}