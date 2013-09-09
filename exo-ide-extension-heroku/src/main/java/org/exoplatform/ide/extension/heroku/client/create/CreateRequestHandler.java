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
package org.exoplatform.ide.extension.heroku.client.create;

import org.exoplatform.ide.client.framework.job.Job;
import org.exoplatform.ide.client.framework.job.Job.JobStatus;
import org.exoplatform.ide.client.framework.job.JobChangeEvent;
import org.exoplatform.ide.client.framework.job.RequestStatusHandlerBase;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;

/**
 * Handler for Heroku application request.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 12, 2011 12:45:04 PM anya $
 */
public class CreateRequestHandler extends RequestStatusHandlerBase {
    public CreateRequestHandler() {
        super(null);
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.RequestStatusHandler#requestInProgress(java.lang.String) */
    @Override
    public void requestInProgress(String id) {
        Job job = new Job(id, JobStatus.STARTED);
        job.setStartMessage(HerokuExtension.LOCALIZATION_CONSTANT.creatingApplication());
        IDE.fireEvent(new JobChangeEvent(job));
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.RequestStatusHandler#requestFinished(java.lang.String) */
    @Override
    public void requestFinished(String id) {
        Job job = new Job(id, JobStatus.FINISHED);
        job.setFinishMessage(HerokuExtension.LOCALIZATION_CONSTANT.applicationCreated());
        IDE.fireEvent(new JobChangeEvent(job));
    }

}
