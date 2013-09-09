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
package org.exoplatform.ide.extension.jenkins.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.jenkins.shared.Job;
import org.exoplatform.ide.extension.jenkins.shared.JobStatus;

/**
 * Client service for Jenkins Extension
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class JenkinsService {

    private String restContext;

    private Loader loader;

    private static JenkinsService instance;

    private static final String JENKINS = Utils.getWorkspaceName() + "/jenkins";

    /**
     * @param restContext
     * @param loader
     */
    public JenkinsService(Loader loader) {
        this.restContext = Utils.getRestContext();
        this.loader = loader;
        instance = this;
    }

    /** @return instance of {@link JenkinsService} */
    public static JenkinsService get() {
        if (instance == null)
            throw new IllegalStateException("Jenkins Service uninitialized");
        return instance;
    }

    /**
     * Create new Jenkins job
     *
     * @param name
     *         of job
     * @param git
     *         Got repository URL
     * @param user
     *         User name
     * @param mail
     *         User e-mail
     * @param workDir
     *         Git working directory
     * @param callback
     * @throws RequestException
     */
    public void createJenkinsJob(String name, String user, String mail, String vfsId, String projectId,
                                 AsyncRequestCallback<Job> callback) throws RequestException {
        String url =
                restContext + JENKINS + "/job/create?name=" + name + "&user=" + user + "&email=" + mail + "&vfsid=" + vfsId
                + "&projectid=" + projectId;
        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }

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
    public void buildJob(String vfsId, String projectId, String jobName, AsyncRequestCallback<Object> callback)
            throws RequestException {
        String params = "name=" + jobName + "&vfsid=" + vfsId + "&projectid=" + projectId;

        String url = restContext + JENKINS + "/job/build?" + params;
        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }

    /**
     * Get Job status
     *
     * @param jobName
     *         Name of Job
     * @param callback
     * @throws RequestException
     */
    public void jobStatus(String vfsId, String projectId, String jobName, AsyncRequestCallback<JobStatus> callback)
            throws RequestException {
        String url =
                restContext + JENKINS + "/job/status?name=" + jobName + "&vfsid=" + vfsId + "&projectid=" + projectId;
        AsyncRequest.build(RequestBuilder.GET, url).send(callback);
    }

    public void getJenkinsOutput(String vfsId, String projectId, String jobName,
                                 AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        String url =
                restContext + JENKINS + "/job/console-output?name=" + jobName + "&vfsid=" + vfsId + "&projectid=" + projectId;
        AsyncRequest.build(RequestBuilder.GET, url).send(callback);
    }
}
