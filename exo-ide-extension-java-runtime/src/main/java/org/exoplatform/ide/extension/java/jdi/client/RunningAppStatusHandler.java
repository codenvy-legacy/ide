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
package org.exoplatform.ide.extension.java.jdi.client;

import org.exoplatform.ide.client.framework.job.Job;
import org.exoplatform.ide.client.framework.job.Job.JobStatus;
import org.exoplatform.ide.client.framework.job.JobChangeEvent;
import org.exoplatform.ide.client.framework.job.RequestStatusHandlerBase;
import org.exoplatform.ide.client.framework.module.IDE;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class RunningAppStatusHandler extends RequestStatusHandlerBase {


    public RunningAppStatusHandler(String projectName) {
        super(projectName);
    }

    /**
     * @see org.exoplatform.ide.client.framework.websocket.messages.RESTfulRequestStatusHandler.RequestStatusHandler#requestInProgress
     *      (java.lang.String)
     */
    @Override
    public void requestInProgress(String id) {
        Job job = new Job(id, JobStatus.STARTED);
        job.setStartMessage(DebuggerExtension.LOCALIZATION_CONSTANT.starting(projectName));
        IDE.fireEvent(new JobChangeEvent(job));
    }

    /**
     * @see org.exoplatform.ide.client.framework.websocket.messages.RESTfulRequestStatusHandler.RequestStatusHandler#requestFinished(java
     *      .lang.String)
     */
    @Override
    public void requestFinished(String id) {
        Job job = new Job(id, JobStatus.FINISHED);
        job.setFinishMessage(DebuggerExtension.LOCALIZATION_CONSTANT.started(projectName));
        IDE.fireEvent(new JobChangeEvent(job));
    }

}
