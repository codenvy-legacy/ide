/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.git.client.commit;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.job.Job;
import com.codenvy.ide.job.JobChangeEvent;
import com.codenvy.ide.job.RequestStatusHandlerBase;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Oct 31, 2011 evgen $
 */
public class CommitRequestHandler extends RequestStatusHandlerBase {
    private String                  comment;
    private GitLocalizationConstant constant;

    /**
     * Create handler.
     *
     * @param projectName
     * @param comment
     * @param eventBus
     * @param constant
     */
    public CommitRequestHandler(@NotNull String projectName, @NotNull String comment, @NotNull EventBus eventBus,
                                @NotNull GitLocalizationConstant constant) {
        super(projectName, eventBus);
        this.comment = comment;
        this.constant = constant;
    }

    /** {@inheritDoc} */
    @Override
    public void requestInProgress(String id) {
        Job job = new Job(id, Job.JobStatus.STARTED);
        job.setStartMessage(constant.commitStarted(projectName, comment));
        eventBus.fireEvent(new JobChangeEvent(job));
    }

    /** {@inheritDoc} */
    @Override
    public void requestFinished(String id) {
        Job job = new Job(id, Job.JobStatus.FINISHED);
        job.setFinishMessage(constant.commitFinished(projectName, comment));
        eventBus.fireEvent(new JobChangeEvent(job));
    }
}