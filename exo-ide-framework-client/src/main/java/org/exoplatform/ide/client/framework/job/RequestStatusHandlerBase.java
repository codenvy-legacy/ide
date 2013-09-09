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
package org.exoplatform.ide.client.framework.job;

import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.ide.client.framework.job.Job.JobStatus;
import org.exoplatform.ide.client.framework.module.IDE;

/**
 * Standard handling of errors for {@link RequestStatusHandler} interface
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Oct 31, 2011 evgen $
 */
public abstract class RequestStatusHandlerBase implements RequestStatusHandler {

    protected String projectName;

    /** @param projectName */
    public RequestStatusHandlerBase(String projectName) {
        super();
        this.projectName = projectName;
    }

    /**
     * @see org.exoplatform.ide.client.framework.websocket.messages.RESTfulRequestStatusHandler.RequestStatusHandler#requestError(java
     * .lang.String,
     *      java.lang.Throwable)
     */
    @Override
    public void requestError(String id, Throwable exception) {
        Job job = new Job(id, JobStatus.ERROR);
        job.setError(exception);
        IDE.fireEvent(new JobChangeEvent(job));
    }

}
