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
package org.exoplatform.ide.git.client.add;

import org.exoplatform.ide.client.framework.job.Job;
import org.exoplatform.ide.client.framework.job.Job.JobStatus;
import org.exoplatform.ide.client.framework.job.JobChangeEvent;
import org.exoplatform.ide.client.framework.job.RequestStatusHandlerBase;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.git.client.GitExtension;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Oct 31, 2011 evgen $
 */
public class AddRequestHandler extends RequestStatusHandlerBase {

    /** @param pojectName */
    public AddRequestHandler(String pojectName) {
        super(pojectName);
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.RequestStatusHandler#requestInProgress(java.lang.String) */
    @Override
    public void requestInProgress(String id) {
        Job job = new Job(id, JobStatus.STARTED);
        job.setStartMessage(GitExtension.MESSAGES.addStarted(projectName));
        IDE.fireEvent(new JobChangeEvent(job));
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.RequestStatusHandler#requestFinished(java.lang.String) */
    @Override
    public void requestFinished(String id) {
        Job job = new Job(id, JobStatus.FINISHED);
        job.setFinishMessage(GitExtension.MESSAGES.addFinished(projectName));
        IDE.fireEvent(new JobChangeEvent(job));
    }

}
