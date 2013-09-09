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
package org.exoplatform.ide.extension.appfog.client;

import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.ide.client.framework.job.Job;
import org.exoplatform.ide.client.framework.job.Job.JobStatus;
import org.exoplatform.ide.client.framework.job.JobChangeEvent;
import org.exoplatform.ide.client.framework.module.IDE;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class CreateApplicationRequestStatusHandler implements RequestStatusHandler {
    private String applicationName;

    public CreateApplicationRequestStatusHandler(String applicationName) {
        this.applicationName = applicationName;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.RequestStatusHandler#requestInProgress(java.lang.String) */
    @Override
    public void requestInProgress(String id) {
        Job job = new Job(id, JobStatus.STARTED);
        job.setStartMessage(AppfogExtension.LOCALIZATION_CONSTANT.createApplicationStarted(applicationName));
        IDE.fireEvent(new JobChangeEvent(job));
    }

    /** @see org.exoplatform.gwtframework.commons.rest.RequestStatusHandler#requestFinished(java.lang.String) */
    @Override
    public void requestFinished(String id) {
        Job job = new Job(id, JobStatus.FINISHED);
        job.setFinishMessage(AppfogExtension.LOCALIZATION_CONSTANT.createApplicationFinished(applicationName));
        IDE.fireEvent(new JobChangeEvent(job));
    }

    /** @see org.exoplatform.gwtframework.commons.rest.RequestStatusHandler#requestError(java.lang.String, java.lang.Throwable) */
    @Override
    public void requestError(String id, Throwable exception) {
        Job job = new Job(id, JobStatus.ERROR);
        job.setError(exception);
        IDE.fireEvent(new JobChangeEvent(job));
    }
}
