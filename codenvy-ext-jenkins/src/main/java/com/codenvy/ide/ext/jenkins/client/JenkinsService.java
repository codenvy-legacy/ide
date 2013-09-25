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
    void getJenkinsOutput(String vfsId, String projectId, String jobName, AsyncRequestCallback<String> callback) throws RequestException;
}