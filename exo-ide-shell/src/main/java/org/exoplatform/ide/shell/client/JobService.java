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
package org.exoplatform.ide.shell.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;

/**
 * Service for getting running jobs (asynchronous tasks).
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 13, 2012 9:58:05 AM anya $
 */
public class JobService {
    /** Service. */
    private static JobService jobService;

    /** REST context. */
    private final String REST_CONTEXT = "rest/private";

    /** Path to job's list service. */
    private final String JOBS_PATH = "async";

    protected JobService() {
    }

    /** @return {@link JobService} job service */
    public static JobService getService() {
        if (jobService == null) {
            jobService = new JobService();
        }
        return jobService;
    }

    /**
     * Get the list of running jobs (asynchronous tasks) in text format.
     *
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void getJobs(AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        String url = REST_CONTEXT + "/" + JOBS_PATH;

        AsyncRequest.build(RequestBuilder.GET, url).header(HTTPHeader.ACCEPT, MimeType.TEXT_PLAIN).send(callback);
    }

    public void killJob(String jobId, AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        StringBuilder url = new StringBuilder(REST_CONTEXT).append("/").append(JOBS_PATH).append("/").append(jobId);

        AsyncRequest.build(RequestBuilder.DELETE, url.toString()).send(callback);
    }
}
