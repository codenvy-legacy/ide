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
package org.exoplatform.ide.extension.cloudfoundry.client;

import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.ide.client.framework.job.Job;
import org.exoplatform.ide.client.framework.job.Job.JobStatus;
import org.exoplatform.ide.client.framework.job.JobChangeEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;

import static org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.LOCALIZATION_CONSTANT;
import static org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER.WEB_FABRIC;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 21, 2012 9:35:47 AM anya $
 */
public class CreateApplicationRequestStatusHandler implements RequestStatusHandler {
    private String applicationName;

    private PAAS_PROVIDER paasProvider;

    public CreateApplicationRequestStatusHandler(String applicationName, PAAS_PROVIDER paasProvider) {
        this.applicationName = applicationName;
        this.paasProvider = paasProvider;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.RequestStatusHandler#requestInProgress(java.lang.String) */
    @Override
    public void requestInProgress(String id) {
        Job job = new Job(id, JobStatus.STARTED);
        if (paasProvider == WEB_FABRIC) {
            job.setStartMessage(LOCALIZATION_CONSTANT.createApplicationStartedWebFabric(applicationName));
        } else {
            job.setStartMessage(LOCALIZATION_CONSTANT.createApplicationStartedCloudFoundry(applicationName));
        }
        IDE.fireEvent(new JobChangeEvent(job));
    }

    /** @see org.exoplatform.gwtframework.commons.rest.RequestStatusHandler#requestFinished(java.lang.String) */
    @Override
    public void requestFinished(String id) {
        Job job = new Job(id, JobStatus.FINISHED);
        if (paasProvider == WEB_FABRIC) {
            job.setFinishMessage(LOCALIZATION_CONSTANT.createApplicationFinishedWebFabric(applicationName));
        } else {
            job.setFinishMessage(LOCALIZATION_CONSTANT.createApplicationFinishedCloudFoundry(applicationName));
        }
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
