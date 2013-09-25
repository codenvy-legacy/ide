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
package com.codenvy.ide.ext.git.client.clone;

import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.job.Job;
import com.codenvy.ide.job.JobChangeEvent;
import com.codenvy.ide.job.RequestStatusHandlerBase;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Oct 28, 2011 evgen $
 */
public class CloneRequestStatusHandler extends RequestStatusHandlerBase {
    private String                  remoteUri;
    private GitLocalizationConstant constant;

    /**
     * Create handler.
     *
     * @param projectName
     * @param remoteUri
     * @param eventBus
     * @param constant
     */
    public CloneRequestStatusHandler(String projectName, String remoteUri, EventBus eventBus, GitLocalizationConstant constant) {
        super(projectName, eventBus);
        this.remoteUri = remoteUri;
        this.constant = constant;
    }

    /** {@inheritDoc} */
    @Override
    public void requestInProgress(String id) {
        Job job = new Job(id, Job.JobStatus.STARTED);
        job.setStartMessage(constant.cloneStarted(projectName, remoteUri));
        eventBus.fireEvent(new JobChangeEvent(job));
    }

    /** {@inheritDoc} */
    @Override
    public void requestFinished(String id) {
        Job job = new Job(id, Job.JobStatus.FINISHED);
        job.setFinishMessage(constant.cloneFinished(projectName, remoteUri));
        eventBus.fireEvent(new JobChangeEvent(job));
    }
}