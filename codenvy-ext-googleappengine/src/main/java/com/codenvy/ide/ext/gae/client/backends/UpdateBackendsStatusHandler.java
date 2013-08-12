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
package com.codenvy.ide.ext.gae.client.backends;

import com.codenvy.ide.ext.gae.client.GAELocalization;
import com.codenvy.ide.job.Job;
import com.codenvy.ide.job.JobChangeEvent;
import com.codenvy.ide.rest.RequestStatusHandler;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Status handler for update backends request.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 31, 2012 10:28:19 AM anya $
 */
public class UpdateBackendsStatusHandler implements RequestStatusHandler {
    private EventBus        eventBus;
    private GAELocalization constant;

    /**
     * Update backends status handler.
     */
    public UpdateBackendsStatusHandler(EventBus eventBus, GAELocalization constant) {
        this.eventBus = eventBus;
        this.constant = constant;
    }

    /** {@inheritDoc} */
    @Override
    public void requestInProgress(String id) {
        Job job = new Job(id, Job.JobStatus.STARTED);
        job.setStartMessage(constant.backendsUpdateStarted());
        eventBus.fireEvent(new JobChangeEvent(job));
    }

    /** {@inheritDoc} */
    @Override
    public void requestFinished(String id) {
        Job job = new Job(id, Job.JobStatus.FINISHED);
        job.setFinishMessage(constant.backendsUpdateFinished());
        eventBus.fireEvent(new JobChangeEvent(job));
    }

    /** {@inheritDoc} */
    @Override
    public void requestError(String id, Throwable exception) {
        Job job = new Job(id, Job.JobStatus.ERROR);
        job.setError(exception);
        eventBus.fireEvent(new JobChangeEvent(job));
    }
}