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
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Implementation of {@link JenkinsService} service.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
@Singleton
public class JenkinsServiceImpl implements JenkinsService {
    private static final String JENKINS = '/' + Utils.getWorkspaceName() + "/jenkins";
    private String restContext;
    private Loader loader;

    /**
     * Create service.
     *
     * @param restContext
     * @param loader
     */
    @Inject
    protected JenkinsServiceImpl(@Named("restContext") String restContext, Loader loader) {
        this.restContext = restContext;
        this.loader = loader;
    }

    /** {@inheritDoc} */
    @Override
    public void createJenkinsJob(String name, String user, String mail, String vfsId, String projectId, AsyncRequestCallback<Job> callback)
            throws RequestException {
        String url = restContext + JENKINS + "/job/create?name=" + name + "&user=" + user + "&email=" + mail + "&vfsid=" + vfsId +
                     "&projectid=" + projectId;
        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void buildJob(String vfsId, String projectId, String jobName, AsyncRequestCallback<Object> callback) throws RequestException {
        String params = "name=" + jobName + "&vfsid=" + vfsId + "&projectid=" + projectId;

        String url = restContext + JENKINS + "/job/build?" + params;
        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void jobStatus(String vfsId, String projectId, String jobName, AsyncRequestCallback<JobStatus> callback)
            throws RequestException {
        String url = restContext + JENKINS + "/job/status?name=" + jobName + "&vfsid=" + vfsId + "&projectid=" + projectId;
        AsyncRequest.build(RequestBuilder.GET, url).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getJenkinsOutput(String vfsId, String projectId, String jobName, AsyncRequestCallback<String> callback)
            throws RequestException {
        String url = restContext + JENKINS + "/job/console-output?name=" + jobName + "&vfsid=" + vfsId + "&projectid=" + projectId;
        AsyncRequest.build(RequestBuilder.GET, url).send(callback);
    }
}