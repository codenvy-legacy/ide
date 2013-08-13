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
    public void getJenkinsOutput(String vfsId, String projectId, String jobName, AsyncRequestCallback<StringBuilder> callback)
            throws RequestException {
        String url = restContext + JENKINS + "/job/console-output?name=" + jobName + "&vfsid=" + vfsId + "&projectid=" + projectId;
        AsyncRequest.build(RequestBuilder.GET, url).send(callback);
    }
}