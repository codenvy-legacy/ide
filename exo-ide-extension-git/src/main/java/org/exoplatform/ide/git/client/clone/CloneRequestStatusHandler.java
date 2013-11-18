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
package org.exoplatform.ide.git.client.clone;

import org.exoplatform.ide.client.framework.job.Job;
import org.exoplatform.ide.client.framework.job.Job.JobStatus;
import org.exoplatform.ide.client.framework.job.JobChangeEvent;
import org.exoplatform.ide.client.framework.job.RequestStatusHandlerBase;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.git.client.GitExtension;

/**
 * Status handler for clone operation.
 */
public class CloneRequestStatusHandler extends RequestStatusHandlerBase {

    private String remoteUri;

    /**
     * Create handler.
     *
     * @param projectName
     *         name for project which cloning
     * @param remoteUri
     *         url for remote repository
     */
    public CloneRequestStatusHandler(String projectName, String remoteUri) {
        super(projectName);
        this.remoteUri = remoteUri;
    }

    /** {@inheritDoc} */
    @Override
    public void requestInProgress(String id) {
        Job job = new Job(id, JobStatus.STARTED);
        job.setStartMessage(GitExtension.MESSAGES.cloneStarted(projectName, remoteUri));
        IDE.fireEvent(new JobChangeEvent(job));
    }

    /** {@inheritDoc} */
    @Override
    public void requestFinished(String id) {
        Job job = new Job(id, JobStatus.FINISHED);
        job.setFinishMessage(GitExtension.MESSAGES.cloneFinished(projectName, remoteUri));
        IDE.fireEvent(new JobChangeEvent(job));
    }
}
