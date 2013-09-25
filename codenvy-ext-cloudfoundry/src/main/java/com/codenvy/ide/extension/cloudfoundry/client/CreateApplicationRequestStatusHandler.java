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
package com.codenvy.ide.extension.cloudfoundry.client;

import com.codenvy.ide.job.Job;
import com.codenvy.ide.job.Job.JobStatus;
import com.codenvy.ide.job.JobChangeEvent;
import com.codenvy.ide.rest.RequestStatusHandler;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The class helps to work with status of application.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 21, 2012 9:35:47 AM anya $
 */
public class CreateApplicationRequestStatusHandler implements RequestStatusHandler {
    private String                              applicationName;
    private EventBus                            eventBus;
    private CloudFoundryLocalizationConstant    constant;
    private CloudFoundryExtension.PAAS_PROVIDER paasProvider;

    /**
     * Create application request status handler.
     *
     * @param applicationName
     * @param eventBus
     * @param constant
     * @param paasProvider
     */
    public CreateApplicationRequestStatusHandler(String applicationName, EventBus eventBus, CloudFoundryLocalizationConstant constant,
                                                 CloudFoundryExtension.PAAS_PROVIDER paasProvider) {
        this.applicationName = applicationName;
        this.eventBus = eventBus;
        this.constant = constant;
        this.paasProvider = paasProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void requestInProgress(String id) {
        Job job = new Job(id, JobStatus.STARTED);

        if (paasProvider == CloudFoundryExtension.PAAS_PROVIDER.WEB_FABRIC) {
            job.setStartMessage(constant.createApplicationStartedWebFabric(applicationName));
        } else {
            job.setStartMessage(constant.createApplicationStartedCloudFoundry(applicationName));
        }

        eventBus.fireEvent(new JobChangeEvent(job));
    }

    /** {@inheritDoc} */
    @Override
    public void requestFinished(String id) {
        Job job = new Job(id, JobStatus.FINISHED);

        if (paasProvider == CloudFoundryExtension.PAAS_PROVIDER.WEB_FABRIC) {
            job.setFinishMessage(constant.createApplicationFinishedWebFabric(applicationName));
        } else {
            job.setFinishMessage(constant.createApplicationFinishedCloudFoundry(applicationName));
        }

        eventBus.fireEvent(new JobChangeEvent(job));
    }

    /** {@inheritDoc} */
    @Override
    public void requestError(String id, Throwable exception) {
        Job job = new Job(id, JobStatus.ERROR);
        job.setError(exception);
        eventBus.fireEvent(new JobChangeEvent(job));
    }
}